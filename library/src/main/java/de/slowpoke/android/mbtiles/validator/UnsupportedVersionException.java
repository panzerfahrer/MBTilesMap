package de.slowpoke.android.mbtiles.validator;

/**
 * The version is not supported
 */
public class UnsupportedVersionException extends Exception {

    private static final long serialVersionUID = -4503518245530854727L;

    public UnsupportedVersionException(int version) {
        super("Unsupported version code: " + version);
    }
}
