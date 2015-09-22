package de.slowpoke.mbtilesmap;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

/**
 * Metadata for {@link MBTiles}.
 *
 * @author Brian
 */
public class MBTilesMetadata implements MBTilesSQLite.Columns.Metadata {

    /** File format used for all tiles. */
    @IntDef({TILE_FORMAT_JPEG, TILE_FORMAT_PNG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TileFormat {
    }

    public static final int TILE_FORMAT_JPEG = 10;
    public static final int TILE_FORMAT_PNG = 11;

    /** Layer type of the map. */
    @IntDef({LAYER_TYPE_BASELAYER, LAYER_TYPE_OVERLAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LayerType {
    }

    public static final int LAYER_TYPE_BASELAYER = 20;
    public static final int LAYER_TYPE_OVERLAY = 21;

    @StringDef({LAYER_NAME_BASELAYER, LAYER_NAME_OVERLAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LayerName {
    }

    public static final String LAYER_NAME_BASELAYER = "baselayer";
    public static final String LAYER_NAME_OVERLAY = "overlay";

    /**
     * The plain-english name of the tileset.
     */
    public final String name;

    /**
     * A description of the layer as plain text.
     */
    public final String description;

    /**
     * overlay or baselayer
     */
    public final @LayerType int type;

    /**
     * The version of the tileset, as a plain number.
     */
    public final int version;

    /**
     * The image file format of the tile data: png or jpg
     */
    public final @TileFormat int format;

    /**
     * The maximum extent of the rendered map area
     */
    public final MBTilesBounds bounds;

    /**
     * Additional key-value pairs. Might be <code>null</code>.
     */
    public final Map<String, String> extra;

    /**
     * Create new metadata.
     *
     * @param name
     * @param description
     * @param type
     * @param version
     * @param format
     * @param bounds
     */
    public MBTilesMetadata(String name, String description, @LayerType int type, int version, @TileFormat int format,
                           MBTilesBounds bounds) {
        this(name, description, type, version, format, bounds, null);
    }

    /**
     * @param name
     * @param description
     * @param type
     * @param version
     * @param format
     * @param bounds
     * @param extra
     */
    public MBTilesMetadata(String name, String description, @LayerType int type, int version, @TileFormat int format,
                           MBTilesBounds bounds, Map<String, String> extra) {
        this.name = name;
        this.type = type;
        this.version = version;
        this.description = description;
        this.format = format;
        this.bounds = bounds;
        this.extra = extra;
    }

    /**
     * Create new metadata and also create tables and indexes in the underlying database.
     *
     * @param database
     * @param name
     * @param description
     * @param type
     * @param version
     * @param format
     * @param bounds
     */
    public static MBTilesMetadata create(SQLiteDatabase database, String name, String description, @LayerType int type,
                                         int version, @TileFormat int format, MBTilesBounds bounds) {

        MBTilesSQLite.createTableMetadata(database, version);
        MBTilesSQLite.createIndexMetadata(database, version);

        ContentValues cv = new ContentValues();
        cv.put(MBTilesSQLite.ContentValues.Metadata.KEY_NAME, name);
        cv.put(MBTilesSQLite.ContentValues.Metadata.KEY_DESCRIPTION, description);
        cv.put(MBTilesSQLite.ContentValues.Metadata.KEY_TYPE, type);
        cv.put(MBTilesSQLite.ContentValues.Metadata.KEY_VERSION, version);
        cv.put(MBTilesSQLite.ContentValues.Metadata.KEY_FORMAT, format);
        cv.put(MBTilesSQLite.ContentValues.Metadata.KEY_BOUNDS, bounds.toString());

        database.insert(TABLE_NAME, null, cv);

        return new MBTilesMetadata(name, description, type, version, format, bounds);
    }

}