package com.bloobirds.analytics.dashboards.datamodel;


import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("4")
public class ActivityLinkedIn extends Activity {
    //        ACTIVITY__LINKEDIN_THREAD
    //        ACTIVITY__LINKEDIN_MESSAGE_HASH
    //        ACTIVITY__MESSAGE_BODY
    //        ACTIVITY__MESSAGE_SUBJECT
    public int getActivityType() {
        return Activity.ACTIVITY__TYPE__LINKEDIN_MESSAGE;
    }
}
