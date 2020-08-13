package edu.uci.ics.kanec1.service.billing.core;

import edu.uci.ics.kanec1.service.billing.logger.ServiceLogger;

import java.util.Date;

public class Validator {
    public static int emailValidator(String input) {
        if (input == null) return -10;
        int len = input.length();
        if (len <= 0 || len > 50) return -10;

        String[] emailUser = input.split("@", 2);
        if (emailUser.length != 2) return -11;
        String suffix = emailUser[1];
        ServiceLogger.LOGGER.info("email suffix: " + suffix);
        String[] emailDomain = suffix.split("\\.", 0);
        if (emailDomain.length < 2) return -11;

        // Check prefix format
        String prefix = emailUser[0];
        if (prefix.length() == 0) return -11;
        ServiceLogger.LOGGER.info("user prefix: " + prefix);
        char curr;
        boolean prevSpecial = false;
        for (int x = 0; x < prefix.length(); x++) {
            curr = prefix.charAt(x);
            if (!((curr >= 48 && curr <= 57) || (curr >= 65 && curr <= 90) || (curr >= 97 && curr <= 122))) {
                if (curr == 45 || curr == 46 || curr == 95) {
                    if (x == (prefix.length() - 1)) return -11;
                    if (x == 0) return -11;
                    if (prevSpecial) return -11;
                    prevSpecial = true;
                } else {
                    return -11;
                }
            } else {
                if (prevSpecial) {
                    prevSpecial = false;
                }
            }
        }

        // Check domain format
        String domain;
        for (int y = 0; y < emailDomain.length; y++) {
            domain = emailDomain[y];
            if (domain.length() == 0) return -11;
            if (domain.length() < 2 && y > 0) return -11;
            for (int x = 0; x < domain.length(); x++) {
                curr = domain.charAt(x);
                if (!((curr >= 48 && curr <= 57) || (curr >= 65 && curr <= 90) || (curr >= 97 && curr <= 122) || (curr == 45))) {
                    return -11;
                }
            }
        }

        return 1;
    }

    public static int validateCC(String id) {
        if(id == null) return 321;
        int len = id.length();
        if(!(len >= 16 && len <= 20)) return 321;
        char c;
        for(int x = 0; x < len; x++) {
            c = id.charAt(x);
            if(!(c >= 48 && c <= 57)) return 322;
        }
        return 1;
    }

    public static int validateDate(Date d) {
        if(d.before(new Date())) {
            ServiceLogger.LOGGER.info("The expiration date has already passed.");
            return 323;
        }
        ServiceLogger.LOGGER.info("The expiration date is valid.");
        return 1;
    }
}
