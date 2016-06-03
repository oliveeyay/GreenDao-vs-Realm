package com.db.oliviergoutay.greendao;

import com.db.oliviergoutay.greendao.schema.DailyMealSchema;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Schema;

/**
 * Database generator that creates the auto-gen sqlite files
 * Generator can only operate on one {@link de.greenrobot.daogenerator.Schema}, therefore all entities
 * must use a static {@link de.greenrobot.daogenerator.Schema}
 * that they add themselves to
 */
public class DatabaseGenerator {

    public static void main(String args[]) throws Exception {
        generateSchemas(args[0]);
    }

    private static void generateSchemas(String outputPath)  throws Exception{
        DaoGenerator generator = new DaoGenerator();
        generator.generateAll(getSchema(), outputPath);
    }

    public static Schema getSchema(){
        DailyMealSchema dailyMealSchema = new DailyMealSchema();
        return dailyMealSchema.getSchema();
    }
}
