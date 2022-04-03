package com.bloobirds.analytics.dashboards.datamodel;

import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;
import org.hibernate.annotations.Type;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
@DiscriminatorValue("7")
public class ActivityCall extends Activity {

    //    ACTIVITY__CALL_LEAD_PHONE_NUMBER
    //    ACTIVITY__CALL_RECORD_URL
    //    ACTIVITY__CALL_SID
    //    ACTIVITY__CONTACT_RESULT
// ACTIVITY__CALL_STATUS
// ACTIVITY__CALL_STATUS__ONGOING
// ACTIVITY__CALL_STATUS__CONNECTING
// ACTIVITY__CALL_STATUS__ENDED

    public static final int ACTIVITY__DIRECTION__MISSED = 0;
    public static final int ACTIVITY__DIRECTION__OUTGOING = 1;
    public static final int ACTIVITY__DIRECTION__INCOMING = 2;


    public static final int ACTIVITY__CALL_RESULT__GATEKEEPER=0;
    public static final int ACTIVITY__CALL_RESULT__CORRECT_CONTACT=1;
    public static final int ACTIVITY__CALL_RESULT__REFERRAL=2;
    public static final int ACTIVITY__CALL_RESULT__LEFT_VOICEMAIL=3;
    public static final int ACTIVITY__CALL_RESULT__NO_ANSWER=4;
    public static final int ACTIVITY__CALL_RESULT__BUSY=5;
    public static final int ACTIVITY__CALL_RESULT__APPROACH=6;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    // bug de hibernate y postgres https://shred.zone/cilla/page/299/string-lobs-on-postgresql-with-hibernate-36.html
    public String note;     //ACTIVITY__NOTE
    public String origin;     //    ACTIVITY__CALL_USER_PHONE_NUMBER

    public double seconds; //    ACTIVITY__CALL_DURATION
    public int callResult;     //    ACTIVITY__CALL_RESULT
    public String callResultFieldID;     //    ACTIVITY__CALL_RESULT

    public boolean pitch_done; //    ACTIVITY__PITCH_DONE
    public String pitch_doneFieldID;
    public String pitch_used;        //    ACTIVITY__PITCH
    public int direction;     //    ACTIVITY__DIRECTION
    public String directionFieldID;

    public int getActivityType() {
        return Activity.ACTIVITY__TYPE__CALL;
    }

}
