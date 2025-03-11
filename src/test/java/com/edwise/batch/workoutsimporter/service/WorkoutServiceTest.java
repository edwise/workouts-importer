package com.edwise.batch.workoutsimporter.service;

import com.edwise.batch.workoutsimporter.model.Workout;
import com.edwise.batch.workoutsimporter.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    private static final LocalDateTime END_TIME = LocalDateTime.of(2025, 3, 10, 7, 59);

    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private WorkoutService workoutService;

    @Test
    void givenExistingWorkout_whenGetLastEndTime_ShouldReturnLastEndTime() {
        Workout workout = new Workout();
        workout.setEndTime(END_TIME);
        when(workoutRepository.findTopByOrderByEndTimeDesc()).thenReturn(Optional.of(workout));

        Optional<LocalDateTime> result = workoutService.getLastEndTime();

        assertThat(result)
                .isPresent()
                .contains(END_TIME);
        verify(workoutRepository, times(1)).findTopByOrderByEndTimeDesc();
    }

    @Test
    void givenNotExistingWorkout_whenGetLastEndTime_ShouldReturnEmpty() {
        when(workoutRepository.findTopByOrderByEndTimeDesc()).thenReturn(Optional.empty());

        Optional<LocalDateTime> result = workoutService.getLastEndTime();

        assertThat(result).isEmpty();
        verify(workoutRepository, times(1)).findTopByOrderByEndTimeDesc();
    }
}