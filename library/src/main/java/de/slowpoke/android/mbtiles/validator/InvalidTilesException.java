package de.slowpoke.android.mbtiles.validator;

/**
 * The tiles are malformed.
 */
public class InvalidTilesException extends InvalidFormatException {
    private static final long serialVersionUID = 908659191920805263L;

    public InvalidTilesException(String errorMessage) {
        super(errorMessage);
    }
}
