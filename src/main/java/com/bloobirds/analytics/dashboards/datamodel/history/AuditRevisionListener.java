package com.bloobirds.analytics.dashboards.datamodel.history;


import lombok.extern.java.Log;
import org.hibernate.envers.RevisionListener;

import javax.enterprise.context.Dependent;

@Log
@Dependent
public class AuditRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
// no need to do anything, just extending to change int for long in reventity
    }
}
