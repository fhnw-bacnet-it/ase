package ch.fhnw.bacnetit.ase.application.auth;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import ch.fhnw.bacnetit.ase.application.configuration.api.KeystoreConfig;
import ch.fhnw.bacnetit.ase.application.configuration.api.TruststoreConfig;

/**
 * X509AccessControl extends javax.net.ssl.X509TrustManager, therefore further
 * validation checks are possible.
 *
 * @author IMVS, FHNW
 *
 */
public class X509AccessControl implements X509TrustManager {

    private X509TrustManager x509 = null;
    private TruststoreConfig truststoreConfig;

    public X509AccessControl(final KeystoreConfig keystoreConfig,
            final TruststoreConfig truststoreConfig) {

        try (final InputStream readStreamTrust = new FileInputStream(
                truststoreConfig.path)) {

            this.truststoreConfig = truststoreConfig;

            // Create the default trust manager one
            final KeyStore ts = KeyStore.getInstance("JKS");

            ts.load(readStreamTrust, keystoreConfig.pass.toCharArray());

            final TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);

            // Get all trust manager from trustmanager factory
            final TrustManager[] tms = tmf.getTrustManagers();

            // Iterate over all trustmanagers to find the X509 instance
            for (final TrustManager tm : tms) {
                if (tm instanceof X509TrustManager) {
                    this.x509 = (X509TrustManager) tm;
                    return;
                }
            }
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void checkClientTrusted(

            final java.security.cert.X509Certificate[] chain,
            final String authType) throws CertificateException {

        this.x509.checkClientTrusted(chain, authType);

        // Read out alternativeNames
        final String alternativeNames = (String) chain[0]
                .getSubjectAlternativeNames().iterator().next().get(1);
        boolean certificateException = true;

        for (final String trusted : truststoreConfig.list) {
            if (alternativeNames.trim().toLowerCase()
                    .contains(trusted.trim().toLowerCase())) {
                certificateException = false;
            }
        }

        // alternate names not found
        if (certificateException) {
            throw new CertificateException(
                    "SubjectAlternativeName not in trustlist");
        }

    }

    @Override
    public void checkServerTrusted(
            final java.security.cert.X509Certificate[] chain,
            final String authType) {
        try {

            this.x509.checkServerTrusted(chain, authType);
        } catch (final Exception e) {
            System.out.println("Fail in checkServerTrusted: ");
            for (final StackTraceElement ste : e.getStackTrace()) {
                System.err.println(ste);
            }
        }
    }

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return this.x509.getAcceptedIssuers();
    }

}
