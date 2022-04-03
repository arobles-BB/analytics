package com.bloobirds.analytics.dashboards.datamodel;

import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("6")
public class ActivityCadence extends Activity {
    //            ACTIVITY__TYPE_CADENCE
    //            ACTIVITY__CADENCE
    //    ACTIVITY__CADENCE_TITLE
    public int getActivityType() {
        return Activity.ACTIVITY__TYPE__CADENCE;
    }
}
