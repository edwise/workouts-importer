package com.edwise.batch.workoutsimporter.batch;

import com.edwise.batch.workoutsimporter.model.Workout;
import com.edwise.batch.workoutsimporter.repository.WorkoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkoutItemWriter implements ItemWriter<Workout> {

    private static final Logger log = LoggerFactory.getLogger(WorkoutItemWriter.class);

    private final WorkoutRepository workoutRepository;

    @Autowired
    public WorkoutItemWriter(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    @Override
    public void write(Chunk<? extends Workout> workouts) {
        workouts.forEach(workout -> {
            boolean exists = workoutRepository.existsByGenId(workout.getGenId());
            if (exists) {
                log.error("Existing workout with genId={}. startTime={}, endTime={}, exerciseTitle={}, setIndex={} already " +
                          "exists in the database. It will not be inserted.",
                          workout.getGenId(), workout.getStartTime(), workout.getEndTime(), workout.getExerciseTitle(),
                          workout.getSetIndex());
            } else {
                workoutRepository.save(workout);
                log.info("Workout saved: {}", workout);
            }
        });
    }
}
