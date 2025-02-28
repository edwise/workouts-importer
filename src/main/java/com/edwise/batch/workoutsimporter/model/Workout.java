package com.edwise.batch.workoutsimporter.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
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
