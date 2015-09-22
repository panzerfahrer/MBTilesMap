package de.slowpoke.mbtilesmap.validator.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.List;

import de.slowpoke.mbtilesmap.validator.InvalidTilesException;
import de.slowpoke.mbtilesmap.validator.TilesValidator;

/**
 * https://github.com/mapbox/mbtiles-spec/blob/master/1.0/spec.md
 */
public class TilesValidator_1_0 implements TilesValidator {
    @Override
    public boolean validate(SQLiteDatabase database) throws InvalidTilesException {
        final Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        final List<String> colNames = Arrays.asList(cursor.getColumnNames());

        boolean valid = false;
        if (colNames.contains(COL_ZOOM_LEVEL) && colNames.contains(COL_TILE_COLUMN)
                && colNames.contains(COL_TILE_ROW) && colNames.contains(COL_TILE_DATA)) {
            valid = true;
        }

        cursor.close();

        if (valid) {
            return valid;
        } else {
            throw new InvalidTilesException("");
        }
    }
}
