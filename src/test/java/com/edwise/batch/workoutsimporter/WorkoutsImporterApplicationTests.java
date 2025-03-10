package com.edwise.batch.workoutsimporter;

import com.edwise.batch.workoutsimporter.model.Workout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest
class WorkoutsImporterApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private Job workoutsImporterJob;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeAll
    static void setupContainer() {
        mongoDBContainer.start();
    }

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(workoutsImporterJob);
    }

    @AfterEach
    void tearDown() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void shouldEndCompletedWorkoutImporterJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        assertThatWorkoutsAreInTheDB();
    }

    private void assertThatWorkoutsAreInTheDB() {
        List<Workout> workouts = mongoTemplate.findAll(Workout.class);

        assertThat(workouts)
                .isNotEmpty()
                .hasSize(6);
        assertThat(workouts)
                .anySatisfy(workout -> {
                    assertThat(workout.getTitle()).isEqualTo("Rutina Test");
                    assertThat(workout.getExerciseTitle()).isEqualTo("Bench Press (Barbell)");
                    assertThat(workout.getWeightKg()).isEqualTo(40.0);
                    assertThat(workout.getReps()).isEqualTo(10);
                });
    }
}
