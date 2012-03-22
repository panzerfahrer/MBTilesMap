package de.slowpoke.mbtilesmap;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Provides helper methods for database actions.
 * 
 * @author Brian
 * 
 */
public class MBTilesSQLite {

	private final static String CREATE_TILES_10 = "CREATE TABLE " + IMBTiles.TABLE_NAME + "( "
	        + IMBTiles.COL_ZOOM_LEVEL + " INTEGER, " + IMBTiles.COL_TILE_COLUMN + " INTEGER, " + IMBTiles.COL_TILE_ROW
	        + " INTEGER, " + IMBTiles.COL_TILE_DATA + " BLOB" + ")";

	private final static String CREATE_INDEX_TILES_10 = "CREATE UNIQUE INDEX " + IMBTiles.TABLE_NAME + "_index ON "
	        + IMBTiles.TABLE_NAME + " (" + IMBTiles.COL_ZOOM_LEVEL + ", " + IMBTiles.COL_TILE_COLUMN + ", "
	        + IMBTiles.COL_TILE_ROW + ")";

	private final static String CREATE_METADATA_10 = "CREATE TABLE" + IMetadata.TABLE_NAME + "( "
	        + IMetadata.COL_METADATA_NAME + " TEXT, " + IMetadata.COL_METADATA_VALUE + " TEXT " + ")";

	private final static String CREATE_INDEX_METADATA_10 = "CREATE UNIQUE INDEX " + IMetadata.TABLE_NAME + "_index ON "
	        + IMetadata.TABLE_NAME + "( " + IMetadata.COL_METADATA_NAME + ")";

	/**
	 * Create the metadata table.
	 * 
	 * @param database
	 *            a writable databse
	 * @param version
	 *            the mbtiles version
	 * @throws SQLException
	 */
	public static void createTableMetadata(SQLiteDatabase database, MBTilesVersion version) throws SQLException {
		switch (version) {
		case VERSION_1_0:
		case VERSION_1_1:
			database.execSQL(CREATE_METADATA_10);
			break;

		default:
			break;
		}
	}

	/**
	 * Create an index for the metadata table
	 * 
	 * @param database
	 *            a writable databse
	 * @param version
	 *            the mbtiles version
	 * @throws SQLException
	 */
	public static void createIndexMetadata(SQLiteDatabase database, MBTilesVersion version) throws SQLException {
		switch (version) {
		case VERSION_1_0:
		case VERSION_1_1:
			database.execSQL(CREATE_INDEX_METADATA_10);
			break;

		default:
			break;
		}
	}

	/**
	 * Create the tiles table.
	 * 
	 * @param database
	 *            a writable databse
	 * @param version
	 *            the mbtiles version
	 * 
	 * @throws SQLException
	 */
	public static void createTableTiles(SQLiteDatabase database, MBTilesVersion version) throws SQLException {
		switch (version) {
		case VERSION_1_0:
		case VERSION_1_1:
			database.execSQL(CREATE_TILES_10);
			break;

		default:
			break;
		}
	}

	/**
	 * Create an index for the tiles table.
	 * 
	 * @param database
	 *            a writable databse
	 * @param version
	 *            the mbtiles version
	 * @throws SQLException
	 */
	public static void createIndexTiles(SQLiteDatabase database, MBTilesVersion version) throws SQLException {
		switch (version) {
		case VERSION_1_0:
		case VERSION_1_1:
			database.execSQL(CREATE_INDEX_TILES_10);
			break;

		default:
			break;
		}
	}

}
