package ru.ifmo.pharmacies.analysis.config.scheduling;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
public class QuartzSubmitJobs {
    private static final String CRON_EVERY_MIDNIGHT = "0 0 0 * * ? *";
    private static final String CRON_EVERY_MINUTE = "* 0/1 * * * ? *";

    @Bean(name = "medicinesParserJobDetail")
    public JobDetailFactoryBean jobMedicinesParser() {
        return QuartzConfig.createJobDetail(MedicinesParserJob.class, "Medicines Parser Job");
    }

    @Bean(name = "medicinesParserTrigger")
    public CronTriggerFactoryBean triggerMedicicnesParser(@Qualifier("medicinesParserJobDetail") JobDetail jobDetail) {
        return QuartzConfig.createCronTrigger(jobDetail, CRON_EVERY_MIDNIGHT, "Medicines Parser Trigger");
    }
}
