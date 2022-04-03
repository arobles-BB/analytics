package com.bloobirds.analytics.dashboards.datamodel;

import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import com.bloobirds.analytics.dashboards.datamodel.repository.SalesUserRepository;

import javax.inject.Inject;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class SalesUser {

    @EmbeddedId
    public BBObjectID objectID = new BBObjectID();

}
