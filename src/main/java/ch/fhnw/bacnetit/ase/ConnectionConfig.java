package ch.fhnw.bacnetit.ase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ConnectionConfig extends PropertiesConfiguration {

    private final String iface;

    public ConnectionConfig(final String path, final String iface)
            throws ConfigurationException {
        super(path);
        this.iface = iface;
    };

    @Override
    public String getString(final String keyOfInterest) {
        return super.getString(prepareKey(keyOfInterest));
    }

    @Override
    public int getInt(final String keyOfInterest) {
        return super.getInt(prepareKey(keyOfInterest));
    }

    @Override
    public long getLong(final String keyOfInterest) {
        return super.getLong(prepareKey(keyOfInterest));
    }

    @Override
    public boolean getBoolean(final String keyOfInterest) {
        return super.getBoolean(prepareKey(keyOfInterest));
    }

    @Override
    public String[] getStringArray(final String keyOfInterest) {
        return super.getStringArray(prepareKey(keyOfInterest));
    }

    private String prepareKey(final String _key) {
        if (_key.startsWith("global.")) {
            return _key;
        } else {
            return this.iface + "." + _key;
        }
    }

}
