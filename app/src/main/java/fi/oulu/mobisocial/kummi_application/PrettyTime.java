package fi.oulu.mobisocial.kummi_application;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * Created by opoku on 18-Apr-17.
 */

public class PrettyTime{
    public String TextJustNow;
    public String TextOneMinuteAgo;
    public String TextMinutesAgo;
    public String TextOneHourAgo;
    public String TextHoursAgo;
    public String TextYesterDay;
    public String TextDaysAgo;
    public String TextWeeksAgo;

    public Date date;

    public PrettyTime(Date date){
        this.TextJustNow="just now";
        this.TextOneMinuteAgo="1 minute ago";
        this.TextMinutesAgo="%s minutes ago";
        this.TextHoursAgo="%s hours ago";
        this.TextOneHourAgo="1 hour ago";
        this.TextYesterDay="yesterday";
        this.TextDaysAgo="%s days ago";
        this.TextWeeksAgo="%s weeks ago";
        this.date=date;
    }

    @Override
    public String toString(){
        Date now=new Date();
        long difference=now.getTime()-date.getTime();
        long dayDiff=TimeUnit.DAYS.convert(difference,TimeUnit.MILLISECONDS);
        long secDiff=TimeUnit.SECONDS.convert(difference,TimeUnit.MILLISECONDS);
        if (dayDiff == 0)
        {
            // Less than one minute ago.
            if (secDiff < 60)
            {
                return this.TextJustNow;
            }

            // Less than 2 minutes ago.
            if (secDiff < 120)
            {
                return this.TextOneMinuteAgo;
            }

            // Less than one hour ago.
            if (secDiff < 3600)
            {
                return  String.format(this.TextMinutesAgo, (int) Math.floor ((double)secDiff / 60));
            }

            // Less than 2 hours ago.
            if (secDiff < 7200)
            {
                return this.TextOneHourAgo;
            }

            // Less than one day ago.
            if (secDiff < 86400)
            {
                return String.format(this.TextHoursAgo, (int) Math.floor((double)secDiff / 3600));
            }
        }
        // Handle previous days.
        if (dayDiff == 1)
        {
            return this.TextYesterDay;
        }

        if (dayDiff < 7)
        {
            return String.format(this.TextDaysAgo, (int) dayDiff);
        }

        return String.format(this.TextWeeksAgo, (int) Math.ceil((double)dayDiff / 7));
    }
}
