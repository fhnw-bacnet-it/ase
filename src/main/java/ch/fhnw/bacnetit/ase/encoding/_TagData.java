package ch.fhnw.bacnetit.ase.encoding;

public class _TagData {
    public int tagNumber;

    public boolean contextSpecific;

    public long length;

    public int tagLength;

    public int getTotalLength() {
        return (int) (length + tagLength);
    }

    public boolean isStartTag() {
        return contextSpecific && ((length & 6) == 6);
    }

    public boolean isStartTag(final int contextId) {
        return isStartTag() && tagNumber == contextId;
    }

    public boolean isEndTag() {
        return contextSpecific && ((length & 7) == 7);
    }

    public boolean isEndTag(final int contextId) {
        return isEndTag() && tagNumber == contextId;
    }
}
