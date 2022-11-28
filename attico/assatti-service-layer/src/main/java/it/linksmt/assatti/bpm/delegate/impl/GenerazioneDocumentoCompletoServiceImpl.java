package it.linksmt.assatti.bpm.delegate.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.util.BpmThreadLocalUtil;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.service.DocumentoPdfService;
import it.linksmt.assatti.service.ModelloHtmlService;
import it.linksmt.assatti.service.ReportService;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.dto.ReportDTO.DelegaFirma;

/**
 * Service Task Camunda per la generazione del documento pdf completo
 *
 */
@Service
@Transactional
public class GenerazioneDocumentoCompletoServiceImpl implements DelegateBusinessLogic {

	private final Logger log = LoggerFactory.getLogger(GenerazioneDocumentoCompletoServiceImpl.class);

	@Inject
	private ReportService reportService;
	
	@Inject
	private ModelloHtmlService modelloHtmlService;
	
	@Inject
	private DocumentoPdfService documentoPdfService;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private ProfiloRepository profiloRepository;

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		try {
			Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
			ReportDTO reportDto = new ReportDTO();
			Profilo profiloFirmatario = profiloRepository.findOne(BpmThreadLocalUtil.getProfiloId());
			Utente utFirmatario = profiloFirmatario.getUtente();
			long idProfDelega = BpmThreadLocalUtil.getProfiloDeleganteId();
			if(idProfDelega < 0) {
				idProfDelega = BpmThreadLocalUtil.getProfiloOriginarioId();
			}
			Profilo profiloDelegante = profiloRepository.findOne(idProfDelega);
			if (atto.getTipoAtto().getCodice().equalsIgnoreCase("DD")) {
				if (idProfDelega > 0) {
					if ((profiloDelegante != null) && (profiloDelegante.getUtente() != null)) {
						Utente utDelegante = profiloDelegante.getUtente();
						if(reportDto.getDelegheFirme()==null) {
							reportDto.setDelegheFirme(new ArrayList<DelegaFirma>());
						}
						reportDto.getDelegheFirme().add(reportDto.new DelegaFirma(utDelegante.getNome() + " " + utDelegante.getCognome(), utFirmatario.getNome() + " " + utFirmatario.getCognome()));
					}
				}
			}else if(Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice())){
				if(reportDto.getDelegheFirme()==null) {
					reportDto.setDelegheFirme(new ArrayList<DelegaFirma>());
				}
				if (profiloFirmatario != null && profiloFirmatario.getUtente() != null && profiloDelegante != null && profiloDelegante.getUtente() != null && !profiloDelegante.getUtente().getId().equals(profiloFirmatario.getUtente().getId())) {
					Utente utDelegante = profiloDelegante.getUtente();
					reportDto.getDelegheFirme().add(reportDto.new DelegaFirma(utDelegante.getNome() + " " + utDelegante.getCognome(), utFirmatario.getNome() + " " + utFirmatario.getCognome()));
				}else {
					reportDto.getDelegheFirme().add(reportDto.new DelegaFirma(utFirmatario.getNome() + " " + utFirmatario.getCognome(), null));
				}
				this.manageDeleghe(atto, reportDto, 1);
			}
//			this.manageDeleghe(atto, reportDto);
			Object tipoDocObj = execution.getVariableLocal("DOCUMENTO_COMPLETO");
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
					
					documentoPdfService.salvaDocumentoPdf(atto, pdfAtto, (String)tipoDocObj, omissis, false, false, true);
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
	
	//non include il documento appena firmato, in quanto non ancora committato sul db il savataggio
	private void manageDeleghe(Atto atto, ReportDTO reportDto, int nDoc) {
		if(atto.getDocumentiPdfAdozione()!=null && atto.getDocumentiPdfAdozione().size() > 0) {
			int i = 1;
			for(DocumentoPdf doc : atto.getDocumentiPdfAdozione()) {
				if(doc!=null && doc.getFirmato()!=null && doc.getFirmato()) {
					if(reportDto.getDelegheFirme()==null) {
						reportDto.setDelegheFirme(new ArrayList<DelegaFirma>());
					}
					if(doc.getFirmatarioDelegante()!=null && !doc.getFirmatarioDelegante().trim().isEmpty()) {
						reportDto.getDelegheFirme().add(reportDto.new DelegaFirma(doc.getFirmatarioDelegante(), doc.getFirmatario()));
					}else {
						reportDto.getDelegheFirme().add(reportDto.new DelegaFirma(doc.getFirmatario(), null));
					}
				}
				if(nDoc <= i) {
					break;
				}else {
					i++;
				}
			}
		}
		
	}
}
