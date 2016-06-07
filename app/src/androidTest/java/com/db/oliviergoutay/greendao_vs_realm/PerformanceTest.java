package com.db.oliviergoutay.greendao_vs_realm;

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
public class PerformanceTest extends AbstractAndroidTestCase {

    private static final String TAG = "PerformanceTest";

    /**
     * Tests performance of {@link com.db.oliviergoutay.greendao_vs_realm.greendao.GreenDaoDailyMealManager}
     * versus {@link com.db.oliviergoutay.greendao_vs_realm.realm.RealmDailyMealManager}
     */
    @MediumTest
    public void testUpdateDatabaseListPerformance() throws InterruptedException {
        List<DailyMealRealm> mList = new ArrayList<>();
        Date date = new Date();
        long j = 0;
        for (int i = 0; i < 365; i++) {
            //long eatenOn, Integer glassesWater, Long updatedAt, String reflection, RealmList<MealRealm> meals
            DailyMealRealm dailyMealRealm = new DailyMealRealm(date.getTime(), 0, 0L, "reflection", null);

            RealmList<MealItemRealm> items = new RealmList<>();
            items.add(new MealItemRealm(date.getTime() + "item 1", "item 1"));
            items.add(new MealItemRealm(date.getTime() + "item 2", "item 2"));
            items.add(new MealItemRealm(date.getTime() + "item 3", "item 3"));

            //String size, String mealType, Long updatedAt, Integer healthiness, String photoUrl, Long eatenOn, RealmList<MealItemRealm> items
            RealmList<MealRealm> mealRealms = new RealmList<>();
            mealRealms.add(new MealRealm(date.getTime() + "BREAKFAST", "size", "BREAKFAST", 0L, 0, "photoUrl", date.getTime(), items));
            mealRealms.add(new MealRealm(date.getTime() + "LUNCH", "size", "LUNCH", 0L, 0, "photoUrl", date.getTime(), items));
            mealRealms.add(new MealRealm(date.getTime() + "DINNER", "size", "DINNER", 0L, 0, "photoUrl", date.getTime(), items));
            mealRealms.add(new MealRealm(date.getTime() + "SNACK", "size", "SNACK", 0L, 0, "photoUrl", date.getTime(), items));

            dailyMealRealm.setMeals(mealRealms);
            mList.add(dailyMealRealm);

            date = getYesterday(date);
            j += 4;
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
