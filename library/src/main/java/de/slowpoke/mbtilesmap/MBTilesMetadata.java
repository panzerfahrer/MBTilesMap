package de.slowpoke.mbtilesmap;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

/**
 * Metadata for {@link MBTiles}.
 *
 * @author Brian
 */
public class MBTilesMetadata implements IMetadata {

    /**
     * File format used for all tiles.
     *
     * @author Brian
     */
    public static enum TileFormat {

        JPEG, PNG;

        public static TileFormat fromString(String format) {
            for (TileFormat tf : values()) {
                if (tf.toString().equals(format)) {
                    return tf;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * Layer type of the map.
     *
     * @author Brian
     */
    public static enum LayerType {

        BASELAYER, OVERLAY;

        public static LayerType fromString(String format) {
            for (LayerType tf : values()) {
                if (tf.toString().equals(format)) {
                    return tf;
                }
            }
            return null;
        }        @Override
        public String toString() {
            return name().toLowerCase();
        }


    }
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
    public final LayerType type;
    /**
     * The version of the tileset, as a plain number.
     */
    public final MBTilesVersion version;
    /**
     * The image file format of the tile data: png or jpg
     */
    public final TileFormat format;
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
    public MBTilesMetadata(String name, String description, LayerType type, MBTilesVersion version, TileFormat format, MBTilesBounds bounds) {
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
    public MBTilesMetadata(String name, String description, LayerType type, MBTilesVersion version, TileFormat format, MBTilesBounds bounds, Map<String, String> extra) {
        this.name = name;
        this.type = type;
        this.version = version;
        this.description = description;
        this.format = format;
        this.bounds = bounds;
        this.extra = extra;
    }

    /**
     * Create new metadata.
     *
     * @param name
     * @param description
     * @param type
     * @param version
     * @param format
     */
    public MBTilesMetadata(String name, String description, LayerType type, MBTilesVersion version, TileFormat format) {
        this(name, description, type, version, format, null, null);
    }

    /**
     * Create new metadata.
     *
     * @param name
     * @param description
     * @param type
     * @param version
     * @param extra
     */
    public MBTilesMetadata(String name, String description, LayerType type, MBTilesVersion version, Map<String, String> extra) {
        this(name, description, type, version, null, null, extra);
    }

    /**
     * Create new metadata and also create tables and indexes in the underlying
     * database.
     *
     * @param database
     * @param name
     * @param description
     * @param type
     * @param version
     * @param format
     * @param bounds
     */
    public static MBTilesMetadata create(SQLiteDatabase database, String name, String description, LayerType type,
                                         MBTilesVersion version, TileFormat format, MBTilesBounds bounds) {

        MBTilesSQLite.createTableMetadata(database, version);
        MBTilesSQLite.createIndexMetadata(database, version);

        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_DESCRIPTION, description);
        cv.put(KEY_TYPE, type.toString());
        cv.put(KEY_VERSION, version.toString());
        cv.put(KEY_FORMAT, format.toString());
        cv.put(KEY_BOUNDS, bounds.toString());

        database.insert(TABLE_NAME, null, cv);

        return new MBTilesMetadata(name, description, type, version, format, bounds);
    }

    @Override
    public String toString() {
        String none = " - ";
        String separator = " | ";
        StringBuilder sb = new StringBuilder("Metadata:\n");
        sb.append("Name: " + (name == null ? none : name) + separator);
        sb.append("Type: " + (type == null ? none : type) + separator);
        sb.append("Version: " + (version == null ? none : version) + separator);
        sb.append("Description: " + (description == null ? none : description) + separator);
        sb.append("Format: " + (format == null ? none : format) + separator);
        sb.append("Bounds: " + (bounds == null ? none : bounds));
        sb.append("\n");
        sb.append("Extra: " + (extra == null ? none : extra.toString()));
        return sb.toString();
    }

}