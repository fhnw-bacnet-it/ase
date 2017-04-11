package ch.fhnw.bacnetit.stack.application.transaction;

/**
 * The listener interface for receiving events emitted in the
 * {@link TransactionManager}.
 *
 * @author juerg.luthiger@fhnw.ch
 *
 */
public interface TransactionManagerListener {

    void onAdd(TransactionKey key, Transaction t) throws Exception;

    void onChange(TransactionKey key, Transaction t,
            TransactionState previousState) throws Exception;

}
