package com.bloobirds.analytics.dashboards.datamodel.repository;

import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;

@Log
@ApplicationScoped
public class ActivityRepository implements PanacheRepositoryBase<Activity, BBObjectID> {
}
