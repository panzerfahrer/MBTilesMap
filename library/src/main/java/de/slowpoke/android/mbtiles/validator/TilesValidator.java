package de.slowpoke.android.mbtiles.validator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import de.slowpoke.android.mbtiles.MBTilesSQLite;

/**
 * Validator for the tiles table.
 */
public abstract class TilesValidator implements Validator, MBTilesSQLite.Columns.MBTiles {

    @Override
    public void validate(SQLiteDatabase database) throws InvalidTilesException {
        final Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        onValidateTiles(cursor);
    }

    protected abstract void onValidateTiles(Cursor cursor) throws InvalidTilesException;
}
