# Tracking

Application to aggregate tracking data


Prerequisites
-------------
Maven https://maven.apache.org/
Java 8


Prerequisites
-------------
* Build the application jar and run unit tests with `mvn clean install` from the root directory.
* Run program with `java -jar target/tracking-1.0.0-SNAPSHOT.jar [-h] --log-path LOG_PATH --date-range DATE_RANGE [--output-path OUTPUT_PATH]` 
* Example execution: `java -jar target/tracking-1.0.0-SNAPSHOT.jar --log-path src/main/resources/log.txt --date-range "2013-09-01 09:00:00UTC,2013-09-01 10:59:59UTC" --output-path src/main/resources/output.txt`
