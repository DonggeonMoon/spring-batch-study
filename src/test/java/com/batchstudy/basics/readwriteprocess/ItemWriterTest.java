package com.batchstudy.basics.readwriteprocess;

import com.batchstudy.testutils.CourseUtilBatchTestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@SpringBootTest(classes = {ItemWriterTest.TestConfig.class, CourseUtilBatchTestConfig.class})
public class ItemWriterTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void runJob() throws Exception {
        JobParameters emptyJobParameters = new JobParametersBuilder()
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(emptyJobParameters);
        Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @Configuration
    @EnableBatchProcessing
    static class TestConfig {
        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private StepBuilderFactory stepBuilderFactory;

        @Bean
        public Job job() {
            return jobBuilderFactory.get("myJob")
                    .start(readerStep())
                    .build();
        }

        @Bean
        public Step readerStep() {
            return stepBuilderFactory.get("readJsonStep")
                    .<InputAndOutputData, InputAndOutputData>chunk(1).reader(reader())
                    .processor(new PassThroughItemProcessor<>())
                    .writer(writer())
                    .build();
        }

        @Bean
        public JsonItemReader<InputAndOutputData> reader() {
            File file;
            try {
                file = ResourceUtils.getFile("classpath:files/_A/input.json");
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException(e);
            }

            return new JsonItemReaderBuilder<InputAndOutputData>()
                    .jsonObjectReader(new JacksonJsonObjectReader<>(InputAndOutputData.class))
                    .resource(new FileSystemResource(file))
                    .name("JsonItemReader")
                    .build();
        }

        @Bean
        public JsonFileItemWriter<InputAndOutputData> writer() {
            FileSystemResource outputResource = new FileSystemResource("output/output.json");
            return new JsonFileItemWriterBuilder<InputAndOutputData>()
                    .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                    .resource(outputResource)
                    .name("jsonItemWriter")
                    .build();
        }

        public static class InputAndOutputData {
            public String value;

            @Override
            public String toString() {
                return "Input{" +
                        "value='" + value + '\'' +
                        '}';
            }
        }
    }
}
