package de.slowpoke.android.mbtiles;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Provides helper methods for database actions.
 */
public class MBTilesSQLite {

    @IntDef({VERSION_1_0, VERSION_1_1})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VersionCode {
    }

    public static final int VERSION_1_0 = 1000;
    public static final int VERSION_1_1 = 1001;

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

        public interface BaseMetadata {

            String KEY_NAME = "name";
            String KEY_TYPE = "type";
            String KEY_VERSION = "version";
            String KEY_DESCRIPTION = "description";

            String TYPE_BASELAYER = "baselayer";
            String TYPE_OVERLAY = "overlay";
        }

        public interface Metadata_1_0 extends BaseMetadata {
        }

        public interface Metadata_1_1 extends Metadata_1_0 {
            String KEY_BOUNDS = "bounds";
            String KEY_FORMAT = "format";

            String FORMAT_JPG = "jpg";
            String FORMAT_PNG = "png";
        }
    }

    private final static String CREATE_TILES_1_0 = "CREATE TABLE " + Columns.MBTiles.TABLE_NAME + "( "
            + Columns.MBTiles.COL_ZOOM_LEVEL + " INTEGER, "
            + Columns.MBTiles.COL_TILE_COLUMN + " INTEGER, "
            + Columns.MBTiles.COL_TILE_ROW + " INTEGER, "
            + Columns.MBTiles.COL_TILE_DATA + " BLOB" +
            ")";

    private final static String CREATE_INDEX_TILES_1_0 = "CREATE UNIQUE INDEX " + Columns.MBTiles.TABLE_NAME + "_index ON "
            + Columns.MBTiles.TABLE_NAME + " ("
            + Columns.MBTiles.COL_ZOOM_LEVEL + ", "
            + Columns.MBTiles.COL_TILE_COLUMN + ", "
            + Columns.MBTiles.COL_TILE_ROW +
            ")";

    private final static String CREATE_METADATA_1_0 = "CREATE TABLE" + Columns.Metadata.TABLE_NAME + "( "
            + Columns.Metadata.COL_METADATA_NAME + " TEXT, "
            + Columns.Metadata.COL_METADATA_VALUE + " TEXT " +
            ")";

    private final static String CREATE_INDEX_METADATA_1_0 = "CREATE UNIQUE INDEX " + Columns.Metadata.TABLE_NAME + "_index ON "
            + Columns.Metadata.TABLE_NAME + "( "
            + Columns.Metadata.COL_METADATA_NAME +
            ")";

    private final static String CREATE_METADATA_1_1 = CREATE_METADATA_1_0;
    private final static String CREATE_INDEX_METADATA_1_1 = CREATE_INDEX_METADATA_1_0;
    private final static String CREATE_TILES_1_1 = CREATE_TILES_1_0;
    private final static String CREATE_INDEX_TILES_1_1 = CREATE_INDEX_TILES_1_0;

    public static SQLiteDatabase open(File dbpath) {
        return SQLiteDatabase.openDatabase(dbpath.getAbsolutePath(), null,
                SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    /**
     * Create the metadata table.
     *
     * @param database a writable database
     * @param version  the mbtiles version
     * @throws SQLException
     */
    public static void createTableMetadata(SQLiteDatabase database, @VersionCode int version) throws SQLException {
        switch (version) {
            case VERSION_1_0:
                database.execSQL(CREATE_METADATA_1_0);
                break;

            case VERSION_1_1:
                database.execSQL(CREATE_METADATA_1_1);
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
    public static void createIndexMetadata(SQLiteDatabase database, @VersionCode int version) throws SQLException {
        switch (version) {
            case VERSION_1_0:
                database.execSQL(CREATE_INDEX_METADATA_1_0);
                break;

            case VERSION_1_1:
                database.execSQL(CREATE_INDEX_METADATA_1_1);
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
    public static void createTableTiles(SQLiteDatabase database, @VersionCode int version) throws SQLException {
        switch (version) {
            case VERSION_1_0:
                database.execSQL(CREATE_TILES_1_0);
                break;

            case VERSION_1_1:
                database.execSQL(CREATE_TILES_1_1);
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
    public static void createIndexTiles(SQLiteDatabase database, @VersionCode int version) throws SQLException {
        switch (version) {
            case VERSION_1_0:
                database.execSQL(CREATE_INDEX_TILES_1_0);
                break;

            case VERSION_1_1:
                database.execSQL(CREATE_INDEX_TILES_1_1);
                break;
        }
    }

}
