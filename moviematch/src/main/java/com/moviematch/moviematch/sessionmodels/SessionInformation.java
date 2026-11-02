package com.moviematch.moviematch.sessionmodels;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



@Entity
@Table(name="sessionInformation")
public class SessionInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @NotBlank(message ="Title is Required")
    @Size(max = 1000)
    private String sessionTitle;

    @Size(max = 1000, message = "Optional: Describe the movie, or Reasons to vote")
    private String sessionBlurb;

    public SessionInformation(){

    }
    public SessionInformation(String sessionTitle, String sessionBlurb) {
        this.sessionTitle = sessionTitle;
        this.sessionBlurb = sessionBlurb;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
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
