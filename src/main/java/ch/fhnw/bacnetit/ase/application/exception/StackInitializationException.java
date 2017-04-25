package ch.fhnw.bacnetit.ase.application.exception;

import ch.fhnw.bacnetit.ase.encoding.TransportError;
import ch.fhnw.bacnetit.ase.encoding.exception.TransportErrorException;

public class StackInitializationException extends TransportErrorException {
    private static final long serialVersionUID = -1;

    public StackInitializationException(final String message,
            final TransportError transportError) {
        super(message, transportError);
    }

}
