package com.cjmware.repo;

import com.cjmware.entities.LogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository("LogRepository")
public interface LogRepository extends JpaRepository<LogItem, Integer> {

    @Query(value = "select ip_address, count(*) as hits " +
            "from access_log_items " +
            "where batch_id = :batchid AND time_stamp >= :start AND time_stamp <= :end " +
            "group by ip_address " +
            "having count(ip_address) >=:threshold", nativeQuery = true)
    public List<Map> findExceptions(@Param("start") Date startDate,
                                    @Param("end") Date endDate,
                                    @Param("threshold") Integer threshold,
                                    @Param("batchid") String batchId);

    @Query( value="Select timeStamp, request from LogItem where ip_address = :ipaddress")
    public List<Map> findByIPAddress(@Param("ipaddress") String ipAddress);
}
