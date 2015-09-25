package de.slowpoke.android.mbtiles.validator;

/**
 * The format of an mbtiles file is invalid.
 */
public class InvalidFormatException extends Exception {

    private static final long serialVersionUID = -4503518245530854727L;

    public InvalidFormatException() {
    }

    public InvalidFormatException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidFormatException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InvalidFormatException(Throwable throwable) {
        super(throwable);
    }
}
