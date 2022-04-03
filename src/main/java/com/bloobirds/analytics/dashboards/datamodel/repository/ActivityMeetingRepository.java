package com.bloobirds.analytics.dashboards.datamodel.repository;

import com.bloobirds.analytics.dashboards.datamodel.ActivityMeeting;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.ExtendedAttribute;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.logicroles.ActivityMeetingLogicRoles;
import com.bloobirds.analytics.dashboards.reports.MeetingReport;
import com.bloobirds.analytics.dashboards.reports.ReportFilters;
import com.bloobirds.analytics.dashboards.reports.abstraction.GroupBy;
import com.bloobirds.analytics.dashboards.reports.abstraction.Segment;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ActivityMeetingRepository implements PanacheRepositoryBase<ActivityMeeting, BBObjectID> {

    public PanacheQuery<ActivityMeeting> findWithFilters(ReportFilters filters){
        if (filters == null) return findAll();
        Map<String, Object> params = new HashMap<>();
        String query = createQuery(filters,params);
        return find(query,params);
    }

    public List<ActivityMeeting> listWithFilters(ReportFilters filters) {
        if (filters == null) return listAll();
        Map<String, Object> params = new HashMap<>();
        String query = createQuery(filters,params);
        // alternative in-memory for non-pageable queries...faster?
//        return filterMeetingAttributes(list(query,params),filters.getAttributes());
        return list(query,params);
    }

    private String createQuery(ReportFilters filters, Map<String, Object> params) {
        StringBuilder query = new StringBuilder();

        if (filters.getRangeStart() != null) params.put("start", filters.getRangeStart());
        if (filters.getRangeEnd() == null) {
            query.append("date = :start");
        } else {
            params.put("end", filters.getRangeEnd());
            query.append("date between :start and :end");
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
        if (filters.getGroupBy() != GroupBy.None) query.append(" group by activity_type, bbobjectid, tenantid").append(groupBy);

        return query.toString();
    }

    private List<ActivityMeeting> filterMeetingAttributes(List<ActivityMeeting> original, Map<String, String> attributes) {
        if (attributes != null && original!=null) {
            return original.stream().filter(activity -> andMatch(activity, attributes)).collect(Collectors.toList());
        } else return original;
    }

    private boolean andMatch(ActivityMeeting a ,Map<String, String> attr) {
        for(String key: attr.keySet())
            if(a.attributes.get(key)==null || !a.attributes.get(key).equals(attr.get(key))) return false;
        return true;
    }
    @Transactional
    public final ActivityMeeting createSample(String tenantID){
        ActivityMeeting a;
        Random r = new Random();
        BBObjectID id = BBObjectID.createSample(tenantID);
        a = findById(id);
        if (a == null) {
            a= new ActivityMeeting();
            a.objectID= id;
            a.date= Date.from(LocalDate.now().plus(Period.ofDays(r.nextInt(15))).atStartOfDay(ZoneId.systemDefault()).toInstant());
            a.scenario=BBObjectID.createSample(tenantID).getBBobjectID();
            a.targetMarket=BBObjectID.createSample(tenantID).getBBobjectID();
            a.icp=BBObjectID.createSample(tenantID).getBBobjectID();
            a.channel= r.nextInt(4);
            a.channelID = BBObjectID.createSample(tenantID).getBBobjectID();
            a.attributes= new HashMap<String, ExtendedAttribute>();
            ExtendedAttribute attribute= new ExtendedAttribute();
            attribute.assign(ActivityMeetingLogicRoles.NONE,"1");
            a.attributes.put(BBObjectID.createSample(tenantID).getBBobjectID(),attribute);
            if(r.nextInt(5)==1) { //20%
                a.meetingResult=r.nextInt(10);
                a.meetingResultID=BBObjectID.createSample(tenantID).getBBobjectID();
            }
            persist(a);
        }
        if (a.getActivityType()!= Activity.ACTIVITY__TYPE__MEETING) return null;
        else return a;
    }


    public MeetingReport reportOverview(ReportFilters filters) {
        LocalDateTime now= LocalDateTime.now();
         // obtain all meetings that follow the criteria
        List<ActivityMeeting> meetings = listWithFilters(filters);

//        Log.info("queryTime:"+ ChronoUnit.MILLIS.between(now,LocalDateTime.now()));


        // init all local variables for calculating the stats
        MeetingReport result = new MeetingReport();

        Map<Integer, Integer> meetingsPerSegment = new HashMap<>();
        HashMap<String, Integer> meetingsPerChannel = new HashMap<>();
        HashMap meetingsPerChannelPerPeriod = new HashMap<>();
        Map<Integer, Integer> meetingsResults = new HashMap<>();
        HashMap meetingResultsPerPeriod = new HashMap<>();

        LocalDate when = LocalDate.now();
        int key = when.getYear();
        Integer value = 0;
        int iterations = 1;

        switch (filters.getSegmentBy()) {
            case Daily -> iterations = 365;
            case Weekly -> iterations = 52;
            case Monthly -> iterations = 12;
            case Quarterly -> iterations = 4;
            default -> iterations = 1;
        }

        for (int i = 0; i < iterations; i++) {
            switch (filters.getSegmentBy()) {
                case Daily -> key = when.minusDays(i).getDayOfYear();
                case Weekly -> key = when.minusWeeks(i).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                case Monthly -> key = when.minusMonths(i).getMonthValue();
                case Quarterly -> key = when.minus(i, IsoFields.QUARTER_YEARS).get(IsoFields.QUARTER_OF_YEAR);
                default -> key = 1;
            }
            meetingsPerSegment.put(key, value);
            meetingsPerChannelPerPeriod.put(key, new HashMap());
            meetingResultsPerPeriod.put(key, new HashMap());
        }

        // Total meetings: we want only first meetings
        meetings= meetings.stream().filter(m -> m.opportunity==null).collect(Collectors.toList());
        result.setTotalMeetings(meetings.size());

        // meetings per segment
        Map<Integer,List<ActivityMeeting>> mListPerSegment= meetings.stream()
                .collect(Collectors.groupingBy(m -> categorize(filters.getSegmentBy(),m)));
        mListPerSegment.keySet().forEach(k -> meetingsPerSegment.put(k,mListPerSegment.get(k).size()));
        result.setMeetingsPerSegment(meetingsPerSegment);

        // meetings per channel
        Map<Integer,List<ActivityMeeting>> mListPerChannel= meetings.stream()
                .collect(Collectors.groupingBy(m -> m.channel)); // alt channelId willl include non-standard channels
        mListPerChannel.keySet().forEach(k -> meetingsPerChannel.put(mListPerChannel.get(k).get(0).channelID,mListPerChannel.get(k).size()));
        result.setMeetingsPerChannel(meetingsPerChannel);

        // meetings Per Channel Per Period;
        mListPerChannel.forEach((k,v) -> {
            Map<Integer,List<ActivityMeeting>> oneChannelPerPeriod= v.stream().collect(Collectors.groupingBy(m -> categorize(filters.getSegmentBy(), m)));
            oneChannelPerPeriod.keySet().forEach(period -> {
                Map<String, Integer> onePeriod= (Map<String, Integer>) meetingsPerChannelPerPeriod.get(period);
                onePeriod.put(oneChannelPerPeriod.get(period).get(0).channelID,oneChannelPerPeriod.get(period).size());
                    });
        });
        result.setMeetingsPerChannelPerPeriod(meetingsPerChannelPerPeriod);

        // meetings Results;

        Map<Integer,List<ActivityMeeting>> mListPerResult= meetings.stream()
                .collect(Collectors.groupingBy(m -> m.meetingResult)); // @todo fieldID could be null! negotiate "No Value"
        mListPerResult.keySet().forEach(k -> meetingsResults.put(k,mListPerResult.get(k).size()));
        result.setMeetingsResults(meetingsResults);

        // HashMap<Integer, HashMap<String, Integer>>  meetingsResultsPerPeriod;
        mListPerResult.forEach((k,v) -> {
            Map<Integer, List<ActivityMeeting>> oneResultTypePerPeriod = v.stream().collect(Collectors.groupingBy(m -> categorize(filters.getSegmentBy(), m)));
            oneResultTypePerPeriod.keySet().forEach(period -> {
                Map<Integer, Integer> onePeriod = (Map<Integer, Integer>) meetingResultsPerPeriod.get(period);
                onePeriod.put(k,oneResultTypePerPeriod.get(period).size());
            });
        });
        result.setMeetingsResultsPerPeriod(meetingResultsPerPeriod);

        return result;
    }

    private int categorize(Segment segmentBy, ActivityMeeting m) {
            LocalDate localDate;
            int category=0;
            if (m.date instanceof java.sql.Date) localDate = ((java.sql.Date) m.date).toLocalDate(); // ???
            else localDate = m.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            switch (segmentBy) {
                case Daily -> category = localDate.getDayOfYear();
                case Weekly -> category = localDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                case Monthly -> category = localDate.getMonthValue();
                case Quarterly -> category = localDate.get(IsoFields.QUARTER_OF_YEAR);
                default -> category = 1;
            }
            return category;
    }
}
