package ch.fhnw.bacnetit.stack.network.directory;

import java.net.UnknownHostException;

import ch.fhnw.bacnetit.stack.application.configuration.DiscoveryConfig;
import ch.fhnw.bacnetit.stack.network.directory.bindings.DNSSD;
import ch.fhnw.bacnetit.stack.network.directory.bindings.MDNS;

public class DirectoryBindingFactory {

    public static DirectoryBinding createDirectoryBinding(
            final DirectoryBindingType dbt, final String dnsIp,
            final DiscoveryConfig config) throws UnknownHostException {
        System.out.println("Directory Binding Type: " + dbt);
        switch (dbt) {
        case DNSSD:
            return new DNSSD(config);
        case MDNS:
            return new MDNS(config);
        default:
            System.out.println("DNS not set");
            return null;
        }

    }
}
