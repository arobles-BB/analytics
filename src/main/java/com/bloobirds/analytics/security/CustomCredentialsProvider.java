package com.bloobirds.analytics.security;

import io.quarkus.arc.Arc;
import io.quarkus.arc.Unremovable;
import io.quarkus.credentials.CredentialsProvider;
import io.quarkus.logging.Log;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Unremovable
@Named("bb-credentials-provider")
public class CustomCredentialsProvider implements CredentialsProvider {
    @Inject
    RoutingContext context;
    @Override
    public Map<String, String> getCredentials(String credentialsProviderName) {

        String tenantId;

        if ( Arc.container().requestContext().isActive() ) {
            tenantId=context.get("tenantId");
        } else {
            Log.error("Could not extract tenant information because the RequestContext is not active. Switching to default");
            //this theoretically only occurs at startup
            tenantId="postgres";
        }

        Map<String, String> properties = new HashMap<>();
        properties.put(USER_PROPERTY_NAME, tenantId);
        // @todo get the pass from the postgress
        properties.put(PASSWORD_PROPERTY_NAME, "bloobirds");
        return properties;
    }

}