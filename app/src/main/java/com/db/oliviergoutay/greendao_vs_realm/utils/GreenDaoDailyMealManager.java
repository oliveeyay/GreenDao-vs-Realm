package com.db.oliviergoutay.greendao_vs_realm.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.db.oliviergoutay.greendao_vs_realm.DbApp;
import com.db.oliviergoutay.greendao_vs_realm.schema.DailyMeal;
import com.db.oliviergoutay.greendao_vs_realm.schema.DaoSession;
import com.db.oliviergoutay.greendao_vs_realm.schema.Meal;
import com.db.oliviergoutay.greendao_vs_realm.schema.MealItem;
import com.db.oliviergoutay.greendao_vs_realm.schema.MealItemDao;
import com.db.oliviergoutay.greendao_vs_realm.schema.MealPhoto;

import java.util.ArrayList;
import java.util.List;

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
     * Update a {@link DailyMeal} and all of his
     * {@link Meal}.
     *
     * @param dailyMeal the {@link DailyMeal} to update completely in database
     */
    public Long updateDatabase(DailyMeal dailyMeal) {
        Long idDailyMeal = mDao.getDailyMealDao().insertOrReplace(dailyMeal);

        if (dailyMeal != null && dailyMeal.getMeals() != null) {
            for (Meal meal : dailyMeal.getMeals()) {
                updateMealDatabase(meal, dailyMeal, false);
            }
        }
        return idDailyMeal;
    }

    /**
     * Updates a list of {@link DailyMeal} in database, async for better perf
     * !!! Do not insert the {@link MealItem}, as it would take too long !!!
     */
    public void updateDatabase(final Context context, final List<DailyMeal> dailyMeals) {
        if (dailyMeals == null) {
            return;
        }

        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                //Add eatenOn to meal apis and hold
                //Insert all daily meals
                if (dailyMeals != null) {
                    mDao.getDailyMealDao().insertOrReplaceInTx(dailyMeals);

                    if (dailyMeals.size() > 1 && context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO finish
                            }
                        });
                    }
                }

                //Insert all meals
                List<Meal> meals = setEatenOnDatesToMealApi(dailyMeals);
                if (meals != null) {
                    mDao.getMealDao().insertOrReplaceInTx(meals);
                }
            }
        });
    }

    /**
     * Update the database for the concerned {@link Meal}
     *
     * @param meal the {@link Meal we want to update}
     */
    public Long updateMealDatabase(Meal meal, DailyMeal dailyMeal, boolean hasToDeletePhoto) {
        Long idDailyMeal = mDao.getDailyMealDao().insertOrReplace(dailyMeal);

        //Set the eatenOn each time
        meal.setEatenOn(dailyMeal.getEatenOn());

        //Meal id
        Long mealId = getMealIdFromDb(dailyMeal, meal);

        //Photo handling
        Long photoId = insertOrMergeMealPhoto(dailyMeal, meal, mealId);
        MealPhoto mealPhoto = mDao.getMealPhotoDao().load(photoId);

        meal.setMealPhoto(mealPhoto);
        Long idMeal = mDao.getMealDao().insertOrReplace(meal);
        Meal mealDb = mDao.getMealDao().load(idMeal);

        //Delete all photos related to this meal
        if (hasToDeletePhoto) {
            deleteMealPhoto(idMeal);
        }

        try {
            List<MealItem> currentMealItems = mealDb.getItems();
            List<MealItem> newMealItems = meal.getItems();

            //Delete items that are not in new list
            List<MealItem> mealItemsToDelete = new ArrayList<>();
            for (MealItem mealItem : currentMealItems) {
                if (!newMealItems.contains(mealItem)) {
                    mealItemsToDelete.add(mealItem);
                }
            }
            mDao.getMealItemDao().deleteInTx(mealItemsToDelete);

            //Add Items that are not in db yet
            for (MealItem newItem : newMealItems) {
                if (!isMealItemAlreadyExistingForMeal(idMeal, newItem.getItem())) {
                    MealItem item = new MealItem(null, newItem.getItem(), idMeal);
                    if (item.getItem() != null) {
                        mDao.getMealItemDao().insertOrReplace(item);
                    }
                }
            }

            mealDb.resetItems();
        } catch (Exception e) {
            Log.d(TAG, "Problem inserting a meal in database, notifying crashlytics");
        }

        return idDailyMeal;
    }

    /**
     * Checks whether there is a {@link Meal} in the database
     *
     * @param mealDb The dailyMeal in the database whose meals we are checking
     * @param meal1  The meal from the server or application
     * @return The database id of the meal or null
     */
    private Long getMealIdFromDb(DailyMeal mealDb, Meal meal1) {
        if (mealDb == null || meal1 == null) {
            return null;
        }
        List<Meal> meals = mealDb.getMeals();
        if (meals == null) {
            return null;
        }
        mealDb.resetMeals();
        for (Meal meal : meals) {
            return meal.getId();
        }
        return null;
    }

    /**
     * Insert a new {@link MealPhoto} in database or merge it with a current {@link MealPhoto}
     *
     * @param dailyMeal The current {@link DailyMeal} db object containing the current {@link Meal}
     * @param meal      The {@link Meal} we want to merge the {@link MealPhoto} with the database object, or just insert as a new object
     * @param mealId    The mealId for which we want to insert this new {@link MealPhoto}
     * @return
     */
    private Long insertOrMergeMealPhoto(DailyMeal dailyMeal, Meal meal, Long mealId) {
        if (dailyMeal != null && meal != null && mealId != null) {
            dailyMeal.resetMeals();

            MealPhoto dbPhoto = meal.getMealPhoto();
            if (dbPhoto != null) {
                if (meal.getMealPhoto() == null) {
                    meal.setMealPhoto(dbPhoto);
                } else if (dbPhoto.getDownloadUrl() != null && meal.getMealPhoto().getDownloadUrl() == null) {
                    meal.getMealPhoto().setDownloadUrl(dbPhoto.getDownloadUrl());
                }
            }

            if (dbPhoto != null) {
                return mDao.getMealPhotoDao().insertOrReplace(dbPhoto);
            }
        }
        return null;
    }

    /**
     * Deletes all the {@link MealPhoto}'s associated to a particular
     * {@link Meal}
     *
     * @param mealId The database id of the meal
     */
    private void deleteMealPhoto(long mealId) {
        Meal meal = mDao.getMealDao().load(mealId);
        if (meal != null) {
            MealPhoto photo = meal.getMealPhoto();
            if (photo != null) {
                mDao.getMealPhotoDao().deleteByKey(photo.getId());
                meal.setPhotoId(null);
                meal.setMealPhoto(null);
                mDao.getMealDao().insertOrReplace(meal);
            }
        }
    }

    public List<Meal> setEatenOnDatesToMealApi(List<DailyMeal> dailyMeals) {
        if (dailyMeals == null) {
            return null;
        }
        List<Meal> mealApiList = new ArrayList<>(dailyMeals.size());
        for (DailyMeal dailyMeal : dailyMeals) {
            mealApiList.addAll(dailyMeal.getMeals());
        }
        return mealApiList;
    }

    private boolean isMealItemAlreadyExistingForMeal(Long mealId, String item) {
        if (mealId == null || item == null) {
            return true;
        }

        QueryBuilder<MealItem> queryBuilder = mDao.getMealItemDao().queryBuilder();
        queryBuilder.where(MealItemDao.Properties.MealId.eq(mealId), MealItemDao.Properties.Item.eq(item));
        return queryBuilder.count() > 0;
    }

}
