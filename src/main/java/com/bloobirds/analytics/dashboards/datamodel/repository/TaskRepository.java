package com.bloobirds.analytics.dashboards.datamodel.repository;

import com.bloobirds.analytics.dashboards.datamodel.Task;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskRepository implements PanacheRepositoryBase<Task, BBObjectID> {
}
