package de.dhbw.tinf18e.LeapMotionClassifier.ai;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class VerticalLevelTranscoder extends AbstractMotionLevelTranscoder {
    @Override
    public List<Integer> getMeasureEntryCode(FrequencyLevel level) {
        switch (level) {
            case RANDOM:
                return getDstBitmap(Arrays.asList(Difficulty.ClassD));
            case HIGH:
                return getDstBitmap(Arrays.asList(Difficulty.ClassA));
            case MEDIUM:
                return getDstBitmap(Arrays.asList(Difficulty.ClassA, Difficulty.ClassB));
            case LOW:
                return getDstBitmap(Arrays.asList(Difficulty.ClassC));
        }
        return null;
    }

    @Override
    public Motion getMotion() {
        return Motion.VerticalMovement;
    }
}
