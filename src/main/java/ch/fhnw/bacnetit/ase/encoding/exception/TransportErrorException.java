package ch.fhnw.bacnetit.ase.encoding.exception;

import ch.fhnw.bacnetit.ase.encoding.TransportError;
import ch.fhnw.bacnetit.ase.encoding.TransportError.TransportErrorType;

public abstract class TransportErrorException extends RuntimeException {

    private static final long serialVersionUID = 4899436753445332038L;
    private final TransportError transportError;

    public TransportErrorException(final String message,
            final TransportError transportError) {
        super(message);
        this.transportError = (transportError == null)
                ? new TransportError(TransportErrorType.Undefined, 0)
                : transportError;
    }

    public TransportError getTransportError() {
        return this.transportError;
    }
}
