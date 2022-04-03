package com.bloobirds.analytics.dashboards.datamodel;

import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.ExtendedAttribute;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
public class Company {

    public static final int COMPANY__STATUS__OTHER = 0;
    public static final int COMPANY__STATUS__NEW = 1;
    public static final int COMPANY__STATUS__DELIVERED = 2;
    public static final int COMPANY__STATUS__ON_PROSPECTION = 3;
    public static final int COMPANY__STATUS__READY_TO_PROSPECT = 4;
    public static final int COMPANY__STATUS__FINDING_LEADS = 2;
    public static final int COMPANY__STATUS__CONTACTED = 3;
    public static final int COMPANY__STATUS__ENGAGED = 4;
    public static final int COMPANY__STATUS__MEETING = 5;
    public static final int COMPANY__STATUS__CLIENT = 6;
    public static final int COMPANY__STATUS__NURTURING = 7;
    public static final int COMPANY__STATUS__DISCARDED = 8;
    public static final int COMPANY__STATUS__ACCOUNT = 9;
    public static final int COMPANY__STATUS__BACKLOG = 10;

    public static final int COMPANY__SOURCE__OTHER = 0;
    public static final int COMPANY__SOURCE__OUTBOUND = 1;
    public static final int COMPANY__SOURCE__INBOUND = 2;

    @EmbeddedId
    public BBObjectID objectID;

    public String name; //COMPANY__NAME
    @Audited(withModifiedFlag=true)
    public int status; //COMPANY__STATUS
    @Audited
    public int prevStatus;
    @Audited
    public String statusFieldID; // ID en caso de que no sea uno con logic role
    @Audited
    @Temporal(TemporalType.TIMESTAMP)
    public Date dateStatusUpdate;


    @Temporal(TemporalType.TIMESTAMP)
    public Date startedToProspect; //COMPANY__STATUS__CHANGED_DATE_READY_TO_PROSPECT

    @Audited
    public String discardedReasons; // COMPANY__DISCARDED_REASONS
    @Audited
    public String nurturingReasons; // COMPANY__NURTURING_REASONS
    public int source; // COMPANY__SOURCE
    public String sourcePicklistID;
    public String targetMarket; // COMPANY__TARGET_MARKET
    public String country; // COMPANY__COUNTRY
    public String industry; // COMPANY__INDUSTRY
    public String employeeRange; // COMPANY__SIZE
    public String scenario; // COMPANY__SCENARIO

    @Audited
    public String cadence;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "SUobjectID", referencedColumnName = "BBobjectID")
    })
    public SalesUser assignTo;

    @ElementCollection(fetch = FetchType.EAGER) // Problemas con Jackson, Quarkus, Envers...
    @CollectionTable(
            joinColumns = {@JoinColumn(name = "BBobjectID"), @JoinColumn(name = "tenantID")}
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "BBObjectID")
    @Cascade(value= {org.hibernate.annotations.CascadeType.ALL})
    @ToString.Exclude
    public Map<String, ExtendedAttribute> attributes;
}

