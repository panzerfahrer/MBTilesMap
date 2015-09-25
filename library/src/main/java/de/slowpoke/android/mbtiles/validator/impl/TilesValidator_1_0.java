package de.slowpoke.android.mbtiles.validator.impl;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

import de.slowpoke.android.mbtiles.validator.InvalidTilesException;
import de.slowpoke.android.mbtiles.validator.TilesValidator;

/**
 * https://github.com/mapbox/mbtiles-spec/blob/master/1.0/spec.md
 */
public class TilesValidator_1_0 extends TilesValidator {

    @Override
    protected void onValidateTiles(Cursor cursor) throws InvalidTilesException {
        final List<String> colNames = Arrays.asList(cursor.getColumnNames());

        if (!colNames.contains(COL_ZOOM_LEVEL)) {
            throw new InvalidTilesException("Required column '" + COL_ZOOM_LEVEL + "' is missing.");
        }

        if (!colNames.contains(COL_TILE_COLUMN)) {
            throw new InvalidTilesException("Required column '" + COL_TILE_COLUMN + "' is missing.");
        }

        if (!colNames.contains(COL_TILE_ROW)) {
            throw new InvalidTilesException("Required column '" + COL_TILE_ROW + "' is missing.");
        }

        if (!colNames.contains(COL_TILE_DATA)) {
            throw new InvalidTilesException("Required column '" + COL_TILE_DATA + "' is missing.");
        }

        cursor.close();
    }
}
