package com.bloobirds.analytics.dashboards.reports;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CompanyReport {
    private int companiesStartedToProspect;
    private Map<Integer, Integer> companiesStartedToProspectPerPeriod;

    private Map<String, Integer> nuturingReasons;
    private Map<Integer, Map<String, Integer>> nuturingReasonsPerPeriod;

    private Map<Integer, Integer> discardedReasons;
    private HashMap<Integer, HashMap<Integer, Integer>>  discardedReasonsPerPeriod;

    private Map<Integer, Integer> companiesByStatus;
}
