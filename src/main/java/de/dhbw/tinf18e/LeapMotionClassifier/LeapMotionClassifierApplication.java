package de.dhbw.tinf18e.LeapMotionClassifier;

import de.dhbw.tinf18e.LeapMotionClassifier.entities.LeapRecord;
import de.dhbw.tinf18e.LeapMotionClassifier.io.LeapDataReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class LeapMotionClassifierApplication implements ApplicationRunner {

    private static final Logger LOGGER = LogManager.getLogger(LeapMotionClassifierApplication.class);

    @Autowired
    private LeapDataReader reader;

    public static void main(String[] args) {
        SpringApplication.run(LeapMotionClassifierApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {

        String fileArg;
        try {
            fileArg = args.getOptionValues("file").get(0);
            if (fileArg == null || fileArg.length() == 3) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            LOGGER.error("Missing argument: file");
            return;
        }


        System.out.println("Hello world!");
        try {
            Path path = (new File(fileArg)).toPath();
            List<LeapRecord> leapRecords = reader.readLeapData(path);
            for (LeapRecord leapRecord : leapRecords) {
                System.out.println(leapRecord);
            }
        } catch (Exception e) {
            System.out.println("Catched: " + e.getMessage());
        }

    }

}
