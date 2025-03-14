package com.edwise.batch.workoutsimporter.batch;

import com.edwise.batch.workoutsimporter.batch.listener.JobCompletionNotificationListener;
import com.edwise.batch.workoutsimporter.model.Workout;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

@Configuration
public class BatchConfiguration {

    @Value("${workout-config.fileNamePattern}")
    private String workoutFileNamePattern;

    @Bean
    public FlatFileItemReader<Workout> reader() {
        return new FlatFileItemReaderBuilder<Workout>()
                .name("workoutReader")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Workout.class);
                    setCustomEditors(Collections.singletonMap(
                            LocalDateTime.class,
                            new LocalDateTimeEditor()
                    ));
                }})
                .linesToSkip(1)
                .delimited()
                .names(Workout.CSV_FIELDS)
                .build();
    }

    @Bean
    public MultiResourceItemReader<Workout> multiResourceReader(FlatFileItemReader<Workout> reader) {
        return new MultiResourceItemReaderBuilder<Workout>()
                .name("multiWorkoutReader")
                .resources(loadWorkoutFiles())
                .delegate(reader)
                .build();
    }

    private Resource[] loadWorkoutFiles() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources;
        try {
            resources = resolver.getResources("classpath:" + workoutFileNamePattern);
            Arrays.sort(resources, Comparator.comparing(Resource::getFilename));
        } catch (IOException e) {
            throw new RuntimeException("Error loading CSV files", e);
        }
        return resources;
    }

    @Bean
    public Job importJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importWorkoutsJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      MultiResourceItemReader<Workout> reader,
                      ItemProcessor<Workout, Workout> processor,
                      ItemWriter<Workout> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Workout, Workout>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}