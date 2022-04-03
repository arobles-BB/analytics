package com.bloobirds.analytics.dashboards.datamodel.history;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="revinfo")
@RevisionEntity(AuditRevisionListener.class)
public class AuditRevisionEntity {
    @Id
    @GeneratedValue
    @RevisionNumber
    private long id;

    @RevisionTimestamp
    private long timestamp;
}
