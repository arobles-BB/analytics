package com.bloobirds.analytics.dashboards.reports;

import com.bloobirds.analytics.dashboards.datamodel.ActivityMeeting;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MeetingReport {

    public int totalMeetings;
    public Map<String,Integer> totalMeetingsGroupedBy;
    public Map<Integer, Integer> meetingsPerSegment;

    public Map<String, Integer> meetingsPerChannel;
    Map<String,Map<String, Integer>> perChannelGroupedBy;
    public Map<Integer, Map<String, Integer>> meetingsPerChannelPerPeriod;

    public Map<Integer, Integer> meetingsResults;
    public HashMap<Integer, HashMap<Integer, Integer>>  meetingsResultsPerPeriod;

}
