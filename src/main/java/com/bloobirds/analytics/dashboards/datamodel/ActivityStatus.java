package com.bloobirds.analytics.dashboards.datamodel;


import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("0")
public class ActivityStatus extends Activity {

    public static final int ACTIVITY__TYPE_STATUS__NONE = 0;
    public static final int ACTIVITY__TYPE_STATUS__LEAD_STATUS_CHANGED = 1;
    public static final int ACTIVITY__TYPE_STATUS__NEW_LEAD_ADDED = 2;
    public static final int ACTIVITY__TYPE_STATUS__NEW_LEAD_CREATED = 3;
    public static final int ACTIVITY__TYPE_STATUS__COMPANY_STATUS_CHANGED = 4;
    public static final int ACTIVITY__TYPE_STATUS__COMPANY_ASSIGNED = 5;
    public static final int ACTIVITY__TYPE_STATUS__COMPANY_CREATED = 6;
    public static final int ACTIVITY__TYPE_STATUS__NEW_LEAD_ADDED_TO_OPPORTUNITY = 7;
    public static final int ACTIVITY__TYPE_STATUS__OPPORTUNITY_CREATED = 8;
    public static final int ACTIVITY__TYPE_STATUS__OPPORTUNITY_ASSIGNED = 9;
    public static final int ACTIVITY__TYPE_STATUS__OPPORTUNITY_STATUS_CHANGED = 10;

    public String title; //    	ACTIVITY__STATUS_TITLE

    public int status; // ACTIVITY__TYPE_STATUS
    public String statusFieldID;
    public String changedFrom; //    ACTIVITY__TYPE_STATUS_CHANGED_FROM
    public String changedTo;     //    	ACTIVITY__TYPE_STATUS_CHANGED_TO
    public boolean automated; //     ACTIVITY__DATA_SOURCE_AUTOMATED,
    public int dataSource;     // ACTIVITY__DATA_SOURCE,
    public String dataSourceID;

    public static final int DATA_SOURCE__NONE=0;
    public static final int DATA_SOURCE__LINKEDIN_CHROME_EXTENSION=1;
    public static final int DATA_SOURCE__FUTURE_ACTIONS=2;
    public static final int DATA_SOURCE__WORKFLOW=3;
    public static final int DATA_SOURCE__PUBLIC_API=4;
    public static final int DATA_SOURCE__WEB_ADMIN=5;
    public static final int DATA_SOURCE__HUBSPOT=6;
    public static final int DATA_SOURCE__IMPORT=7;
    public static final int DATA_SOURCE__SALESFORCE=8;
    public static final int DATA_SOURCE__WEB_APP=9;
    public static final int DATA_SOURCE__NYLAS=10;
    public static final int DATA_SOURCE__ZAPIER=11;
    public static final int DATA_SOURCE__SUPPORT_PANEL=12;
    public static final int DATA_SOURCE__TWILIO=13;
    public static final int DATA_SOURCE__AIRCALL=14;
    public static final int DATA_SOURCE__CALLS_SYNC_APP=15;

    public int getActivityType() {
            return Activity.ACTIVITY__TYPE__STATUS;
        }
}
