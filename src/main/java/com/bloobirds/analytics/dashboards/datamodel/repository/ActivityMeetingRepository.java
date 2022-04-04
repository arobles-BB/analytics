package com.bloobirds.analytics.dashboards.datamodel.repository;

import com.bloobirds.analytics.dashboards.datamodel.ActivityMeeting;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.Activity;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.ExtendedAttribute;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.logicroles.ActivityMeetingLogicRoles;
import com.bloobirds.analytics.dashboards.reports.MeetingReport;
import com.bloobirds.analytics.dashboards.reports.ReportFilters;
import com.bloobirds.analytics.dashboards.reports.abstraction.Segment;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ActivityMeetingRepository implements PanacheRepositoryBase<ActivityMeeting, BBObjectID> {

    public PanacheQuery<ActivityMeeting> findWithFilters(ReportFilters filters) {
        if (filters == null) return findAll();
        Map<String, Object> params = new HashMap<>();
        String query = ReportFilters.createQuery(filters, params);
        if (query != null)
            return find(query, params);
        else return findAll();
    }

    public List<ActivityMeeting> listWithFilters(ReportFilters filters) {
        if (filters == null) return listAll();
        Map<String, Object> params = new HashMap<>();
        String query = ReportFilters.createQuery(filters, params);
        // alternative in-memory for non-pageable queries...faster?
//        return filterMeetingAttributes(list(query,params),filters.getAttributes());
        if (query != null)
            return list(query, params);
        else return listAll();
    }


    private List<ActivityMeeting> filterMeetingAttributes(List<ActivityMeeting> original, Map<String, String> attributes) {
        if (attributes != null && original != null) {
            return original.stream().filter(activity -> andMatch(activity, attributes)).collect(Collectors.toList());
        } else return original;
    }

    private boolean andMatch(ActivityMeeting a, Map<String, String> attr) {
        for (String key : attr.keySet())
            if (a.attributes.get(key) == null || !a.attributes.get(key).equals(attr.get(key))) return false;
        return true;
    }

    @Transactional
    public final ActivityMeeting createSample(String tenantID) {
        ActivityMeeting a;
        Random r = new Random();
        BBObjectID id = BBObjectID.createSample(tenantID);
        a = findById(id);
        if (a == null) {
            a = new ActivityMeeting();
            a.objectID = id;
            a.date = Date.from(LocalDate.now().plus(Period.ofDays(r.nextInt(15))).atStartOfDay(ZoneId.systemDefault()).toInstant());
            a.scenario = BBObjectID.createSample(tenantID).getBBobjectID();
            a.targetMarket = "TM-" + r.nextInt(4);//BBObjectID.createSample(tenantID).getBBobjectID();
            a.icp = BBObjectID.createSample(tenantID).getBBobjectID();
            a.channel = r.nextInt(4);
            a.channelID = BBObjectID.createSample(tenantID).getBBobjectID();
            a.attributes = new HashMap<>();
            ExtendedAttribute attribute = new ExtendedAttribute();
            attribute.assign(ActivityMeetingLogicRoles.NONE, "1");
            a.attributes.put(BBObjectID.createSample(tenantID).getBBobjectID(), attribute);
            if (r.nextInt(5) == 1) { //20%
                a.meetingResult = r.nextInt(10);
                a.meetingResultID = BBObjectID.createSample(tenantID).getBBobjectID();
            }
            persist(a);
        }
        if (a.getActivityType() != Activity.ACTIVITY__TYPE__MEETING) return null;
        else return a;
    }

    public List<ActivityMeeting> getFirstMeetings(List<ActivityMeeting> meetings, ReportFilters filters) {
        if (meetings == null) meetings = listWithFilters(filters);
        // Total meetings: we want only first meetings
        return meetings.stream().filter(m -> m.opportunity == null).collect(Collectors.toList());
    }

    public MeetingReport reportOverview(ReportFilters filters) {
//        LocalDateTime now= LocalDateTime.now();
        // obtain all meetings that follow the criteria
        List<ActivityMeeting> meetings = listWithFilters(filters);
//        Log.info("queryTime:"+ ChronoUnit.MILLIS.between(now,LocalDateTime.now()));

        // init local variables
        MeetingReport result = new MeetingReport();

        // meetings Total
        meetings = getFirstMeetings(meetings, filters);
        result.setTotalMeetings(meetings.size());

        // Group By
        Map<String, List<ActivityMeeting>> meetingsGroupedBy = getMeetingsGroupedBy(meetings, filters);
        Map<String, Integer> totalMeetingsGroupedBy = new HashMap<>();
        meetingsGroupedBy.forEach((k, v) -> totalMeetingsGroupedBy.put(k, v.size()));
        result.setTotalMeetingsGroupedBy(totalMeetingsGroupedBy);

        // meetings per segment
        Map<Integer, List<ActivityMeeting>> meetingsPerSegment = getMeetingsPerSegment(meetings, filters);
        Map<Integer, Integer> totalMeetingsPerSegment = initPerSegmentInteger(filters.getSegmentBy());
        meetingsPerSegment.forEach((k, v) -> totalMeetingsPerSegment.put(k, v.size()));
        result.setMeetingsPerSegment(totalMeetingsPerSegment);

        // meetings per segment per groupby ¿can we?

        // meetings per channel
        Map<String, List<ActivityMeeting>> meetingsPerChannel = getMeetingsPerChannel(meetings, filters);
        Map<String, Integer> totalMeetingsPerChannel = new HashMap<>();
        meetingsPerChannel.forEach((k, v) -> totalMeetingsPerChannel.put(k, v.size()));
        result.setMeetingsPerChannel(totalMeetingsPerChannel);

        // meetings per channel per GroupBy
        Map<String, Map<String, List<ActivityMeeting>>> meetingsPerChannelGroupBy = new HashMap<>();
        meetingsGroupedBy.forEach((k, v) -> {
            Map<String, List<ActivityMeeting>> groupedBy = v.stream().collect(Collectors.groupingBy(m -> m.channelID));
            meetingsPerChannelGroupBy.put(k, groupedBy);
        });
        Map<String, Map<String, Integer>> totalMeetingsPerChannelGroupBy = new HashMap<>();
        meetingsPerChannelGroupBy.forEach((k, v) -> {
            Map<String, Integer> groupedBy = new HashMap<>();
            v.forEach((channelID, l) -> groupedBy.put(channelID, l.size()));
            totalMeetingsPerChannelGroupBy.put(k, groupedBy);
        });
        result.setPerChannelGroupedBy(totalMeetingsPerChannelGroupBy);

        // meetings Per Channel Per Period

        Map<String, Map<Integer, List<ActivityMeeting>>> meetingsPerChannelPerPeriod = new HashMap<>();

        meetingsPerChannel.forEach((k, v) -> {
            Map<Integer, List<ActivityMeeting>> groupedBy = v.stream().collect(Collectors.groupingBy(m -> categorize(filters.getSegmentBy(), m)));
            meetingsPerChannelPerPeriod.put(k, groupedBy);
        });

        Map<String, Map<Integer, Integer>> totalMeetingsPerChannelPerPeriod = new HashMap<>();
        meetingsPerChannelPerPeriod.forEach((k, v) -> {
            Map<Integer, Integer> groupedBy = initPerSegmentInteger(filters.getSegmentBy());
            v.forEach((period, l) -> groupedBy.put(period, l.size()));
            totalMeetingsPerChannelPerPeriod.put(k, groupedBy);
        });

        result.setMeetingsPerChannelPerPeriod(totalMeetingsPerChannelPerPeriod);

        // meetings Per Channel Per Period per Group By ¿can we?

        // meetings Results

        Map<String, List<ActivityMeeting>> meetingResults = meetings.stream().collect(Collectors.groupingBy(m -> m.meetingResultID));
        Map<String, Integer> totalMeetingResults = new HashMap<>();
        meetingResults.forEach((k, v) -> totalMeetingResults.put(k, v.size()));
        result.setMeetingsResults(totalMeetingResults);

        // meetings Results per Period

        Map<String, Map<Integer, List<ActivityMeeting>>> meetingResultsPerPeriod = new HashMap<>();
        meetingResults.forEach((k, v) -> {
            Map<Integer, List<ActivityMeeting>> groupedBy = v.stream().collect(Collectors.groupingBy(m -> categorize(filters.getSegmentBy(), m)));
            meetingResultsPerPeriod.put(k, groupedBy);
        });
        Map<String, Map<Integer, Integer>> totalMeetingResultsPerPeriod = new HashMap<>();
        meetingResultsPerPeriod.forEach((k, v) -> {
            Map<Integer, Integer> groupedBy = initPerSegmentInteger(filters.getSegmentBy());
            v.forEach((period, l) -> groupedBy.put(period, l.size()));
            totalMeetingResultsPerPeriod.put(k, groupedBy);
        });
        result.setMeetingResultsPerPeriod(totalMeetingResultsPerPeriod);

        return result;
    }

    private Map<Integer, List<ActivityMeeting>> getMeetingsPerSegment(List<ActivityMeeting> meetings, ReportFilters filters) {
        Map<Integer, List<ActivityMeeting>> meetingsPerSegment = meetings.stream()
                .collect(Collectors.groupingBy(m -> categorize(filters.getSegmentBy(), m)));
        return meetingsPerSegment;
    }

    private Map<String, List<ActivityMeeting>> getMeetingsPerChannel(List<ActivityMeeting> meetings, ReportFilters filters) {
        Map<String, List<ActivityMeeting>> meetingsPerChannel = meetings.stream()
                .collect(Collectors.groupingBy(m -> m.channelID)); // alt channel. channelId includes non-standard channels
        return meetingsPerChannel;
    }

    private Map<Integer, Integer> initPerSegmentInteger(Segment segment) {
        Map<Integer, Integer> result= new HashMap<>();
        int iterations;
        int key;
        Integer value= 0;
        LocalDate when = LocalDate.now();
        switch (segment) {
            case Daily -> iterations = 365;
            case Weekly -> iterations = 52;
            case Monthly -> iterations = 12;
            case Quarterly -> iterations = 4;
            default -> iterations = 1;
        }
        for (int i = 0; i < iterations; i++) {
            switch (segment) {
                case Daily -> key = when.minusDays(i).getDayOfYear();
                case Weekly -> key = when.minusWeeks(i).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                case Monthly -> key = when.minusMonths(i).getMonthValue();
                case Quarterly -> key = when.minus(i, IsoFields.QUARTER_YEARS).get(IsoFields.QUARTER_OF_YEAR);
                default -> key = 1;
            }
            result.put(key, value);
        }
        return result;
    }

    private Map<Integer, Integer> getTotalMeetingsPerSegment(List<ActivityMeeting> meetings, ReportFilters filters) {
        Map<Integer, Integer> meetingsPerSegment = initPerSegmentInteger(filters.getSegmentBy());
                Map<Integer, List<ActivityMeeting>> mListPerSegment = meetings.stream()
                .collect(Collectors.groupingBy(m -> categorize(filters.getSegmentBy(), m)));
        mListPerSegment.keySet().forEach(k -> meetingsPerSegment.put(k, mListPerSegment.get(k).size()));
        return meetingsPerSegment;
    }

    private Map<String, List<ActivityMeeting>> getMeetingsGroupedBy(List<ActivityMeeting> meetings, ReportFilters filters) {

        Map<String, List<ActivityMeeting>> totalMeetingsGroupedBy = new HashMap<>();

        switch (filters.getGroupBy()) {
            case Scenario -> totalMeetingsGroupedBy = meetings.stream()
                    .collect(Collectors.groupingBy(m -> m.scenario));
            case User -> totalMeetingsGroupedBy = meetings.stream()
                    .collect(Collectors.groupingBy(m -> m.user.objectID.getBBobjectID()));
            case ICP -> totalMeetingsGroupedBy = meetings.stream()
                    .collect(Collectors.groupingBy(m -> m.icp));
            case Source -> totalMeetingsGroupedBy = meetings.stream()
                    .collect(Collectors.groupingBy(m -> m.company.sourcePicklistID));
            case AssignedTo -> totalMeetingsGroupedBy = meetings.stream()
                    .collect(Collectors.groupingBy(m -> m.assignTo.objectID.getBBobjectID()));
            case TargetMarket -> totalMeetingsGroupedBy = meetings.stream()
                    .collect(Collectors.groupingBy(m -> m.targetMarket));
            default -> {
                break; // @todo resolve how to deal with this. anything could come from company's fields... same for lead
            }
        }

        return totalMeetingsGroupedBy;
    }

    private int categorize(Segment segmentBy, ActivityMeeting m) {
        LocalDate localDate;
        int category;
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
