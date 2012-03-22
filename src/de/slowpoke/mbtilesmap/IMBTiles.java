/**
 * 
 */
package de.slowpoke.mbtilesmap;


/**
 * Mapbox tiles interface.
 * 
 * @author Brian
 * 
 */
public interface IMBTiles {

	public final static String TABLE_NAME = "tiles";
	public final static String COL_ZOOM_LEVEL = "zoom_level";
	public final static String COL_TILE_COLUMN = "tile_column";
	public final static String COL_TILE_ROW = "tile_row";
	public final static String COL_TILE_DATA = "tile_data";

	public final static String[] COLUMNS = new String[] { COL_ZOOM_LEVEL, COL_TILE_COLUMN, COL_TILE_ROW, COL_TILE_DATA };

}
