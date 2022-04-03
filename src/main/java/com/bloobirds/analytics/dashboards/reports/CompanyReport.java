package com.bloobirds.analytics.dashboards.reports;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CompanyReport {
    public int companiesStartedToProspect;
    public Map<Integer, Integer> companiesStartedToProspectPerPeriod;

    public Map<String, Integer> nuturingReasons;
    public Map<Integer, Map<String, Integer>> nuturingReasonsPerPeriod;

    public Map<Integer, Integer> discardedReasons;
    public Map<Integer, Map<Integer, Integer>>  discardedReasonsPerPeriod;

    public Map<Integer, Integer> companiesByStatus;
}
