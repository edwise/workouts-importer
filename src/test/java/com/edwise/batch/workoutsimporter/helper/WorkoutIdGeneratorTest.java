package com.edwise.batch.workoutsimporter.helper;

import com.edwise.batch.workoutsimporter.model.Workout;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WorkoutIdGeneratorTest {

    @Test
    void givenSameWorkout_whenGenerateWorkoutId_ShouldReturnSameId() {
        Workout workout1 = Workout.builder()
                                  .startTime(LocalDateTime.of(2025, 3, 14, 10, 0))
                                  .endTime(LocalDateTime.of(2025, 3, 14, 11, 0))
                                  .exerciseTitle("Bench Press")
                                  .setIndex(1)
                                  .build();

        Workout workout2 = Workout.builder()
                                  .startTime(LocalDateTime.of(2025, 3, 14, 10, 0))
                                  .endTime(LocalDateTime.of(2025, 3, 14, 11, 0))
                                  .exerciseTitle("Bench Press")
                                  .setIndex(1)
                                  .build();

        String generatedId1 = WorkoutIdGenerator.generateWorkoutId(workout1);
        String generatedId2 = WorkoutIdGenerator.generateWorkoutId(workout2);

        assertThat(generatedId1).isEqualTo(generatedId2);
    }

    @Test
    void givenDifferentWorkout_whenGenerateWorkoutId_ShouldReturnDifferentId() {
        Workout workout1 = Workout.builder()
                                  .startTime(LocalDateTime.of(2025, 2, 11, 9, 33))
                                  .endTime(LocalDateTime.of(2025, 2, 11, 10, 40))
                                  .exerciseTitle("Bench Press")
                                  .setIndex(0)
                                  .build();

        Workout workout2 = Workout.builder()
                                  .startTime(LocalDateTime.of(2025, 3, 14, 10, 0))
                                  .endTime(LocalDateTime.of(2025, 3, 14, 11, 0))
                                  .exerciseTitle("Squat")
                                  .setIndex(1)
                                  .build();

        String generatedId1 = WorkoutIdGenerator.generateWorkoutId(workout1);
        String generatedId2 = WorkoutIdGenerator.generateWorkoutId(workout2);

        assertThat(generatedId1).isNotEqualTo(generatedId2);
    }

    @Test
    void givenWorkoutWithSmallVariation_whenGenerateWorkoutId_ShouldReturnDifferentId() {
        Workout workout1 = Workout.builder()
                                  .startTime(LocalDateTime.of(2025, 3, 14, 10, 0))
                                  .endTime(LocalDateTime.of(2025, 3, 14, 11, 0))
                                  .exerciseTitle("Bench Press")
                                  .setIndex(1)
                                  .build();

        Workout workout2 = Workout.builder()
                                  .startTime(LocalDateTime.of(2025, 3, 14, 10, 0))
                                  .endTime(LocalDateTime.of(2025, 3, 14, 11, 1))
                                  .exerciseTitle("Bench Press")
                                  .setIndex(1)
                                  .build();

        String generatedId1 = WorkoutIdGenerator.generateWorkoutId(workout1);
        String generatedId2 = WorkoutIdGenerator.generateWorkoutId(workout2);

        assertThat(generatedId1).isNotEqualTo(generatedId2);
    }
}