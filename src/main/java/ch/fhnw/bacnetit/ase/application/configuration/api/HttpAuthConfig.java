package ch.fhnw.bacnetit.ase.application.configuration.api;

import java.io.Serializable;

public class HttpAuthConfig implements Serializable {
    private static final long serialVersionUID = 7054355218776948716L;
    public final String httpAuthValidate;
    public final String httpAuthRequest;
    private boolean isEnabled = false;

    public HttpAuthConfig(final String httpAuthValidate,
            final String httpAuthRequest) {
        this.httpAuthRequest = httpAuthRequest;
        this.httpAuthValidate = httpAuthValidate;
    }

    public void enableAuth(final boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

}
