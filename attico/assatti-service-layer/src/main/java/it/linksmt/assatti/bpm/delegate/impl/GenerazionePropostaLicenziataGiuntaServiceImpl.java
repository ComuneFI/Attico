package it.linksmt.assatti.bpm.delegate.impl;

import java.util.List;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.service.DocumentoPdfService;
import it.linksmt.assatti.service.ModelloHtmlService;
import it.linksmt.assatti.service.ReportService;
import it.linksmt.assatti.service.dto.ReportDTO;

/**
 * Service Task Camunda per la generazione del documento pdf completo
 *
 */
@Service
@Transactional
public class GenerazionePropostaLicenziataGiuntaServiceImpl implements DelegateBusinessLogic {

	private final Logger log = LoggerFactory.getLogger(GenerazionePropostaLicenziataGiuntaServiceImpl.class);

	@Inject
	private ReportService reportService;
	
	@Inject
	private ModelloHtmlService modelloHtmlService;
	
	@Inject
	private DocumentoPdfService documentoPdfService;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		try {
			log.info("Generazione Documento Licenziato dalla giunta per la DPC con attoID:"+execution.getProcessBusinessKey());
			Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
			ReportDTO reportDto = new ReportDTO();
			
			Object tipoDocObj = execution.getVariableLocal("DOCUMENTO_LICENZIATO");
			if(tipoDocObj!=null && tipoDocObj instanceof String && !((String)tipoDocObj).isEmpty()) {
				List<ModelloHtml> modelloHtmlList = modelloHtmlService.findByTipoDocumento((String)tipoDocObj);
				if(modelloHtmlList!=null && modelloHtmlList.size() == 1) {
					boolean omissis = false;
					if ((atto.getRiservato() == null || atto.getRiservato().equals(false)) && (atto.getPubblicazioneIntegrale() == null || atto.getPubblicazioneIntegrale().equals(false))) {
						omissis = true;
					}
					
					ModelloHtml modelloHtml = modelloHtmlList.get(0);
					reportDto.setIdAtto(atto.getId());
					reportDto.setIdModelloHtml(modelloHtml.getId());
					reportDto.setOmissis(omissis);
					java.io.File pdfAtto = reportService.previewAtto(atto, reportDto);
					
					documentoPdfService.salvaDocumentoPdf(atto, pdfAtto, (String)tipoDocObj, omissis, false, false, false);
				}
			}
		}catch(Exception e) {
			if(e!=null && e instanceof RuntimeException) {
				throw (RuntimeException)e;
			}else {
				throw new RuntimeException(e);
			}
		}
	}
}
