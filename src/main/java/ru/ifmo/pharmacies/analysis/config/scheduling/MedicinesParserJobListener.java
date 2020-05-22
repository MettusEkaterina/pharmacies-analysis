package ru.ifmo.pharmacies.analysis.config.scheduling;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import ru.ifmo.pharmacies.analysis.repository.ProductRepository;

import java.util.Date;

public class MedicinesParserJobListener implements JobListener {

    private Date startJobDate;

    @Override
    public String getName() {
        return "MedicinesParserJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        startJobDate = new Date();
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException arg1) {

        ProductRepository.lastUpdateDate = startJobDate;
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {}
}
