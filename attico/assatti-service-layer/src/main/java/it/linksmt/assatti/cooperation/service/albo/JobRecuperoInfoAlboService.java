package it.linksmt.assatti.cooperation.service.albo;

import it.linksmt.assatti.datalayer.domain.JobPubblicazione;

public interface JobRecuperoInfoAlboService{
	public void aggiornaInfoPubblicazioneAlbo(final Iterable<JobPubblicazione> jobs);
	
}