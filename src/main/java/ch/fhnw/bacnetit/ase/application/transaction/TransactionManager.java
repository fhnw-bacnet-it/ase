package ch.fhnw.bacnetit.ase.application.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import ch.fhnw.bacnetit.ase.encoding.UnsignedInteger8;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataIndication;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataRequest;

/**
 * Maintains incoming and outgoing communication of a BACnet/IT stack.
 *
 * @author IMVS, FHNW
 *
 */
public class TransactionManager {
    /**
     *
     */

    private final HashSet<Integer> uniqueIdSrcDst = new HashSet<Integer>();

    // Pool of maintained transactions
    private final Map<TransactionKey, Transaction> transactions = new ConcurrentHashMap<TransactionKey, Transaction>();

    // private final List<TransactionManagerListener> listeners = new ArrayList<TransactionManagerListener>();

    // Add a Transaction Manager Listener, e.g. the Logger nodejs application
//    public void addListener(final TransactionManagerListener l) {
//        listeners.add(l);
//    }

    public Transaction findTransaction(final TransactionKey key) {
        if (transactions.containsKey(key)) {
            return transactions.get(key);
        } else {
            return null;
        }
    }

//    public boolean changeTransactionState(final TransactionKey key,
//            final TransactionState state) {
//        final Transaction t = this.findTransaction(key);
//        if (t == null) {
//            return false;
//        }
//        t.setNewState(state);
//        return true;
//    }

    public UnsignedInteger8 createOutboundTransaction(
            final T_UnitDataRequest t_unitDataRequest) {
        // T_UnitDataRequest does not contain an invokeId
        if (t_unitDataRequest.getData().getInvokeId() == null) {
            final TransactionKey key = new TransactionKey(
                    t_unitDataRequest.getData().getSourceEID(),
                    t_unitDataRequest.getData().getDestinationEID(),
                    this.getUniqueInvokeId(
                            t_unitDataRequest.getData().getSourceEID(),
                            t_unitDataRequest.getData().getDestinationEID()),
                    TransactionKey.DIRECTION_OUT);
            // Distinguish between a request that awaits an answer and a request
            // that doesn't await an answer.
            final Transaction t = new Transaction(
                    (t_unitDataRequest.getDataExpectingReply())
                            ? TransactionState.REQUESTED_WAITING
                            : TransactionState.REQUESTED_DONE);
            this.transactions.put(key, t);
            // LOG.debug("Created outbound transaction " + key.getInvokeId());
//            this.listeners.forEach(l -> {
//                try {
//                    l.onAdd(key, t);
//                } catch (final Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            });
            return key.getInvokeId();
        }
        // T_UnitDataRequest does contain an invokeId
        else {
            // Check if it is an answer, so update the transaction state
            final TransactionKey keyIfAnswer = new TransactionKey(
                    t_unitDataRequest.getData().getDestinationEID(),
                    t_unitDataRequest.getData().getSourceEID(),
                    t_unitDataRequest.getData().getInvokeId(),
                    TransactionKey.DIRECTION_IN);            
            final Transaction transactionToAnswer = this
                    .findTransaction(keyIfAnswer);

            if (transactionToAnswer != null && transactionToAnswer
                    .getState() == TransactionState.INDICATED_WAITING) {
                // transaction is a confirm
                transactionToAnswer
                        .setNewState(TransactionState.INDICATED_DONE);
//                this.listeners.forEach(l -> {
//                    try {
//                        l.onChange(keyIfAnswer, transactionToAnswer,
//                                TransactionState.INDICATED_WAITING);
//                    } catch (final Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                });
                return t_unitDataRequest.getData().getInvokeId();
            } else {
                TransactionKey key = new TransactionKey(
                        t_unitDataRequest.getData().getSourceEID(),
                        t_unitDataRequest.getData().getDestinationEID(),
                        t_unitDataRequest.getData().getInvokeId(),
                        TransactionKey.DIRECTION_OUT);
                this.transactions.put(key, new Transaction(
                        t_unitDataRequest.getDataExpectingReply()?TransactionState.REQUESTED_WAITING:TransactionState.REQUESTED_DONE
                        ));
                // transaction is new. most likely a resend after a timeout
//                this.listeners.forEach(l -> {
//                    try {
//                        l.onAdd(keyIfAnswer, new Transaction(
//                                (t_unitDataRequest.getDataExpectingReply())
//                                        ? TransactionState.REQUESTED_WAITING
//                                        : TransactionState.REQUESTED_DONE));
//                        // TODO: this behaviour is basically the same as if the
//                        // transaction had no invoke id -> validate this
//                    } catch (final Exception e) {
//                        e.printStackTrace();
//                    }
//                });
                return t_unitDataRequest.getData().getInvokeId();
            }
        }
    }

    public void createInboundTransaction(
            final T_UnitDataIndication t_unitDataIndication) {
        
        if (t_unitDataIndication.getData().getInvokeId() == null){
            System.err.println("t_unitDataIndication has no invoke Id");
            return;
        }

        final TransactionKey key = new TransactionKey(
                t_unitDataIndication.getData().getSourceEID(),
                t_unitDataIndication.getData().getDestinationEID(),
                t_unitDataIndication.getData().getInvokeId(),
                TransactionKey.DIRECTION_IN);

        // Update an existing AWAIT-RESPONSE Transaction if one is available
        // Change SRC and DEST
        final TransactionKey changedKey = new TransactionKey(
                key.getDestination(), key.getSource(), key.getInvokeId(),
                TransactionKey.DIRECTION_OUT);
        Transaction existingTransaction = findTransaction(changedKey);

        // Does the existing transaction awaits a response
        existingTransaction = (existingTransaction != null
                && existingTransaction
                        .getState() == TransactionState.REQUESTED_WAITING)
                                ? existingTransaction : null;
        if (existingTransaction != null) {
            existingTransaction.setNewState(TransactionState.REQUESTED_DONE);
//            for (final TransactionManagerListener l : this.listeners) {
//                try {
//                    l.onChange(changedKey, existingTransaction,
//                            TransactionState.REQUESTED_WAITING);
//                } catch (final Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
        }
        // Create a new Transaction entry for incoming request
        // Distinguish between requests which demand for a response and request
        // which don't.
        else if (t_unitDataIndication.getDataExpectingReply()) {
            
            final TransactionKey haveToResponseTransactionKey = new TransactionKey(
                    t_unitDataIndication.getData().getSourceEID(),
                    t_unitDataIndication.getData().getDestinationEID(),
                    t_unitDataIndication.getData().getInvokeId(),
                    TransactionKey.DIRECTION_IN);
            final Transaction t = new Transaction(
                    TransactionState.INDICATED_WAITING);
            this.transactions.put(haveToResponseTransactionKey, t);
//            this.listeners.forEach(l -> {
//                try {
//                    l.onAdd(haveToResponseTransactionKey, t);
//                } catch (final Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            });
        } else if (!t_unitDataIndication.getDataExpectingReply()) {
            final TransactionKey noResponseTransactionKey = new TransactionKey(
                    t_unitDataIndication.getData().getSourceEID(),
                    t_unitDataIndication.getData().getDestinationEID(),
                    t_unitDataIndication.getData().getInvokeId(),
                    TransactionKey.DIRECTION_IN);
            final Transaction t = new Transaction(
                    TransactionState.INDICATED_DONE);
            this.transactions.put(noResponseTransactionKey, t);
//            this.listeners.forEach(l -> {
//                try {
//                    l.onAdd(noResponseTransactionKey, t);
//                } catch (final Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            });
        }

    }

    /**
     * Returns all Transactions as a collection ordered by time.
     *
     * @return all transactions as a collection ordered by time
     */
    public Collection<Transaction> getTransactionsSortedByTime() {
        final ArrayList<Transaction> list = new ArrayList<Transaction>(
                transactions.values());
        Collections.sort(list);
        return list;
    }

    protected UnsignedInteger8 getUniqueInvokeId(final BACnetEID source,
            final BACnetEID dest) {
        int x = 0;
        while (this.uniqueIdSrcDst.contains(Objects.hash(x, source, dest))) {
            x++;
            if (x > 254) {
                try {
                    throw new Exception(
                            "Inconsistency during unique id calculation");
                } catch (final Exception e) {
                    System.err.println(e);
                    return null;
                }
            }
        }
        this.uniqueIdSrcDst.add(Integer.valueOf(Objects.hash(x, source, dest)));
        return new UnsignedInteger8(x);

    }

    public int size() {
        return transactions.size();
    }
}
