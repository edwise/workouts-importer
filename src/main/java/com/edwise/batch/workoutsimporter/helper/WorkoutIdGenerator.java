package com.edwise.batch.workoutsimporter.helper;

import com.edwise.batch.workoutsimporter.model.Workout;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class WorkoutIdGenerator {

    private WorkoutIdGenerator() {
    }

    public static String generateWorkoutId(Workout workout) {
        String rawData =
                workout.getStartTime().toString() + "|" +
                workout.getEndTime().toString() + "|" +
                workout.getExerciseTitle() + "|" +
                workout.getSetIndex();

        return sha256(rawData);
    }

    private static String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
}
