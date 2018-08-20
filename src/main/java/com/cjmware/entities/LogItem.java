package com.cjmware.entities;

import com.cjmware.utils.DateUtil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="access_log_items")
public class LogItem {

    public static final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";

    @Id
    @GeneratedValue()
    public Integer ID;

    public String batch_id;

    public Date timeStamp;

    public String ipAddress;

    public String request;

    public Integer status;

    public String userAgent;

    public LogItem(String record, String batchId) {

        String[] segments = record.split("\\|");
        SimpleDateFormat localDateFormat = new SimpleDateFormat(dateFormat);
        Date logDt = DateUtil.convertToDate(segments[0], dateFormat);

        if(logDt == null) {
            System.out.println("Cannot parse date from logfile entry: " +segments[0]);
            System.exit(2);
        }

        timeStamp = logDt;
        ipAddress = segments[1];
        request = segments[2];
        status = Integer.valueOf(segments[3]);
        userAgent = segments[4];
        batch_id = batchId;
    }

}
