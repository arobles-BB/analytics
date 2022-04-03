package com.bloobirds.analytics.dashboards.datamodel.abstraction;


import lombok.extern.java.Log;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Log
@Embeddable
public class ExtendedAttribute {
    public String logicRole;
    public String stringValue;
    @Temporal(TemporalType.TIMESTAMP)
    public Date dateValue;

    public Double numericValue;

    public void assign(LogicRoles lrole, String v) {
        if (v == null) return;
        logicRole = lrole.name();
        stringValue = v;

        try {
            LocalDateTime dateToConvert = LocalDateTime.parse(v, DateTimeFormatter.ISO_DATE_TIME);
            dateValue = Date.from(dateToConvert.atZone(ZoneId.systemDefault())
                            .toInstant());
        } catch (DateTimeParseException e) {
        }

        try {
            numericValue = Double.parseDouble(v);
        } catch (NumberFormatException e) {
        }

    }
}
