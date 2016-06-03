package com.db.oliviergoutay.greendao.schema;

import com.db.oliviergoutay.greendao.DatabaseGenerator;

import de.greenrobot.daogenerator.Schema;

/**
 * An abstract class that holds the defaults for {@link de.greenrobot.daogenerator.Schema}
 * generation in our application's database. All {@link de.greenrobot.daogenerator.Schema}
 * generation should inherit from this class
 * <p></p>
 */
public abstract class AbstractSchema {
    /**
     * The database version code, increment by 1 every time new schema are to be added to
     * the database.
     * <p></p>
     * Warning : GreenDao does not perform db migrations between versions itself,
     * these have to be done manually
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * The package that all the schema fall into
     */
    public static final String DATABASE_PACKAGE = "com.db.oliviergoutay.greendao_vs_realm.schema";

    /**
     * The database schema to be set in child classes and created in {@link DatabaseGenerator}.
     * Initializes the mSchema object with {@link #DATABASE_VERSION} and {@link #DATABASE_PACKAGE}
     */
    protected static Schema mSchema = new Schema(DATABASE_VERSION, DATABASE_PACKAGE);

    /**
     * Default constructor. Sets the schema properties.
     */
    public AbstractSchema() {
        setSchemaProperties();
    }

    /**
     * Abstract method that should set all the properties on {@link #mSchema}
     */
    public abstract void setSchemaProperties();

    /**
     * Get method for schema object
     *
     * @return {@link #mSchema}
     */
    public Schema getSchema() {
        return mSchema;
    }

}
