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
public class Opportunity {

    public static final int OPPORTUNITY__STATUS__NONE = 0;
    public static final int OPPORTUNITY__STATUS__CLOSED_WON = 1;
    public static final int OPPORTUNITY__STATUS__CLOSED_LOST = 2;
    public static final int OPPORTUNITY__STATUS__FIRST_MEETING_SCHEDULED = 3;
    public static final int OPPORTUNITY__STATUS__VERBAL_OK = 4;
    public static final int OPPORTUNITY__STATUS__FIRST_MEETING_DONE = 5;
    public static final int OPPORTUNITY__STATUS__PROPOSAL_EXPLAINED = 6;
    public static final int OPPORTUNITY__STATUS__THIRD_MEETING_DONE = 7;
    public static final int OPPORTUNITY__STATUS__SECOND_MEETING_DONE = 8;
    public static final int OPPORTUNITY__STATUS__PROPOSAL_SENT = 9;
    public static final int OPPORTUNITY__STATUS__ON_HOLD_NURTURING = 10;

    public static final int OPPORTUNITY__TYPE__NONE = 0;
    public static final int OPPORTUNITY__TYPE__RENEWAL = 1;
    public static final int OPPORTUNITY__TYPE__UPSELL = 2;
    public static final int OPPORTUNITY__TYPE__EXISTING_BUSINESS = 3;
    public static final int OPPORTUNITY__TYPE__NEW_BUSINESS = 4;

    @EmbeddedId
    public BBObjectID objectID;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "COtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "COobjectID", referencedColumnName = "BBobjectID")
    })
    public Company company; //    OPPORTUNITY__COMPANY

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "SUobjectID", referencedColumnName = "BBobjectID")
    })
    public SalesUser assignTo; //    OPPORTUNITY__ASSIGNED_TO

    public String name; //    OPPORTUNITY__NAME

    public double amount; // OPPORTUNITY__AMOUNT

    public int type; //    OPPORTUNITY__TYPE
    public String typeFieldID;

    @Audited(withModifiedFlag=true)
    public int status; //    OPPORTUNITY__STATUS
    @Audited
    public int prevStatus;
    @Audited
    public String statusFieldID;
    @Audited
    @Temporal(TemporalType.TIMESTAMP)
    public Date dateStatusUpdate;

    public Date creationDate; //    OPPORTUNITY__CREATION_DATE, //    OPPORTUNITY__CREATION_DATETIME,
    public Date closingDate; //    OPPORTUNITY__CLOSE_DATE,

//    OPPORTUNITY__ACTIVE_UNIQUE_CADENCE_ID,

//    OPPORTUNITY__ATTEMPTS_COUNT_DAYS,
//    OPPORTUNITY__ATTEMPTS_COUNT,
//    OPPORTUNITY__ATTEMPTS_LAST_DAY,
//    OPPORTUNITY__AUTHOR,
//    OPPORTUNITY__CADENCE_DATA,
//    OPPORTUNITY__CADENCE_STOPPED,
//    OPPORTUNITY__CADENCE,

//    OPPORTUNITY__CLOSED_LOST_REASON,

//    OPPORTUNITY__DATA_SOURCE_AUTOMATED,
//    OPPORTUNITY__DATA_SOURCE,
//    OPPORTUNITY__EMPLOYEE_ROLE,
//    OPPORTUNITY__HAS_ATTEMPTS_AFTER_REASSIGN,
//    OPPORTUNITY__LAST_ASSIGNED_DATE,
//    OPPORTUNITY__LEAD_APPROVER,
//    OPPORTUNITY__LEAD_BUYER,
//    OPPORTUNITY__LEAD_DECISION_MAKER,
//    OPPORTUNITY__LEAD_GATEKEEPER,
//    OPPORTUNITY__LEAD_INFLUENCER,
//    OPPORTUNITY__LEAD_OTHER,
//    OPPORTUNITY__LEAD_PRIMARY_CONTACT,
//    OPPORTUNITY__LEAD_USER,
//    OPPORTUNITY__LEADS_COUNT,

//    OPPORTUNITY__START_CADENCE,
//    OPPORTUNITY__STATUS__CLOSED_LOST_DATE,
//    OPPORTUNITY__STATUS__CLOSED_WIN_DATE,
//    OPPORTUNITY__STATUS__LAST_UPDATE,

//    OPPORTUNITY__TOUCHES_COUNT_DAYS,
//    OPPORTUNITY__TOUCHES_COUNT,
//    OPPORTUNITY__TOUCHES_LAST_DAY,

//    OPPORTUNITY__UPDATE_DATETIME,
//    OPPORTUNITY__WITHOUT_FUTURE_TASKS


    @ElementCollection(fetch = FetchType.EAGER) // Problemas con Jackson, Quarkus, Envers...
    @CollectionTable(
            joinColumns = {@JoinColumn(name = "ATBBobjectID"), @JoinColumn(name = "ATtenantID")}
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ATBBObjectID")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    @ToString.Exclude
    public Map<String, ExtendedAttribute> attributes;

}
