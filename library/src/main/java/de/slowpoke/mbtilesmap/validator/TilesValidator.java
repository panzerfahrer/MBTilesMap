package de.slowpoke.mbtilesmap.validator;

import android.database.sqlite.SQLiteDatabase;

import de.slowpoke.mbtilesmap.MBTilesSQLite;

/**
 * Validator interface for the tiles table.
 *
 * @author Brian
 */
public interface TilesValidator extends MBTilesSQLite.Columns.MBTiles {

    /**
     * Validata an MBTiles database
     *
     * @param database
     * @return <code>true</code> if the database fullfills the MBTiles spec.
     * @throws InvalidTilesException
     */
    boolean validate(SQLiteDatabase database) throws InvalidTilesException;
}
