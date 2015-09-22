package de.slowpoke.mbtilesmap.validator.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedHashMap;

import de.slowpoke.mbtilesmap.MBTilesBounds;
import de.slowpoke.mbtilesmap.MBTilesMetadata;
import de.slowpoke.mbtilesmap.MBTilesSQLite;
import de.slowpoke.mbtilesmap.validator.InvalidMetadataException;
import de.slowpoke.mbtilesmap.validator.MetadataValidator;

/**
 * https://github.com/mapbox/mbtiles-spec/blob/master/1.1/spec.md
 */
public class MetadataValidator_1_1 implements MetadataValidator {

    private MBTilesMetadata validated;

    @Override
    public void validate(SQLiteDatabase database) throws InvalidMetadataException {
        final Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        final int idxKey = cursor.getColumnIndex(COL_METADATA_NAME);
        final int idxVal = cursor.getColumnIndex(COL_METADATA_VALUE);

        final LinkedHashMap<String, String> dumped = new LinkedHashMap<String, String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                dumped.put(cursor.getString(idxKey), cursor.getString(idxVal));
            } while (cursor.moveToNext());
        }
        cursor.close();

        final String name = dumped.remove(MBTilesSQLite.ContentValues.Metadata.KEY_NAME);
        if (name == null)
            throw new InvalidMetadataException("No mandatory field 'name'.");

        final String description = dumped.remove(MBTilesSQLite.ContentValues.Metadata.KEY_DESCRIPTION);
        if (description == null)
            throw new InvalidMetadataException("No mandatory field 'description'.");

        final @MBTilesMetadata.LayerType int type = dumped.remove(MBTilesSQLite.ContentValues.Metadata.KEY_TYPE);
        if (type == null)
            throw new InvalidMetadataException("No mandatory field 'type' or not in [ overlay, baselayer ].");

        final String version = dumped.remove(MBTilesSQLite.ContentValues.Metadata.KEY_VERSION);
        if (version == null)
            throw new InvalidMetadataException("No mandatory field 'version'");

        try {
            Double.parseDouble(version);
        } catch (NumberFormatException e) {
            throw new InvalidMetadataException(
                    "Invalid syntax for mandatory field 'version'. Must be a plain number.");
        }

        final MBTilesMetadata.TileFormat format = MBTilesMetadata.TileFormat.fromString(dumped.remove(MBTilesSQLite.ContentValues.Metadata.KEY_FORMAT));
        if (format == null)
            throw new InvalidMetadataException("No mandatory field 'format' or not in [ png, jpg ].");

        // optional
        MBTilesBounds bounds = null;
        if (dumped.containsKey(MBTilesSQLite.ContentValues.Metadata.KEY_BOUNDS)) {
            try {
                bounds = new MBTilesBounds(dumped.remove(MBTilesSQLite.ContentValues.Metadata.KEY_BOUNDS));

                if (!(bounds.left >= -180 && bounds.left <= 0 && bounds.right <= 180 && bounds.right >= 0
                        && bounds.bottom >= -85 && bounds.bottom <= 0 && bounds.top <= 85 && bounds.top >= 0)) {
                    bounds = null;
                }
            } catch (NumberFormatException e) {
                bounds = null;
            }

            if (bounds == null)
                throw new InvalidMetadataException(
                        "Invalid syntax for optional field 'bounds'."
                                + "Should be latitude and longitude values in OpenLayers Bounds format - left, bottom, right, top."
                                + "Example of the full earth: -180.0,-85,180,85");
        }

        this.validated = new MBTilesMetadata(name, description, type, MBTilesVersion.fromString(version), format,
                bounds, dumped);

        return true;
    }

    @Override
    public MBTilesMetadata getMetadata() {
        return this.validated;
    }
}
