package com.humanize.android.service;

/**
 * Created by kamal on 11/24/15.
 */
public class InputValidationService {

    public boolean validateEmailId(String emailId) {
        if (isNull(emailId) || isEmpty(emailId)) {
            return false;
        }

        return true;
    }

    public boolean validatePassword(String password) {
        if (isNull(password) || isEmpty(password)) {
            return false;
        }

        return true;
    }

    public boolean isNull(String string) {
        if (string == null) {
            return true;
        }

        return false;
    }

    public boolean isEmpty(String string) {
        if (string == null || string.length() == 0) {
            return true;
        }

        return false;
    }
}
