package com.batchstudy.projectone.config;

import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.job.DefaultJobParametersValidator;

public class AnonymizeJobParameterValidator extends DefaultJobParametersValidator {
    private static final String[] REQUIRED_KEYS = {
            AnonymizeJobParameterKeys.INPUT_PATH,
            AnonymizeJobParameterKeys.OUTPUT_PATH,
            AnonymizeJobParameterKeys.UPLOAD_PATH,
            AnonymizeJobParameterKeys.ERROR_PATH
    };
    private static final String[] OPTIONAL_KEYS = {
            AnonymizeJobParameterKeys.ANONYMIZED_DATA
    };

    public AnonymizeJobParameterValidator() {
        super(REQUIRED_KEYS, OPTIONAL_KEYS);
    }

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        super.validate(parameters);

        String inputPath = parameters.getString(AnonymizeJobParameterKeys.INPUT_PATH);
        String extension = FilenameUtils.getExtension(inputPath);
        if (extension == null || !extension.equals("json")) {
            throw new JobParametersInvalidException("Input file must be in JSON format");

        }

        String anonymize = parameters.getString(AnonymizeJobParameterKeys.ANONYMIZED_DATA);
        if (anonymize != null) {
            if (!anonymize.equals("true") && !anonymize.equals("false")) {
                throw new JobParametersInvalidException("ANONMIZE_DATA must be either true or false");
            }
        }
    }
}
