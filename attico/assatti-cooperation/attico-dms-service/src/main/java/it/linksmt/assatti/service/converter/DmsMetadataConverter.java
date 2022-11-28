package it.linksmt.assatti.service.converter;

import java.util.Map;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.service.dto.DmsPermanentProperties;

public interface DmsMetadataConverter {

	public Map<String, Object> getPropertiesForCreate(Atto atto, TipoDocumento tipoDocumento, boolean omissis);
	
	public DmsPermanentProperties getAllImmutableProperties(Atto atto);
	
	public DmsPermanentProperties getAllImmutableProperties(DocumentoInformatico allegato, String tipoDefault, boolean forOmissis);
	
	public DmsPermanentProperties getAllImmutableProperties(DocumentoPdf attoOmissis);
	
	public void checkDocumentoDaInviare(String documentId);
	
	public DmsPermanentProperties getAllImmutablePropertiesRelata(DocumentoPdf relata);
	
	public DmsPermanentProperties getAllImmutablePropertiesReportIter(DocumentoPdf reportIter);
	
	public DmsPermanentProperties getAllImmutablePropertiesAsAllegato(DocumentoPdf doc, String tipoAllegato);
}
