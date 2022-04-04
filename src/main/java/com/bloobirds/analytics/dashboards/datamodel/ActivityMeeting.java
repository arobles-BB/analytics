package com.bloobirds.analytics.dashboards.datamodel;

import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;

import javax.persistence.*;

@Entity
@DiscriminatorValue("3")
public class ActivityMeeting extends Activity {

    public String meetingTitle; // ACTIVITY__MEETING_TITLE

    public static final int ACTIVITY__MEETING_RESULT__NONE=0;
    public static final int ACTIVITY__MEETING_RESULT__UNQUALIFIED_LEAD=1;
    public static final int ACTIVITY__MEETING_RESULT__NOT_INTERESTED=2;
    public static final int ACTIVITY__MEETING_RESULT__NOT_TARGET=3;
    public static final int ACTIVITY__MEETING_RESULT__NOT_THE_RIGHT_MOMENT=4;
    public static final int ACTIVITY__MEETING_RESULT__GOOD=5;
    public static final int ACTIVITY__MEETING_RESULT__NO_SHOW=6;
    public static final int ACTIVITY__MEETING_RESULT__SCHEDULED=7;
    public static final int ACTIVITY__MEETING_RESULT__UNQUALIFIED=8;
    public static final int ACTIVITY__MEETING_RESULT__QUALIFIED=9;


    public int meetingResult; // ACTIVITY__MEETING_RESULT
    public String meetingResultID = "No Value"; // @todo fieldID could be null! negotiate "No Value" value

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
