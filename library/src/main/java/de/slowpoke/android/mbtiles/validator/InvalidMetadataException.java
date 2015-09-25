package de.slowpoke.android.mbtiles.validator;

/**
 * The metadata is malformed.
 */
public class InvalidMetadataException extends InvalidFormatException {

    private static final long serialVersionUID = -8980900477318280977L;

    public InvalidMetadataException(String errorMessage) {
        super(errorMessage);
    }
}
