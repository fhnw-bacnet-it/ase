package ch.fhnw.bacnetit.ase.application.configuration.api;

import java.io.Serializable;

public class DiscoveryConfig implements Serializable {
    private static final long serialVersionUID = -4029212896305139220L;
    public final String dnsIp, primaryZone, sstypeBds, sstypeDev, sstypeObj,
            directoryBindingType;
    public final boolean persistentCache;

    /**
     * Configuration object passed to Directory Service while constructing.
     *
     * @param directoryBindingType
     *            Type of DNS binding
     * @param dnsIp
     *            DNS server IP
     * @param primaryZone
     *            DNS zone name
     * @param sstypeBds
     *            Type BDS device
     * @param sstypeDev
     *            Type device
     * @param sstypeObj
     *            Type object
     * @param persistentCache
     *            Must the EID cache be saved locally when device powers down
     */
    public DiscoveryConfig(final String directoryBindingType,
            final String dnsIp, final String primaryZone,
            final String sstypeBds, final String sstypeDev,
            final String sstypeObj, final boolean persistentCache) {
        this.directoryBindingType = directoryBindingType;
        this.dnsIp = dnsIp;
        this.primaryZone = primaryZone;
        this.sstypeBds = sstypeBds;
        this.sstypeDev = sstypeDev;
        this.sstypeObj = sstypeObj;
        this.persistentCache = persistentCache;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("\tDISCOVERY:\n");
        builder.append("DNS IP: " + dnsIp + "\n");
        builder.append("type: " + directoryBindingType + "\n");
        builder.append("primary zone: " + primaryZone + " \n");
        builder.append("BDS: " + sstypeBds + ", DEV: " + sstypeDev + ", OBJ: "
                + sstypeObj);
        return builder.toString();
    }
}
