package com.cjmware;

import com.cjmware.models.Parameters;
import com.cjmware.parsers.AccessLogParser;
import com.cjmware.utils.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This is the main work class that initiates the validation and processing of parameters
 * then invokes the parser implementation com.cjmware.parsers.AccessLogParser to process the
 * LOG file and write out the MySQL entries. Error messages are translatable.
 *
 * NOTE: The parameter value --source was added to allow the LOG file location to be
 * set from the command line. The default location is <launch-folder>/access.log
 *
 */
@Slf4j
@Component
public class CommandLineProcessor  implements CommandLineRunner {

    public static final String dateFormat = "yyyy-MM-dd.HH:mm:ss";

    @Autowired
    private AccessLogParser accesslogParser;

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void run(String... args) throws Exception {
        Parameters parameters = new Parameters();

        if(args.length < 3) {
            log.error(messageHandler.getMessage("too.few.arguments"));
            displaySyntax();
            System.exit(1);
        }

        List<String> argumentsList = Arrays.asList(args);

        for( String argument : argumentsList) {

            if(argument.toLowerCase().startsWith("--startdate")) {
                parameters.setStartDate(argument.substring(argument.indexOf("=")+1));
                SimpleDateFormat localDateFormat = new SimpleDateFormat(dateFormat);
                Date stDt = null;
                try {
                    stDt = localDateFormat.parse(parameters.getStartDate());
                }
                catch(Exception e) {
                    log.error(messageHandler.getMessage("invalid.argument.value", parameters.getStartDate()));
                    displaySyntax();
                    System.exit(2);
                }
            }
            else if(argument.toLowerCase().startsWith("--duration")) {
                parameters.setDuration(argument.substring(argument.indexOf("=")+1));
                if(!Parameters.durationValues.contains(parameters.getDuration())) {
                    log.error(messageHandler.getMessage("invalid.argument.value", parameters.getDuration()));
                    displaySyntax();
                    System.exit(2);
                }
            }
            else if(argument.toLowerCase().startsWith("--threshold")) {
                String value = argument.substring(argument.indexOf("=")+1);
                Integer threshold = null;
                try {
                    threshold =Integer.valueOf(value);
                }
                catch(NumberFormatException nfe) {}
                if(threshold== null || threshold == 0) {
                    log.error(messageHandler.getMessage("invalid.argument.value", value));
                    displaySyntax();
                    System.exit(2);
                }
                parameters.setThreshold(threshold);
            }
            // Added source parameter to allow for specified location and name of access log file
            else if(argument.toLowerCase().startsWith("--source")) {
                parameters.setSource(argument.substring(argument.indexOf("=")+1));
                if(!Files.exists(Paths.get(parameters.getSource()))) {
                    log.error(messageHandler.getMessage("invalid.argument.value", parameters.getSource()));
                    displaySyntax();
                    System.exit(2);
                }
            }
            else {
                log.error(messageHandler.getMessage("invalid.argument", argument));
                displaySyntax();
                System.exit(3);
            }
        }

        accesslogParser.parseLogFile(parameters);
        moveFileToProcessed(parameters.getSource());
    }

    private void displaySyntax() {
        log.info("Syntax");
        log.info("java -jar parser.jar --startDate=<date string> --duration=hourly|weekly --threshold=<integer:100> --source=<path to log file: ./access.log>");
        log.info(" ");
    }

    private void moveFileToProcessed(String filePath) {

        try {
            File fileToMove = new File(filePath);
            File processedFolder = new File(fileToMove.getParent() + "/processed");
            processedFolder.mkdirs();
            String moveToFile = processedFolder.toString() + "/" + fileToMove.getName() +"_" + LocalDateTime.now().toString();
            Files.move(Paths.get(filePath), Paths.get(moveToFile));
        }
        catch(Exception e) {
            log.error(messageHandler.getMessage("unable.to.move.log.file"));
        }
    }
}
