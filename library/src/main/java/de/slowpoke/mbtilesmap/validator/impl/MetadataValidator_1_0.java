package de.slowpoke.mbtilesmap.validator.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import de.slowpoke.mbtilesmap.MBTilesMetadata;
import de.slowpoke.mbtilesmap.MBTilesSQLite;
import de.slowpoke.mbtilesmap.validator.InvalidMetadataException;
import de.slowpoke.mbtilesmap.validator.MetadataValidator;

/**
 * https://github.com/mapbox/mbtiles-spec/blob/master/1.0/spec.md
 */
public class MetadataValidator_1_0 implements MetadataValidator {

    private MBTilesMetadata validated;

    @Override
    public void validate(SQLiteDatabase database) throws InvalidMetadataException {
        final Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        final int idxKey = cursor.getColumnIndex(COL_METADATA_NAME);
        final int idxVal = cursor.getColumnIndex(COL_METADATA_VALUE);

        final HashMap<String, String> dumped = new HashMap<String, String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                dumped.put(cursor.getString(idxKey), cursor.getString(idxVal));
            } while (cursor.moveToNext());
        }
        cursor.close();

        String name = dumped.remove(MBTilesSQLite.ContentValues.Metadata.KEY_NAME);
        if (name == null) {
            throw new InvalidMetadataException("No mandatory field 'name'.");
        }

        String description = dumped.remove(MBTilesSQLite.ContentValues.Metadata.KEY_DESCRIPTION);
        if (description == null) {
            throw new InvalidMetadataException("No mandatory field 'description'.");
        }

        String typeStr = dumped.remove(MBTilesSQLite.ContentValues.Metadata.KEY_TYPE);
        if (typeStr == null) {
            throw new InvalidMetadataException("No mandatory field 'type'");
        }

        @MBTilesMetadata.LayerType int typeValid;
        if (MBTilesMetadata.LAYER_NAME_BASELAYER.equals(typeStr)) {
            typeValid = MBTilesMetadata.LAYER_TYPE_BASELAYER;
        } else if (MBTilesMetadata.LAYER_NAME_OVERLAY.equals(typeStr)) {
            typeValid = MBTilesMetadata.LAYER_TYPE_OVERLAY;
        } else {
            throw new InvalidMetadataException("Field 'type' must be one of [ overlay, baselayer ].");
        }

        final String version = dumped.remove(MBTilesSQLite.ContentValues.Metadata.KEY_VERSION);
        if (version == null) {
            throw new InvalidMetadataException("No mandatory field 'version'");
        }

        try {
            Double.parseDouble(version);
        } catch (NumberFormatException e) {
            throw new InvalidMetadataException(
                    "Invalid syntax for mandatory field 'version'. Must be a plain number.");
        }

        this.validated = new MBTilesMetadata(name, description, typeValid, version, dumped);
    }

    @Override
    public MBTilesMetadata getMetadata() {
        return this.validated;
    }
}
