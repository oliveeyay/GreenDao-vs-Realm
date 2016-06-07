package com.db.oliviergoutay.greendao_vs_realm;

import android.content.Context;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import com.db.oliviergoutay.greendao_vs_realm.realm.DailyMealRealm;
import com.db.oliviergoutay.greendao_vs_realm.realm.MealItemRealm;
import com.db.oliviergoutay.greendao_vs_realm.realm.MealRealm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.realm.RealmList;

/**
 * Created by stoyan on 9/17/14.
 */
public class RealmPerformanceTest extends AbstractAndroidTestCase {

    private static final String TAG = "RealmPerformanceTest";

    /**
     * Tests performance of {@link com.db.oliviergoutay.greendao_vs_realm.realm.RealmDailyMealManager#queryDailyMeal(long)}
     * and {@link com.db.oliviergoutay.greendao_vs_realm.realm.RealmDailyMealManager#queryAllDailyMealsOrdered(boolean)}
     */
    @MediumTest
    public void testQueryDatabasePerformance() throws InterruptedException {
        //Add stuff in db
        testUpdateDatabaseListPerformance();

        //Query one object
        long eatenOn = realmDailyMealManager.queryAllDailyMealsOrdered(true).get(0).getEatenOn();
        long start = System.currentTimeMillis();
        assertNotNull(realmDailyMealManager.queryDailyMeal(eatenOn));
        long end = System.currentTimeMillis();
        Log.i(TAG, "Query of one DailyMealRealm took : " + (end - start) + " milliseconds");

        //Query all objects (not ordered)
        start = System.currentTimeMillis();
        assertEquals(365, realmDailyMealManager.queryAllDailyMealsOrdered(false).size());
        end = System.currentTimeMillis();
        Log.i(TAG, "Query of all the DailyMealRealm (not ordered) took : " + (end - start) + " milliseconds");

        //Query all objects (ordered)
        start = System.currentTimeMillis();
        assertEquals(365, realmDailyMealManager.queryAllDailyMealsOrdered(true).size());
        end = System.currentTimeMillis();
        Log.i(TAG, "Query of all the DailyMealRealm (ordered) took : " + (end - start) + " milliseconds");
    }

    /**
     * Tests performance of {@link com.db.oliviergoutay.greendao_vs_realm.realm.RealmDailyMealManager#updateDatabase(DailyMealRealm)}
     */
    @MediumTest
    public void testUpdateDatabasePerformance() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        realmDailyMealManager.setTestCountDownLatch(countDownLatch);
        long start = System.currentTimeMillis();
        realmDailyMealManager.updateDatabase(getMockDailyMealForDate(new Date()));
        countDownLatch.await();
        long end = System.currentTimeMillis();

        Log.i(TAG, "Insert of 1 DailyMealRealm took : " + (end - start) + " milliseconds");
        //Check time is less than 3000 millis
        assertTrue(3000 > end - start);

        //Check all were inserted
        assertEquals(1, DbApp.getRealm().where(DailyMealRealm.class).count());
        assertEquals(4, DbApp.getRealm().where(MealRealm.class).count());
        assertEquals(3, DbApp.getRealm().where(MealItemRealm.class).count());
    }

    /**
     * Tests performance of {@link com.db.oliviergoutay.greendao_vs_realm.realm.RealmDailyMealManager#updateDatabase(Context, List)}
     */
    @MediumTest
    public void testUpdateDatabaseListPerformance() throws InterruptedException {
        List<DailyMealRealm> mList = new ArrayList<>();
        Date date = new Date();
        for (int i = 0; i < 365; i++) {
            mList.add(getMockDailyMealForDate(date));

            date = getYesterday(date);
        }

        CountDownLatch countDownLatch = new CountDownLatch(1);
        realmDailyMealManager.setTestCountDownLatch(countDownLatch);
        long start = System.currentTimeMillis();
        realmDailyMealManager.updateDatabase(mContext, mList);
        countDownLatch.await();
        long end = System.currentTimeMillis();

        Log.i(TAG, "mass insert of 365 DailyMealRealm took : " + (end - start) + " milliseconds");
        //Check time is less than 3000 millis
        assertTrue(3000 > end - start);

        //Check all were inserted
        assertEquals(365, DbApp.getRealm().where(DailyMealRealm.class).count());
        assertEquals(365 * 4, DbApp.getRealm().where(MealRealm.class).count());
        assertEquals(365 * 3, DbApp.getRealm().where(MealItemRealm.class).count());
    }

    /**
     * Creates a mocked {@link DailyMealRealm} for this specific date
     */
    private DailyMealRealm getMockDailyMealForDate(Date date) {
        DailyMealRealm dailyMealRealm = new DailyMealRealm(date.getTime(), 0, 0L, "reflection", null);

        RealmList<MealItemRealm> items = new RealmList<>();
        items.add(new MealItemRealm(date.getTime() + "item 1", "item 1"));
        items.add(new MealItemRealm(date.getTime() + "item 2", "item 2"));
        items.add(new MealItemRealm(date.getTime() + "item 3", "item 3"));

        RealmList<MealRealm> mealRealms = new RealmList<>();
        mealRealms.add(new MealRealm(date.getTime() + "BREAKFAST", "size", "BREAKFAST", 0L, 0, "photoUrl", date.getTime(), items));
        mealRealms.add(new MealRealm(date.getTime() + "LUNCH", "size", "LUNCH", 0L, 0, "photoUrl", date.getTime(), items));
        mealRealms.add(new MealRealm(date.getTime() + "DINNER", "size", "DINNER", 0L, 0, "photoUrl", date.getTime(), items));
        mealRealms.add(new MealRealm(date.getTime() + "SNACK", "size", "SNACK", 0L, 0, "photoUrl", date.getTime(), items));
        dailyMealRealm.setMeals(mealRealms);

        return dailyMealRealm;
    }

    /**
     * Returns the previous day of the passed {@link Date}
     *
     * @return The date - 24 hours
     */
    public static Date getYesterday(Date date) {
        if (date == null) {
            return getYesterday(new Date());
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_WEEK, -1);

        return new Date(cal.getTimeInMillis());
    }

}
