package com.db.oliviergoutay.greendao_vs_realm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.test.AndroidTestCase;
import android.util.Log;

import com.db.oliviergoutay.greendao_vs_realm.greendao.GreenDaoDailyMealManager;
import com.db.oliviergoutay.greendao_vs_realm.schema.DaoSession;

import java.io.File;


/**
 * Created by oliviergoutay on 10/8/15.
 * Gives you access to {@link com.db.oliviergoutay.greendao_vs_realm.schema.DaoSession} and a bunch of things like that needed in the unit tests.
 * Don't provide access to the views or anything else.
 */
public class AbstractAndroidTestCase extends AndroidTestCase {

    private static final String TAG = "AbstractAndroidTestCase";

    public DaoSession dao;
    public GreenDaoDailyMealManager greenDaoDailyMealManager;

    private int numberOfTry;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        numberOfTry = 0;
        createApplication();
        dao = DbApp.getDaoSession();

        if (dao == null) {
            throw new RuntimeException("dao not created yet");
        }

        greenDaoDailyMealManager = GreenDaoDailyMealManager.getInstance(getContext());
    }

    @Override protected void tearDown() throws Exception {
        dao.getDailyMealDao().deleteAll();
        dao.getMealDao().deleteAll();
        dao.getMealItemDao().deleteAll();

        super.tearDown();
    }

    /**
     * Create the {@link DbApp} by calling {@link DbApp#setupDatabase(Context)}.
     * If init failed (often the database not ready, then we wait for 1 sec and recall the same function.
     * Will try 5 times.
     */
    private void createApplication() {
        if (numberOfTry >= 5) {
            throw new RuntimeException("Could not create application database in five times");
        }

        try {
            DbApp.setupDatabase(getContext());
        } catch (Exception e) {
            Log.e(TAG, "Error creating the app, retry in 1sec");
            numberOfTry++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                Log.e(TAG, "Error waiting to create the app");
            }
            createApplication();
        }
    }

    /**
     * Returns the app file path
     */
    public String getFileDir() {
        PackageManager m = getContext().getPackageManager();
        String s = getContext().getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }

        return s;
    }

    /**
     * Returns the length of a file (its size)
     */
    public static long getFileSize(File f) {
        return f.length();
    }
}
