package de.slowpoke.android.mbtiles;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import de.slowpoke.android.mbtiles.validator.UnsupportedVersionException;

/**
 * Metadata for {@link MBTiles}.
 */
public abstract class MBTilesMetadata implements MBTilesSQLite.Columns.Metadata {

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

    public static final String LAYER_NAME_BASELAYER = MBTilesSQLite.ContentValues.Metadata_1_0.TYPE_BASELAYER;
    public static final String LAYER_NAME_OVERLAY = MBTilesSQLite.ContentValues.Metadata_1_0.TYPE_OVERLAY;

    protected final SQLiteDatabase mDatabase;

    private String mName;
    private String mDescription;
    @LayerType private int mType;
    private int mVersion;
    public Map<String, String> mExtras;

    /**
     * @param name
     * @param description
     * @param type
     * @param version
     * @param extras
     */
    public MBTilesMetadata(SQLiteDatabase database, String name, String description, @LayerType int type, int version,
                           Map<String, String> extras) {

        mDatabase = database;

        this.mName = name;
        this.mType = type;
        this.mVersion = version;
        this.mDescription = description;
        this.mExtras = extras;
    }

    /**
     * Create new metadata.
     *
     * @param name
     * @param description
     * @param type
     * @param version
     */
    public MBTilesMetadata(SQLiteDatabase database, String name, String description, @LayerType int type, int version) {
        this(database, name, description, type, version, null);
    }

    /**
     * The plain-english name of the tileset.
     */
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    /**
     * A description of the layer as plain text.
     */
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    /**
     * The layer type
     *
     * @see de.slowpoke.android.mbtiles.MBTilesMetadata.LayerType
     */
    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getVersion() {
        return mVersion;
    }

    /**
     * The version of the tileset, as a plain number.
     */
    public void setVersion(int version) {
        mVersion = version;
    }

    /**
     * Additional key-value pairs.
     */
    public String getExtra(String key) {
        return mExtras.get(key);
    }

    public void setExtra(String key, String value) {
        mExtras.put(key, value);
    }

    public final void save() {
        mDatabase.beginTransaction();
        try {
            mDatabase.delete(TABLE_NAME, null, null);

            ContentValues cv = new ContentValues();

            for (Map.Entry<String, String> extra : mExtras.entrySet()) {
                cv.put(COL_METADATA_NAME, extra.getKey());
                cv.put(COL_METADATA_VALUE, extra.getValue());
                mDatabase.insert(TABLE_NAME, null, cv);
            }

            cv.put(COL_METADATA_NAME, MBTilesSQLite.ContentValues.BaseMetadata.KEY_NAME);
            cv.put(COL_METADATA_VALUE, mName);
            mDatabase.insert(TABLE_NAME, null, cv);

            cv.put(COL_METADATA_NAME, MBTilesSQLite.ContentValues.BaseMetadata.KEY_DESCRIPTION);
            cv.put(COL_METADATA_VALUE, mDescription);
            mDatabase.insert(TABLE_NAME, null, cv);

            cv.put(COL_METADATA_NAME, MBTilesSQLite.ContentValues.BaseMetadata.KEY_TYPE);
            cv.put(COL_METADATA_VALUE, layerTypeToName(mType));
            mDatabase.insert(TABLE_NAME, null, cv);

            cv.put(COL_METADATA_NAME, MBTilesSQLite.ContentValues.BaseMetadata.KEY_VERSION);
            cv.put(COL_METADATA_VALUE, mVersion);
            mDatabase.insert(TABLE_NAME, null, cv);

            onSave();

            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    protected abstract void onSave();

    public static MBTilesMetadata open(SQLiteDatabase database, @MBTilesSQLite.VersionCode int version) throws UnsupportedVersionException {

        final Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        final int idxKey = cursor.getColumnIndex(COL_METADATA_NAME);
        final int idxVal = cursor.getColumnIndex(COL_METADATA_VALUE);

        final HashMap<String, String> dumbed = new HashMap<String, String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                dumbed.put(cursor.getString(idxKey), cursor.getString(idxVal));
            } while (cursor.moveToNext());
        }
        cursor.close();

        String name;
        String desc;
        String type;
        String vers;

        switch (version) {
            case MBTilesSQLite.VERSION_1_0:
                name = dumbed.remove(MBTilesSQLite.ContentValues.Metadata_1_0.KEY_NAME);
                desc = dumbed.remove(MBTilesSQLite.ContentValues.Metadata_1_0.KEY_DESCRIPTION);
                type = dumbed.remove(MBTilesSQLite.ContentValues.Metadata_1_0.KEY_TYPE);
                vers = dumbed.remove(MBTilesSQLite.ContentValues.Metadata_1_0.KEY_VERSION);

                return new Version_1_0(database, name, desc, layerNameToType(type), Integer.parseInt(vers));

            case MBTilesSQLite.VERSION_1_1:
                name = dumbed.remove(MBTilesSQLite.ContentValues.Metadata_1_1.KEY_NAME);
                desc = dumbed.remove(MBTilesSQLite.ContentValues.Metadata_1_1.KEY_DESCRIPTION);
                type = dumbed.remove(MBTilesSQLite.ContentValues.Metadata_1_1.KEY_TYPE);
                vers = dumbed.remove(MBTilesSQLite.ContentValues.Metadata_1_1.KEY_VERSION);
                String format = dumbed.remove(MBTilesSQLite.ContentValues.Metadata_1_1.KEY_FORMAT);
                String bounds = dumbed.remove(MBTilesSQLite.ContentValues.Metadata_1_1.KEY_BOUNDS);

                return new Version_1_1(database, name, desc, layerNameToType(type), Integer.parseInt(vers), Version_1_1
                        .tileFormatNameToType(format), new MBTilesBounds(bounds));

            default:
                throw new UnsupportedVersionException(version);
        }
    }

    @LayerType
    public static int layerNameToType(String layerName) {
        if (LAYER_NAME_BASELAYER.equals(layerName)) {
            return LAYER_TYPE_BASELAYER;
        }

        if (LAYER_NAME_OVERLAY.equals(layerName)) {
            return LAYER_TYPE_OVERLAY;
        }

        throw new IllegalArgumentException("Unsupported layer name: " + layerName);
    }

    @LayerName
    public static String layerTypeToName(int layerType) {
        switch (layerType) {
            case LAYER_TYPE_BASELAYER:
                return LAYER_NAME_BASELAYER;

            case LAYER_TYPE_OVERLAY:
                return LAYER_NAME_OVERLAY;

            default:
                throw new IllegalArgumentException("Unsupported layer type: " + layerType);
        }
    }

    public static class Version_1_0 extends MBTilesMetadata {

        public Version_1_0(SQLiteDatabase database, String name, String description, @LayerType int type, int version) {
            super(database, name, description, type, version);
        }

        @Override
        protected void onSave() {
        }

        /**
         * Create new metadata and also create tables and indexes in the underlying database.
         */
        public static MBTilesMetadata create(SQLiteDatabase database, String name, String description, @LayerType int type, int version) {

            MBTilesSQLite.createTableMetadata(database, MBTilesSQLite.VERSION_1_0);
            MBTilesSQLite.createIndexMetadata(database, MBTilesSQLite.VERSION_1_0);

            Version_1_0 metadata = new Version_1_0(database, name, description, type, version);
            metadata.save();

            return metadata;
        }
    }

    public static class Version_1_1 extends MBTilesMetadata {

        /** File format used for all tiles. */
        @IntDef({TILE_FORMAT_JPEG, TILE_FORMAT_PNG})
        @Retention(RetentionPolicy.SOURCE)
        public @interface TileFormat {
        }

        public static final int TILE_FORMAT_JPEG = 10;
        public static final int TILE_FORMAT_PNG = 11;

        /**
         * The image file format of the tile data: png or jpg
         */
        @TileFormat public final int format;

        /**
         * The maximum extent of the rendered map area
         */
        public final MBTilesBounds bounds;

        public Version_1_1(SQLiteDatabase database, String name, String description, @LayerType int type, int version, @TileFormat int
                format, MBTilesBounds bounds) {
            super(database, name, description, type, version);
            this.format = format;
            this.bounds = bounds;
        }

        @Override
        protected void onSave() {
            ContentValues cv = new ContentValues();

            cv.put(COL_METADATA_NAME, MBTilesSQLite.ContentValues.Metadata_1_1.KEY_FORMAT);
            cv.put(COL_METADATA_VALUE, tileFormatTypeToName(format));
            mDatabase.insert(TABLE_NAME, null, cv);

            cv.put(COL_METADATA_NAME, MBTilesSQLite.ContentValues.Metadata_1_1.KEY_BOUNDS);
            cv.put(COL_METADATA_VALUE, bounds.toString());
            mDatabase.insert(TABLE_NAME, null, cv);
        }

        @TileFormat
        public static int tileFormatNameToType(String layerName) {
            if (MBTilesSQLite.ContentValues.Metadata_1_1.FORMAT_JPG.equals(layerName)) {
                return TILE_FORMAT_JPEG;
            }

            if (MBTilesSQLite.ContentValues.Metadata_1_1.FORMAT_PNG.equals(layerName)) {
                return TILE_FORMAT_PNG;
            }

            throw new IllegalArgumentException("Unsupported format type: " + layerName);
        }

        public static String tileFormatTypeToName(int layerType) {
            switch (layerType) {
                case TILE_FORMAT_JPEG:
                    return MBTilesSQLite.ContentValues.Metadata_1_1.FORMAT_JPG;

                case TILE_FORMAT_PNG:
                    return MBTilesSQLite.ContentValues.Metadata_1_1.FORMAT_PNG;

                default:
                    throw new IllegalArgumentException("Unsupported format type: " + layerType);
            }
        }

        /**
         * Create new metadata and also create tables and indexes in the underlying database.
         */
        public static MBTilesMetadata create(SQLiteDatabase database, String name, String description, @LayerType int type, int version,
                                             @TileFormat int format, MBTilesBounds bounds) {

            MBTilesSQLite.createTableMetadata(database, MBTilesSQLite.VERSION_1_1);
            MBTilesSQLite.createIndexMetadata(database, MBTilesSQLite.VERSION_1_1);

            Version_1_1 metadata = new Version_1_1(database, name, description, type, version, format, bounds);
            metadata.save();

            return metadata;
        }
    }

}