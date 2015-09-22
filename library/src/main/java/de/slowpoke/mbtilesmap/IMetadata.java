/**
 *
 */
package de.slowpoke.mbtilesmap;

/**
 * Mapbox tile metadata interface.
 *
 * @author Brian
 */
public interface IMetadata {

    public final static String TABLE_NAME = "metadata";
    public final static String COL_METADATA_NAME = "name";
    public final static String COL_METADATA_VALUE = "value";

    public static final String[] COLUMNS = new String[]{COL_METADATA_NAME, COL_METADATA_VALUE};

    public static final String KEY_BOUNDS = "bounds";
    public static final String KEY_FORMAT = "format";
    public static final String FORMAT_JPG = "jpg";
    public static final String FORMAT_PNG = "png";
    public static final String KEY_VERSION = "version";
    public static final String KEY_TYPE = "type";
    public static final String TYPE_BASELAYER = "baselayer";
    public static final String TYPE_OVERLAY = "overlay";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_NAME = "name";

}
