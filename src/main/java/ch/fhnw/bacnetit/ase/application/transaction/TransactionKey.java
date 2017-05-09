package ch.fhnw.bacnetit.ase.application.transaction;

import ch.fhnw.bacnetit.ase.encoding.UnsignedInteger8;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;

public class TransactionKey {

    public static final int DIRECTION_IN = 0;
    public static final int DIRECTION_OUT = 1;
    private final BACnetEID source;
    private final BACnetEID dest;
    private final UnsignedInteger8 invokeId;
    private final int direction;

    TransactionKey(final BACnetEID _source, final BACnetEID _dest,
            final UnsignedInteger8 _invokeId, final int _direction) {
        if (_source == null) {
            System.err.println(
                    "Transaction Key Init is not valid, source missing");
        }
        if (_dest == null) {
            System.err
                    .println("Transaction Key Init is not valid, dest missing");
        }
        if (_invokeId == null) {
            System.err.println(
                    "Transaction Key Init is not valid, invokeId missing");
        }

        this.source = _source;
        this.dest = _dest;
        this.invokeId = _invokeId;
        this.direction = _direction;
    }

    public BACnetEID getSource() {
        return this.source;
    }

    public BACnetEID getDestination() {
        return this.dest;
    }

    public UnsignedInteger8 getInvokeId() {
        return this.invokeId;
    }

    public int getDirection() {
        return this.direction;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof TransactionKey) {
            final TransactionKey k = (TransactionKey) o;
            return (this.source.equals(k.source) && this.dest.equals(k.dest)
                    && this.invokeId.equals(k.invokeId)
                    && this.direction == k.direction);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.source.hashCode() + this.dest.hashCode()
                + this.invokeId.hashCode() + this.direction;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Transaction key (src: ").append(source.getIdentifier())
                .append(" dest: ").append(dest.getIdentifier())
                .append(" invokeId: ").append(invokeId).append(" direction: ")
                .append(this.direction);
        return sb.toString();
    }
}
