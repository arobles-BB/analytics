package com.bloobirds.analytics.dashboards.datamodel;


import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class ActivityInbound extends Activity {
//    ACTIVITY__INBOUND_FORM_NAME
//    	ACTIVITY__INBOUND_LEAD_EMAIL
//    ACTIVITY__INBOUND_PRIORITY
    public int getActivityType() {
        return Activity.ACTIVITY__TYPE__INBOUND;
    }
}
