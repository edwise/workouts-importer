package com.edwise.batch.workoutsimporter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workouts")
public class Workout {

    public static final String[] FIELDS = {"title", "startTime", "endTime", "description", "exerciseTitle",
            "superSetId", "exerciseNotes", "setIndex", "setType", "weightKg", "reps", "distanceKm", "durationSeconds", "rpe"};

    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private String exerciseTitle;
    private Integer superSetId;
    private String exerciseNotes;
    private Integer setIndex;
    private String setType;
    private Double weightKg;
    private Integer reps;
    private Double distanceKm;
    private Integer durationSeconds;
    private Double rpe;

}
