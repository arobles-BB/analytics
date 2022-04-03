package com.bloobirds.analytics.dashboards.datamodel;


import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("5")
public class ActivityEmail extends Activity {
    public int getActivityType() {
        return Activity.ACTIVITY__TYPE__EMAIL;
    }
//        ACTIVITY__EMAIL_HISTORY_CLICK
//        ACTIVITY__EMAIL_HISTORY_OPEN
//        ACTIVITY__EMAIL_HISTORY_REPLY
//        ACTIVITY__EMAIL_LAST_DATE_OPEN
//        ACTIVITY__EMAIL_LEAD
//        ACTIVITY__EMAIL_TIMES_CLICK
//        ACTIVITY__EMAIL_TIMES_OPEN
//        ACTIVITY__EMAIL_TIMES_REPLY
//        ACTIVITY__EMAIL_UID
//        ACTIVITY__EMAIL_USER
//        ACTIVITY__MESSAGE_BODY
//        ACTIVITY__MESSAGE_SUBJECT

}
