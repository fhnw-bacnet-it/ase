package ch.fhnw.bacnetit.ase.application;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;

import ch.fhnw.bacnetit.ase.application.configuration.KeystoreConfig;

/**
 * Each BACnetDevice instance has one NetworkPortObject. The NetworkProtObject
 * provides information about the accessibility
 *
 * @author IMVS, FHNW
 *
 */
public class NetworkPortObj {

    // private _CharacterString uri;
    private URI uri;
    private final KeystoreConfig keystoreConfig;

    public NetworkPortObj(final String protocol, final int port,
            final KeystoreConfig keystoreConfig) {
        this.keystoreConfig = keystoreConfig;
        try {
            this.getFQDN(protocol, port);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    // public _CharacterString getUri() {
    public URI getUri() {
        return this.uri;
    }

    /**
     * The Operational Device Identity Certificate contains the Subject
     * Alternative Name. The Subject Alternative Name is the url of the host,
     * where the BACnetDevice is running.
     *
     * @param protocol
     *            The Subject Alternative Name doesn't contain information about
     *            the protocol
     * @param port
     *            The Subject Alternative Name doesn't contain information about
     *            the port
     * @throws Exception
     */
    private void getFQDN(final String protocol, final int port) {
        // TODO FQDN does not contain protocol and host parts!
        // URI is only needed for registering in DNS, or is it...
        try (final InputStream readStreamk = new FileInputStream(
                keystoreConfig.path)) {
            X509KeyManager x509 = null;
            final KeyStore ks = KeyStore.getInstance("JKS");
            // Open the configured KeyStore

            ks.load(readStreamk, keystoreConfig.pass.toCharArray());
            readStreamk.close();
            final KeyManagerFactory kmf = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, keystoreConfig.pass.toCharArray());

            for (final KeyManager km : kmf.getKeyManagers()) {
                if (km instanceof X509KeyManager) {
                    x509 = (X509KeyManager) km;
                    break;
                }
            }

            if (x509 == null) {
                throw new Exception("FQDN from Keystore could not be read");
            }

            // this.uri = new _CharacterString(protocol + "://"
            // + ((String) x509
            // .getCertificateChain(keystoreConfig.alias)[0]
            // .getSubjectAlternativeNames().iterator()
            // .next().get(1))
            // + ":" + port);
            this.uri = new URI(protocol + "://"
                    + ((String) x509
                            .getCertificateChain(keystoreConfig.alias)[0]
                                    .getSubjectAlternativeNames().iterator()
                                    .next().get(1))
                    + ":" + port);

        } catch (final Exception io) {
            System.err.println(io);

        }
    }

}
