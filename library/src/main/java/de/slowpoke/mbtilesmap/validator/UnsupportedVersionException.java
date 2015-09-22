package de.slowpoke.mbtilesmap.validator;

/**
 * The version is not supported
 *
 * @author Brian
 */
public class UnsupportedVersionException extends Exception {
    private static final long serialVersionUID = -4503518245530854727L;

    public UnsupportedVersionException(int version) {
        super("Unsupported version code: " + version);
    }
}
