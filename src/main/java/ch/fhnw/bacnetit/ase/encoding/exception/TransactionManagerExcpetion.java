package ch.fhnw.bacnetit.ase.encoding.exception;

public class TransactionManagerExcpetion extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TransactionManagerExcpetion(final String message) {
        super(message);
    }

}
