package com.bloobirds.analytics.dashboards.reports;

import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
public class MeetingReport {

    private int totalMeetings;

    private Map<Integer, Integer> meetingsPerSegment;

    private Map<String, Integer> meetingsPerChannel;

    private Map<Integer, Map<String, Integer>> meetingsPerChannelPerPeriod;

    private Map<Integer, Integer> meetingsResults;

    private HashMap<Integer, HashMap<Integer, Integer>>  meetingsResultsPerPeriod;

}
