/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.albo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import it.linksmt.assatti.cooperation.albo.dto.AttoTxtDto;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.JobPubblicazione;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.utility.configuration.SchedulerProps;

/**
 * @author Gianluca Pindinelli
 *
 */
@Component
public class JobPubblicazioneToAttoTxtConverter {

	private static final String DEFAULT_ENTE = "XSEGEN";
	private static final String DATE_FORMAT = "yyyyMMdd";
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

	public AttoTxtDto convert(JobPubblicazione jobPubblicazione) throws ServiceException {

		AttoTxtDto attoTxtDto;
		try {
			attoTxtDto = null;
			Atto atto = jobPubblicazione.getAtto();
			if (atto != null) {
				attoTxtDto = new AttoTxtDto();
				attoTxtDto.setEnteRichiedente(StringUtils.rightPad(DEFAULT_ENTE, AttoTxtDto.ENTE_RICHIEDENTE_MAX_LENGHT));
				attoTxtDto.setEnteMittente(StringUtils.rightPad(DEFAULT_ENTE, AttoTxtDto.ENTE_EMITTENTE_MAX_LENGHT));

				LocalDate inizioPubblicazioneAlbo = atto.getDataInizioPubblicazionePresunta();

				String dataInizioPubblicazioneString = simpleDateFormat.format(inizioPubblicazioneAlbo.toDate());
				attoTxtDto.setDataInizioPubblicazione(StringUtils.rightPad(dataInizioPubblicazioneString, AttoTxtDto.DATA_INIZIO_PUBBLICAZIONE_MAX_LENGHT));

				LocalDate finePubblicazioneAlbo = atto.getDataFinePubblicazionePresunta();

				if (finePubblicazioneAlbo != null) {
					String dataFinePubblicazioneString = simpleDateFormat.format(finePubblicazioneAlbo.toDate());
					attoTxtDto.setDataFinePubblicazione(StringUtils.rightPad(dataFinePubblicazioneString, AttoTxtDto.DATA_FINE_PUBBLICAZIONE_MAX_LENGHT));
				}

				String tipoRegistroCronologico = SchedulerProps.getProperty("scheduler.job.pubblicazione.albo.mapping.registroCronologico." + atto.getTipoAtto().getCodice());
				String numeroAtto = atto.getDataNumerazione().getYear() + "/" + tipoRegistroCronologico + "/" + atto.getNumeroAdozione();
				attoTxtDto.setNumeroAtto(StringUtils.rightPad(numeroAtto, AttoTxtDto.NUMERO_ATTO_MAX_LENGHT));

				Date dataAtto = null;
				LocalDate dataAdozione = atto.getDataAdozione();
				if (dataAdozione != null) {
					dataAtto = dataAdozione.toDate();
				}
				if (dataAtto == null) {
					LocalDate dataEsecutivita = atto.getDataEsecutivita();
					if (dataEsecutivita != null) {
						dataAtto = dataEsecutivita.toDate();
					}
				}

				String dataAdozioneString = simpleDateFormat.format(dataAtto);
				attoTxtDto.setDataAtto(StringUtils.rightPad(dataAdozioneString, AttoTxtDto.DATA_ATTO_MAX_LENGHT));

				String tipoAtto = SchedulerProps.getProperty("scheduler.job.pubblicazione.albo.mapping.tipoatto." + atto.getTipoAtto().getCodice());
				if (tipoAtto == null) {
					throw new ServiceException("Impossibile individuare la tipologia di atto per l'atto con ID : " + atto.getId());
				}
				attoTxtDto.setTipoAtto(StringUtils.rightPad(tipoAtto, AttoTxtDto.TIPO_ATTO_MAX_LENGHT));
				String oggetto = atto.getOggetto();
				String oggettoSenzaACapo = oggetto.replaceAll("[\\t\\n\\r]+"," ");
				if(oggettoSenzaACapo.length()>AttoTxtDto.OGGETTO_MAX_LENGHT) {
					oggettoSenzaACapo = oggettoSenzaACapo.substring(0, AttoTxtDto.OGGETTO_MAX_LENGHT);
				}
				attoTxtDto.setOggetto(StringUtils.rightPad(oggettoSenzaACapo, AttoTxtDto.OGGETTO_MAX_LENGHT));
			}
		}
		catch (Exception e) {
			throw new ServiceException(e);
		}

		return attoTxtDto;

	}
}
