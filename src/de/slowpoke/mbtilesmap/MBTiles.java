package de.slowpoke.mbtilesmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

/**
 * Maintains a {@link SQLiteDatabase} and provides read and write access to
 * tiles.
 * 
 * @author Brian
 * @version 1.1
 */
public class MBTiles implements IMBTiles {

	private final static String[] COLUMNS_TILE_DATA = new String[] { COL_TILE_DATA };
	private final static String SELECTION = COL_TILE_COLUMN + "=? " + COL_TILE_ROW + "=? " + COL_ZOOM_LEVEL + "=?";

	private final SQLiteDatabase database;
	private final MBTilesVersion version;
	private final MBTilesMetadata metadata;

	/**
	 * Create a new {@link MBTiles} based on an existing file.
	 * 
	 * @param database
	 * @param version
	 */
	public MBTiles(SQLiteDatabase database, MBTilesVersion version) {
		this(database, null, version);
	}

	/**
	 * Create a new {@link MBTiles} based on an existing file.
	 * 
	 * @param database
	 * @param metadata
	 * @param version
	 */
	public MBTiles(SQLiteDatabase database, MBTilesMetadata metadata, MBTilesVersion version) {
		this.database = database;
		this.metadata = metadata;
		this.version = version;
	}

	/**
	 * Create a new {@link MBTiles} and also create tables and indexes in the
	 * underlying database file.
	 * 
	 * @param database
	 * @param metadata
	 * @param version
	 * @return
	 */
	public static MBTiles create(SQLiteDatabase database, MBTilesMetadata metadata, MBTilesVersion version) {

		MBTilesSQLite.createTableTiles(database, version);
		MBTilesSQLite.createIndexTiles(database, version);

		return new MBTiles(database, metadata, version);
	}

	/**
	 * Get one tile. The coordinates are checked if there are in the bounds (if
	 * available from the metadata)
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * 
	 * @return the bitmap of the tile or <code>null</code> if no tile matched
	 *         the given parameters
	 */
	public Bitmap getTile(final int x, final int y, final int z) {
		try {
			if (!this.metadata.bounds.isInBounds(x, y, z)) {
				return null;
			}
		} catch (NullPointerException ignore) {
		}

		final String[] selArgs = new String[] { Integer.toString(x), Integer.toString(y), Integer.toString(z) };
		final Cursor c = database.query(TABLE_NAME, COLUMNS_TILE_DATA, SELECTION, selArgs, null, null, null, "1");

		if (!c.moveToFirst()) {
			c.close();
			return null;
		}
		final byte[] bb = c.getBlob(c.getColumnIndex(COL_TILE_DATA));
		c.close();

		return BitmapFactory.decodeByteArray(bb, 0, bb.length);
	}

	/**
	 * Set a tile at the specified coordinates. Existing tiles will be
	 * overwritten.
	 * 
	 * @param tile
	 * @param x
	 * @param y
	 * @param z
	 * @return <code>true</code> if the tile data has been inserted to the
	 *         database, <code>false</code> otherwise
	 */
	public boolean setTile(final Bitmap tile, final int x, final int y, final int z) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			switch (this.metadata.format) {
			case JPEG:
				tile.compress(CompressFormat.JPEG, 100, baos);
				break;

			case PNG:
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

		final long id = database.insertOrThrow(TABLE_NAME, null, values);

		try {
			baos.close();
		} catch (IOException ignore) {
		}

		return (id == -1) ? false : true;
	}

	public MBTilesMetadata getMetadata() {
		return this.metadata;
	}

	public boolean isWriteable() {
		return this.database.isOpen() && !this.database.isReadOnly();
	}

	public boolean isReadable() {
		return this.database.isOpen() && this.database.isReadOnly();
	}

	/**
	 * Close the underlying database.
	 */
	public void close() {
		this.database.close();
	}

}
