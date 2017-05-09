package ch.fhnw.bacnetit.ase.application.configuration.api;

import java.io.Serializable;

public class ConnectionConfig implements Serializable {
    private static final long serialVersionUID = -5887874403305717477L;
    public final String name;
    public final int version;
    public final int port, timeout;
    public final String protocol;
    public String httpAuthValidate, httpAuthRequest, secprotocol;
    public boolean doesControlMessages = false, doesCors = false,
            doesHttpAuth = false, isSsl = false;

    public ConnectionConfig(final String name, final String protocol,
            final int port, final int timeout) {
        this.name = name;
        this.protocol = protocol;
        this.port = port;
        this.timeout = timeout;
        // TODO
        this.version = 1;
    }

    public void setSecprotocol(final String secprotocol) {
        this.secprotocol = secprotocol;
    }

    public void enableSSL(final boolean enabled) {
        this.isSsl = enabled;
    }

    public void enableControlMessages(final boolean doesControlMessages) {
        this.doesControlMessages = doesControlMessages;
    }

    public void enableCors(final boolean doesCors) {
        this.doesCors = doesCors;
    }

    public void enableHttpAuth(final boolean doesHttpAuth) {
        this.doesHttpAuth = doesHttpAuth;
    }

    public void setHttpAuthParams(final String validate, final String request) {
        this.httpAuthValidate = validate;
        this.httpAuthRequest = request;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("\tCHANNEL:\n");
        builder.append("name: " + name + "\n");
        builder.append("timeout: " + timeout + "\n");
        builder.append("version: " + version + "\n");
        builder.append("protocol: " + protocol + "\n");
        builder.append("HTTP auth validate: " + httpAuthValidate + "\n");
        builder.append("HTTP auth request: " + httpAuthRequest + "\n");
        builder.append("control messages: " + doesControlMessages + "\n");
        builder.append("CORS: " + doesCors + "\n");
        builder.append("HTTP auth: " + doesHttpAuth + "\n");
        builder.append("Websocket-upgrade-secprotocol: " + secprotocol + "\n");
        return builder.toString();
    }

}
