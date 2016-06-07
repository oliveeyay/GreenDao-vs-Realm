package com.db.oliviergoutay.greendao_vs_realm;

import android.app.Application;
import android.content.Context;

import com.db.oliviergoutay.greendao_vs_realm.utils.SharedPreferencesUtils;

import java.nio.charset.Charset;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by olivier.goutay on 6/3/16.
 */
public class DbApp extends Application {

    private static final String TAG = "DbApp";

    /**
     * Store the {@link android.content.Context} of the app
     */
    private static Context mApplicationContext;

    /**
     * The encrypted configuration of {@link io.realm.Realm}
     */
    private static RealmConfiguration mConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        setupDatabase(getApplicationContext());
    }

    /**
     * Sets up the databases
     */
    public static void setupDatabase(Context context) {
        //Init the random seed
        String seed = getSeed(context);

        //Create the database (Realm)
        initRealmConfig(context);
    }

    /**
     * Returns a random generated seed stored in {@link android.content.SharedPreferences} thanks to {@link SharedPreferencesUtils}
     */
    private static String getSeed(Context context) {
        String seed = SharedPreferencesUtils.getStringForKey(context, SharedPreferencesUtils.SEED_KEY);

        if (seed == null) {
            seed = SharedPreferencesUtils.generateRandomString();
            SharedPreferencesUtils.storeStringForKey(context, SharedPreferencesUtils.SEED_KEY, seed);
        }

        return seed;
    }

    /**
     * Init the Realm database by configuring {@link #mConfig}
     */
    private static void initRealmConfig(Context context) {
        mConfig = new RealmConfiguration.Builder(context)
                .encryptionKey(getSeed(context).getBytes(Charset.defaultCharset()))
                .build();
    }

    public static Realm getRealm() {
        if (mConfig == null) {
            initRealmConfig(getAppContext());
        }

        return Realm.getInstance(mConfig);
    }

    public static Context getAppContext() {
        return mApplicationContext;
    }

}
