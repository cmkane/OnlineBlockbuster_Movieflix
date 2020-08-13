package edu.uci.ics.kanec1.service.idm.core;

import edu.uci.ics.kanec1.service.idm.logger.BasicLogger;

public class Validator {
    public static int validateUserCredentials(String email, char[] password) {
        int code;
        code = emailValidator(email);
        if(code == 1) {
            code = passwordValidator(password);
        }
        return code;
    }

    public static int emailValidator(String input) {
        if(input == null) return -10;
        int len = input.length();
        if(len <= 0 || len > 50) return -10;

        String[] emailUser = input.split("@", 2);
        if(emailUser.length != 2) return -11;
        String suffix = emailUser[1];
        BasicLogger.LOGGER.info("email suffix: "+suffix);
        String[] emailDomain = suffix.split("\\.", 5);
        if(emailDomain.length != 2) return -11;

        // Check prefix format
        String prefix = emailUser[0];
        if(prefix.length() == 0) return -11;
        BasicLogger.LOGGER.info("user prefix: "+prefix);
        char curr;
        boolean prevSpecial = false;
        for(int x = 0; x < prefix.length(); x++) {
            curr = prefix.charAt(x);
            if (!((curr >= 48 && curr <= 57) || (curr >= 65 && curr <= 90) || (curr >= 97 && curr <= 122))) {
                if(curr == 45 || curr == 46 || curr == 95) {
                    if(x == (prefix.length() - 1)) return -11;
                    if(x == 0) return -11;
                    if(prevSpecial) return -11;
                    prevSpecial = true;
                } else {
                    return -11;
                }
            } else {
                if(prevSpecial) {
                    prevSpecial = false;
                }
            }
        }

        // Check domain format
        String domainPrefix = emailDomain[0];
        String domainSuffix = emailDomain[1];
        BasicLogger.LOGGER.info("domainPrefix: "+domainPrefix+", domainSuffix: "+domainSuffix);
        if(domainSuffix.length() < 2) return -11;
        for(int x = 0; x < domainPrefix.length(); x++) {
            curr = domainPrefix.charAt(x);
            if (!((curr >= 48 && curr <= 57) || (curr >= 65 && curr <= 90) || (curr >= 97 && curr <= 122) || (curr == 45))) {
                return -11;
            }
        }

        return 1;
    }

    public static int passwordValidator(char[] input) {
        BasicLogger.LOGGER.info("Starting password validator...");
        if(input == null) {
            return -12;
        }
        int len = input.length;

        if(len == 0) {
            return -12;
        }

        // Check if password has correct length
        if(len < 7 || len > 16) {
            return 12;
        }

        // Check if password has all required characters
        boolean hasUpper = false, hasLower = false, hasNum = false, hasSpecial = false;
        char curr;
        for(int x = 0; x < len; x++) {
            curr = input[x];
            if(curr >= 65 && curr <= 90) hasUpper = true;
            if(curr >= 97 && curr <= 122) hasLower = true;
            if(curr >= 48 && curr <= 57) hasNum = true;
            if((curr >= 33 && curr <= 47) || (curr >= 58 && curr <= 64) && (curr >= 91 && curr <= 96) && (curr >= 123 && curr <= 126)) hasSpecial = true;
        }
        if(!hasUpper || !hasLower || !hasNum || !hasSpecial) return 13;

        BasicLogger.LOGGER.info("Password is valid.");
        return 1;
    }

    public static int sessionIDValidator(String sessionID) {
        BasicLogger.LOGGER.info("Sarting sessionID validator...");
        int len = sessionID.length();
        if(len != 128) {
            BasicLogger.LOGGER.info("SessionID length is invalid.");
            return -13;
        }
        BasicLogger.LOGGER.info("SessionID is valid.");
        return 1;
    }

    public static int privilegeValidator(int privilege) {
        if(privilege <=0 || privilege > 5) {
            return -14;
        }
        else return 1;
    }
}
