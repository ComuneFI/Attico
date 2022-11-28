/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.albo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.cooperation.albo.util.JobPubblicazioneToAttoTxtConverter;
import it.linksmt.assatti.cooperation.dto.AllegatoDto;
import it.linksmt.assatti.cooperation.service.AllegatoService;
import it.linksmt.assatti.cooperation.service.albo.JobPubblicazioneAlboService;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.EventoEnum;
import it.linksmt.assatti.datalayer.domain.JobPubblicazione;
import it.linksmt.assatti.datalayer.domain.StatoAttoEnum;
import it.linksmt.assatti.datalayer.domain.StatoJob;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.EventoService;
import it.linksmt.assatti.service.JobPubblicazioneService;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.SchedulerProps;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * @author Gianluca Pindinelli
 *
 */
@Service
public class JobPubblicazioneAlboServiceImpl implements JobPubblicazioneAlboService {

	private static final String TXT_FOLDER_PATH = SchedulerProps.getProperty("scheduler.job.pubblicazione.albo.txt.folder.path");
	private static final String TXT_SUFFIX = ".txt";

	private final Logger log = LoggerFactory.getLogger(JobPubblicazioneAlboServiceImpl.class);

	@Inject
	private JobPubblicazioneService jobPubblicazioneService;

	@Autowired
	private AllegatoService allegatoService;

	@Inject
	private AttoService attoService;

	@Autowired
	private JobPubblicazioneToAttoTxtConverter jobPubblicazioneToAttoTxtConverter;
	
	@Autowired
	private EventoService eventoService;
	
	@Inject
	private AttoRepository attoRepository;

	/*
	 * (non-Javadoc)
	 *
	 * @see it.linksmt.assatti.cooperation.service.JobPubblicazioneAlboService#sendAtti()
	 */
	@Override
	public void sendAtti() throws ServiceException {

		try {
			List<JobPubblicazione> lista = jobPubblicazioneService.getDaPubblicare();
			if (lista != null) {
				for (JobPubblicazione job : lista) {
					try {
						// Per i job "IN_PROGRESS" verifico l'ultimo tentativo per capire se va
						// ritentato
						if (StatoJob.IN_PROGRESS.toString().equalsIgnoreCase(job.getStato().toString())) {
							DateTime lastUpdate = job.getLastModifiedDate();
							if (lastUpdate == null) {
								lastUpdate = job.getCreatedDate();
							}

							// Passata oltre 1 ora ritenta la pubblicazione
							Calendar check = Calendar.getInstance();
							check.add(Calendar.HOUR_OF_DAY, -1);

							if (lastUpdate.getMillis() > check.getTimeInMillis()) {
								continue;
							}
						}

						jobPubblicazioneService.inProgress(job.getId());

						managePubblicazione(job);
						
						jobPubblicazioneService.waitingInfo(job.getId());
						eventoService.saveEvento(EventoEnum.EVENTO_ATTO_PUBBLICATO_ALBO.getDescrizione(), job.getAtto());
						
						Atto attoDb = attoRepository.findOne(job.getAtto().getId());
						attoDb.setStato(StatoAttoEnum.PUBBLICATO.getDescrizione());
						attoRepository.save(attoDb);
						
					}
					catch (Exception e) {
						log.error("Errore durante la pubblicazione :: id : " + job.getId(), e);
						jobPubblicazioneService.error(job.getId(), e.getMessage());
					}
				}
			}
		}
		catch (Exception e) {
			log.error("Errore in effettuaPubblicazione - Error: " + e.getMessage(), e);
		}
	}

	@Transactional
	protected void managePubblicazione(JobPubblicazione job) throws Exception {

		Atto atto = attoService.findOneWithSchedeObbligoAndDocuments(job.getAtto().getId());

		invioPubblicazione(job, atto);
	}

	private void invioPubblicazione(JobPubblicazione job, Atto atto) throws Exception {

		log.info("Invio ad Albo - Atto: " + atto.getCodiceCifra());
		String tipoRegistroCronologico = SchedulerProps.getProperty("scheduler.job.pubblicazione.albo.mapping.registroCronologico." + atto.getTipoAtto().getCodice());

		String nomeFile = "";
		if(atto.getDataAdozione() != null && atto.getNumeroAdozione() != null) {
			nomeFile = atto.getDataAdozione().getYear() + "-" + tipoRegistroCronologico + "-" + atto.getNumeroAdozione();

			if (atto.getRiservato() != null && atto.getRiservato() == false) {
				// Salvataggio allegati in folder
				List<AllegatoDto> allegati = allegatoService.getAllegati(atto, false);
				if (allegati != null) {
					for (AllegatoDto allegatoDto : allegati) {
						// Aggiornamento nome file
						allegatoDto.setNome(nomeFile + "_" + allegatoDto.getNome());
						saveFileIntoFolder(allegatoDto);
					}
				}
				
				// Salvataggio documento principale in folder
				AllegatoDto documenoPrincipale = allegatoService.getDocumenoPrincipaleAsAllegatoDto(atto, false);
				String documentoPrincipaleExtension = FilenameUtils.getExtension(documenoPrincipale.getNome());
				documenoPrincipale.setNome(nomeFile + "." + documentoPrincipaleExtension);
				saveFileIntoFolder(documenoPrincipale);
				
				if(WebApplicationProps.getPropertyList(ConfigPropNames.TIPIATTO_DOC_COMPLETO_LIST, new ArrayList<Object>()).contains(atto.getTipoAtto().getCodice())) {
					documenoPrincipale = allegatoService.getDocumenoPrincipaleAsAllegatoDto(atto, true);
					if(documenoPrincipale!=null) {
						documenoPrincipale.setNome(nomeFile + "_Provvedimento_Completo" + "." + documentoPrincipaleExtension);
						saveFileIntoFolder(documenoPrincipale);
					}
				}
			}

			// Salvataggio TXT descrittore in folder
			String txtFileName = nomeFile + TXT_SUFFIX;
			String txtContent = jobPubblicazioneToAttoTxtConverter.convert(job).toString();
			File tmpFolder = new File(TXT_FOLDER_PATH);
			if(!tmpFolder.exists() || !tmpFolder.isDirectory()) {
				tmpFolder.mkdirs();
			}
			PrintWriter writer = new PrintWriter(TXT_FOLDER_PATH + "/" + txtFileName, "UTF-8");
			writer.println(txtContent);
			writer.close();
			
		} else {
			throw new Exception("Errore in effettuaPubblicazione - Error: Data adozione e-o numero adozione non presenti;");
		}

	}

	/**
	 * Salva l'allegato nella folder prevista.
	 *
	 * @param allegatoDto
	 * @throws IOException
	 */
	private void saveFileIntoFolder(AllegatoDto allegatoDto) throws IOException {
		byte data[] = allegatoDto.getContenuto();
		FileOutputStream out = null;
		try {
			File tmpFolder = new File(TXT_FOLDER_PATH);
			if(!tmpFolder.exists() || !tmpFolder.isDirectory()) {
				tmpFolder.mkdirs();
			}
			out = new FileOutputStream(TXT_FOLDER_PATH + "/" + allegatoDto.getNome());
			out.write(data);
		}
		catch (Exception e) {
			log.error("saveFileIntoFolder :: " + e.getMessage(), e);
		}
		finally {
			if (out != null) {
				out.close();
			}
		}
	}

}
