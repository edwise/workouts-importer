package com.edwise.batch.workoutsimporter.repository;

import com.edwise.batch.workoutsimporter.model.Workout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkoutRepository extends MongoRepository<Workout, String> {

    Optional<Workout> findTopByOrderByEndTimeDesc();
}
