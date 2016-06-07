package com.db.oliviergoutay.greendao_vs_realm.realm;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by olivier.goutay on 6/6/16.
 */
public class MealItemRealm extends RealmObject implements Serializable {

    @PrimaryKey
    private String id;
    private String item;

    public MealItemRealm() {
    }

    public MealItemRealm(String id, String item) {
        this.id = id;
        this.item = item;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
