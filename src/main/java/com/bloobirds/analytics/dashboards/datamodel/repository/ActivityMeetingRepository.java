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
        String query = ReportFilters.createQuery(filters,params);
        if (query!=null)
            return find(query,params);
        else return findAll();
    }

    public List<ActivityMeeting> listWithFilters(ReportFilters filters) {
        if (filters == null) return listAll();
        Map<String, Object> params = new HashMap<>();
        String query = ReportFilters.createQuery(filters,params);
        // alternative in-memory for non-pageable queries...faster?
//        return filterMeetingAttributes(list(query,params),filters.getAttributes());
        if (query!=null)
            return list(query,params);
        else return listAll();
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
//        LocalDateTime now= LocalDateTime.now();
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
