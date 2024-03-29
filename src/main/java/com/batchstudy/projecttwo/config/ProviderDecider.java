package com.batchstudy.projecttwo.config;

import com.batchstudy.projecttwo.model.Airport;
import com.batchstudy.projecttwo.service.AirlineSearchService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.Set;

public class ProviderDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        JobParameters jobParameters = jobExecution.getJobParameters();
        String departureParameter = jobParameters.getString(AirlineSearchService.JOB_KEY_DEPARTURE_AIRPORT);
        Airport departure = Airport.valueOf(departureParameter);

        String arrivalParameter = jobParameters.getString(AirlineSearchService.JOB_KEY_ARRIVAL_AIRPORT);
        Airport arrival = Airport.valueOf(arrivalParameter);

        Set<Airport> flightAirports = Set.of(departure, arrival);
        if (flightAirports.contains(Airport.DUBAI) && flightAirports.contains(Airport.AMSTERDAM)) {
            return new FlowExecutionStatus("DUBAI_AMSTERDAM_OFFER_ONLY");
        } else if (flightAirports.contains(Airport.NEWYORK) && flightAirports.contains(Airport.AMSTERDAM)) {
            return new FlowExecutionStatus("NEW_YORK_AMSTERDAM_OFFER_INCLUDED");
        } else {
            return new FlowExecutionStatus("DEFAULT_SEARCH");
        }
    }
}
