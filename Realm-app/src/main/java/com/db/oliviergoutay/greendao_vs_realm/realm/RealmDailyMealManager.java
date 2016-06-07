package com.db.oliviergoutay.greendao_vs_realm.realm;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.db.oliviergoutay.greendao_vs_realm.DbApp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.realm.Realm;

/**
 * The managing class for the persistence and synchronization of daily meals.
 */
public class RealmDailyMealManager {

    /**
     * A {@link CountDownLatch} used for test purpose to measure performances of insert etc...
     */
    protected CountDownLatch mTestCountDownLatch;

    /**
     * Tag for logging this class
     */
    private static final String TAG = "RealmDailyMealManager";

    /**
     * The current context
     */
    private Context mContext;

    /**
     * Partial singleton of this class.
     */
    private static RealmDailyMealManager mInstance;

    /**
     * Singleton, lazy initialization of this class
     *
     * @param context The context of the activity, needed for network calls
     * @return
     */
    public synchronized static RealmDailyMealManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RealmDailyMealManager(context);
        }
        return mInstance;
    }

    protected RealmDailyMealManager(Context context) {
        this.mContext = context;
    }

    /**
     * Update a {@link DailyMealRealm} and all of his
     * {@link MealRealm}.
     *
     * @param dailyMealRealm the {@link DailyMealRealm} to update completely in database
     */
    public void updateDatabase(DailyMealRealm dailyMealRealm) {
        if (dailyMealRealm != null) {
            setEatenOnDatesToMeal(dailyMealRealm);

            Realm realm = DbApp.getRealm();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(dailyMealRealm);
            realm.commitTransaction();
        }
    }

    /**
     * Updates a list of {@link DailyMealRealm} in database, async for better perf
     * !!! Do not insert the {@link MealItemRealm}, as it would take too long !!!
     */
    public void updateDatabase(final Context context, final List<DailyMealRealm> dailyMealRealms) {
        if (dailyMealRealms == null) {
            return;
        }

        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                //Add eatenOn to meal apis and hold
                //Insert all daily mealRealms
                if (dailyMealRealms != null) {
                    setEatenOnDatesToMeal(dailyMealRealms);

                    Realm realm = DbApp.getRealm();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(dailyMealRealms);
                    realm.commitTransaction();

                    if (dailyMealRealms.size() > 1 && context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO finish
                            }
                        });
                    }

                    if (mTestCountDownLatch != null && mTestCountDownLatch.getCount() > 0) {
                        mTestCountDownLatch.countDown();
                    }
                }
            }
        });
    }

    public void setEatenOnDatesToMeal(List<DailyMealRealm> dailyMealRealms) {
        if (dailyMealRealms == null) {
            return;
        }
        List<MealRealm> mealApiList = new ArrayList<>(dailyMealRealms.size());
        for (DailyMealRealm dailyMealRealm : dailyMealRealms) {
            setEatenOnDatesToMeal(dailyMealRealm);
        }
    }

    public void setEatenOnDatesToMeal(DailyMealRealm dailyMealRealm) {
        if (dailyMealRealm != null) {
            for (MealRealm mealRealm : dailyMealRealm.getMeals()) {
                mealRealm.setEatenOn(dailyMealRealm.getEatenOn());
            }
        }
    }

    /**
     * Set the test {@link CountDownLatch}
     */
    public void setTestCountDownLatch(CountDownLatch testCountDownLatch) {
        this.mTestCountDownLatch = testCountDownLatch;
    }

}
