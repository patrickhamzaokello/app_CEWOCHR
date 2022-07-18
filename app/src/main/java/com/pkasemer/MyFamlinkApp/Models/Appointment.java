package com.pkasemer.MyFamlinkApp.Models;


import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Appointment {

    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("purpose")
    @Expose
    private String purpose;
    @SerializedName("appointment_date")
    @Expose
    private String appointmentDate;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

}