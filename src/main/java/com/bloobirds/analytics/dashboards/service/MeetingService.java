package com.bloobirds.analytics.dashboards.service;

import com.bloobirds.analytics.dashboards.datamodel.*;
import com.bloobirds.analytics.dashboards.datamodel.abstraction.BBObjectID;
import com.bloobirds.analytics.dashboards.datamodel.repository.*;
import com.bloobirds.analytics.dashboards.reports.MeetingReport;
import com.bloobirds.analytics.dashboards.reports.ReportFilters;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Path("/Meeting")
public class MeetingService {
    @Inject
    ActivityMeetingRepository meetingRepository;
    @Inject
    CompanyRepository companyRepository;
    @Inject
    ContactRepository contactRepository;
    @Inject
    SalesUserRepository salesUserRepository;
    @Inject
    OpportunityRepository opportunityRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/Report/Overview")
    public MeetingReport reportOverview(ReportFilters filters){
        return meetingRepository.reportOverview(filters);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/List/")
    public List<ActivityMeeting> list(ReportFilters filters) {
        return meetingRepository.listWithFilters(filters);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/TestData/{amount}")
    @Transactional
    public List<ActivityMeeting> testData(@PathParam("amount") Integer amount) {

        List result= new LinkedList();
        while (amount-- >0) {
            Random r = new Random();
            String tID=tenantID[r.nextInt(tenantID.length)];

            ActivityMeeting a= meetingRepository.createSample(tID);
            if (a==null) continue; // we've found an activity with same ID but different type; skip

            BBObjectID suid = BBObjectID.createSample(tID);

            SalesUser su=salesUserRepository.createSample(tID);
            a.assignTo=su;
            a.user=salesUserRepository.createSample(tID);

            Company c= companyRepository.createSample(tID);
            c.assignTo=su;
            companyRepository.persist(c);
            a.company=c;

            Contact co=contactRepository.createSample(tID);
            co.company=c;
            co.assignTo=su;
            contactRepository.persist(co);
            a.lead=co;

            a.meetingTitle = "Meeting with "+a.company.name;

            if(r.nextInt(5)==1) { // 20%
                Opportunity o= opportunityRepository.createSample(tID);
                o.company=c;
                o.assignTo=su;
                o.name = "Opportunity on "+a.company.name;
                opportunityRepository.persist(o);
                a.opportunity=o;
            }
            meetingRepository.persist(a);
            result.add(a);
        }
        return result;
    }

    private final static String[] tenantID ={"E493solY8aDLTGyA","grHNtxYuTHif6Opr"};

}
