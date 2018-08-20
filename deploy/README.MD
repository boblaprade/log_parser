Robert Laprade
robert.laprade@gmail.com
(413)374-5255

The following assets are included to demonstrate load a web access LOG data into MySql and to query that data for specific entries that meet the provided search criteria.

These assets include:

parser.jar - This is an executable JAR that was built with Java version 1.8.0_181
application.properties - The properties file that provides the MySql connectivity settings
access.log - The LOG file provided for the task
README.MD - This file


After updating the application.properties file entries with the appropriate connectivity settings for your environment, the application can be run using the following command line:

java -jar parser.jar --startDate=2017-01-01.15:00:00 --duration=hourly --threshold=200

Parameters and values are validated, duration defaults to "hourly" and threshold defaults 100

I have provided an additional parameter --source <"path to log file"> which allows the execution to process a log file that is not in the execution folder or may have a different name.

Note that the SPRING JPA framework is set to create/update the MySql schema to create the database and tables if they do not exist.

CREATE DATABASE IF NOT EXISTS log_entries

Using log_entries database

The following SQL represents the "access_log_items" table

CREATE TABLE `access_log_items` (
  `id` int(11) NOT NULL,
  `batch_id` varchar(255) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `request` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `time_stamp` datetime DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


The following SQL represents the "threshold_items" table

CREATE TABLE `threshold_items` (
  `id` int(11) NOT NULL,
  `batch_date` datetime DEFAULT NULL,
  `batch_id` varchar(255) DEFAULT NULL,
  `hits` bigint(20) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


The schema includes a "batchid" field to distinguish between multiple executions of the utility.



SQL Query example used to demonstrate search for threshold items (actual values are parameterized in code to avoid SQL injection);


select ip_address, count(*) as hits 
from access_log_items 
where batch_id = '9310b42c-b609-4d44-9227-1074ac89fb3d' AND 
time_stamp >= '2017-01-01 15:00:00' AND 
time_stamp <= '2017-01-01 16:00:00' 
group by ip_address 
having count(ip_address) > 200 


NOTE that this query is represented and parameterized in class com.cjmware.repo.LogRepository

The query for a specific IP address is as follows:

Select timeStamp, request from LogItem where ip_address = :ipaddress (named parameter where ip address value is provided)


Additionally, all messages are I18N. I have provided English and French resources.

When processing is complete, the LOG file is moved to a "processed" subfolder and renamed by appending the batchid to the end of the file name.







