package ch.fhnw.bacnetit.ase.application.transaction;

public enum TransactionState {
    REQUESTED_DONE, REQUESTED_WAITING, INDICATED_DONE, INDICATED_WAITING, TIMEDOUT;
}
