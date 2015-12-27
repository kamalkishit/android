package com.humanize.android.data;

/**
 * Created by kamal on 12/27/15.
 */
public class SignupUser {

    private String emailId;
    private String invitationCode;
    private String password;

    public String getEmailId() {
        return emailId;
    }
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    public String getInvitationCode() {
        return invitationCode;
    }
    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
