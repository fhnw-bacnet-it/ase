package ch.fhnw.bacnetit.stack.application.transaction;

import java.util.Date;

public class Transaction implements Comparable<Transaction> {

    // private final Time createdTime;
    // private Time lastmodTime;
    private final Date createdTime;
    private Date lastmodTime;

    private TransactionState state;

    Transaction(final TransactionState _state) {

        this.createdTime = new Date();
        this.lastmodTime = this.createdTime;
        this.state = _state;
    }

    public Date getCreatedTime() {
        return (Date) this.createdTime.clone();
    }

    public Date getLastmodTime() {
        return (Date) this.lastmodTime.clone();
    }

    public TransactionState getState() {
        return state;
    }

    public void setNewState(final TransactionState state) {
        this.state = state;
        this.lastmodTime = new Date();
    }

    @Override
    public synchronized String toString() {
        final StringBuilder output = new StringBuilder();
        output.append("Created Time: " + this.createdTime);
        output.append(System.getProperty("line.separator"));
        output.append(", ");
        output.append("Lastmod Time: " + this.lastmodTime);
        output.append(System.getProperty("line.separator"));
        output.append(", ");
        output.append("CurrentState: " + this.state);
        output.append(System.getProperty("line.separator"));
        return output.toString();

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((createdTime == null) ? 0 : createdTime.hashCode());
        result = prime * result
                + ((lastmodTime == null) ? 0 : lastmodTime.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transaction other = (Transaction) obj;
        if (createdTime == null) {
            if (other.createdTime != null) {
                return false;
            }
        } else if (!createdTime.equals(other.createdTime)) {
            return false;
        }
        if (lastmodTime == null) {
            if (other.lastmodTime != null) {
                return false;
            }
        } else if (!lastmodTime.equals(other.lastmodTime)) {
            return false;
        }
        if (state != other.state) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final Transaction transaction) {
        if (this.createdTime.equals(transaction.createdTime)) {
            return 0;
        } else if (this.createdTime.after(transaction.createdTime)) {
            return 1;
        } else {
            return -1;
        }
    }

}
