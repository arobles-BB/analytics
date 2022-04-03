package com.bloobirds.analytics.dashboards.service;

import com.bloobirds.analytics.dashboards.datamodel.Company;
import com.bloobirds.analytics.dashboards.datamodel.repository.CompanyRepository;
import com.bloobirds.analytics.dashboards.reports.CompanyReport;
import com.bloobirds.analytics.dashboards.reports.ReportFilters;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/Company")
public class CompanyService {
    @Inject
    CompanyRepository companyRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/Report/Overview")
    public CompanyReport reportOverview(ReportFilters filters){
        return companyRepository.reportOverview(filters);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/List/")
    public List<Company> list(ReportFilters filters) {
        return companyRepository.listWithFilters(filters);
    }

}
