/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.gestatti.scheduler.pubblicazione.albo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import it.linksmt.assatti.cooperation.service.albo.JobRecuperoInfoAlboService;
import it.linksmt.assatti.datalayer.domain.JobPubblicazione;
import it.linksmt.assatti.datalayer.domain.StatoJob;
import it.linksmt.assatti.datalayer.domain.util.ISchedulerGestatti;
import it.linksmt.assatti.service.JobPubblicazioneService;

/**
 * Scheduler per il recupero delle info di pubblicazione dal sistema esterno di albo pretorio.
 *
 * @author Daniele Mazzotta
 *
 */
@Component
public class JobRecuperoInfoAlboScheduler implements ISchedulerGestatti{

	private final Logger log = LoggerFactory.getLogger(JobRecuperoInfoAlboScheduler.class);

	@Autowired
	private JobRecuperoInfoAlboService jobRecuperoInfoAlboService;

	@Autowired
	private JobPubblicazioneService jobPubblicazioneService;
	
	private static final String NOME_SCHEDULER = "recuperoinfo_albo";
	
	private static final String ENABLED_PROPERTY_NAME = "scheduler.job.pubblicazione.recuperoInfoAlbo.enable";

	@Override
	public String getNomeScheduler() {
		return NOME_SCHEDULER;
	}
	
	@Override
	public String getEnabledPropertyName() {
		return ENABLED_PROPERTY_NAME;
	}
	
	@Scheduled(cron = "${scheduler.job.pubblicazione.recuperoInfoAlbo.cron}")
	public void aggiornaInfoPubblicazioneAlboJob() {

		log.info("Start job recupero info su componenente albo pretorio");

		try {
			Iterable<JobPubblicazione> jobs = jobPubblicazioneService.getJobsWatingInfo();
			List<JobPubblicazione> jobList = new ArrayList<JobPubblicazione>();
			for(JobPubblicazione job : jobs) {
				if (StatoJob.IN_PROGRESS_WAITING.toString().equalsIgnoreCase(job.getStato().toString())) {
					DateTime lastUpdate = job.getLastModifiedDate();
					if (lastUpdate == null) {
						lastUpdate = job.getCreatedDate();
					}

					// Passata oltre 1 ora ritenta la procedura
					Calendar check = Calendar.getInstance();
					check.add(Calendar.HOUR_OF_DAY, -1);

					if (lastUpdate.getMillis() > check.getTimeInMillis()) {
						continue;
					}
				}
				jobList.add(job);
			}
			if(jobList!=null && jobList.size() > 0) {
				jobList = jobPubblicazioneService.inProgressForWaitingTask(Lists.newArrayList(jobs));
				jobRecuperoInfoAlboService.aggiornaInfoPubblicazioneAlbo(jobList);
			}
		}
		catch (Exception e) {
			log.error("jobRecuperoInfoAlboService :: " + e.getMessage(), e);
		}

		log.info("Job recupero info su componenente albo pretorio terminato.");

	}
}
