package com.db.oliviergoutay.greendao_vs_realm.greendao;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.db.oliviergoutay.greendao_vs_realm.DbApp;
import com.db.oliviergoutay.greendao_vs_realm.schema.DailyMeal;
import com.db.oliviergoutay.greendao_vs_realm.schema.DailyMealDao;
import com.db.oliviergoutay.greendao_vs_realm.schema.DaoSession;
import com.db.oliviergoutay.greendao_vs_realm.schema.Meal;
import com.db.oliviergoutay.greendao_vs_realm.schema.MealItem;
import com.db.oliviergoutay.greendao_vs_realm.schema.MealItemDao;
import com.db.oliviergoutay.greendao_vs_realm.utils.Utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * The managing class for the persistence and synchronization of daily meals.
 */
public class GreenDaoDailyMealManager {
    /**
     * Tag for logging this class
     */
    private static final String TAG = "GDDailyMealManager";

    /**
     * The current context
     */
    private Context mContext;

    /**
     * The database session
     */
    protected DaoSession mDao;

    /**
     * Partial singleton of this class.
     */
    private static GreenDaoDailyMealManager mInstance;

    /**
     * A {@link CountDownLatch} used for test purpose to measure performances of insert etc...
     */
    protected CountDownLatch mTestCountDownLatch;

    /**
     * Singleton, lazy initialization of this class
     *
     * @param context The context of the activity, needed for network calls
     * @return
     */
    public synchronized static GreenDaoDailyMealManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GreenDaoDailyMealManager(context);
        }
        return mInstance;
    }

    protected GreenDaoDailyMealManager(Context context) {
        this.mContext = context;
        this.mDao = DbApp.getDaoSession();
    }

    /**
     * Returns a {@link DailyMeal} thanks to its primary key
     */
    public DailyMeal queryDailyMeal(long date) {
        return mDao.getDailyMealDao().queryBuilder().where(DailyMealDao.Properties.EatenOn.eq(date)).unique();
    }

    /**
     * Returns a list of {@link DailyMeal} ordered by date
     */
    public List<DailyMeal> queryAllDailyMealsOrdered(boolean order) {
        QueryBuilder<DailyMeal> queryBuilder = mDao.getDailyMealDao().queryBuilder();

        if (order) {
            queryBuilder.orderDesc(DailyMealDao.Properties.EatenOn);
        }

        return queryBuilder.list();
    }

    /**
     * Update a {@link DailyMealApi} and all of his
     * {@link MealApi}. Use only when you want to update all, otherwise
     * use {@link #updateMealDatabase(MealApi, DailyMeal, boolean)}
     * to update a single meal at a time.
     *
     * @param dailyMealApi the {@link DailyMealApi} to update completely in database
     */
    public Long updateDatabase(DailyMealApi dailyMealApi) {
        DailyMeal dailyMeal = toDbObject(dailyMealApi);
        Long idDailyMeal = mDao.getDailyMealDao().insertOrReplace(dailyMeal);

        if (dailyMealApi != null && dailyMealApi.getMeals() != null) {
            for (MealApi mealApi : dailyMealApi.getMeals()) {
                updateMealDatabase(mealApi, dailyMeal, false);
            }
        }

        if (mTestCountDownLatch != null && mTestCountDownLatch.getCount() > 0) {
            mTestCountDownLatch.countDown();
        }

        return idDailyMeal;
    }

    /**
     * Updates a list of {@link DailyMealApi} in database, async for better perf
     * !!! Do not insert the {@link MealItem}, as it would take too long !!!
     */
    public void updateDatabase(final Context context, final List<DailyMealApi> dailyMealList) {
        if (dailyMealList == null) {
            return;
        }

        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                //Add eatenOn to meal apis and hold
                //Insert all daily meals
                List<DailyMeal> dailyMeals = toDbObject(dailyMealList);
                if (dailyMeals != null) {
                    mDao.getDailyMealDao().insertOrReplaceInTx(dailyMeals);

                    if (dailyMeals.size() > 1 && context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO nothing
                            }
                        });
                    }
                }

                //Insert all meals
                List<Meal> meals = MealApi.toMealObject(setEatenOnDatesToMealApi(dailyMealList));
                if (meals != null) {
                    mDao.getMealDao().insertOrReplaceInTx(meals);
                }

                if (mTestCountDownLatch != null && mTestCountDownLatch.getCount() > 0) {
                    mTestCountDownLatch.countDown();
                }
            }
        });
    }

    /**
     * Update the database for the concerned {@link MealApi}
     *
     * @param mealApi the {@link MealApi we want to update}
     */
    public Long updateMealDatabase(MealApi mealApi, DailyMealApi dailyMealApi, boolean hasToDeletePhoto) {
        DailyMeal dailyMeal = toDbObject(dailyMealApi);
        Long idDailyMeal = mDao.getDailyMealDao().insertOrReplace(dailyMeal);

        updateMealDatabase(mealApi, dailyMeal, hasToDeletePhoto);

        return idDailyMeal;
    }

    /**
     * Update the database for the concerned {@link MealApi}
     *
     * @param mealApi   the {@link MealApi we want to update}
     * @param dailyMeal the {@link DailyMeal} database object (for id)
     */
    private synchronized void updateMealDatabase(MealApi mealApi, DailyMeal dailyMeal, boolean hasToDeletePhoto) {
        //Set the eatenOn each time
        mealApi.setEatenOn(dailyMeal.getEatenOn());

        //Meal id
        Long mealId = getMealIdFromDb(dailyMeal, mealApi);
        Long idMeal = mDao.getMealDao().insertOrReplace(MealApi.toMealObject(mealApi, mealId));
        Meal mealDb = mDao.getMealDao().load(idMeal);

        try {
            List<MealItem> currentMealItems = mealDb.getItems();
            List<String> newMealItems = mealApi.getItems();

            //Delete items that are not in new list
            List<MealItem> mealItemsToDelete = new ArrayList<>();
            for (MealItem mealItem : currentMealItems) {
                if (!newMealItems.contains(mealItem.getItem())) {
                    mealItemsToDelete.add(mealItem);
                }
            }
            mDao.getMealItemDao().deleteInTx(mealItemsToDelete);

            //Add Items that are not in db yet
            for (String newItem : newMealItems) {
                if (!isMealItemAlreadyExistingForMeal(idMeal, newItem)) {
                    MealItem item = new MealItem(null, newItem, idMeal);
                    if (item.getItem() != null) {
                        mDao.getMealItemDao().insertOrReplace(item);
                    }
                }
            }

            mealDb.resetItems();
        } catch (Exception e) {
            Log.d(TAG, "Problem inserting a meal in database, notifying crashlytics");
        }
    }

    private boolean isMealItemAlreadyExistingForMeal(Long mealId, String item) {
        if (mealId == null || item == null) {
            return true;
        }

        QueryBuilder<MealItem> queryBuilder = mDao.getMealItemDao().queryBuilder();
        queryBuilder.where(MealItemDao.Properties.MealId.eq(mealId), MealItemDao.Properties.Item.eq(item));
        return queryBuilder.count() > 0;
    }

    public DailyMealApi toApiObject(DailyMeal dailyMeal) {
        if (dailyMeal == null) {
            return null;
        }
        DailyMealApi dailyMealConverted = new DailyMealApi(dailyMeal.getGlassesWater(),
                Utilities.dateToString(new Date(dailyMeal.getEatenOn())), dailyMeal.getReflection(), dailyMeal.getUpdatedAt() != null ? dailyMeal.getUpdatedAt() : System.currentTimeMillis());

        dailyMeal.resetMeals();
        for (Meal meal : dailyMeal.getMeals()) {
            MealApi andMeal = MealApi.toMealApiObject(meal);
            dailyMealConverted.setMeal(andMeal);
        }
        return dailyMealConverted;
    }

    public DailyMeal toDbObject(DailyMealApi dailyMealApi) {
        if (dailyMealApi == null) {
            return new DailyMeal(null, new Date().getTime(), null, null, null);
        }

        DailyMeal dailyMeal = findByEatenOn(dailyMealApi.getEatenOn());
        if (dailyMeal == null) {
            return new DailyMeal(null, dailyMealApi.getEatenOnLong(), dailyMealApi.getGlassesWater(), dailyMealApi.getUpdatedAt(), dailyMealApi.getReflection());
        }
        dailyMeal.setEatenOn(dailyMealApi.getEatenOnLong());
        dailyMeal.setGlassesWater(dailyMealApi.getGlassesWater());
        dailyMeal.setUpdatedAt(dailyMealApi.getUpdatedAt());
        dailyMeal.setReflection(dailyMealApi.getReflection());
        return dailyMeal;
    }

    public List<MealApi> setEatenOnDatesToMealApi(List<DailyMealApi> dailyMealApis) {
        if (dailyMealApis == null) {
            return null;
        }
        List<MealApi> mealApiList = new ArrayList<>(dailyMealApis.size());
        for (DailyMealApi dailyApi : dailyMealApis) {
            for (MealApi mealApi : dailyApi.getMeals()) {
                mealApi.setEatenOn(dailyApi.getEatenOnLong());
            }
            mealApiList.addAll(dailyApi.getMeals());
        }
        return mealApiList;
    }

    /**
     * Converts a list of {@link DailyMealApi} using {@link #toDbObject(DailyMealApi)}
     */
    public List<DailyMeal> toDbObject(List<DailyMealApi> dailyMealApis) {
        if (dailyMealApis == null) {
            return null;
        }

        List<DailyMeal> dailyMeals = new ArrayList<>();
        for (DailyMealApi dailyMealApi : dailyMealApis) {
            dailyMeals.add(toDbObject(dailyMealApi));
        }

        return dailyMeals;
    }

    /**
     * Checks whether there is a {@link Meal} in the database
     * that corresponds to the {@link MealApi} so that we don't duplicate
     * {@link Meal} when we {@link #updateDatabase(DailyMealApi)}
     *
     * @param mealDb  The dailyMeal in the database whose meals we are checking
     * @param mealApi The meal from the server or application
     * @return The database id of the meal or null
     */
    private Long getMealIdFromDb(DailyMeal mealDb, MealApi mealApi) {
        if (mealDb == null || mealApi == null) {
            return null;
        }
        List<Meal> meals = mealDb.getMeals();
        if (meals == null) {
            return null;
        }
        mealDb.resetMeals();
        for (Meal meal : meals) {
            if (meal.getMealType().equalsIgnoreCase(mealApi.getMealName())) {
                return meal.getId();
            }
        }
        return null;
    }

    /**
     * Search the {@link DailyMeal} in the database for
     * one that has a particular eatenOn date
     *
     * @param eatenOn The date to search for, use {@link Utilities#stringToDate(String)}
     * @return The dailyMeal from the database, or null
     */
    private DailyMeal findByEatenOn(String eatenOn) {
        if (eatenOn == null) {
            return null;
        }
        return mDao.getDailyMealDao().queryBuilder()
                .where(DailyMealDao.Properties.EatenOn.eq(Utilities.stringToDate(eatenOn).getTime()))
                .unique();
    }

    /**
     * Set the test {@link CountDownLatch}
     */
    public void setTestCountDownLatch(CountDownLatch testCountDownLatch) {
        this.mTestCountDownLatch = testCountDownLatch;
    }

}
