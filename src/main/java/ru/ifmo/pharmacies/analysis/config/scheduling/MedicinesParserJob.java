package ru.ifmo.pharmacies.analysis.config.scheduling;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ifmo.pharmacies.analysis.parser.MedicinesParser;

@Component
@DisallowConcurrentExecution
public class MedicinesParserJob implements Job {

    @Autowired
    MedicinesParser medicinesParser;

    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("Job ** {" + context.getJobDetail().getKey().getName() + "} ** starting @ {" + context.getFireTime() + "}" );

        medicinesParser.parse();

        System.out.println("Job ** {" + context.getJobDetail().getKey().getName() + "} ** completed.  Next job scheduled @ {" + context.getNextFireTime() + "}");
    }

}
