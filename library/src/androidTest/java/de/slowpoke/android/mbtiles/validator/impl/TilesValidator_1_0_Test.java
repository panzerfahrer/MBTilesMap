package de.slowpoke.android.mbtiles.validator.impl;

import android.database.MatrixCursor;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import de.slowpoke.android.mbtiles.MBTilesSQLite;
import de.slowpoke.android.mbtiles.validator.InvalidTilesException;

/**
 * Created by brianhoffmann on 24.09.2015.
 */
public class TilesValidator_1_0_Test {

    @Test
    public void testOnValidateTiles() throws Exception {
        TilesValidator_1_0 validator = new TilesValidator_1_0();

        MatrixCursor cursor = new MatrixCursor(new String[]{
                MBTilesSQLite.Columns.MBTiles.COL_ZOOM_LEVEL,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_COLUMN,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_ROW,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_DATA});

        validator.onValidateTiles(cursor);

        cursor = new MatrixCursor(new String[]{
                MBTilesSQLite.Columns.MBTiles.COL_ZOOM_LEVEL,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_COLUMN,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_ROW});

        try {
            validator.onValidateTiles(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidTilesException.class);
        } catch (Exception e) {
        }

        cursor = new MatrixCursor(new String[]{
                MBTilesSQLite.Columns.MBTiles.COL_ZOOM_LEVEL,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_COLUMN,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_DATA});

        try {
            validator.onValidateTiles(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidTilesException.class);
        } catch (Exception e) {
        }

        cursor = new MatrixCursor(new String[]{
                MBTilesSQLite.Columns.MBTiles.COL_ZOOM_LEVEL,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_ROW,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_DATA});

        try {
            validator.onValidateTiles(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidTilesException.class);
        } catch (Exception e) {
        }

        cursor = new MatrixCursor(new String[]{
                MBTilesSQLite.Columns.MBTiles.COL_TILE_COLUMN,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_ROW,
                MBTilesSQLite.Columns.MBTiles.COL_TILE_DATA});

        try {
            validator.onValidateTiles(cursor);
            Assertions.failBecauseExceptionWasNotThrown(InvalidTilesException.class);
        } catch (Exception e) {
        }
    }
}