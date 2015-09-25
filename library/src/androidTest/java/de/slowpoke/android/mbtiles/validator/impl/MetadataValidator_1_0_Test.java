package de.slowpoke.android.mbtiles.validator.impl;

import android.database.MatrixCursor;
import android.support.annotation.NonNull;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import de.slowpoke.android.mbtiles.MBTilesSQLite;
import de.slowpoke.android.mbtiles.validator.InvalidMetadataException;

/**
 * Created by brianhoffmann on 24.09.2015.
 */
public class MetadataValidator_1_0_Test {

    @Test
    public void testOnValidateMetadata() throws Exception {
        MetadataValidator_1_0 validator = new MetadataValidator_1_0();

        MatrixCursor cursor;

        cursor = new MatrixCursor(new String[]{
                MBTilesSQLite.Columns.Metadata.COL_METADATA_VALUE});

        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", MBTilesSQLite.ContentValues.Metadata_1_0.TYPE_BASELAYER, "1");
        validator.onValidateMetadata(cursor);

        cursor = newCursor("test name", "test description", MBTilesSQLite.ContentValues.Metadata_1_0.TYPE_OVERLAY, "1");
        validator.onValidateMetadata(cursor);

        cursor = newCursor(null, "test description", MBTilesSQLite.ContentValues.Metadata_1_0.TYPE_BASELAYER, "1");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", null, MBTilesSQLite.ContentValues.Metadata_1_0.TYPE_BASELAYER, "1");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", null, "1");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", MBTilesSQLite.ContentValues.Metadata_1_0.TYPE_BASELAYER, null);
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", MBTilesSQLite.ContentValues.Metadata_1_0.TYPE_BASELAYER, "1.0");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", "invalid-type", "1");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }
    }

    @NonNull
    private MatrixCursor newCursor(String name, String description, String type, String version) {
        MatrixCursor cursor;
        cursor = new MatrixCursor(new String[]{
                MBTilesSQLite.Columns.Metadata.COL_METADATA_NAME,
                MBTilesSQLite.Columns.Metadata.COL_METADATA_VALUE});

        if (name != null) {
            cursor.addRow(new String[]{
                    MBTilesSQLite.ContentValues.Metadata_1_0.KEY_NAME,
                    name
            });
        }

        if (description != null) {
            cursor.addRow(new String[]{
                    MBTilesSQLite.ContentValues.Metadata_1_0.KEY_DESCRIPTION,
                    description
            });
        }

        if (type != null) {
            cursor.addRow(new String[]{
                    MBTilesSQLite.ContentValues.Metadata_1_0.KEY_TYPE,
                    type
            });
        }

        if (version != null) {
            cursor.addRow(new String[]{
                    MBTilesSQLite.ContentValues.Metadata_1_0.KEY_VERSION,
                    version
            });
        }

        return cursor;
    }
}