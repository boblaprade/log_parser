package com.cjmware.parsers;

import com.cjmware.CommandLineProcessor;
import com.cjmware.entities.LogItem;
import com.cjmware.entities.ThresholdItem;
import com.cjmware.models.Parameters;
import com.cjmware.repo.LogRepository;
import com.cjmware.repo.ThresholdRepository;
import com.cjmware.utils.DateUtil;
import com.cjmware.utils.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

/**
 * Parses the LOG file, expecting the format of each line in the file to follow:
 *
 * TimeStamp | IP | Request | Status | User Agent (pipe delimited, open the example file in text editor)
 *
 * TimeStamp Format: "yyyy-MM-dd HH:mm:ss.SSS"
 *
 * Maps to Entity com.cjmware.entities.LogItem
 */

@Slf4j
@Controller
public class AccessLogParser {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    ThresholdRepository thresholdRepository;

    @Autowired
    MessageHandler messageHandler;

    public void parseLogFile(Parameters parameters) {
        log.info(parameters.toString());

        Stream<String> fileStream = null;
        String logRecord = null;
        List<LogItem> logItems = new ArrayList<>();
        Integer batchCount = 0;
        String batchId = UUID.randomUUID().toString();

        try {
            fileStream = Files.lines(Paths.get(parameters.getSource()));

            fileStream.forEach(s -> {
                LogItem logItem = new LogItem(s, batchId);
                logItems.add(logItem);

                // Every 250 LOG entries read, push them to MySQL
                if(logItems.size() >= 250) {
                    log.info("Storing 250 record batch ...");

                    logRepository.saveAll(logItems);
                    logRepository.flush();
                    logItems.clear();
                }
            });

            if(logItems.size() > 0)
                logRepository.saveAll(logItems);
        }
        catch(IOException ioe) {
            log.error(messageHandler.getMessage("error.processing.log.file"));
            System.exit(4);
        }

        processThreshold(parameters, batchId);
    }

    private void processThreshold(Parameters parameters, String batchId) {


        Date batchDate = Timestamp.valueOf(LocalDateTime.now());
        Date startDate = DateUtil.convertToDate(parameters.getStartDate(), CommandLineProcessor.dateFormat);
        Date endDate = null;
        if(parameters.getDuration().equalsIgnoreCase("hourly"))
            endDate = Timestamp.from(startDate.toInstant().plus(1, ChronoUnit.HOURS));
        else
            endDate = Timestamp.from(startDate.toInstant().plus(1, ChronoUnit.DAYS));

        try {
            List<Map> results = logRepository.findExceptions(startDate, endDate, parameters.getThreshold(), batchId);

            if(results.size() > 0) {
                log.info(messageHandler.getMessage("threshold.header", batchId));
            }

            for (Map queryResult : results) {
                ThresholdItem thresholdItem = new ThresholdItem();
                thresholdItem.setIp_address(queryResult.get("ip_address").toString());
                thresholdItem.setHits(Long.valueOf(queryResult.get("hits").toString()));
                thresholdItem.setBatch_id(batchId);
                thresholdItem.setBatch_date(batchDate);
                thresholdItem.setMessage(messageHandler.getMessage("threshold.parameter.values", batchId, parameters.getThreshold().toString(), parameters.getStartDate(), endDate.toString()));
                thresholdRepository.save(thresholdItem);
                log.info(messageHandler.getMessage("threshold.ip.is", thresholdItem.getIp_address()));
            }
        }
        catch(Exception e) {
            log.error(messageHandler.getMessage("error.processing.threshold.items", batchId));
            System.exit(5);
        }
    }
}
