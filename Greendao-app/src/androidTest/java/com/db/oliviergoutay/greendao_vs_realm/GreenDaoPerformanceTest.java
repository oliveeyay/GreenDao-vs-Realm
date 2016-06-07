package com.db.oliviergoutay.greendao_vs_realm;

import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import com.db.oliviergoutay.greendao_vs_realm.greendao.DailyMealApi;
import com.db.oliviergoutay.greendao_vs_realm.greendao.MealApi;
import com.db.oliviergoutay.greendao_vs_realm.utils.Utilities;

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
     * Tests performance of {@link com.db.oliviergoutay.greendao_vs_realm.greendao.GreenDaoDailyMealManager}
     */
    @MediumTest
    public void testUpdateDatabaseListPerformance() throws InterruptedException {
        List<DailyMealApi> mList = new ArrayList<>();
        Date date = new Date();
        for (int i = 0; i < 365; i++) {
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
            mList.add(dailyMealApi);

            date = Utilities.getYesterday(date);
        }

        CountDownLatch countDownLatch = new CountDownLatch(1);
        greenDaoDailyMealManager.setTestCountDownLatch(countDownLatch);
        long start = System.currentTimeMillis();
        greenDaoDailyMealManager.updateDatabase(mContext, mList);
        countDownLatch.await();
        long end = System.currentTimeMillis();

        Log.i(TAG, "mass insert of 365 DailyMealApi took : " + (end - start) + " milliseconds");
        //Check time is less than 3000 millis
        assertTrue(3000 > end - start);

        //Check all were inserted
        assertEquals(365, dao.getDailyMealDao().loadAll().size());
        assertEquals(365 * 4, dao.getMealDao().loadAll().size());
    }

}
