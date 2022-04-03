package com.bloobirds.analytics.dashboards.datamodel;

import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.ExtendedAttribute;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
public class Task {

    public static final int TASK__STATUS__NONE = 0;
    public static final int TASK__STATUS__REJECTED = 1;
    public static final int TASK__STATUS__COMPLETED_OVERDUE = 2;
    public static final int TASK__STATUS__COMPLETED = 3;
    public static final int TASK__STATUS__TODO = 4;
    public static final int TASK__STATUS__OVERDUE = 5;


    public static final int TASK__TYPE_NONE = 0;
    public static final int TASK__TYPE_CONTACT_BEFORE_MEETING = 1;
    public static final int TASK__TYPE_PROSPECT_CADENCE = 2;
    public static final int TASK__TYPE_ADD_QC = 3;
    public static final int TASK__TYPE_NEXT_STEP = 4;
    public static final int TASK__TYPE_ALLOCATE_QC = 5;
    public static final int TASK__TYPE_ADD_LEADS_TO_QC = 6;
    public static final int TASK__TYPE_CONTACT = 7;
    public static final int TASK__TYPE_START_CADENCE = 8;
    public static final int TASK__TYPE_MEETING = 9;
    public static final int TASK__TYPE_SCHEDULED_EMAIL = 10;


    @EmbeddedId
    public BBObjectID objectID;

    public String title; //    TASK__TITLE
    @Audited
    public int status; //    TASK__STATUS
    @Audited
    public String statusFieldID;
    @Audited
    @Temporal(TemporalType.TIMESTAMP)
    public Date dateStatusUpdate;

    @Temporal(TemporalType.DATE)
    public Date scheduled; // TASK__SCHEDULED_DATETIME

    public int type; //    TASK__TASK_TYPE
    public String typeFieldID;


    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "OPtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "OPobjectID", referencedColumnName = "BBobjectID")
    })
    public Opportunity opportunity; //    TASK__OPPORTUNITY

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "COtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "COobjectID", referencedColumnName = "BBobjectID")
    })
    public Company company; //    TASK__COMPANY

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LEtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "LEobjectID", referencedColumnName = "BBobjectID")
    })
    public Contact lead;  //    TASK__LEAD

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "SUobjectID", referencedColumnName = "BBobjectID")
    })
    public SalesUser assignTo; //    TASK__ASSIGNED_TO

    @ElementCollection(fetch = FetchType.EAGER) // Problemas con Jackson, Quarkus, Envers...
    @CollectionTable(
            joinColumns = {@JoinColumn(name = "BBobjectID"), @JoinColumn(name = "tenantID")}
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "BBObjectID")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    @ToString.Exclude
    public Map<String, ExtendedAttribute> attributes;
}
