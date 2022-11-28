/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.gestatti.scheduler.pubblicazione.albo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.linksmt.assatti.cooperation.service.albo.JobPubblicazioneAlboService;
import it.linksmt.assatti.datalayer.domain.util.ISchedulerGestatti;
import it.linksmt.assatti.service.exception.ServiceException;

/**
 * Scheduler per l'invio degli atti al sistema esterno di albo pretorio.
 *
 * @author Gianluca Pindinelli
 *
 */
@Component
public class JobPubblicazioneAlboScheduler implements ISchedulerGestatti{

	private final Logger log = LoggerFactory.getLogger(JobPubblicazioneAlboScheduler.class);

	@Autowired
	private JobPubblicazioneAlboService jobPubblicazioneAlboService;

	private static final String NOME_SCHEDULER = "pubblicazione_albo";
	
	private static final String ENABLED_PROPERTY_NAME = "scheduler.job.pubblicazione.albo.enable";

	@Override
	public String getNomeScheduler() {
		return NOME_SCHEDULER;
	}
	
	@Override
	public String getEnabledPropertyName() {
		return ENABLED_PROPERTY_NAME;
	}
	
	@Scheduled(cron = "${scheduler.job.pubblicazione.albo.cron}")
	public void sendAttiToAlboJob() {
		log.info("Start job pubblicazione atti su componenente albo pretorio");

		try {
			jobPubblicazioneAlboService.sendAtti();
		}
		catch (ServiceException e) {
			log.error("sendAttiToAlboJob :: " + e.getMessage(), e);
		}

		log.info("Job pubblicazione atti su componenente albo pretorio terminato.");
	}

}
