package de.slowpoke.mbtilesmap.validator;

/**
 * The tiles are malformed.
 *
 * @author Brian
 */
public class InvalidTilesException extends Exception {
    private static final long serialVersionUID = 908659191920805263L;

    public InvalidTilesException(String errorMessage) {
        super(errorMessage);
    }
}
