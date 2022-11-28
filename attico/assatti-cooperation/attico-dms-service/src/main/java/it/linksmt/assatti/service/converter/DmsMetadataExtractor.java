package it.linksmt.assatti.service.converter;

import org.springframework.context.ApplicationContext;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.service.dto.DmsPermanentProperties;

public interface DmsMetadataExtractor {

	public DmsPermanentProperties extractMetadataByTipoAtto(Atto atto, DocumentoPdf documentoPrincipale, ApplicationContext appContext);
}
