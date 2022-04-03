package com.bloobirds.analytics.dashboards.reports;

import com.bloobirds.analytics.dashboards.reports.abstraction.GroupBy;
import com.bloobirds.analytics.dashboards.reports.abstraction.Segment;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Data
public class ReportFilters {
    // Default value TODAY
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date rangeStart= Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date rangeEnd=null;

    // Default value WEEKLY
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Segment segmentBy=Segment.Weekly;

    // Default value NONE
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GroupBy groupBy=GroupBy.None;

    // defaults to ALL, so no AssignTo Filter (null)
    // does not include tenantID, need to be established (User vs CustomTenantResolver)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String[] assignedTo;

    // defaults to ALL, so no targetMarket Filter (null)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String[] targetMarket;

    // defaults to ALL, so no ICP Filter (null)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String[] icp;

    // defaults to ALL, so no scenario Filter (null)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String[] scenario;

    // defaults to NONE, so no attributes Filter (null)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> attributes = null;
}
