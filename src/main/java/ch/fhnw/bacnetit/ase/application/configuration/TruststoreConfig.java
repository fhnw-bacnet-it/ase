package ch.fhnw.bacnetit.ase.application.configuration;

import java.io.Serializable;
import java.util.Arrays;

public class TruststoreConfig implements Serializable {
    private static final long serialVersionUID = -2272586270991098060L;
    public final String path, pass;
    public final String[] list;

    public TruststoreConfig(final String trustorePath,
            final String truststorePass, final String... truststoreList) {
        path = trustorePath;
        pass = truststorePass;
        list = truststoreList;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("\tTRUSTSTORE:\n");
        builder.append("Truststore path: " + path + "\n");
        builder.append("Truststore pass: " + pass + "\n");
        builder.append("Truststore list: " + Arrays.toString(list) + "\n");
        return builder.toString();
    }
}
