package com.bloobirds.analytics.dashboards.datamodel.repository;

import com.bloobirds.analytics.dashboards.datamodel.SalesUser;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SalesUserRepository  implements PanacheRepositoryBase<SalesUser, BBObjectID> {
    public  SalesUser createSample(String tenantID){
        BBObjectID suid = BBObjectID.createSample(tenantID);
        SalesUser su=findById(suid);
        if (su == null) {
            su= new SalesUser();
            su.objectID= suid;
            persist(su);
        }
        return su;
    }
}
