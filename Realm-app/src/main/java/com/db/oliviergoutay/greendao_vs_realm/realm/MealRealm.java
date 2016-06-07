package com.db.oliviergoutay.greendao_vs_realm.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by olivier.goutay on 6/6/16.
 */
public class MealRealm extends RealmObject {

    @PrimaryKey
    private String id;
    private Long eatenOn;
    private String size;
    private String mealType;
    private Long updatedAt;
    private Integer healthiness;
    private String photoUrl;

    private RealmList<MealItemRealm> items;

    public MealRealm() {
    }

    public MealRealm(String id, String size, String mealType, Long updatedAt, Integer healthiness, String photoUrl, Long eatenOn, RealmList<MealItemRealm> items) {
        this.id = id;
        this.size = size;
        this.mealType = mealType;
        this.updatedAt = updatedAt;
        this.healthiness = healthiness;
        this.photoUrl = photoUrl;
        this.eatenOn = eatenOn;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getHealthiness() {
        return healthiness;
    }

    public void setHealthiness(Integer healthiness) {
        this.healthiness = healthiness;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getEatenOn() {
        return eatenOn;
    }

    public void setEatenOn(Long eatenOn) {
        this.eatenOn = eatenOn;
    }

    public RealmList<MealItemRealm> getItems() {
        return items;
    }

    public void setItems(RealmList<MealItemRealm> items) {
        this.items = items;
    }
}
