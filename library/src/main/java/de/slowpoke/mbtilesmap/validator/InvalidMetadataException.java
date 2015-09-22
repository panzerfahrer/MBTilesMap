package de.slowpoke.mbtilesmap.validator;

/**
 * The metadata is malformed.
 */
public class InvalidMetadataException extends Exception {
    private static final long serialVersionUID = -8980900477318280977L;

    public InvalidMetadataException(String errorMessage) {
        super(errorMessage);
    }
}
