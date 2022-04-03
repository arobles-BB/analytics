package com.bloobirds.analytics.dashboards.datamodel;


import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("2")
public class ActivityNote extends Activity {
    //ACTIVITY__NOTE
    public int getActivityType() {
        return Activity.ACTIVITY__TYPE__NOTE;
    }
}
