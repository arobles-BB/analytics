package com.bloobirds.analytics.security;

import io.quarkus.hibernate.orm.PersistenceUnitExtension;
import io.quarkus.hibernate.orm.runtime.tenant.TenantResolver;
import io.vertx.ext.web.RoutingContext;
import io.quarkus.logging.Log;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;

@PersistenceUnitExtension
@RequestScoped
public class CustomTenantResolver implements TenantResolver {

    String[] tenantID = {"e493soly8adltgya","grhntxyuthif6opr"}; // no usar mayusculas!!

    @Inject
    RoutingContext context;

    @Inject
    DataSource defaultDataSource;

    @Override
    public String getDefaultTenantId() {
        return "postgres";
    }

    @Override
    public String resolveTenantId() {
        // @todo Extract the JWT tenant
        try {
            if (defaultDataSource.getConnection().getSchema()==null) defaultDataSource.getConnection().setSchema("public");
        } catch (SQLException e) {
            Log.error("Problems connecting to the DB[:"+e.getSQLState()+"] "+e.getMessage());
            return getDefaultTenantId();
        }
        context.put("tenantId", tenantID[0]);

        return tenantID[0];
    }

}