package it.linksmt.assatti.dms.extractor.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.Evento;
import it.linksmt.assatti.datalayer.domain.TipoEvento;
import it.linksmt.assatti.datalayer.repository.EventoRepository;
import it.linksmt.assatti.datalayer.repository.TipoEventoRepository;
import it.linksmt.assatti.dms.util.DmsComuneFirenzeConstants;
import it.linksmt.assatti.dms.util.MetadataCommonUtil;
import it.linksmt.assatti.service.dto.DmsPermanentProperties;
import it.linksmt.assatti.service.util.TipiEventoEnum;

public class AttoDetLiquidazioneMetadataExtractor extends AttoDeterminaMetadataExtractor {
	
	private final static Logger log = LoggerFactory.getLogger(AttoDetLiquidazioneMetadataExtractor.class);

	@Override
	public DmsPermanentProperties extractMetadataByTipoAtto(Atto atto, DocumentoPdf documentoPrincipale, ApplicationContext appContext) {
		
		DmsPermanentProperties retVal = super.extractMetadataByTipoAtto(atto, documentoPrincipale, appContext);
		
		// Per le determine di Liquidazione viene considerata come Data Riferimento la data di firma del RT
		TipoEventoRepository tipoEventoRepo = appContext.getBean(TipoEventoRepository.class);
		EventoRepository eventoRepo = appContext.getBean(EventoRepository.class);
		
		TipoEvento firmaResp = tipoEventoRepo.findByCodice(TipiEventoEnum.FIRMA_RESP_TECNICO.getCodice());
		List<Evento> firmaEv = eventoRepo.findByTipoEventoAndAtto(firmaResp, atto);
		
		Evento evFirma = null;
		if (firmaEv != null) {
			for (Evento evento : firmaEv) {
				if (evFirma == null || (evento.getDataCreazione().getMillis() > 
					evFirma.getDataCreazione().getMillis())) {
					evFirma = evento;
				}
			}
		}
		
		if (evFirma == null) {
			log.error("IMPOSSIBILE RECUPERARE L'EVENTO DI FIRMA RESPONSABILE TECNICO!!!");
		}
		else {
			Map<String, Object> docProps = retVal.getDocumentProperties();
			docProps.put(DmsComuneFirenzeConstants.DATA_RIFERIMENTO, 
					evFirma.getDataCreazione().toDate());	
			retVal.setDocumentProperties(docProps);
			
			log.info("Modifica data riferimento per DETERMINE LIQUIDAZIONE: " + 
					MetadataCommonUtil.DATA_ESECUTIVITA_DF.format(evFirma.getDataCreazione().toDate()));
		}
		
		return retVal;
	}
}
