package com.db.oliviergoutay.greendao_vs_realm;

import android.content.Context;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import com.db.oliviergoutay.greendao_vs_realm.greendao.DailyMealApi;
import com.db.oliviergoutay.greendao_vs_realm.greendao.MealApi;
import com.db.oliviergoutay.greendao_vs_realm.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by stoyan on 9/17/14.
 */
public class GreenDaoPerformanceTest extends AbstractAndroidTestCase {

    private static final String TAG = "GreenDaoPerformanceTest";

    /**
     * Test the size of the database
     */
    @MediumTest
    public void testSizeDatabase() throws InterruptedException {
        //Add stuff in db
        testUpdateDatabaseListPerformance();

        //Show size database
        File db = new File(getFileDir() + "/databases/greendao_encrypted.db");
        Log.i(TAG, "Size of the DailyMeal GreenDao database is : " + getFileSize(db) / 1024 + " KB");
    }

    /**
     * Tests performance of {@link com.db.oliviergoutay.greendao_vs_realm.greendao.GreenDaoDailyMealManager#queryDailyMeal(long)}
     * and {@link com.db.oliviergoutay.greendao_vs_realm.greendao.GreenDaoDailyMealManager#queryAllDailyMealsOrdered(boolean)}
     */
    @MediumTest
    public void testQueryDatabasePerformance() throws InterruptedException {
        //Add stuff in db
        testUpdateDatabaseListPerformance();

        //Query one object
        long eatenOn = greenDaoDailyMealManager.queryAllDailyMealsOrdered(true).get(0).getEatenOn();
        long start = System.currentTimeMillis();
        assertNotNull(greenDaoDailyMealManager.queryDailyMeal(eatenOn));
        long end = System.currentTimeMillis();
        Log.i(TAG, "Query of one DailyMealApi took : " + (end - start) + " milliseconds");

        //Query all objects (not ordered)
        start = System.currentTimeMillis();
        assertEquals(365, greenDaoDailyMealManager.queryAllDailyMealsOrdered(false).size());
        end = System.currentTimeMillis();
        Log.i(TAG, "Query of all the DailyMealApi (not ordered) took : " + (end - start) + " milliseconds");

        //Query all objects (ordered)
        start = System.currentTimeMillis();
        assertEquals(365, greenDaoDailyMealManager.queryAllDailyMealsOrdered(true).size());
        end = System.currentTimeMillis();
        Log.i(TAG, "Query of all the DailyMealApi (ordered) took : " + (end - start) + " milliseconds");
    }

    /**
     * Tests performance of {@link com.db.oliviergoutay.greendao_vs_realm.greendao.GreenDaoDailyMealManager#updateDatabase(DailyMealApi)}
     */
    @MediumTest
    public void testUpdateDatabasePerformance() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        greenDaoDailyMealManager.setTestCountDownLatch(countDownLatch);
        long start = System.currentTimeMillis();
        greenDaoDailyMealManager.updateDatabase(getMockDailyMealForDate(new Date()));
        countDownLatch.await();
        long end = System.currentTimeMillis();

        Log.i(TAG, "Insert of 1 DailyMealApi took : " + (end - start) + " milliseconds");
        //Check time is less than 3000 millis
        assertTrue(3000 > end - start);

        //Check all were inserted
        assertEquals(1, dao.getDailyMealDao().loadAll().size());
        assertEquals(4, dao.getMealDao().loadAll().size());
    }

    /**
     * Tests performance of {@link com.db.oliviergoutay.greendao_vs_realm.greendao.GreenDaoDailyMealManager#updateDatabase(Context, List)}
     */
    @MediumTest
    public void testUpdateDatabaseListPerformance() throws InterruptedException {
        List<DailyMealApi> mList = new ArrayList<>();
        Date date = new Date();
        for (int i = 0; i < 365; i++) {
            mList.add(getMockDailyMealForDate(date));
            date = Utilities.getYesterday(date);
        }

        CountDownLatch countDownLatch = new CountDownLatch(1);
        greenDaoDailyMealManager.setTestCountDownLatch(countDownLatch);
        long start = System.currentTimeMillis();
        greenDaoDailyMealManager.updateDatabase(mContext, mList);
        countDownLatch.await();
        long end = System.currentTimeMillis();

        Log.i(TAG, "Mass insert of 365 DailyMealApi took : " + (end - start) + " milliseconds");
        //Check time is less than 3000 millis
        assertTrue(3000 > end - start);

        //Check all were inserted
        assertEquals(365, dao.getDailyMealDao().loadAll().size());
        assertEquals(365 * 4, dao.getMealDao().loadAll().size());
    }

    /**
     * Creates a mocked {@link DailyMealApi} for this specific date
     */
    private DailyMealApi getMockDailyMealForDate(Date date) {
        DailyMealApi dailyMealApi = new DailyMealApi(date);

        ArrayList<String> items = new ArrayList<>(Arrays.asList("test1", "test2", "test3"));

        //List<String> items, Integer size, Integer healthiness, String mealType, String photoUrl, Long eatenOn, MealPhotoApi mealPhotoApi, Long updatedAt
        MealApi breakfast = new MealApi(items, 0, 0, MealApi.MealType.BREAKFAST, "test", date.getTime(), 0l);
        MealApi lunch = new MealApi(items, 0, 0, MealApi.MealType.LUNCH, "test", date.getTime(), 0l);
        MealApi dinner = new MealApi(items, 0, 0, MealApi.MealType.DINNER, "test", date.getTime(), 0l);
        MealApi snack = new MealApi(items, 0, 0, MealApi.MealType.SNACK, "test", date.getTime(), 0l);

        dailyMealApi.setBreakfast(breakfast);
        dailyMealApi.setLunch(lunch);
        dailyMealApi.setDinner(dinner);
        dailyMealApi.setSnack(snack);

        return dailyMealApi;
    }

}
