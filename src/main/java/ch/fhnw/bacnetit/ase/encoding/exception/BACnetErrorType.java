package ch.fhnw.bacnetit.ase.encoding.exception;

public enum BACnetErrorType {
    TOO_MANY_TRANSACTIONS_ERROR(0,
            "too many transactions are running"), RESOLUTION_ERROR(1,
                    "resolution too bad");

    private int code;

    private String message;

    private BACnetErrorType(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
