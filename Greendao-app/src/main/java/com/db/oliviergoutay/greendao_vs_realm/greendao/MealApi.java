package com.db.oliviergoutay.greendao_vs_realm.greendao;

import com.db.oliviergoutay.greendao_vs_realm.schema.Meal;
import com.db.oliviergoutay.greendao_vs_realm.schema.MealItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Represents one of the 4 different types of meals defined in {@link MealApi.MealType}.
 * They form a part of the {@link DailyMealApi}. This is the mobile/v3 implementation
 * <p/>
 * "meal" = {
 * "items" : [
 * "Filet Mignon",
 * "Beef Wellington"
 * ],
 * "healthiness" : 5,
 * "size" : "small"
 * "photo" : {
 * "filename " : "test.png",
 * "download_url" : "http://sample.com/test.png",
 * "photo_type" : "png"
 * }
 * }
 * <p></p>
 * All have the same format : "breakfast":["Sandwich","hot-dog","salmon"]
 * <p></p>
 * <p/>
 * Do not Upload this format, from dailymeals/ route "breakfast":{"items":["Ba"],"size":2,"healthiness":5,"photo_url":"https://www.filepicker.io/api/file/0F7TCtERQNi4o2Cdy6Cw","updated_at":1415995460937}
 * Created by stoyan on 8/28/14.
 */
public class MealApi implements Serializable {
    /**
     * Tag for logging this class
     */
    private static final String TAG = "MealApi";

    /**
     * The date in string format of when this meal was eaten, should
     * match and be set by its {@link DailyMealApi}
     */
    private Long mEatenOn;

    public static final String API_TYPE = "meal";
    public static final String REQUEST_TYPE_PHOTO = "photo";
    public static final String REQUEST_UPDATED_AT = "updated_at";
    public static final String REQUEST_SIZE = "size";
    public static final String REQUEST_HEALTHINESS = "healthiness";
    public static final String REQUEST_DOWNLOAD_URL = "download_url";
    public static final String REQUEST_PHOTO_TYPE = "photo_type";
    public static final String REQUEST_ITEMS = "items";

    /**
     * The types of meals possible
     */
    public enum MealType {
        BREAKFAST,
        LUNCH,
        DINNER,
        SNACK;

        public static MealType getFromString(String type) {
            for (MealType mealType : MealType.values()) {
                if (mealType.name().equalsIgnoreCase(type)) {
                    return mealType;
                }
            }
            return null;
        }

        public static MealType getFromContained(String key) {
            key = key.toLowerCase(Locale.getDefault());
            for (MealType mealType : MealType.values()) {
                if (key.contains(mealType.name().toLowerCase(Locale.getDefault()))) {
                    return mealType;
                }
            }
            return null;
        }
    }

    /**
     * The sizes that a meal can take
     */
    public static class MEAL_SIZE {
        public static final int SMALL = 1;
        public static final int MEDIUM = 2;
        public static final int LARGE = 3;
    }

    /**
     * The sizes that a meal can take
     */
    public static class MEAL_HEALTHINESS {
        public static final int NOT_HEALTHY = 1;
        public static final int MEDIUM_HEALTHY = 5;
        public static final int HEALTHY = 10;
    }

    /**
     * The items string key, key for an array of strings
     */
    public static final String ITEMS = "items";

    /**
     * The size string key, [1-3]; use {@link MealApi.MEAL_SIZE}
     */
    public static final String SIZE = "size";

    /**
     * The healthiness string key, [0-10]
     */
    public static final String HEALTHINESS = "healthiness";

    /**
     * The serializable name that we get from the GET /dailymeals/ of meals.
     * Object to send does not have the sane format
     */
    public static final String PHOTO_URL = "photo_url";

    /**
     * The serializable name that we get from the GET /dailymeals/ of meals.
     * The Long representing the last updated date.
     */
    public static final String UPDATED_AT = "updated_at";

    /**
     * The meal type {@link MealApi.MealType}
     */
    public static final String MEAL_TYPE = "meal_type";

    private Long mServerId;//"id":114190
    private List<String> mItems;
    private Integer mSize;
    private Integer mHealthiness;
    private MealType mMealType;
    private String mPhotoUrl;
    private Long mUpdatedAt;

    /**
     * Constructor of this class
     *
     * @param mealType The type of meal
     */
    public MealApi(MealType mealType, Long eatenOn, Long updatedAt) {
        super();
        setUpdatedAt(updatedAt);
        setMealType(mealType);
        setEatenOn(eatenOn);
    }


    /**
     * Constructor of the meal
     *
     * @param items       The items of the meal, separated with the "|" character
     * @param size        The size of the meal [1-3]
     * @param healthiness The healthiness of the meal [0-10]
     * @param mealType    The meal type
     * @param updatedAt   The meal type
     */
    public MealApi(String items, Integer size, Integer healthiness, MealType mealType, String photoUrl, Long eatenOn, Long updatedAt) {
        super();
        if (items != null) {
            setItems(Arrays.asList(items.split("\\|")));
        }
        setUpdatedAt(updatedAt);
        setSize(size);
        setHealthiness(healthiness);
        setPhotoUrl(photoUrl);
        setMealType(mealType);
        setEatenOn(eatenOn);
    }

    /**
     * Constructor of the meal
     *
     * @param items       The items of the meal, separated with the "|" character
     * @param size        The size of the meal [1-3]
     * @param healthiness The healthiness of the meal [0-10]
     * @param mealType    The meal string type
     */
    public MealApi(List<String> items, Integer size, Integer healthiness, MealType mealType, String photoUrl, Long eatenOn, Long updatedAt) {
        super();
        setUpdatedAt(updatedAt);
        setItems(items);
        setSize(size);
        setHealthiness(healthiness);
        setPhotoUrl(photoUrl);
        setMealType(mealType);
        setEatenOn(eatenOn);
    }

    /**
     * Constructor of the meal
     *
     * @param items       The items of the meal, separated with the "|" character
     * @param size        The size of the meal [1-3]
     * @param healthiness The healthiness of the meal [0-10]
     * @param mealType    The meal string type
     */
    public MealApi(List<String> items, Integer size, Integer healthiness, String mealType, String photoUrl, Long eatenOn, Long updatedAt) {
        super();
        setUpdatedAt(updatedAt);
        setItems(items);
        setSize(size);
        setHealthiness(healthiness);
        setPhotoUrl(photoUrl);
        setMealType(MealType.getFromString(mealType));
        setEatenOn(eatenOn);
    }

    /**
     * Compares {@link MealApi} based on {@link #getUpdatedAt()}, {@link #getEatenOn()}
     *
     * @param object The {@link MealApi} we want to compare to
     * @return true if same, false otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MealApi)) {
            return false;
        }
        MealApi mealApi = (MealApi) object;
        if (mealApi.getMealType() == null || mealApi.getUpdatedAt() == null || mealApi.getEatenOn() == null) {
            return false;
        }
        return mealApi.getMealType().equals(this.getMealType()) && mealApi.getUpdatedAt().equals(this.getUpdatedAt()) && mealApi.getEatenOn().equals(this.getEatenOn());
    }

    /**
     * Converts a list of {@link Meal} into a list
     * of {@link MealApi} by calling{@link #toMealApiObject(Meal)}
     * on each meal
     *
     * @param meals The list of meals to convert
     * @return The converted meals
     */
    public static List<MealApi> toMealApiObject(List<Meal> meals) {
        if (meals == null) {
            return new ArrayList<>();
        }
        List<MealApi> converted = new ArrayList<>(meals.size());
        for (Meal meal : meals) {
            converted.add(toMealApiObject(meal));
        }
        return converted;
    }

    /**
     * Converts a {@link Meal} into a
     * {@link MealApi}
     * on each meal
     *
     * @param meal The meal to convert
     * @return The converted meal
     */
    public static MealApi toMealApiObject(Meal meal) {
        MealApi converted = new MealApi(null, getIntSize(meal.getSize()), meal.getHealthiness(), meal.getMealType(), meal.getPhotoUrl(), meal.getEatenOn(), meal.getUpdatedAt());

        meal.resetItems();
        for (MealItem item : meal.getItems()) {
            converted.setItem(item.getItem());
        }

        return converted;
    }

    /**
     * Converts a {@link MealApi} into a
     * {@link Meal}
     * on each meal
     * <p></p>
     *
     * @param mealApis The meals to convert
     * @return The converted meal
     */
    public static List<Meal> toMealObject(List<MealApi> mealApis) {
        if (mealApis == null) {
            return new ArrayList<>();
        }
        List<Meal> converted = new ArrayList<>(mealApis.size());
        for (MealApi mealApi : mealApis) {
            converted.add(toMealObject(mealApi, null));
        }
        return converted;
    }

    /**
     * Converts a {@link MealApi} into a
     * {@link Meal}
     * on each meal
     * <p></p>
     *
     * @param mealApi The meal to convert
     * @return The converted meal
     */
    public static Meal toMealObject(MealApi mealApi, Long mealId) {
        Meal meal = new Meal(mealId, mealApi.getStringSize(), mealApi.getMealName(), mealApi.getUpdatedAt(), mealApi.getHealthiness(), mealApi.getPhotoUrl(), mealApi.getEatenOn(), null);
        return meal;
    }

    public Long getServerId() {
        return mServerId;
    }

    public void setServerId(Long serverId) {
        this.mServerId = serverId;
    }

    /**
     * Turns the {@link MealApi.MealType} of this instance
     *
     * @return The string equivalent of the {@link #mMealType}
     */
    public String getMealName() {
        if (this.getMealType() == null) {
            return null;
        }
        return this.mMealType.name();
    }

    /**
     * @return The list of items in this meal
     */
    public List<String> getItems() {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        return mItems;
    }

    public void setItems(final List<String> items) {
        mItems = items;
    }

    public void setItem(String item) {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        mItems.add(item);
    }

    /**
     * @return The size of the meal
     */
    public String getStringSize() {
        if (mSize == null) {
            return "";
        }
        switch (mSize) {
            case 1:
                return "small";
            case 2:
                return "medium";
            case 3:
                return "big";
            default:
                return "";
        }
    }

    public static Integer getIntSize(String size) {
        switch (size) {
            case "small":
                return 1;
            case "medium":
                return 2;
            case "big":
                return 3;
            default:
                return null;
        }
    }

    public Integer getSize() {
        return mSize;
    }

    /**
     * The size of the meal, must be 1-3 inclusive. Forces number to
     * be withing range
     *
     * @param size
     */
    public void setSize(Integer size) {
        if (size == null) {
            return;
        }
        if (size > 3) {
            mSize = 3;
            return;
        }
        if (size < 1) {
            mSize = 1;
            return;
        }
        mSize = size;
    }

    public Integer getHealthiness() {
        return mHealthiness;
    }

    /**
     * The healthiness of this meal, must be 0-10 inclusive
     * Forces number to be withing range
     *
     * @param healthiness
     */
    public void setHealthiness(Integer healthiness) {
        if (healthiness == null) {
            return;
        }
        if (healthiness > 10) {
            mHealthiness = 10;
            return;
        }
        if (healthiness < 0) {
            mHealthiness = 0;
            return;
        }
        mHealthiness = healthiness;
    }

    /**
     * Gets the type of meal this is
     *
     * @return The meal type
     */
    public MealType getMealType() {
        return mMealType;
    }

    /**
     * Set the meal type of this instance
     *
     * @param mealType The meal type
     */
    public void setMealType(MealType mealType) {
        mMealType = mealType;
    }

    /**
     * Returns the amazon kairos photo url
     *
     * @return
     */
    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.mPhotoUrl = photoUrl;
    }

    public Long getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.mUpdatedAt = updatedAt;
    }


    public Long getEatenOn() {
        return mEatenOn;
    }

    public void setEatenOn(Long eatenOn) {
        this.mEatenOn = eatenOn;
    }
}
