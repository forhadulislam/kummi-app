package fi.oulu.mobisocial.kummi_application;

import java.util.Calendar;

public class Task {
    private static final String TAG = "TASKERI TASK";

    //private variables
    int _id;
    String _info;
    Calendar _calendar;
    double _latitude;
    double _longitude;

    // Empty constructor
    public Task(){
        init();
    }

    public void init() {
        this._id = -1;
        this._info = "";
        this._calendar = null;
        this._latitude = 0;
        this._longitude = 0;
    }

    // constructor
    public Task(int id, String info, Calendar calendar, double latitude, double longitude){
        this._id = id;
        this._info = info;
        this._calendar = calendar;
        this._latitude = latitude;
        this._longitude = longitude;
    }

    // constructor
    public Task(int id, String info, Calendar calendar){
        this._id = id;
        this._info = info;
        this._calendar = calendar;
        this._latitude = 0;
        this._longitude = 0;
    }

    // constructor
    public Task(String info, Calendar calendar, double latitude, double longitude){
        this._info = info;
        this._calendar = calendar;
        this._latitude = latitude;
        this._longitude = longitude;
    }

    // constructor
    public Task(String info, Calendar calendar){
        this._info = info;
        this._calendar = calendar;
        this._latitude = 0;
        this._longitude = 0;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting info
    public String getInfo(){
        return this._info;
    }

    // setting info
    public void setInfo(String info){
        this._info = info;
    }

    // check if calendar exists
    public boolean calendarExists() {
        if (this._calendar != null) {
            return true;
        } else {
            return false;
        }
    }

    // getting calendar
    public Calendar getCalendar(){
        return this._calendar;
    }

    // getting readable date and time
    public String getTimeOrLocation(){
        if ((this._calendar != null) && (this._calendar.getTimeInMillis() != 0)) {
            return String.format("%1$tH:%1$tM %1$td.%1$tm.%1$tY ", this._calendar);
        }
        if (this.getLatitude() != 0) {
            return String.format("lat:%f, lng:%f", this.getLatitude(), this.getLongitude());
        }
        return "";
    }

    // setting calendar
    public void setCalendar(Calendar calendar){
        this._calendar = calendar;
    }

    public void setDate(int year, int month, int day) {
        if (this._calendar == null) {
            this._calendar = Calendar.getInstance();
        }
        this._calendar.set(Calendar.YEAR, year);
        this._calendar.set(Calendar.MONTH, month);
        this._calendar.set(Calendar.DAY_OF_MONTH, day);
    }

    public void setTime(int hour, int minute) {
        if (this._calendar == null) {
            this._calendar = Calendar.getInstance();
        }
        this._calendar.set(Calendar.HOUR_OF_DAY, hour);
        this._calendar.set(Calendar.MINUTE, minute);
    }

    // setting calendar in millis
    public void setCalendarInMillis(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        this._calendar = calendar;
    }

    public void setLocation(double latitude, double longitude) {
        this._latitude = latitude;
        this._longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this._latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this._longitude = longitude;
    }

    public double getLatitude() {
        return this._latitude;
    }

    public double getLongitude() {
        return this._longitude;
    }
}
