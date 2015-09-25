package de.slowpoke.android.mbtiles.validator;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by brianhoffmann on 24.09.2015.
 */
public interface Validator {

    /**
     * Validate an MBTiles database.
     *
     * @param database the mbtiles database to be validated
     * @throws InvalidFormatException if the format is not valid
     */
    void validate(SQLiteDatabase database) throws InvalidFormatException;
}
