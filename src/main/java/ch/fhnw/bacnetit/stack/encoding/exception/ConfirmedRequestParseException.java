package ch.fhnw.bacnetit.stack.encoding.exception;

public class ConfirmedRequestParseException extends Exception {
    private static final long serialVersionUID = -1;

    private final int originalInvokeId;

    public ConfirmedRequestParseException(final int originalInvokeId) {
        this.originalInvokeId = originalInvokeId;
    }

    public ConfirmedRequestParseException(final int originalInvokeId,
            final Throwable cause) {
        super(cause);
        this.originalInvokeId = originalInvokeId;
    }

    public int getOriginalInvokeId() {
        return originalInvokeId;
    }
}
