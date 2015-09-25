package de.slowpoke.android.mbtiles;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.slowpoke.android.mbtiles.validator.InvalidMetadataException;
import de.slowpoke.android.mbtiles.validator.InvalidTilesException;
import de.slowpoke.android.mbtiles.validator.MBTilesValidatorFactory;
import de.slowpoke.android.mbtiles.validator.MetadataValidator;
import de.slowpoke.android.mbtiles.validator.TilesValidator;
import de.slowpoke.android.mbtiles.validator.UnsupportedVersionException;

/**
 * Maintains a {@link SQLiteDatabase} and provides read and write access to tiles.
 */
public abstract class MBTiles<MetaData extends MBTilesMetadata> implements MBTilesSQLite.Columns.MBTiles {

    private final static String[] COLUMNS_TILE_DATA = new String[]{COL_TILE_DATA};
    private final static String SELECTION = COL_TILE_COLUMN + "=? AND " + COL_TILE_ROW + "=? AND " + COL_ZOOM_LEVEL + "=?";

    protected final SQLiteDatabase mDatabase;
    protected final MetaData mMetaData;
    protected int mMinZoomLevel = -1;
    protected int mMaxZoomLevel = -1;

    /**
     * Create a new {@link MBTiles} based on an existing file.
     *
     * @param database
     * @param metadata
     */
    public MBTiles(SQLiteDatabase database, MetaData metadata) {
        mDatabase = database;
        mMetaData = metadata;
    }

    /**
     * Get one tile. The coordinates are checked if there are in the bounds (if available from the metadata)
     *
     * @param x
     * @param y
     * @param z
     * @return the bitmap byte array of the tile or <code>null</code> if no tile matched the given parameters
     */
    @Nullable
    public abstract byte[] getTile(final int x, final int y, final int z);

    /**
     * Set a tile at the specified coordinates. Existing tiles will be overwritten.
     *
     * @param tile
     * @param x
     * @param y
     * @param z
     * @return <code>true</code> if the tile data has been inserted to the database, <code>false</code> otherwise
     */
    public abstract boolean setTile(final Bitmap tile, final int x, final int y, final int z);

    public MetaData getMetadata() {
        return mMetaData;
    }

    public boolean isWriteable() {
        return mDatabase.isOpen() && !mDatabase.isReadOnly();
    }

    public boolean isReadable() {
        return mDatabase.isOpen() && mDatabase.isReadOnly();
    }

    public int getMaxZoomLevel() {
        if (mMaxZoomLevel < 0) {
            final Cursor c = mDatabase.query(TABLE_NAME, new String[]{"MAX(" + COL_ZOOM_LEVEL + ")"}, null, null, null, null, null, "1");
            if (c.moveToFirst()) {
                mMaxZoomLevel = c.getInt(0);
            }
            c.close();
        }

        return mMaxZoomLevel;
    }

    public int getMinZoomLevel() {
        if (mMinZoomLevel < 0) {
            final Cursor c = mDatabase.query(TABLE_NAME, new String[]{"MIN(" + COL_ZOOM_LEVEL + ")"}, null, null, null, null, null, "1");
            if (c.moveToFirst()) {
                mMinZoomLevel = c.getInt(0);
            }
            c.close();
        }

        return mMinZoomLevel;
    }

    /**
     * Close the underlying database.
     */
    public void close() {
        mDatabase.close();
    }

    /**
     * Open an existing {@link MBTiles} file and add it to the map.
     *
     * @param dbpath  the absolute path to the file. The file will be opened as an {@link SQLiteDatabase} and the contents will be
     *                validated
     * @param version the version of the {@link MBTiles}
     * @return the newly opened {@link MBTiles} which has been added to the map, <code>null</code> if the {@link MBTiles} could not be
     * opened
     * @throws UnsupportedVersionException If the supplied <code>version</code> is not supported
     * @throws InvalidMetadataException    If the metadata table doesn't meet the constraints as defined by the specifications
     * @throws InvalidTilesException       If the tiles table doesn't meet the constraints as defined by the specifications
     */
    public static MBTiles openValidated(File dbpath, @MBTilesSQLite.VersionCode int version) throws InvalidMetadataException,
            UnsupportedVersionException, InvalidTilesException {

        final SQLiteDatabase database = MBTilesSQLite.open(dbpath);

        final MetadataValidator metaValidator = MBTilesValidatorFactory.getMetadataValidator(version);
        metaValidator.validate(database);

        final TilesValidator tilesValidator = MBTilesValidatorFactory.getTilesValidator(version);
        tilesValidator.validate(database);

        MBTilesMetadata tilesMetadata = MBTilesMetadata.open(database, version);
        return MBTiles.open(database, version, tilesMetadata);
    }

    public static MBTiles open(SQLiteDatabase database, @MBTilesSQLite.VersionCode int version, MBTilesMetadata metadata) throws UnsupportedVersionException {
        switch (version) {
            case MBTilesSQLite.VERSION_1_0:
                return new Version_1_0(database, (MBTilesMetadata.Version_1_0) metadata);

            case MBTilesSQLite.VERSION_1_1:
                return new Version_1_1(database, (MBTilesMetadata.Version_1_1) metadata);

            default:
                throw new UnsupportedVersionException(version);
        }
    }

    public static class Version_1_0 extends MBTiles<MBTilesMetadata.Version_1_0> {

        public Version_1_0(SQLiteDatabase database, MBTilesMetadata.Version_1_0 metadata) {
            super(database, metadata);
        }

        @Override
        public byte[] getTile(int x, int y, int z) {
            byte[] tile = null;

            final String[] selArgs = new String[]{Integer.toString(x), Integer.toString(y), Integer.toString(z)};
            final Cursor c = mDatabase.query(TABLE_NAME, COLUMNS_TILE_DATA, SELECTION, selArgs, null, null, null, "1");

            if (c.moveToFirst()) {
                tile = c.getBlob(c.getColumnIndex(COL_TILE_DATA));
            }

            c.close();

            return tile;
        }

        @Override
        public boolean setTile(Bitmap tile, int x, int y, int z) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
            tile.compress(CompressFormat.JPEG, 100, baos);

            final ContentValues values = new ContentValues();
            values.put(COL_TILE_COLUMN, x);
            values.put(COL_TILE_ROW, y);
            values.put(COL_ZOOM_LEVEL, z);
            values.put(COL_TILE_DATA, baos.toByteArray());

            final long id = mDatabase.insertOrThrow(TABLE_NAME, null, values);

            try {
                baos.close();
            } catch (IOException ignore) {
            }

            return (id != -1);
        }

        /**
         * Create a new {@link MBTiles} and also create tables and indexes in the underlying database file.
         *
         * @param database
         * @param metadata
         * @return
         */
        public static MBTiles.Version_1_0 create(SQLiteDatabase database, MBTilesMetadata.Version_1_0 metadata) {

            MBTilesSQLite.createTableTiles(database, MBTilesSQLite.VERSION_1_0);
            MBTilesSQLite.createIndexTiles(database, MBTilesSQLite.VERSION_1_0);

            return new Version_1_0(database, metadata);
        }
    }

    public static class Version_1_1 extends MBTiles<MBTilesMetadata.Version_1_1> {

        public Version_1_1(SQLiteDatabase database, MBTilesMetadata.Version_1_1 metadata) {
            super(database, metadata);
        }

        @Override
        public byte[] getTile(int x, int y, int z) {
            byte[] tile = null;

            if (this.mMetaData.bounds.isInBounds(x, y, z)) {
                final String[] selArgs = new String[]{Integer.toString(x), Integer.toString(y), Integer.toString(z)};
                final Cursor c = mDatabase.query(TABLE_NAME, COLUMNS_TILE_DATA, SELECTION, selArgs, null, null, null, "1");

                if (c.moveToFirst()) {
                    tile = c.getBlob(c.getColumnIndex(COL_TILE_DATA));
                }

                c.close();
            }

            return tile;
        }

        @Override
        public boolean setTile(Bitmap tile, int x, int y, int z) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);

            try {
                switch (this.mMetaData.format) {
                    case MBTilesMetadata.Version_1_1.TILE_FORMAT_JPEG:
                        tile.compress(CompressFormat.JPEG, 100, baos);
                        break;

                    case MBTilesMetadata.Version_1_1.TILE_FORMAT_PNG:
                        tile.compress(CompressFormat.PNG, 100, baos);
                        break;

                    default:
                        return false;
                }
            } catch (NullPointerException ignore) {
                return false;
            }

            final ContentValues values = new ContentValues();
            values.put(COL_TILE_COLUMN, x);
            values.put(COL_TILE_ROW, y);
            values.put(COL_ZOOM_LEVEL, z);
            values.put(COL_TILE_DATA, baos.toByteArray());

            final long id = mDatabase.insertOrThrow(TABLE_NAME, null, values);

            try {
                baos.close();
            } catch (IOException ignore) {
            }

            return (id != -1);
        }

        /**
         * Create a new {@link MBTiles} and also create tables and indexes in the underlying database file.
         *
         * @param database
         * @param metadata
         * @return
         */
        public static MBTiles.Version_1_1 create(SQLiteDatabase database, MBTilesMetadata.Version_1_1 metadata) {

            MBTilesSQLite.createTableTiles(database, MBTilesSQLite.VERSION_1_1);
            MBTilesSQLite.createIndexTiles(database, MBTilesSQLite.VERSION_1_1);

            return new Version_1_1(database, metadata);
        }
    }

}
