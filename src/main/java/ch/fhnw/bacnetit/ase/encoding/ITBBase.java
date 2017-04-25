package ch.fhnw.bacnetit.ase.encoding;

abstract public class ITBBase extends Serializer {
    private static final long serialVersionUID = -2536344211247711774L;

    @Override
    public void write(final _ByteQueue queue, final int contextId) {
        // Write a start tag
        writeContextTag(queue, contextId, true);
        write(queue);
        // Write an end tag
        writeContextTag(queue, contextId, false);
    }
}
