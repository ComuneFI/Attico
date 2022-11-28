package it.linksmt.assatti.cooperation.albo.recuperoinfo.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDate;

import it.linksmt.assatti.cooperation.albo.recuperoinfo.exception.GenerazionePathException;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.utility.configuration.SchedulerProps;

public class GeneraPathUtil{
	
	private static final DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	public static String generaInfoPath(Atto atto) throws GenerazionePathException{
		String path = null;
		if(atto!=null) {
			String enteEmittente = SchedulerProps.getProperty("scheduler.job.pubblicazione.recuperoInfoAlbo.enteEmittente");
			if(enteEmittente==null || enteEmittente.isEmpty()) {
				throw new GenerazionePathException("Impossibile individuare il campo ente emittente. " + atto.getId());
			}
			
			String tipoAtto = SchedulerProps.getProperty("scheduler.job.pubblicazione.albo.mapping.tipoatto." + atto.getTipoAtto().getCodice());
			if (tipoAtto == null) {
				throw new GenerazionePathException("Impossibile individuare la tipologia di atto per l'atto con ID : " + atto.getId());
			}
			
			Date data = null;
			LocalDate dataAdozione = atto.getDataAdozione();
			if (dataAdozione != null) {
				data = dataAdozione.toDate();
			}
			if (data == null) {
				LocalDate dataEsecutivita = atto.getDataEsecutivita();
				if (dataEsecutivita != null) {
					data = dataEsecutivita.toDate();
				}
			}
			
			if(data==null) {
				throw new GenerazionePathException("Impossibile individuare la data per l'atto con id " + atto.getId());
			}
			
			String dataStr = df.format(data);
			
			String tipoRegistroCronologico = SchedulerProps.getProperty("scheduler.job.pubblicazione.albo.mapping.registroCronologico." + atto.getTipoAtto().getCodice());
			
			if(tipoRegistroCronologico==null || tipoRegistroCronologico.isEmpty()) {
				throw new GenerazionePathException("Impossibile individuare tipoRegistroCronologico per il tipo di atto " + atto.getTipoAtto().getCodice());
			}
			
			String numero = atto.getDataAdozione().getYear() + "/" + tipoRegistroCronologico + "/" + atto.getNumeroAdozione();
			
			path = "?EnteEmittente=" + enteEmittente + "&Tipo=" + tipoAtto + "&Data=" + dataStr + "&Numero=" + numero;
		}
		return path;
	}
}
