package ch.fhnw.bacnetit.ase.application.configuration.api;

import java.io.Serializable;

public class KeystoreConfig implements Serializable {
    private static final long serialVersionUID = 4205320135536036767L;
    public final String path, pass, alias;

    public KeystoreConfig(final String keystorePath, final String keystorePass,
            final String keystoreAlias) {
        path = keystorePath;
        pass = keystorePass;
        alias = keystoreAlias;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("\tKEYSTORE:\n");
        builder.append("Keystore path: " + path + "\n");
        builder.append("Keystore pass: " + pass + "\n");
        builder.append("Keystore alias: " + alias + "\n");
        return builder.toString();
    }
}
