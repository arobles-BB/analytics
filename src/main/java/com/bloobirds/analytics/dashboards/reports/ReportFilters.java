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

    public static String createQuery(ReportFilters filters, Map<String, Object> params) {
        StringBuilder query = new StringBuilder();

        if (filters.getRangeStart() != null) {
            params.put("start", filters.getRangeStart());
            if (filters.getRangeEnd() == null) {
                query.append("date = :start");
            } else {
                params.put("end", filters.getRangeEnd());
                query.append("date between :start and :end");
            }
        }
        if (filters.getTargetMarket() != null) {
            if (query.length() > 0) query.append(" and");
            if (filters.getTargetMarket().length == 1) {
                params.put("tm", filters.getTargetMarket()[0]);
                query.append(" targetmarket = :tm");
            } else {
                query.append(" targetmarket in (");
                for (int i = 0; i < filters.getTargetMarket().length; i++) {
                    query.append(":tm").append(i).append(',');
                    params.put("tm" + i, filters.getTargetMarket()[i]);
                }
                query.deleteCharAt(query.length() - 1);
                query.append(')');
            }
        }

        if (filters.getIcp() != null) {
            if (query.length() > 0) query.append(" and");
            if (filters.getIcp().length == 1) {
                params.put("icp", filters.getIcp()[0]);
                query.append(" icp = :icp");
            } else {
                query.append(" icp in (");
                for (int i = 0; i < filters.getIcp().length; i++) {
                    query.append(":icp").append(i).append(',');
                    params.put("icp" + i, filters.getTargetMarket()[i]);
                }
                query.deleteCharAt(query.length() - 1);
                query.append(')');
            }
        }

        Map<String,String> attr=filters.getAttributes(); //attribute key vs. string value @todo we have to think
        // of a way to pass the most complex filters...Like%, number date

        if(attr!=null) {
            StringBuilder queryAttr = new StringBuilder();
            attr.forEach((k, v) -> {
                if (queryAttr.length() > 0) queryAttr.append(" and");
                queryAttr.append(" KEY(attributes) = :attk").append(k);
                params.put("attk" + k, k);
                queryAttr.append(" and VALUE(attributes).stringValue = :attv").append(k);
                params.put("attv" + k, v);
            });
            if (queryAttr.length() > 0) {
                if (query.length() > 0) query.append(" and");
                query.append(queryAttr);
            }
        }

        if (query.isEmpty()) return null; // we have no filters & no aggregation so let's select all

        String groupBy="";
        switch (filters.getGroupBy()) {
            case Scenario -> groupBy = ", scenario";
            case User -> groupBy = ", user";
            case ICP -> groupBy = ", icp";
            case Source -> groupBy = ", source";
            case AssignedTo -> groupBy = ", assignedto";
            case TargetMarket -> groupBy = ", targetmarket";
            default -> {
                break; // @todo resolve how to deal with this. anything could come from company's fields... same for lead
            }
        }
        if (filters.getGroupBy() != GroupBy.None) query.append(" group by bbobjectid, tenantid").append(groupBy);

        return query.toString();
    }
}
