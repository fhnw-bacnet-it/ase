package ch.fhnw.bacnetit.stack.application.exception;

import ch.fhnw.bacnetit.stack.encoding.TransportError;
import ch.fhnw.bacnetit.stack.encoding.exception.TransportErrorException;

public class StackInitializationException extends TransportErrorException {
    private static final long serialVersionUID = -1;

    public StackInitializationException(final String message,
            final TransportError transportError) {
        super(message, transportError);
    }

}
