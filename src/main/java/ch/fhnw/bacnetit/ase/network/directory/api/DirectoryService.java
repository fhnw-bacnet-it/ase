package ch.fhnw.bacnetit.ase.network.directory.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class DirectoryService {

    public static final String CACHE_FILE_NAME = "localCache.json";

    private final ConcurrentHashMap<BACnetEID, URI> cache;
    private BACnetEID bds = null;
    private DirectoryBinding dnsBinding = null;
    private static DirectoryService instance = null;

    private static final InternalLogger LOG = InternalLoggerFactory
            .getInstance(DirectoryService.class);

    private DirectoryService() {
        this.cache = readCacheFile();
    }

    public static void init() {
        instance = new DirectoryService();
    }

    public static DirectoryService getInstance() {
        if (instance == null) {
            try {
                throw new Exception("Directory Service is not initialized");
            } catch (final Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return instance;
    }

    public synchronized void setDNSBinding(final DirectoryBinding dnsBinding) {
        if (dnsBinding != null) {
            return;
        }
        this.dnsBinding = dnsBinding;
        LOG.debug("DNS", dnsBinding.toString());

    }

    /*
     * public synchronized void setDns(final DiscoveryConfig dsConfig) throws
     * UnknownHostException { dnsBinding =
     * DirectoryBindingFactory.createDirectoryBinding(
     * DirectoryBindingType.valueOf(dsConfig.directoryBindingType),
     * dsConfig.dnsIp, dsConfig);
     *
     * LOG.debug("DNS " + dsConfig.dnsIp + " set"); }
     */

    private synchronized ConcurrentHashMap<BACnetEID, URI> readCacheFile() {
        final ConcurrentHashMap<BACnetEID, URI> readCache = new ConcurrentHashMap<>();
        if (new File(CACHE_FILE_NAME).exists()) {
            try {
                final JsonFactory jsonFactory = new JsonFactory();
                final JsonParser parser = jsonFactory
                        .createJsonParser(new File(CACHE_FILE_NAME));
                while (parser.nextToken() != null) {
                    if (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
                        final BACnetEID eid = new BACnetEID(
                                Integer.parseInt(parser.getCurrentName()));
                        final URI uri = new URI(parser.nextTextValue());
                        LOG.debug("Reading " + eid.getIdentifierAsString()
                                + ": " + uri);
                        readCache.put(eid, uri);
                    }
                }
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return readCache;

    }

    // consult www.json.org for further information on proper object
    // construction in JSON
    private synchronized void writeCacheFile() {
        try {
            final JsonFactory jsonFactory = new JsonFactory();
            // final Writer writer = new FileWriter(new File(CACHE_FILE_NAME));
            final Writer writer = new OutputStreamWriter(
                    new FileOutputStream(new File(CACHE_FILE_NAME)),
                    StandardCharsets.UTF_8);
            final JsonGenerator generator = jsonFactory
                    .createJsonGenerator(writer);
            generator.writeStartObject();
            for (final Entry<BACnetEID, URI> entry : cache.entrySet()) {
                LOG.debug("Writing " + entry.getKey().getIdentifierAsString()
                        + ": " + entry.getValue() + " to file");
                generator
                        .writeFieldName(entry.getKey().getIdentifierAsString());
                generator.writeString(entry.getValue().toString());
            }
            generator.writeEndObject();
            generator.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void register(final BACnetEID eid, final URI url,
            final boolean isBDS, final boolean localOnly) {
        cache.put(eid, url);
        if (isBDS) {
            bds = eid;
        }
        if (!localOnly && dnsBinding != null) {
            dnsBinding.register(eid, url, isBDS);
        }
    }

    public synchronized void registerObject(final String instance,
            final boolean isInstanceObjectName, final String txtvers,
            final BACnetEID bacnetEid, final String oid_oname, final int ttl,
            final int quality, final boolean localOnly) {
        // TODO Cache handling
        if (!localOnly && dnsBinding != null) {
            dnsBinding.registerObject(instance, isInstanceObjectName, txtvers,
                    bacnetEid, oid_oname, ttl, quality);
        }

    }

    public void saveCache() {
        writeCacheFile();
    }

    public synchronized void prepareForShutdown(final List<BACnetEID> eids) {
        for (final BACnetEID eid : eids) {
            cache.remove(eid);
            if (dnsBinding != null) {
                dnsBinding.delete(eid);
            }
        }
    }

    public synchronized BACnetEID getBds() {
        if (bds == null) {
            LOG.debug("No BDS has been found in local cache");
            if (dnsBinding != null) {
                final List<BACnetEID> bdss = dnsBinding.findBDS();

                if (!bdss.isEmpty()) {
                    bds = bdss.get(0);
                    LOG.debug(
                            "BDS found at EID " + bds.getIdentifierAsString());
                } else {
                    LOG.error("No BDS has been found in DNS.");
                }
            }
        }
        return bds;
    }

    public synchronized URI resolve(final BACnetEID eid)
            throws UnknownHostException {
        if (!cache.containsKey(eid) && dnsBinding != null) {
            LOG.debug("URL for " + eid.getIdentifierAsString()
                    + " not found in cache");
            final URI url = dnsBinding.resolve(eid);
            if (url == null) {
                throw new UnknownHostException();
            }
            cache.putIfAbsent(eid, url);
        } else {
            LOG.debug("URL for " + eid.getIdentifierAsString()
                    + " found in cache");
        }
        return cache.get(eid);
    }

    public synchronized URI forceDnsResolve(final BACnetEID eid)
            throws UnknownHostException {
        cache.remove(eid);
        if (dnsBinding != null) {
            final URI url = dnsBinding.resolve(eid);
            if (url == null) {
                throw new UnknownHostException();
            } else {
                cache.put(eid, url);
            }
            return url;
        } else {
            return null;
        }
    }
}
