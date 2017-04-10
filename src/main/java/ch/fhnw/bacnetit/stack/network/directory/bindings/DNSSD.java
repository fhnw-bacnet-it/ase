package ch.fhnw.bacnetit.stack.network.directory.bindings;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.Update;

import ch.fhnw.bacnetit.stack.application.configuration.DiscoveryConfig;
import ch.fhnw.bacnetit.stack.encoding.BACnetEID;
import ch.fhnw.bacnetit.stack.network.directory.DirectoryBinding;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class DNSSD implements DirectoryBinding {

    private final String primaryZone;
    private final Resolver resolver;
    private final DiscoveryConfig dsConfig;

    private static final InternalLogger LOG = InternalLoggerFactory
            .getInstance(DNSSD.class);

    public DNSSD(final DiscoveryConfig dsConfig) throws UnknownHostException {
        this.dsConfig = dsConfig;
        primaryZone = this.dsConfig.primaryZone;
        resolver = new SimpleResolver(dsConfig.dnsIp);
    }

    // API for Directory Service
    @Override
    public synchronized List<BACnetEID> findBDS() {
        final LinkedList<BACnetEID> eids = new LinkedList<>();
        try {

            final Lookup resolveLookup = new Lookup(
                    new Name(dsConfig.sstypeBds + primaryZone), Type.PTR);
            resolveLookup.setResolver(resolver);
            final Record[] records = resolveLookup.run();
            LOG.debug(Arrays.toString(records));
            if (records != null) {
                for (final Record record : records) {
                    if (record instanceof PTRRecord) {
                        final PTRRecord ptrRecord = ((PTRRecord) record);
                        LOG.debug(record.toString());
                        eids.add(new BACnetEID(Integer.parseInt(
                                ptrRecord.getTarget().getLabelString(0))));
                    }
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return eids;
    }

    @Override
    public synchronized URI resolve(final BACnetEID eid) {
        try {
            final Lookup resolveLookup = new Lookup(
                    new Name(eid.getIdentifierAsString() + "."
                            + dsConfig.sstypeDev + primaryZone),
                    Type.SRV);

            final Lookup txtresolveLookup = new Lookup(
                    new Name(eid.getIdentifierAsString() + "."
                            + dsConfig.sstypeDev + primaryZone),
                    Type.TXT);
            resolveLookup.setCache(null); // default cache is always present ->
                                          // delete
            txtresolveLookup.setCache(null);

            resolveLookup.setResolver(resolver);
            final Record[] records = resolveLookup.run();

            txtresolveLookup.setResolver(resolver);
            final Record[] txtRecords = txtresolveLookup.run();

            String protocol = "ws";
            if (txtRecords != null) {
                // Check if wss
                for (final Record record : txtRecords) {
                    if (record instanceof TXTRecord) {
                        final TXTRecord txtRecord = ((TXTRecord) record);
                        for (final Object l : txtRecord.getStrings()) {
                            final String s = (String) l;

                            if (s.contains("wss")) {
                                protocol = "wss";
                            }
                        }
                    }
                }
            }

            if (records != null) {
                LOG.debug(resolveLookup.getErrorString() + ", " + records.length
                        + " found");

                for (final Record record : records) {

                    if (record instanceof SRVRecord) {
                        final SRVRecord srvRecord = ((SRVRecord) record);
                        LOG.debug("found " + srvRecord + " for "
                                + eid.getIdentifierAsString());

                        return new URI(String.format(protocol + "://%s:%d",
                                srvRecord.getTarget().toString(true),
                                srvRecord.getPort()));
                    }
                }
            } else {
                LOG.error(resolveLookup.getErrorString()
                        + ", no records found for EID "
                        + eid.getIdentifierAsString());
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public synchronized void delete(final BACnetEID eid) { // TODO isBDS?
        try {
            final Update deleteUpdate = new Update(new Name(primaryZone));
            deleteUpdate.delete(new Name(eid.getIdentifierAsString() + "."
                    + dsConfig.sstypeDev + primaryZone));
            deleteUpdate.delete(new Name(dsConfig.sstypeDev + primaryZone));
            deleteUpdate.delete(new Name(eid.getIdentifierAsString() + "."
                    + dsConfig.sstypeBds + primaryZone));
            deleteUpdate.delete(new Name(dsConfig.sstypeBds + primaryZone));
            LOG.debug(resolver.send(deleteUpdate).toString());
            LOG.debug(eid.getIdentifierAsString() + " deleted from DNS");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void register(final BACnetEID eid, final URI url,
            final boolean isBDS) {
        try {
            final Update registerUpdate = new Update(new Name(primaryZone));
            registerUpdate.add(makePtrRecord(eid, url, isBDS));
            registerUpdate.add(makeSrvRecord(eid, url, isBDS));
            registerUpdate.add(makeTxtRecord(eid, url, isBDS));
            LOG.debug(resolver.send(registerUpdate).toString());
            LOG.debug(eid.getIdentifierAsString() + " registered in DNS");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void registerObject(final String instance,
            final boolean isInstanceObjectName, final String txtvers,
            final BACnetEID bacnetEid, final String oid_oname, final int ttl,
            final int quality) {
        try {
            final Update registerUpdate = new Update(new Name(primaryZone));
            registerUpdate
                    .add(makeTxtRecordForObject(instance, isInstanceObjectName,
                            txtvers, bacnetEid, oid_oname, ttl, quality));
            LOG.debug(resolver.send(registerUpdate).toString());
            LOG.debug("Object: " + instance + ", registered in DNS");
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    // API for Directory Service end

    private SRVRecord makeSrvRecord(final BACnetEID eid, final URI url,
            final boolean isBDS) {
        SRVRecord ret = null;
        try {
            final Name name = new Name(eid.getIdentifierAsString() + "."
                    + (isBDS ? dsConfig.sstypeBds : dsConfig.sstypeDev)
                    + primaryZone);
            ret = new SRVRecord(name, DClass.IN, 3600, 1, 0, url.getPort(),
                    new Name(url.getHost() + "."));
        } catch (final TextParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private PTRRecord makePtrRecord(final BACnetEID eid, final URI url,
            final boolean isBDS) {
        PTRRecord ret = null;
        try {
            final Name name = new Name(
                    (isBDS ? dsConfig.sstypeBds : dsConfig.sstypeDev)
                            + primaryZone);
            ret = new PTRRecord(name, DClass.IN, 3600, new Name(
                    eid.getIdentifierAsString() + "." + name.toString()));
        } catch (final TextParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private TXTRecord makeTxtRecord(final BACnetEID eid, final URI url,
            final boolean isBDS) {
        TXTRecord ret = null;
        try {
            final Name name = new Name(eid.getIdentifierAsString() + "."
                    + (isBDS ? dsConfig.sstypeBds : dsConfig.sstypeDev)
                    + primaryZone);

            final LinkedList<String> entries = new LinkedList<String>();
            entries.add("txtvers=1");
            entries.add(url.getScheme() + "=/path");
            entries.add("ttl=3600");
            entries.add("prio=3600");
            entries.add("rq=local");

            ret = new TXTRecord(name, DClass.IN, 3600, entries);
        } catch (final TextParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private TXTRecord makeTxtRecordForObject(final String instance,
            final boolean isInstanceObjectName, final String txtvers,
            final BACnetEID bacnetEid, final String oid_oname, final int ttl,
            final int quality) {
        TXTRecord ret = null;
        try {
            final Name name = new Name(
                    instance + "." + dsConfig.sstypeObj + primaryZone);
            final List<String> txtList = new LinkedList<String>();
            txtList.add("txtvers=" + txtvers);
            txtList.add("dev=" + bacnetEid.getIdentifierAsString());
            if (isInstanceObjectName) {
                txtList.add("oid=" + oid_oname);
            } else {
                txtList.add("oname=" + oid_oname);
            }
            txtList.add("ttl=" + ttl);
            txtList.add("rq=" + quality);
            ret = new TXTRecord(name, DClass.IN, 3600, txtList);

        } catch (final TextParseException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
