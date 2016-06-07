package com.db.oliviergoutay.greendao_vs_realm.greendao;

import com.db.oliviergoutay.greendao_vs_realm.utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * DailyMealApi for test purpose
 */
public class DailyMealApi implements Serializable {
    /**
     * Tag for logging this class
     */
    private static final String TAG = "DailyMealApi";

    public static final int FIELD_NOT_SET = -1;

    public static final String API_TYPE = "daily_meal";
    public static final String UPDATED_AT = "updated_at";
    public static final String EATEN_ON = "eaten_on";
    public static final String GLASSES_WATER = "glasses_water";
    public static final String REFLECTION = "reflection";

    private Long mServerId;//"id":114190
    private Integer mGlassesWater;//"glasses_water": 3
    private MealApi mBreakfast;//"breakfast":["Sandwich","hot-dog","salmon"]
    private MealApi mLunch;//"lunch":["Sandwich","hot-dog","salmon"]
    private MealApi mDinner;//"dinner":["Sandwich","hot-dog","salmon"]
    private MealApi mSnack;//"snack":["Sandwich","hot-dog","salmon"]
    private String mEatenOn;//"eaten_on":"2014-08-28"
    private String mReflection;//"reflection":"Something about loosing more weight"
    private long mUpdatedAt;//"updated_at":1409247826944

    public DailyMealApi() {
        this(Calendar.getInstance().getTime());
    }

    public DailyMealApi(Date date) {
        super();
        setEatenOnFromCalendar(date);
        setUpdatedAt(date.getTime());
    }

    public DailyMealApi(Integer mGlassesWater, String mEatenOn, String mReflection, long mUpdatedAt) {
        this.mGlassesWater = mGlassesWater;
        this.mEatenOn = mEatenOn;
        this.mReflection = mReflection;
        this.mUpdatedAt = mUpdatedAt;
    }

    /**
     * Utility method to get the one of the {@link MealApi}, that
     * this class contains. Returns null if it hasn't been created
     *
     * @param mealType The meal to get
     * @return The meal if it exists
     */
    public MealApi getMeal(MealApi.MealType mealType) {
        if (mealType == null) {
            mealType = MealApi.MealType.BREAKFAST;
        }
        switch (mealType) {
            case BREAKFAST:
                return getBreakfast();
            case LUNCH:
                return getLunch();
            case DINNER:
                return getDinner();
            case SNACK:
                return getSnack();
            default:
                return null;

        }
    }

    /**
     * Method that returns all of the non-null {@link MealApi}
     * associated with this {@link DailyMealApi}
     *
     * @return The list of all the meals
     */
    public List<MealApi> getMeals() {
        List<MealApi> meals = new ArrayList<>(4);
        long eatenOn = Utilities.stringToDate(getEatenOn()).getTime();
        if (mBreakfast != null) {
            mBreakfast.setMealType(MealApi.MealType.BREAKFAST);
            mBreakfast.setEatenOn(eatenOn);
            meals.add(mBreakfast);
        }
        if (mLunch != null) {
            mLunch.setMealType(MealApi.MealType.LUNCH);
            mLunch.setEatenOn(eatenOn);
            meals.add(mLunch);
        }
        if (mDinner != null) {
            mDinner.setMealType(MealApi.MealType.DINNER);
            mDinner.setEatenOn(eatenOn);
            meals.add(mDinner);
        }
        if (mSnack != null) {
            mSnack.setMealType(MealApi.MealType.SNACK);
            mSnack.setEatenOn(eatenOn);
            meals.add(mSnack);
        }

        return meals;
    }

    /**
     * Method that returns all of the non-null {@link MealApi} except the {@link MealApi.MealType#SNACK} and empty {@link MealApi#HEALTHINESS}
     * associated with this {@link DailyMealApi}
     *
     * @return The list of all the meals except the {@link MealApi.MealType#SNACK}
     */
    public List<MealApi> getMainMealsForHealthiness() {
        List<MealApi> meals = new ArrayList<>(4);
        if (mBreakfast != null && mBreakfast.getHealthiness() != null) {
            mBreakfast.setMealType(MealApi.MealType.BREAKFAST);
            meals.add(mBreakfast);
        }
        if (mLunch != null && mLunch.getHealthiness() != null) {
            mLunch.setMealType(MealApi.MealType.LUNCH);
            meals.add(mLunch);
        }
        if (mDinner != null && mDinner.getHealthiness() != null) {
            mDinner.setMealType(MealApi.MealType.DINNER);
            meals.add(mDinner);
        }
        if (mSnack != null && mSnack.getHealthiness() != null) {
            mSnack.setMealType(MealApi.MealType.SNACK);
            meals.add(mSnack);
        }

        return meals;
    }

    public void setMeal(MealApi meal) {
        if (meal == null || meal.getMealType() == null) {
            return;
        }
        switch (meal.getMealType()) {
            case BREAKFAST:
                setBreakfast(meal);
                break;
            case LUNCH:
                setLunch(meal);
                break;
            case DINNER:
                setDinner(meal);
                break;
            case SNACK:
                setSnack(meal);
                break;
            default:
                break;
        }
    }

    /**
     * Returns a meal score based on the {@link #getMainMealsForHealthiness()}
     *
     * @return The score
     */
    public int getMealsScore() {
        List<MealApi> mealApis = getMainMealsForHealthiness();
        int score = 0;

        for (MealApi mealApi : mealApis) {
            Integer mealApiScore = mealApi.getHealthiness();
            score += mealApiScore == null ? 0 : mealApiScore;
        }

        score /= mealApis.size() == 0 ? 1 : mealApis.size();

        return score;
    }

    private long getUnixTimeMilli() {
        return System.currentTimeMillis();
    }

    public Long getServerId() {
        return mServerId;
    }

    public void setServerId(final Long id) {
        mServerId = id;
    }

    public Integer getGlassesWater() {
        return mGlassesWater;
    }

    public void setGlassesWater(final Integer glassesWater) {
        mGlassesWater = glassesWater;
    }

    /**
     * @return The breakfast meal
     */
    public MealApi getBreakfast() {
        //Server doesn't know type, !@#$
        if (mBreakfast != null) {
            mBreakfast.setMealType(MealApi.MealType.BREAKFAST);
        }
        return mBreakfast;
    }

    /**
     * Sets the breakfast meal
     *
     * @param breakfast Meal to set
     */
    public void setBreakfast(final MealApi breakfast) {
        mBreakfast = breakfast;
        //Server doesn't know type, !@#$
        if (mBreakfast != null) {
            mBreakfast.setMealType(MealApi.MealType.BREAKFAST);
        }
    }

    /**
     * @return The lunch meal
     */
    public MealApi getLunch() {
        //Server doesn't know type, !@#$
        if (mLunch != null) {
            mLunch.setMealType(MealApi.MealType.LUNCH);
        }
        return mLunch;
    }

    /**
     * Sets the lunch meal
     *
     * @param lunch Meal to set
     */
    public void setLunch(final MealApi lunch) {
        mLunch = lunch;
        //Server doesn't know type, !@#$
        if (mLunch != null) {
            mLunch.setMealType(MealApi.MealType.LUNCH);
        }
    }

    /**
     * @return The dinner meal
     */
    public MealApi getDinner() {
        //Server doesn't know type, !@#$
        if (mDinner != null) {
            mDinner.setMealType(MealApi.MealType.DINNER);
        }
        return mDinner;
    }

    /**
     * Sets the dinner meal
     *
     * @param dinner Meal to set
     */
    public void setDinner(final MealApi dinner) {
        mDinner = dinner;
        //Server doesn't know type, !@#$
        if (mDinner != null) {
            mDinner.setMealType(MealApi.MealType.DINNER);
        }
    }

    /**
     * @return The snack meal
     */
    public MealApi getSnack() {
        //Server doesn't know type, !@#$
        if (mSnack != null) {
            mSnack.setMealType(MealApi.MealType.SNACK);
        }
        return mSnack;
    }


    public void setSnack(final MealApi snack) {
        mSnack = snack;
        //Server doesn't know type, !@#$
        if (mSnack != null) {
            mSnack.setMealType(MealApi.MealType.SNACK);
        }
    }


    public String getEatenOn() {
        return mEatenOn;
    }

    /**
     * format: "eaten_on":"2014-08-28"
     *
     * @return
     */
    public long getEatenOnLong() {
        if (mEatenOn == null) {
            return new Date().getTime();
        }
        return Utilities.stringToDate(mEatenOn).getTime();
    }

    public void setEatenOn(final String eatenOn) {
        mEatenOn = eatenOn;
    }

    /**
     * @param date
     */
    public void setEatenOnFromCalendar(final Date date) {
        setEatenOn(Utilities.dateToString(date));
    }

    public String getReflection() {
        return mReflection;
    }

    public void setReflection(final String reflection) {
        mReflection = reflection;
    }

    public long getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        mUpdatedAt = updatedAt;
    }

}
