package com.db.oliviergoutay.greendao_vs_realm.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by olivier.goutay on 6/6/16.
 */
public class DailyMealRealm extends RealmObject {

    @PrimaryKey
    private long eatenOn;
    private Integer glassesWater;
    private Long updatedAt;
    private String reflection;

    private RealmList<MealRealm> meals;

    public DailyMealRealm() {
    }

    public DailyMealRealm(long eatenOn, Integer glassesWater, Long updatedAt, String reflection, RealmList<MealRealm> meals) {
        this.eatenOn = eatenOn;
        this.glassesWater = glassesWater;
        this.updatedAt = updatedAt;
        this.reflection = reflection;
        this.meals = meals;
    }

    public long getEatenOn() {
        return eatenOn;
    }

    public void setEatenOn(long eatenOn) {
        this.eatenOn = eatenOn;
    }

    public Integer getGlassesWater() {
        return glassesWater;
    }

    public void setGlassesWater(Integer glassesWater) {
        this.glassesWater = glassesWater;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getReflection() {
        return reflection;
    }

    public void setReflection(String reflection) {
        this.reflection = reflection;
    }

    public RealmList<MealRealm> getMeals() {
        return meals;
    }

    public void setMeals(RealmList<MealRealm> meals) {
        this.meals = meals;
    }
}
