package com.bloobirds.analytics.dashboards.datamodel;

import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.ExtendedAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

@Entity
public class Contact {

    public static final int STATUS_OTHER = 0;
    public static final int STATUS_NEW = 1;    // LEAD__STATUS__NEW
    public static final int STATUS_DELIVERED = 2;  // LEAD__STATUS__DELIVERED
    public static final int STATUS_ON_PROSPECTION = 3; // LEAD__STATUS__ON_PROSPECTION
    public static final int STATUS_CONTACTED = 4; // LEAD__STATUS__CONTACTED
    public static final int STATUS_ENGAGED = 5; // LEAD__STATUS__ENGAGED
    public static final int STATUS_MEETING = 6; // LEAD__STATUS__MEETING
    public static final int STATUS_NURTURING = 7; // LEAD__STATUS__NURTURING
    public static final int STATUS_DISCARDED = 8; // LEAD__STATUS__DISCARDED
    public static final int STATUS_CONTACT = 9;    // LEAD__STATUS__CONTACT
    public static final int STATUS_BACKLOG = 10;    // LEAD__STATUS__BACKLOG

    public String name; // LEAD__NAME
    public String surname; // LEAD__SURNAME
    public String jobTitle; // LEAD__LINKEDIN_JOB_TITLE
    public String linkedIn; // LEAD__LINKEDIN_URL
    public String phoneNumber; // LEAD__PHONE
    public String email; // LEAD__EMAIL
    public String icp; // LEAD__ICP
    @Audited
    public String discardedReasons;
    @Audited
    public String nurturingReasons;
    @Audited(withModifiedFlag=true)
    public int status; // LEAD__STATUS
    @Audited
    public int prevStatus;
    @Audited
    public String statusFieldID; // status fieldID en caso de que no sea uno con Logic Role
    @Audited
    @Temporal(TemporalType.TIMESTAMP)
    public Date dateStatusUpdate;
    @Audited
    public String cadence;

    @EmbeddedId
    public BBObjectID objectID;


    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "COtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "COobjectID", referencedColumnName = "BBobjectID")
    })
    public Company company;

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

    @JsonIgnore
    public String getFullName() {
        return MessageFormat.format("{0} {1}", name, surname);
    }

}
