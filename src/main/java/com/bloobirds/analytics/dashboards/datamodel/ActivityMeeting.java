package com.bloobirds.analytics.dashboards.datamodel;

import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;

import javax.persistence.*;

@Entity
@DiscriminatorValue("3")
public class ActivityMeeting extends Activity {

    public String meetingTitle; // ACTIVITY__MEETING_TITLE

    public int meetingResult; // ACTIVITY__MEETING_RESULT
    public String meetingResultID;

    public int meetingType; // MEETING__TYPE
    public String meetingTypeID;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "MEtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "MEobjectID", referencedColumnName = "BBobjectID")
    })
    public SalesUser assignTo; // ACTIVITY__ACCOUNT_EXECUTIVE

    public int getActivityType() {
        return Activity.ACTIVITY__TYPE__MEETING;
    }

}
