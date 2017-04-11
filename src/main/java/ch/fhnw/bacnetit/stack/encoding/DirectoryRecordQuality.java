package ch.fhnw.bacnetit.stack.encoding;

public class DirectoryRecordQuality extends _Enumerated {

    private static final long serialVersionUID = 7137105893311734773L;

    public static final DirectoryRecordQuality invalid = new DirectoryRecordQuality(
            0);
    public static final DirectoryRecordQuality unknown = new DirectoryRecordQuality(
            1);
    public static final DirectoryRecordQuality local = new DirectoryRecordQuality(
            2);
    public static final DirectoryRecordQuality configured = new DirectoryRecordQuality(
            3);
    public static final DirectoryRecordQuality registered = new DirectoryRecordQuality(
            4);
    public static final DirectoryRecordQuality learned = new DirectoryRecordQuality(
            5);
    public static final DirectoryRecordQuality monitored = new DirectoryRecordQuality(
            6);

    public DirectoryRecordQuality(final int value) {
        super(value);
    }

    public DirectoryRecordQuality(final _ByteQueue queue) {
        super(queue);
    }

}
