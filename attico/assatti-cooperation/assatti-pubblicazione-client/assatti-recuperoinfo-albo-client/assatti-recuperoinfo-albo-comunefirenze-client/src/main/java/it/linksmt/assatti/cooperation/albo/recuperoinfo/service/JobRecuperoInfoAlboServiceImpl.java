package it.linksmt.assatti.cooperation.albo.recuperoinfo.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import it.linksmt.assatti.cooperation.albo.recuperoinfo.dto.ResponseInfoDto;
import it.linksmt.assatti.cooperation.albo.recuperoinfo.exception.RecuperoInfoAlboException;
import it.linksmt.assatti.cooperation.albo.recuperoinfo.exception.RestClientException;
import it.linksmt.assatti.cooperation.albo.recuperoinfo.util.GeneraPathUtil;
import it.linksmt.assatti.cooperation.albo.recuperoinfo.util.RestClientService;
import it.linksmt.assatti.cooperation.dto.RelataAlboDto;
import it.linksmt.assatti.cooperation.service.albo.JobRecuperoInfoAlboService;
import it.linksmt.assatti.datalayer.domain.JobPubblicazione;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.JobPubblicazioneService;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.SchedulerProps;

@Service
public class JobRecuperoInfoAlboServiceImpl implements JobRecuperoInfoAlboService{
	
	private final Logger log = LoggerFactory.getLogger(JobRecuperoInfoAlboServiceImpl.class);
	
	@Autowired
	private AttoService attoService;
	
	@Autowired
	private RestClientService restClientService;
	
	@Autowired
	private JobPubblicazioneService jobPubblicazioneService;
	
	private static final Gson gson = new Gson();
	
	private static String ALBO_INFO_ENDPOINT;
	
	private static String ALBO_INFO_LOGIN_URL;
	
	private static String ALBO_INFO_WS_USER;
	
	private static String ALBO_INFO_WS_PASSWORD;
	
	private static String ALBO_INFO_MOCK_DETAIL_URL;
	
	private static String ALBO_INFO_MOCK_ENABLED;
	
	private static final DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	private void init() {
		ALBO_INFO_ENDPOINT = SchedulerProps.getProperty("scheduler.job.pubblicazione.recuperoInfoAlbo.endpoint");
		ALBO_INFO_LOGIN_URL = SchedulerProps.getProperty("scheduler.job.pubblicazione.recuperoInfoAlbo.login");
		ALBO_INFO_WS_USER = SchedulerProps.getProperty("scheduler.job.pubblicazione.recuperoInfoAlbo.user");
		ALBO_INFO_WS_PASSWORD = SchedulerProps.getProperty("scheduler.job.pubblicazione.recuperoInfoAlbo.password");
		
		ALBO_INFO_MOCK_DETAIL_URL = SchedulerProps.getProperty("scheduler.job.pubblicazione.recuperoInfoAlbo.mock.detailurl");
		ALBO_INFO_MOCK_ENABLED = SchedulerProps.getProperty("scheduler.job.pubblicazione.recuperoInfoAlbo.mock.enabled");
	}
	
	public void aggiornaInfoPubblicazioneAlbo(final Iterable<JobPubblicazione> jobs) {
		init();
		
		for(JobPubblicazione job : jobs) {
			try {
				try {
					String operationPath = ALBO_INFO_ENDPOINT + GeneraPathUtil.generaInfoPath(job.getAtto());
					if(ALBO_INFO_MOCK_ENABLED!=null && ALBO_INFO_MOCK_ENABLED.equalsIgnoreCase("true") && ALBO_INFO_MOCK_DETAIL_URL!=null && ALBO_INFO_MOCK_DETAIL_URL.trim().length() > 0) {
						operationPath = ALBO_INFO_MOCK_DETAIL_URL;
					}
					if(operationPath!=null && !operationPath.isEmpty()) {
						String json = restClientService.getJsonInfo(operationPath, ALBO_INFO_LOGIN_URL, ALBO_INFO_WS_USER, ALBO_INFO_WS_PASSWORD);
						ResponseInfoDto info = gson.fromJson(json, ResponseInfoDto.class);
						if(info!=null) {
							log.debug("service return info for atto " + job.getAtto().getId() + ": " + info);
							if(info.getSuccess()!=null && info.getSuccess()) {
								LocalDate inizioPubblicazione = null;
								if(info.getDatainizio()!=null && !info.getDatainizio().isEmpty()) {
									Date inizioDate = df.parse(info.getDatainizio());
									inizioPubblicazione = LocalDate.fromDateFields(inizioDate);
								}
								LocalDate finePubblicazione = null;
								if(info.getDatafine()!=null && !info.getDatafine().isEmpty()) {
									Date fineDate = df.parse(info.getDatafine());
									finePubblicazione = LocalDate.fromDateFields(fineDate);
								}
								RelataAlboDto relata= null;
								try {
									if(info.getUrlrelata()!=null && !info.getUrlrelata().isEmpty()) {
										log.debug("Trying to download relata for atto " + job.getAtto().getId() + " from: " + info.getUrlrelata());
										relata = restClientService.downloadRelata(info.getUrlrelata(), ALBO_INFO_LOGIN_URL, ALBO_INFO_WS_USER, ALBO_INFO_WS_PASSWORD);
									}
								}
								catch(RestClientException e) {
									log.error("Errore nel recupero della relata. Error message: " + e.getMessage());
								}
								
								Long progPubblicazione = null;
								try {
									progPubblicazione = Long.parseLong(StringUtil.trimStr(info.getProgressivo()));
								}
								catch (Exception e) {
									log.error("Errore nel recupero del progressivo di pubblicazione. Error message: " + e.getMessage(), e);
								}
								
								attoService.aggiornaPubblicazioneAlbo(job.getId(), job.getAtto().getId(), 
										inizioPubblicazione, finePubblicazione, progPubblicazione, relata);
							}
							else {
								if(info.getMessage()!=null && !info.getMessage().isEmpty()) {
									throw new RecuperoInfoAlboException("service return success false with message " + info.getMessage());
								}
								else {
									throw new RecuperoInfoAlboException("service return success false");
								}
							}
						}
						else {
							throw new RecuperoInfoAlboException("service return null info");
						}
					}
				}catch(RecuperoInfoAlboException e) {
					String error = "";
					if(job.getAtto()!=null && job.getAtto().getId()!=null) {
						error = "Errore in fase di recupero info albo su atto id " + job.getAtto().getId() + " - Errore message: " + e.getMessage();
						log.error(error);
					}else {
						error = "Errore in fase di recupero info albo. Atto null. Errore message: " + e.getMessage();
						log.error(error);
					}
					if(job!=null) {
						jobPubblicazioneService.errorInWaitingTask(job, error);
					}
				}catch(Exception e) {
					throw new RecuperoInfoAlboException(e);
				}
			}catch(RecuperoInfoAlboException e) {
				String error = "";
				if(job.getAtto()!=null && job.getAtto().getId()!=null) {
					error = "Errore in fase di recupero info albo su atto id " + job.getAtto().getId() + " - Errore message: " + e.getMessage();
					log.error(error);
				}else {
					error = "Errore in fase di recupero info albo. Atto null. Errore message: " + e.getMessage();
					log.error(error);
				}
				if(job!=null) {
					jobPubblicazioneService.errorInWaitingTask(job, error);
				}
			}
		}
	}
	
}