package com.batchstudy.basics.readwriteprocess;

import com.batchstudy.testutils.CourseUtilBatchTestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@SpringBootTest(classes = {ProcessorTest.TestConfig.class, CourseUtilBatchTestConfig.class})
public class ProcessorTest {
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
                    .<InputData, OutputData>chunk(1)
                    .reader(reader())
                    .processor(processor())
                    .writer(writer())
                    .build();
        }

        @Bean
        public JsonItemReader<InputData> reader() {
            File file;

            try {
                file = ResourceUtils.getFile("classpath:files/_A/input.json");
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException(e);
            }

            return new JsonItemReaderBuilder<InputData>()
                    .jsonObjectReader(new JacksonJsonObjectReader<>(InputData.class))
                    .resource(new FileSystemResource(file))
                    .name("JsonItemReader")
                    .build();
        }

        private ItemProcessor<InputData, OutputData> processor() {
            return inputData -> {
                OutputData outputData = new OutputData();
                outputData.outputValue = inputData.value.toUpperCase();
                return outputData;
            };
        }

        @Bean
        public JsonFileItemWriter<OutputData> writer() {
            Resource outputResource = new FileSystemResource("output/output.json");
            return new JsonFileItemWriterBuilder<OutputData>()
                    .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                    .resource(outputResource)
                    .name("jsonItemWriter")
                    .build();
        }

        public static class InputData {
            public String value;

            @Override
            public String toString() {
                return "InputData{" +
                        "value='" + value + '\'' +
                        '}';
            }
        }

        public static class OutputData {
            public String outputValue;

            @Override
            public String toString() {
                return "OutputData{" +
                        "outputValue='" + outputValue + '\'' +
                        '}';
            }
        }
    }
}
