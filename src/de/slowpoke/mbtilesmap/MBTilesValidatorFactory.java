package de.slowpoke.mbtilesmap;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.slowpoke.mbtilesmap.MBTilesMetadata.LayerType;
import de.slowpoke.mbtilesmap.MBTilesMetadata.TileFormat;

/**
 * Factory to validate contents and structure of a Mapbox {@link SQLiteDatabase}
 * file.
 */
public class MBTilesValidatorFactory {

	/**
	 * Get a validator for {@link MBTilesMetadata}.
	 * 
	 * @param version
	 *            the assumed version of the metadata
	 * @return a {@link MetadataValidator}
	 * @throws UnsupportedVersionException
	 */
	public static MetadataValidator getMetadataValidator(MBTilesVersion version) throws UnsupportedVersionException {
		switch (version) {
		case VERSION_1_0:
			return new MetadataValidator_1_0();

		case VERSION_1_1:
			return new MetadataValidator_1_1();

		default:
			throw new UnsupportedVersionException(version);
		}
	}

	/**
	 * Get a validator for {@link MBTiles}
	 * 
	 * @param version
	 *            the assumed version of the tiles
	 * @return a {@link TilesValidator}
	 * @throws UnsupportedVersionException
	 */
	public static TilesValidator getTilesValidtor(MBTilesVersion version) throws UnsupportedVersionException {
		switch (version) {
		case VERSION_1_0:
			return new TilesValidator_1_0();

		case VERSION_1_1:
			return new TilesValidator_1_1();

		default:
			throw new UnsupportedVersionException(version);
		}
	}

	/**
	 * The chosen {@link MBTilesVersion} is not supported
	 * 
	 * @author Brian
	 * 
	 */
	public static class UnsupportedVersionException extends Exception {
		private static final long serialVersionUID = -4503518245530854727L;

		public UnsupportedVersionException(MBTilesVersion version) {
			super("Unsupported: " + version.toString());
		}
	}

	/**
	 * The metadata is malformed.
	 * 
	 */
	public static class InvalidMetadataException extends Exception {
		private static final long serialVersionUID = -8980900477318280977L;

		public InvalidMetadataException(String errorMessage) {
			super(errorMessage);
		}
	}

	/**
	 * The tiles are malformed.
	 * 
	 * @author Brian
	 * 
	 */
	public static class InvalidTilesException extends Exception {
		private static final long serialVersionUID = 908659191920805263L;

		public InvalidTilesException(String errorMessage) {
			super(errorMessage);
		}
	}

	/**
	 * Validator interface for the tiles table.
	 * 
	 * @author Brian
	 * 
	 */
	public static interface TilesValidator extends IMBTiles {

		/**
		 * Validata an MBTiles database
		 * 
		 * @param database
		 * @return <code>true</code> if the database fullfills the MBTiles spec.
		 * @throws InvalidTilesException
		 */
		boolean validate(SQLiteDatabase database) throws InvalidTilesException;
	}

	/**
	 * https://github.com/mapbox/mbtiles-spec/blob/master/1.1/spec.md
	 */
	public static class TilesValidator_1_1 implements TilesValidator {
		@Override
		public boolean validate(SQLiteDatabase database) throws InvalidTilesException {
			return new TilesValidator_1_0().validate(database);
		}
	}

	/**
	 * https://github.com/mapbox/mbtiles-spec/blob/master/1.0/spec.md
	 */
	public static class TilesValidator_1_0 implements TilesValidator {
		@Override
		public boolean validate(SQLiteDatabase database) throws InvalidTilesException {
			final Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
			final List<String> colNames = Arrays.asList(cursor.getColumnNames());

			boolean valid = false;
			if (colNames.contains(COL_ZOOM_LEVEL) && colNames.contains(COL_TILE_COLUMN)
			        && colNames.contains(COL_TILE_ROW) && colNames.contains(COL_TILE_DATA)) {
				valid = true;
			}

			cursor.close();

			if (valid) {
				return valid;
			} else {
				throw new InvalidTilesException("");
			}
		}
	}

	/**
	 * Validator interface for the metadata table.
	 * 
	 * @author Brian
	 * 
	 */
	public static interface MetadataValidator extends IMetadata {

		/**
		 * Validata an MBTiles database.
		 * 
		 * @param database
		 * @return <code>true</code> if the database fullfilles the MBTiles spec
		 * @throws InvalidMetadataException
		 */
		boolean validate(SQLiteDatabase database) throws InvalidMetadataException;

		/**
		 * @return the validated {@link MBTilesMetadata}
		 */
		MBTilesMetadata getMetadata();
	}

	/**
	 * https://github.com/mapbox/mbtiles-spec/blob/master/1.1/spec.md
	 */
	public static class MetadataValidator_1_1 implements MetadataValidator {

		private MBTilesMetadata validated;

		@Override
		public boolean validate(SQLiteDatabase database) throws InvalidMetadataException {
			final Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
			final int idxKey = cursor.getColumnIndex(COL_METADATA_NAME);
			final int idxVal = cursor.getColumnIndex(COL_METADATA_VALUE);

			final LinkedHashMap<String, String> dumped = new LinkedHashMap<String, String>(cursor.getCount());
			if (cursor.moveToFirst()) {
				do {
					dumped.put(cursor.getString(idxKey), cursor.getString(idxVal));
				} while (cursor.moveToNext());
			}
			cursor.close();

			final String name = dumped.remove(KEY_NAME);
			if (name == null)
				throw new InvalidMetadataException("No mandatory field 'name'.");

			final String description = dumped.remove(KEY_DESCRIPTION);
			if (description == null)
				throw new InvalidMetadataException("No mandatory field 'description'.");

			final LayerType type = LayerType.fromString(dumped.remove(KEY_TYPE));
			if (type == null)
				throw new InvalidMetadataException("No mandatory field 'type' or not in [ overlay, baselayer ].");

			final String version = dumped.remove(KEY_VERSION);
			if (version == null)
				throw new InvalidMetadataException("No mandatory field 'version'");

			try {
				Double.parseDouble(version);
			} catch (NumberFormatException e) {
				throw new InvalidMetadataException(
				        "Invalid syntax for mandatory field 'version'. Must be a plain number.");
			}

			final TileFormat format = TileFormat.fromString(dumped.remove(KEY_FORMAT));
			if (format == null)
				throw new InvalidMetadataException("No mandatory field 'format' or not in [ png, jpg ].");

			// optional
			MBTilesBounds bounds = null;
			if (dumped.containsKey(KEY_BOUNDS)) {
				try {
					bounds = new MBTilesBounds(dumped.remove(KEY_BOUNDS));

					if (!(bounds.left >= -180 && bounds.left <= 0 && bounds.right <= 180 && bounds.right >= 0
					        && bounds.bottom >= -85 && bounds.bottom <= 0 && bounds.top <= 85 && bounds.top >= 0)) {
						bounds = null;
					}
				} catch (NumberFormatException e) {
					bounds = null;
				}

				if (bounds == null)
					throw new InvalidMetadataException(
					        "Invalid syntax for optional field 'bounds'."
					                + "Should be latitude and longitude values in OpenLayers Bounds format - left, bottom, right, top."
					                + "Example of the full earth: -180.0,-85,180,85");
			}

			this.validated = new MBTilesMetadata(name, description, type, MBTilesVersion.fromString(version), format,
			        bounds, dumped);

			return true;
		}

		@Override
		public MBTilesMetadata getMetadata() {
			return this.validated;
		}
	}

	/**
	 * https://github.com/mapbox/mbtiles-spec/blob/master/1.0/spec.md
	 */
	public static class MetadataValidator_1_0 implements MetadataValidator {

		private MBTilesMetadata validated;

		@Override
		public boolean validate(SQLiteDatabase database) throws InvalidMetadataException {
			final Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
			final int idxKey = cursor.getColumnIndex(COL_METADATA_NAME);
			final int idxVal = cursor.getColumnIndex(COL_METADATA_VALUE);

			final LinkedHashMap<String, String> dumped = new LinkedHashMap<String, String>(cursor.getCount());
			if (cursor.moveToFirst()) {
				do {
					dumped.put(cursor.getString(idxKey), cursor.getString(idxVal));
				} while (cursor.moveToNext());
			}
			cursor.close();

			final String name = dumped.remove(KEY_NAME);
			if (name == null)
				throw new InvalidMetadataException("No mandatory field 'name'.");

			final String description = dumped.remove(KEY_DESCRIPTION);
			if (description == null)
				throw new InvalidMetadataException("No mandatory field 'description'.");

			final LayerType type = LayerType.fromString(dumped.remove(KEY_TYPE));
			if (type == null)
				throw new InvalidMetadataException("No mandatory field 'type' or not in [ overlay, baselayer ].");

			final String version = dumped.remove(KEY_VERSION);
			if (version == null) {
				throw new InvalidMetadataException("No mandatory field 'version'");
			}

			try {
				Double.parseDouble(version);
			} catch (NumberFormatException e) {
				throw new InvalidMetadataException(
				        "Invalid syntax for mandatory field 'version'. Must be a plain number.");
			}

			this.validated = new MBTilesMetadata(name, description, type, MBTilesVersion.fromString(version), dumped);
			return true;
		}

		@Override
		public MBTilesMetadata getMetadata() {
			return this.validated;
		}
	}
}
