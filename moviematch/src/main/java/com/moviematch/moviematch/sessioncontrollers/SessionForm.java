package com.moviematch.moviematch.sessioncontrollers;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.moviematch.moviematch.sessionmodels.SessionInformation;

public class SessionForm {
    @NotBlank(message ="Title is Required")
    @Size(max = 254)
    private String sessionTitle;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;
    

    @Size(max = 1000, message = "Optional: Describe the session")
    private String sessionBlurb;

    public static SessionForm from(SessionInformation session) {
        SessionForm form = new SessionForm();
        form.setSessionTitle(session.getSessionTitle());
        form.setSessionBlurb(session.getSessionBlurb());
        return form;
    }

    public void applyTo(SessionInformation session) {
        session.setSessionTitle(sessionTitle);
        session.setSessionBlurb(sessionBlurb);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public String getSessionBlurb() {
        return sessionBlurb;
    }

    public void setSessionBlurb(String sessionBlurb) {
        this.sessionBlurb = sessionBlurb;
    }
}
