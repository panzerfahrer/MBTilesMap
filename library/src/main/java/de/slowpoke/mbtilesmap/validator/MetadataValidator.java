package de.slowpoke.mbtilesmap.validator;

import android.database.sqlite.SQLiteDatabase;

import de.slowpoke.mbtilesmap.MBTilesMetadata;
import de.slowpoke.mbtilesmap.MBTilesSQLite;

/**
 * Validator interface for the metadata table.
 *
 * @author Brian
 */
public interface MetadataValidator extends MBTilesSQLite.Columns.Metadata {

    /**
     * Validata an MBTiles database.
     *
     * @param database
     * @return <code>true</code> if the database fullfilles the MBTiles spec
     * @throws InvalidMetadataException
     */
    void validate(SQLiteDatabase database) throws InvalidMetadataException;

    /**
     * @return the validated {@link MBTilesMetadata}
     */
    MBTilesMetadata getMetadata();
}
