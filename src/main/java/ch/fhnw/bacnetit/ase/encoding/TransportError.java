package ch.fhnw.bacnetit.ase.encoding;

public class TransportError {

    public static enum TransportErrorType {
        Undefined, ConnectionError, ResolutionError, SecurityError;
    }

    private TransportErrorType transportErrorType = null;
    private final int code;

    public TransportError(final TransportErrorType type, final int code) {
        this.transportErrorType = type;
        this.code = code;
    }

    public TransportErrorType getTransportErrorType() {
        return this.transportErrorType;
    }

    public int getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return String.format("TransportError-Type:%s, Code:%s",
                this.transportErrorType, this.code);
    }

}
