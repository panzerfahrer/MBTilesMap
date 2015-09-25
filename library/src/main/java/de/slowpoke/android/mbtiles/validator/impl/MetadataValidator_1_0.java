package de.slowpoke.android.mbtiles.validator.impl;

import android.database.Cursor;

import java.util.HashMap;

import de.slowpoke.android.mbtiles.MBTilesMetadata;
import de.slowpoke.android.mbtiles.MBTilesSQLite;
import de.slowpoke.android.mbtiles.validator.InvalidMetadataException;
import de.slowpoke.android.mbtiles.validator.MetadataValidator;

/**
 * https://github.com/mapbox/mbtiles-spec/blob/master/1.0/spec.md
 */
public class MetadataValidator_1_0 extends MetadataValidator {

    @Override
    protected void onValidateMetadata(Cursor cursor) throws InvalidMetadataException {
        final int idxKey = cursor.getColumnIndex(COL_METADATA_NAME);
        final int idxVal = cursor.getColumnIndex(COL_METADATA_VALUE);

        final HashMap<String, String> dumped = new HashMap<String, String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                dumped.put(cursor.getString(idxKey), cursor.getString(idxVal));
            } while (cursor.moveToNext());
        }
        cursor.close();

        String name = dumped.remove(MBTilesSQLite.ContentValues.Metadata_1_0.KEY_NAME);
        if (name == null) {
            throw new InvalidMetadataException("Required field 'name' is missing.");
        }

        String description = dumped.remove(MBTilesSQLite.ContentValues.Metadata_1_0.KEY_DESCRIPTION);
        if (description == null) {
            throw new InvalidMetadataException("Required field 'description' is missing.");
        }

        String typeStr = dumped.remove(MBTilesSQLite.ContentValues.Metadata_1_0.KEY_TYPE);
        if (typeStr == null) {
            throw new InvalidMetadataException("Required field 'type' is missing");
        }

        boolean typeValid = false;
        if (MBTilesMetadata.LAYER_NAME_BASELAYER.equals(typeStr)) {
            typeValid = true;
        } else if (MBTilesMetadata.LAYER_NAME_OVERLAY.equals(typeStr)) {
            typeValid = true;
        }

        if (!typeValid) {
            throw new InvalidMetadataException("Field 'type' must be one of [" + MBTilesMetadata.LAYER_NAME_BASELAYER + ", " +
                    MBTilesMetadata.LAYER_NAME_OVERLAY + "].");
        }

        final String version = dumped.remove(MBTilesSQLite.ContentValues.Metadata_1_0.KEY_VERSION);
        if (version == null) {
            throw new InvalidMetadataException("Required field 'version' is missing");
        }

        try {
            Integer.parseInt(version);
        } catch (NumberFormatException e) {
            throw new InvalidMetadataException(
                    "Invalid syntax for required field 'version'. Must be a plain number.");
        }
    }

}
