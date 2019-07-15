package com.densoftdevelopers.amt;

public class Parameterslist {
   private  String imsi,device_id,voltage,callibration,time;

    public Parameterslist() {
    }

    public Parameterslist(String imsi, String device_id, String voltage, String callibration, String time) {
        this.imsi = imsi;
        this.device_id = device_id;
        this.voltage = voltage;
        this.callibration = callibration;
        this.time = time;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getCallibration() {
        return callibration;
    }

    public void setCallibration(String callibration) {
        this.callibration = callibration;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
