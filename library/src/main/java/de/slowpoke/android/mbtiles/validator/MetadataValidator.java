package de.slowpoke.android.mbtiles.validator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import de.slowpoke.android.mbtiles.MBTilesSQLite;

/**
 * Validator for the metadata table.
 */
public abstract class MetadataValidator implements MBTilesSQLite.Columns.Metadata, Validator {

    @Override
    public void validate(SQLiteDatabase database) throws InvalidMetadataException {
        final Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        onValidateMetadata(cursor);
    }

    protected abstract void onValidateMetadata(Cursor cursor) throws InvalidMetadataException;

}
