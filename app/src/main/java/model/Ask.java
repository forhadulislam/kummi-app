package model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Sadi on 5/1/2017.
 */

public class Ask implements Serializable {

    private String details;
    private String user_id;
    Timestamp timestampn = new Timestamp(System.currentTimeMillis());
    public Long miliTimeStamp = timestampn.getTime();
    private Timestamp timestamp;

    public Ask(String details, String user_id) {
        this.details = details;
        this.user_id = user_id;
    }

    public String getDetails() {
        return details;
    }

    public String getUser_id() {
        return user_id;
    }


    public void setDetails(String details) {
        this.details = details;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setTimestamp() {
        this.timestamp = timestampn;
    }

    @Override
    public String toString() {
        return "Ask{" +
                "details='" + details + '\'' +
                ", user_id='" + user_id + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
