package ch.fhnw.bacnetit.stack.network.directory;

public enum DirectoryBindingType {
    DNSSD(0), MDNS(1);

    public final int id;

    private DirectoryBindingType(final int id) {
        this.id = id;
    }
}
