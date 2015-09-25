package de.slowpoke.android.mbtiles.validator.impl;

import android.database.MatrixCursor;
import android.support.annotation.NonNull;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import de.slowpoke.android.mbtiles.MBTilesSQLite;
import de.slowpoke.android.mbtiles.MBTilesSQLite.ContentValues;
import de.slowpoke.android.mbtiles.validator.InvalidMetadataException;

/**
 * Created by brianhoffmann on 24.09.2015.
 */
public class MetadataValidator_1_1_Test {

    @Test
    public void testOnValidateMetadata() throws Exception {
        MetadataValidator_1_1 validator = new MetadataValidator_1_1();

        MatrixCursor cursor;

        cursor = new MatrixCursor(new String[]{
                MBTilesSQLite.Columns.Metadata.COL_METADATA_VALUE});

        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_BASELAYER, "1",
                ContentValues.Metadata_1_1.FORMAT_JPG, "-180,-85,180,85");
        validator.onValidateMetadata(cursor);

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_OVERLAY, "1",
                ContentValues.Metadata_1_1.FORMAT_JPG, "-180,-85,180,85");
        validator.onValidateMetadata(cursor);

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_OVERLAY, "1",
                ContentValues.Metadata_1_1.FORMAT_PNG, "-180,-85,180,85");
        validator.onValidateMetadata(cursor);

        cursor = newCursor(null, "test description", ContentValues.Metadata_1_1.TYPE_BASELAYER, "1",
                ContentValues.Metadata_1_1.FORMAT_JPG, "-180,-85,180,85");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", null, ContentValues.Metadata_1_1.TYPE_BASELAYER, "1",
                ContentValues.Metadata_1_1.FORMAT_JPG, "-180,-85,180,85");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", null, "1", ContentValues.Metadata_1_1.FORMAT_JPG, "-180,-85,180,85");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_BASELAYER, null,
                ContentValues.Metadata_1_1.FORMAT_JPG, "-180,-85,180,85");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_BASELAYER, "1", null, "-180,-85,180,85");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_BASELAYER, "1",
                ContentValues.Metadata_1_1.FORMAT_JPG, null);
        validator.onValidateMetadata(cursor);

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_BASELAYER, "1.0",
                ContentValues.Metadata_1_1.FORMAT_JPG, "-180,-85,180,85");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", "invalid-type", "1",
                ContentValues.Metadata_1_1.FORMAT_JPG, "-180,-85,180,85");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_BASELAYER, "1",
                "invalid", "-180,-85,180,85");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_BASELAYER, "1",
                ContentValues.Metadata_1_1.FORMAT_JPG, "invalid");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_BASELAYER, "1",
                ContentValues.Metadata_1_1.FORMAT_JPG, "180,85");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }

        cursor = newCursor("test name", "test description", ContentValues.Metadata_1_1.TYPE_BASELAYER, "1",
                ContentValues.Metadata_1_1.FORMAT_JPG, "-190,-90,190,90");
        try {
            validator.onValidateMetadata(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidMetadataException.class);
        } catch (Exception e) {
        }
    }

    @NonNull
    private MatrixCursor newCursor(String name, String description, String type, String version, String format, String bounds) {
        MatrixCursor cursor;
        cursor = new MatrixCursor(new String[]{
                MBTilesSQLite.Columns.Metadata.COL_METADATA_NAME,
                MBTilesSQLite.Columns.Metadata.COL_METADATA_VALUE});

        if (name != null) {
            cursor.addRow(new String[]{
                    ContentValues.Metadata_1_1.KEY_NAME,
                    name
            });
        }

        if (description != null) {
            cursor.addRow(new String[]{
                    ContentValues.Metadata_1_1.KEY_DESCRIPTION,
                    description
            });
        }

        if (type != null) {
            cursor.addRow(new String[]{
                    ContentValues.Metadata_1_1.KEY_TYPE,
                    type
            });
        }

        if (version != null) {
            cursor.addRow(new String[]{
                    ContentValues.Metadata_1_1.KEY_VERSION,
                    version
            });
        }

        if (format != null) {
            cursor.addRow(new String[]{
                    ContentValues.Metadata_1_1.KEY_FORMAT,
                    format
            });
        }

        if (bounds != null) {
            cursor.addRow(new String[]{
                    ContentValues.Metadata_1_1.KEY_BOUNDS,
                    bounds
            });
        }

        return cursor;
    }
}