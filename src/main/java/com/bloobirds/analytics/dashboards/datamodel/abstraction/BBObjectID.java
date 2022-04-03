package com.bloobirds.analytics.dashboards.datamodel.abstraction;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Locale;
import java.util.Random;

@Embeddable
@Data
public class BBObjectID implements Serializable {
    private static final String ID= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private String tenantID;
    private String BBobjectID;

    public static BBObjectID createSample(String tenantID){
        BBObjectID result= new BBObjectID();
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 16)
            salt.append(ID.charAt(rnd.nextInt(ID.length())));
        String saltStr = salt.toString();
        result.setTenantID(tenantID.toLowerCase(Locale.ROOT));
        result.setBBobjectID(saltStr);
        return result;
    }
}
