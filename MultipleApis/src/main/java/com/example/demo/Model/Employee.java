package com.example.demo.Model;

import java.sql.Time;
import java.time.LocalTime;
//import java.util.Date;
import java.sql.Date;
public class Employee {
    private java.sql.Date date;
    private java.sql.Date month;
    private String team;
    private String panelName;
    private String round;
    private String skill;
    private Time time;
    private String currentLocation;
    private String preferredLocation;
    private String candidateName;

    public Employee(java.sql.Date date, java.sql.Date month, String team, String panelName, String round, String skill, Time time, String currentLocation, String preferredLocation, String candidateName) {
        this.date = date;
        this.month=month;
        this.team = team;
        this.panelName = panelName;
        this.round = round;
        this.skill = skill;
        this.time = time;
        this.currentLocation = currentLocation;
        this.preferredLocation = preferredLocation;
        this.candidateName = candidateName;
    }

    public java.sql.Date getMonth() {
        return month;
    }

    public java.sql.Date getDate() {
        return (java.sql.Date) date;
    }

    public String getTeam() {
        return team;
    }

    public String getPanelName() {
        return panelName;
    }
    public String getRound() {
        return round;
    }
    public String getSkill() {
        return skill;
    }

    public Time getTime() {
        return time;
    }

    public String getCurrentLoc() {
        return currentLocation;
    }

    public String getPreferredLoc() {
        return preferredLocation;
    }

    public String getCandidateName() {
        return candidateName;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "date=" + date +
                ", Month=" + month +
                ", Team='" + team + '\'' +
                ", PanelName='" + panelName + '\'' +
                ", Round='" + round + '\'' +
                ", skill='" + skill + '\'' +
                ", time=" + time +
                ", CurrentLoc='" + currentLocation + '\'' +
                ", PreferredLoc='" + preferredLocation + '\'' +
                ", CandidateName='" + candidateName + '\'' +
                '}';
    }
}
