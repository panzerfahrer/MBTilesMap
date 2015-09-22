package de.slowpoke.mbtilesmap;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Provides helper methods for database actions.
 *
 * @author Brian
 */
public class MBTilesSQLite {

    public static final class Columns {

        private Columns() {
        }

        /**
         * Mapbox tiles interface.
         */
        public interface MBTiles {

            String TABLE_NAME = "tiles";
            String COL_ZOOM_LEVEL = "zoom_level";
            String COL_TILE_COLUMN = "tile_column";
            String COL_TILE_ROW = "tile_row";
            String COL_TILE_DATA = "tile_data";

            String[] COLUMNS = new String[]{COL_ZOOM_LEVEL, COL_TILE_COLUMN, COL_TILE_ROW, COL_TILE_DATA};
        }

        /**
         * Mapbox tile metadata interface.
         */
        public interface Metadata {

            String TABLE_NAME = "metadata";
            String COL_METADATA_NAME = "name";
            String COL_METADATA_VALUE = "value";

            String[] COLUMNS = new String[]{COL_METADATA_NAME, COL_METADATA_VALUE};

        }
    }

    public static final class ContentValues {

        private ContentValues() {
        }

        public interface Metadata {

            String KEY_BOUNDS = "bounds";
            String KEY_FORMAT = "format";
            String FORMAT_JPG = "jpg";
            String FORMAT_PNG = "png";
            String KEY_VERSION = "version";
            String KEY_TYPE = "type";
            String TYPE_BASELAYER = "baselayer";
            String TYPE_OVERLAY = "overlay";
            String KEY_DESCRIPTION = "description";
            String KEY_NAME = "name";
        }

    }

    private final static String CREATE_TILES_10 = "CREATE TABLE " + Columns.MBTiles.TABLE_NAME + "( "
            + Columns.MBTiles.COL_ZOOM_LEVEL + " INTEGER, " + Columns.MBTiles.COL_TILE_COLUMN + " INTEGER, " + Columns.MBTiles.COL_TILE_ROW
            + " INTEGER, " + Columns.MBTiles.COL_TILE_DATA + " BLOB" + ")";

    private final static String CREATE_INDEX_TILES_10 = "CREATE UNIQUE INDEX " + Columns.MBTiles.TABLE_NAME + "_index ON "
            + Columns.MBTiles.TABLE_NAME + " (" + Columns.MBTiles.COL_ZOOM_LEVEL + ", " + Columns.MBTiles.COL_TILE_COLUMN + ", "
            + Columns.MBTiles.COL_TILE_ROW + ")";

    private final static String CREATE_METADATA_10 = "CREATE TABLE" + Columns.Metadata.TABLE_NAME + "( "
            + Columns.Metadata.COL_METADATA_NAME + " TEXT, " + Columns.Metadata.COL_METADATA_VALUE + " TEXT " + ")";

    private final static String CREATE_INDEX_METADATA_10 = "CREATE UNIQUE INDEX " + Columns.Metadata.TABLE_NAME + "_index ON "
            + Columns.Metadata.TABLE_NAME + "( " + Columns.Metadata.COL_METADATA_NAME + ")";

    /**
     * Create the metadata table.
     *
     * @param database a writable databse
     * @param version  the mbtiles version
     * @throws SQLException
     */
    public static void createTableMetadata(SQLiteDatabase database, @MBTilesMetadata.VersionCode int version) throws SQLException {
        switch (version) {
            case MBTilesMetadata.VERSION_CODE_1_0:
            case MBTilesMetadata.VERSION_CODE_1_1:
                database.execSQL(CREATE_METADATA_10);
                break;

            default:
                break;
        }
    }

    /**
     * Create an index for the metadata table
     *
     * @param database a writable databse
     * @param version  the mbtiles version
     * @throws SQLException
     */
    public static void createIndexMetadata(SQLiteDatabase database, @MBTilesMetadata.VersionCode int version) throws SQLException {
        switch (version) {
            case MBTilesMetadata.VERSION_CODE_1_0:
            case MBTilesMetadata.VERSION_CODE_1_1:
                database.execSQL(CREATE_INDEX_METADATA_10);
                break;

            default:
                break;
        }
    }

    /**
     * Create the tiles table.
     *
     * @param database a writable databse
     * @param version  the mbtiles version
     * @throws SQLException
     */
    public static void createTableTiles(SQLiteDatabase database, @MBTilesMetadata.VersionCode int version) throws SQLException {
        switch (version) {
            case MBTilesMetadata.VERSION_CODE_1_0:
            case MBTilesMetadata.VERSION_CODE_1_1:
                database.execSQL(CREATE_TILES_10);
                break;

            default:
                break;
        }
    }

    /**
     * Create an index for the tiles table.
     *
     * @param database a writable databse
     * @param version  the mbtiles version
     * @throws SQLException
     */
    public static void createIndexTiles(SQLiteDatabase database, @MBTilesMetadata.VersionCode int version) throws SQLException {
        switch (version) {
            case MBTilesMetadata.VERSION_CODE_1_0:
            case MBTilesMetadata.VERSION_CODE_1_1:
                database.execSQL(CREATE_INDEX_TILES_10);
                break;

            default:
                break;
        }
    }

}
