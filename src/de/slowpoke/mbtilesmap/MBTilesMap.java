/**
 * Provides classes to read and write to Mapbox tiles files.
 */
package de.slowpoke.mbtilesmap;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import de.slowpoke.mbtilesmap.MBTilesMetadata.LayerType;
import de.slowpoke.mbtilesmap.MBTilesValidatorFactory.InvalidMetadataException;
import de.slowpoke.mbtilesmap.MBTilesValidatorFactory.InvalidTilesException;
import de.slowpoke.mbtilesmap.MBTilesValidatorFactory.MetadataValidator;
import de.slowpoke.mbtilesmap.MBTilesValidatorFactory.TilesValidator;
import de.slowpoke.mbtilesmap.MBTilesValidatorFactory.UnsupportedVersionException;

/**
 * A map which maintains access to your {@link MBTiles}.
 * 
 * @author Brian
 * 
 */
public class MBTilesMap extends HashMap<String, MBTiles> {

	private static final long serialVersionUID = -5959556653700563754L;

	private final Context context;

	/**
	 * Create a new {@link MBTiles} table.
	 * 
	 * @param ctx
	 *            Context
	 */
	public MBTilesMap(Context ctx) {
		super();
		this.context = ctx;
	}

	/**
	 * Open an existing {@link MBTiles} file and add it to the map.
	 * 
	 * @param name
	 *            the name. If there is already an entry with that name, it will
	 *            be overwritten
	 * @param dbpath
	 *            the absolute path to the file. The file will be opened as an
	 *            {@link SQLiteDatabase} and the contents will be validated
	 * @param version
	 *            the version of the {@link MBTiles}
	 * @return the newly opened {@link MBTiles} which has been added to the map,
	 *         <code>null</code> if the {@link MBTiles} could not be opened
	 * @throws UnsupportedVersionException
	 *             If the supplied <code>version</code> is not (yet) supported
	 * @throws InvalidMetadataException
	 *             If the metadata table doesn't meet the constraints as defined
	 *             by the specifications
	 * @throws InvalidTilesException
	 *             If the tiles table doesn't meet the constraints as defined by
	 *             the specifications
	 */
	public MBTiles open(String name, File dbpath, MBTilesVersion version) throws InvalidMetadataException,
	        UnsupportedVersionException, InvalidTilesException {
		final SQLiteDatabase database = SQLiteDatabase.openDatabase(dbpath.getAbsolutePath(), null,
		        SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

		final MetadataValidator mValidator = MBTilesValidatorFactory.getMetadataValidator(version);
		final TilesValidator tValidator = MBTilesValidatorFactory.getTilesValidtor(version);

		if (mValidator.validate(database) && tValidator.validate(database)) {
			final MBTiles mbTiles = new MBTiles(database, mValidator.getMetadata(), version);
			put(name, mbTiles);
			return get(name);
		}

		return null;
	}

	/**
	 * Create a new {@link MBTiles} and add it to the map with the supplied
	 * metadata.
	 * 
	 * @param name
	 *            the name.
	 * @param dbpath
	 *            the absolute path where the file shall be saved.
	 * @param version
	 *            the version of the {@link MBTiles}
	 * @param description
	 *            an description of map
	 * @param type
	 *            the type of map layer
	 * @param format
	 *            the file format of the tiles
	 * @param bounds
	 *            bounding box of the map. Might be <code>null</code>
	 * @return the newly created {@link MBTiles} which has been added to the map
	 */
	public MBTiles create(String name, File dbpath, MBTilesVersion version, String description, LayerType type,
	        MBTilesMetadata.TileFormat format, MBTilesBounds bounds) {

		final SQLiteDatabase database = SQLiteDatabase.openDatabase(dbpath.getAbsolutePath(), null,
		        SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.OPEN_READWRITE
		                | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

		final MBTilesMetadata metadata = MBTilesMetadata.create(database, name, description, type, version, format,
		        bounds);
		final MBTiles mbTiles = MBTiles.create(database, metadata, version);

		put(name, mbTiles);
		return get(name);
	}

	/**
	 * Removes an {@link MBTiles} from the map. Mind that resources will be
	 * freed and the underlying database will be closed, so the returned
	 * {@link MBTiles} might not be very useful anymore.
	 * 
	 * @param name
	 *            the name (key) of the {@link MBTiles} to be removed
	 * @return the {@link MBTiles} associated with the name, or
	 *         <code>null</code> if this mapping is not available.
	 * 
	 * @see HashMap#remove(Object)
	 */
	@Override
	public MBTiles remove(Object name) {
		final MBTiles removed = super.remove(name);

		try {
			removed.close();
		} catch (NullPointerException e) {
			return null;
		}

		return removed;
	}

	@Override
	public void clear() {
		for (MBTiles tile : values()) {
			tile.close();
		}
		super.clear();
	}

	@Override
	protected void finalize() throws Throwable {
		this.clear();
		super.finalize();
	}

}
