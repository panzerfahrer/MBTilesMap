package de.slowpoke.mbtilesmap.validator;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.slowpoke.mbtilesmap.MBTiles;
import de.slowpoke.mbtilesmap.MBTilesMetadata;
import de.slowpoke.mbtilesmap.validator.impl.MetadataValidator_1_0;
import de.slowpoke.mbtilesmap.validator.impl.MetadataValidator_1_1;
import de.slowpoke.mbtilesmap.validator.impl.TilesValidator_1_0;
import de.slowpoke.mbtilesmap.validator.impl.TilesValidator_1_1;

/**
 * Factory to validate contents and structure of a Mapbox {@link SQLiteDatabase}
 * file.
 */
public class MBTilesValidatorFactory {

    @IntDef({VERSION_1_0, VERSION_1_1})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Version {
    }

    public static final int VERSION_1_0 = 1000;
    public static final int VERSION_1_1 = 1001;

    /**
     * Get a validator for {@link MBTilesMetadata}.
     *
     * @param version the assumed version of the metadata
     * @return a {@link MetadataValidator}
     * @throws UnsupportedVersionException
     */
    public static MetadataValidator getMetadataValidator(@Version int version) throws UnsupportedVersionException {
        switch (version) {
            case VERSION_1_0:
                return new MetadataValidator_1_0();

            case VERSION_1_1:
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
    public static TilesValidator getTilesValidtor(@Version int version) throws UnsupportedVersionException {
        switch (version) {
            case VERSION_1_0:
                return new TilesValidator_1_0();

            case VERSION_1_1:
                return new TilesValidator_1_1();

            default:
                throw new UnsupportedVersionException(version);
        }
    }

}
