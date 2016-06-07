package com.db.oliviergoutay.greendao.schema;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.ToMany;

/**
 *
 * "{
 *      "id":114190,
 *      "account_id":5041,
 *      "eaten_on":"2014-08-28",
 *      "breakfast":["Sandwich","hot-dog","salmon"],
 *      "lunch":["Sandwich","hot-dog","salmon"],
 *      "snack":["Sandwich","hot-dog","salmon"],
 *      "dinner":["Sandwich","hot-dog","salmon"],
 *      "glasses_water": 3,
 *      "created_at":1409247826944,
 *      "updated_at":1409247826944,
 *      "reflection":"Something about loosing more weight",
 *      "breakfast_size":"3", MealSchema
 *      "lunch_size":"2", MealSchema
 *      "dinner_size":"2",
 *      "snack_size":"2",
 *      "breakfast_healthiness":10,
 *      "lunch_healthiness":4,
 *      "dinner_healthiness":2,
 *      "snack_healthiness":7
 *  }"
 */
public class DailyMealSchema extends AbstractSchema {
    public static final String SCHEMA_KEY = "DailyMeal";
    //Meals
    public static final String BREAKFAST = "breakfast";
    public static final String LUNCH = "lunch";
    public static final String DINNER = "dinner";
    public static final String SNACK = "snack";

    //DailyMeal keys, Outside meal arrays
    public static final String EATEN_ON = "eatenOn";
    public static final String REFLECTION = "reflection";
    public static final String UPDATED_AT = "updatedAt";
    public static final String GLASSES_WATER = "glassesWater";
    public static final String DAILYMEAL_TO_MEAL_RELATION_KEY = "meals";

    //Meal keys
    public static final String SCHEMA_MEAL_KEY = "Meal";
    public static final String MEAL_SIZE = "size";
    public static final String MEAL_HEALTHINESS = "healthiness";
    public static final String MEAL_PHOTO_URL = "photoUrl";
    public static final String MEAL_TYPE = "mealType";
    public static final String MEAL_DAILYMEAL_ID = "dailyMealId";
    public static final String MEAL_PHOTO_ID = "photoId";
    public static final String MEAL_TO_ITEMS_RELATION_KEY = "items";
    public static final String MEAL_UPDATED_AT = "updatedAt";

    //MealItem keys
    public static final String SCHEMA_MEAL_ITEM_KEY = "MealItem";
    public static final String MEAL_ITEM = "item";
    public static final String OWNER_MEAL_ID = "mealId";

    @Override
    public void setSchemaProperties() {
        Entity dailyMeal = mSchema.addEntity(SCHEMA_KEY);
        dailyMeal.addIdProperty().autoincrement();
        Property dailyMealEatenOn = dailyMeal.addLongProperty(EATEN_ON).notNull().unique().index().getProperty();
        dailyMeal.addIntProperty(GLASSES_WATER);
        dailyMeal.addLongProperty(UPDATED_AT);
        dailyMeal.addStringProperty(REFLECTION);

        //Create the meal entity
        Entity meal = mSchema.addEntity(SCHEMA_MEAL_KEY);
        Property mealOrderProperty = meal.addIdProperty().autoincrement().getProperty();
        meal.addStringProperty(MEAL_SIZE);
        Property mealType = meal.addStringProperty(MEAL_TYPE).getProperty();
        meal.addLongProperty(MEAL_UPDATED_AT);
        meal.addIntProperty(MEAL_HEALTHINESS);
        meal.addStringProperty(MEAL_PHOTO_URL);
        Property mealOwnerEatenOnProperty = meal.addLongProperty(EATEN_ON).notNull().index().getProperty();
        Property mealPhotoProperty = meal.addLongProperty(MEAL_PHOTO_ID).getProperty();

        //Add a unique index on MealType + Meal eatenOn
        Index indexUniqueMealType = new Index();
        indexUniqueMealType.addProperty(mealType);
        indexUniqueMealType.addProperty(mealOwnerEatenOnProperty);
        indexUniqueMealType.makeUnique();
        meal.addIndex(indexUniqueMealType);

        //Set the property that the daily meal has meals
        ToMany dailyMealToMealsEatenOn = dailyMeal.addToMany(dailyMealEatenOn, meal, mealOwnerEatenOnProperty);
        dailyMealToMealsEatenOn.setName(DAILYMEAL_TO_MEAL_RELATION_KEY);
        dailyMealToMealsEatenOn.orderDesc(mealOrderProperty);

        //Create the mealItem entity
        Entity mealItem = mSchema.addEntity(SCHEMA_MEAL_ITEM_KEY);
        Property mealItemOrderProperty = mealItem.addIdProperty().autoincrement().getProperty();
        Property mealItemItem = mealItem.addStringProperty(MEAL_ITEM).notNull().getProperty();
        Property mealItemOwnerProperty = mealItem.addLongProperty(OWNER_MEAL_ID).notNull().getProperty();

        //Add a unique index on mealItem + mealId
        Index indexUniqueMealItem = new Index();
        indexUniqueMealItem.addProperty(mealItemItem);
        indexUniqueMealItem.addProperty(mealItemOwnerProperty);
        indexUniqueMealItem.makeUnique();
        mealItem.addIndex(indexUniqueMealItem);

        //Set the property that the meal has meal items
        ToMany mealItemToMeals = meal.addToMany(mealItem, mealItemOwnerProperty);
        mealItemToMeals.setName(MEAL_TO_ITEMS_RELATION_KEY);
        mealItemToMeals.orderDesc(mealItemOrderProperty);
    }
}
