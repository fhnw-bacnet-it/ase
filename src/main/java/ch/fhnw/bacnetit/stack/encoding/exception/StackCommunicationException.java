package ch.fhnw.bacnetit.stack.encoding.exception;

import ch.fhnw.bacnetit.stack.encoding.TransportError;

public class StackCommunicationException extends TransportErrorException {
    private static final long serialVersionUID = -1;

    private TransportError transportError;

    public StackCommunicationException(final String message,
            final TransportError transportError) {
        super(message, transportError);
    }

}
