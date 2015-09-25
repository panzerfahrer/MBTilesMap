package de.slowpoke.android.mbtiles.validator;

import android.database.sqlite.SQLiteDatabase;

import de.slowpoke.android.mbtiles.MBTiles;
import de.slowpoke.android.mbtiles.MBTilesMetadata;
import de.slowpoke.android.mbtiles.MBTilesSQLite;
import de.slowpoke.android.mbtiles.validator.impl.MetadataValidator_1_0;
import de.slowpoke.android.mbtiles.validator.impl.MetadataValidator_1_1;
import de.slowpoke.android.mbtiles.validator.impl.TilesValidator_1_0;
import de.slowpoke.android.mbtiles.validator.impl.TilesValidator_1_1;

/**
 * Factory to validate contents and structure of a Mapbox {@link SQLiteDatabase} file.
 */
public class MBTilesValidatorFactory {

    /**
     * Get a validator for {@link MBTilesMetadata}.
     *
     * @param version the assumed version of the metadata
     * @return a {@link MetadataValidator}
     * @throws UnsupportedVersionException
     */
    public static MetadataValidator getMetadataValidator(@MBTilesSQLite.VersionCode int version) throws UnsupportedVersionException {
        switch (version) {
            case MBTilesSQLite.VERSION_1_0:
                return new MetadataValidator_1_0();

            case MBTilesSQLite.VERSION_1_1:
                return new MetadataValidator_1_1();

            default:
                throw new UnsupportedVersionException(version);
        }
    }

    /**
     * Get a validator for {@link MBTiles}
     *
     * @param version the assumed version of the tiles
     * @return a {@link TilesValidator}
     * @throws UnsupportedVersionException
     */
    public static TilesValidator getTilesValidator(@MBTilesSQLite.VersionCode int version) throws UnsupportedVersionException {
        switch (version) {
            case MBTilesSQLite.VERSION_1_0:
                return new TilesValidator_1_0();

            case MBTilesSQLite.VERSION_1_1:
                return new TilesValidator_1_1();

            default:
                throw new UnsupportedVersionException(version);
        }
    }

}
