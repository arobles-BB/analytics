package com.bloobirds.analytics.dashboards.datamodel.repository;

import com.bloobirds.analytics.dashboards.datamodel.Opportunity;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

@ApplicationScoped
public class OpportunityRepository implements PanacheRepositoryBase<Opportunity, BBObjectID> {
    @Transactional
    public Opportunity createSample(String tenantID) {
        BBObjectID oid = BBObjectID.createSample(tenantID);
        Random r = new Random();

        Opportunity o=findById(oid);
        if(o==null){
            o= new Opportunity();
            o.objectID=oid;
        }
        o.prevStatus=o.status;
        o.status= r.nextInt(10);
        o.statusFieldID=BBObjectID.createSample(tenantID).getBBobjectID();
        o.dateStatusUpdate= Date.from(LocalDate.now().minus(Period.ofDays(r.nextInt(15))).atStartOfDay(ZoneId.systemDefault()).toInstant());
        persist(o);
        return o;
    }
}
