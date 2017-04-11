package ch.fhnw.bacnetit.stack.application.transaction;

public enum TransactionState {
    REQUESTED_DONE, REQUESTED_WAITING, INDICATED_DONE, INDICATED_WAITING, TIMEDOUT;
}
