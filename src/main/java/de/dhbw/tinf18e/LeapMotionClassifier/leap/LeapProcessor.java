package de.dhbw.tinf18e.LeapMotionClassifier.leap;

import de.dhbw.tinf18e.LeapMotionClassifier.ai.Difficulty;
import de.dhbw.tinf18e.LeapMotionClassifier.ai.DifficultyClassifier;
import de.dhbw.tinf18e.LeapMotionClassifier.ai.FrequencyLevel;
import de.dhbw.tinf18e.LeapMotionClassifier.ai.Motion;
import de.dhbw.tinf18e.LeapMotionClassifier.detector.StateMachine;
import de.dhbw.tinf18e.LeapMotionClassifier.detector.StateMachineFactory;
import de.dhbw.tinf18e.LeapMotionClassifier.io.LeapFrameWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeapProcessor {

    private static final Logger LOGGER = LogManager.getLogger(LeapProcessor.class);

    @Value("${time.fps}")
    int FRAMES_PER_SECOND;

    @Value("${time.skip-seconds}")
    double SKIP_SECONDS;

    @Autowired
    private LeapFrameWriter leapFrameWriter;

    @Autowired
    ApplicationArguments args;

    @Autowired
    private StateMachineFactory stateMachineFactory;

    @Autowired
    private DifficultyClassifier difficultyClassifier;

    /**
     * Main method controlling the classification
     * @param data List of input data
     */
    public void start(List<LeapRecord> data) {

        int frameNum = 0;

        StateMachine palmXStateMachine = stateMachineFactory.createForPalmX();
        StateMachine palmYStateMachine = stateMachineFactory.createForPalmY();

        List<LeapFrame> frames = new ArrayList<>();

        for (LeapRecord record : data.stream().filter(record -> record.getValid() == 1).collect(Collectors.toList())) {
            frameNum++;
            LeapFrame frame = new LeapFrame(record, frameNum);
            frames.add(frame);

            // users are assumed to first click start and the move their hand into the
            // measurement area. this causes noise at the measurement's beginning which
            // is skipped here
            if (frameNum <= SKIP_SECONDS * FRAMES_PER_SECOND)
                continue;

            FrequencyLevel xFrequencyLevel = palmXStateMachine.next(frame);
            FrequencyLevel yFrequencyLevel = palmYStateMachine.next(frame);

            frame.setFrequencyLevel(Motion.HorizontalCounterMovement, xFrequencyLevel);
            frame.setFrequencyLevel(Motion.VerticalMovement, yFrequencyLevel);

            Difficulty diff = difficultyClassifier.classify(frame);
            frame.setDifficulty(diff);

            LOGGER.info("[" + frame.getFrameNumber() + "] " + diff + " (X: " + xFrequencyLevel + " | Y: " + yFrequencyLevel + ")");

        }

        // if out parameter is set, the results are printed to csv
        try {
            String fileArg;
            try {
                fileArg = args.getOptionValues("out").get(0);
            } catch (Exception e) {
                LOGGER.info("Argument out not set, skip writing to CSV");
                return;
            }
            leapFrameWriter.write(frames, Path.of(fileArg));
        } catch (Exception e) {
            LOGGER.error("Cannot write frames to CSV", e);
        }
    }
}
