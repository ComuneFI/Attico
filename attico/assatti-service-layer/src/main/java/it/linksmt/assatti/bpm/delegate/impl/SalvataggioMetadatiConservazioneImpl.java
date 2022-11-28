package it.linksmt.assatti.bpm.delegate.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.cmis.client.model.CmisAssociationDTO;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.ICmisDoc;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.DocumentoPdfService;
import it.linksmt.assatti.service.converter.DmsMetadataConverter;
import it.linksmt.assatti.service.dto.DmsPermanentProperties;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.service.util.DocVersionUtil;

@Service
@Transactional
public class SalvataggioMetadatiConservazioneImpl implements DelegateBusinessLogic {

	private final Logger log = LoggerFactory.getLogger(SalvataggioMetadatiConservazioneImpl.class);

	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Autowired
	private DmsMetadataConverter dmsMetadataConverter;
	
	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private AttoService attoService;
	
	@Autowired
	private DocumentoPdfService documentoPdfService;
	
	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		try {
			Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
			
			log.info("Atto con id " + atto.getId() + ": scrittura metadati per conservazione");
			
			// Report Iter, se non c'è lo creo
			if (atto.getReportsIter() == null || atto.getReportsIter().size() < 1) {
				Object docObj = documentoPdfService.generaReportIter(Long.parseLong(execution.getProcessBusinessKey()), true);
				if(docObj!=null && docObj instanceof DocumentoPdf) {
					DocumentoPdf report = (DocumentoPdf)docObj;
					if(atto.getReportsIter()==null) {
						atto.setReportsIter(new ArrayList<DocumentoPdf>());
					}
					atto.getReportsIter().add(report);
				}
			}
			
			// Estrazione proprietà documento principale atto
			DmsPermanentProperties consProps = dmsMetadataConverter.getAllImmutableProperties(atto);
			if (consProps == null) {
				log.error("Impossibile estrarre i metadati del documento principale!");
				throw new DmsException("Impossibile estrarre i metadati del documento principale!");
			}
			
			// Se il documento non è stato conservato, non possono essere aggiornati i metadati
			dmsMetadataConverter.checkDocumentoDaInviare(consProps.getTargetDocumentId());
			
			// Elaborazione Allegati
			if (atto.getAllegati() != null) {
				List<ICmisDoc> allegati = DocVersionUtil.getLatestVersionList(atto.getAllegati());
				if(allegati!=null) {
					for (ICmisDoc allEnt : atto.getAllegati()) {
						updateAllegato((DocumentoInformatico)allEnt, null);
					}
				}
			}
			
			// Allegati ai pareri (es. Consigli Quartiere, Revisori)
			if (atto.getPareri() != null) {
				for (Parere attoPar : atto.getPareri()) {
					if (attoPar.getAllegati() != null) {
						List<ICmisDoc> allegati = DocVersionUtil.getLatestVersionList(attoPar.getAllegati());
						if(allegati!=null) {
							for (ICmisDoc allParere : allegati) {
								updateAllegato((DocumentoInformatico)allParere, attoPar.getTipoAzione().getDescrizione());
							}
						}
					}
				}
			}
					
			// Elaborazione Relata
			if (atto.getRelatePubblicazione() != null && atto.getRelatePubblicazione().size() > 0) {
				List<ICmisDoc> relate = DocVersionUtil.getLatestVersionList(atto.getRelatePubblicazione());
				if(relate!=null) {
					for (ICmisDoc relata : relate) {
						DocumentoPdf doc = (DocumentoPdf)relata;
						// Allegato - versione integrale
						DmsPermanentProperties relataProps = dmsMetadataConverter.getAllImmutablePropertiesRelata(doc);
						if (relataProps == null) {
							throw new DmsException("Impossibile estrarre i metadati della relata! DocumentoPdf Id: " + doc.getId());
						}
						
						String relataId = relataProps.getTargetDocumentId();
						updateDocument(relataId, relataProps);
						
					}
				}
			}
			
			// Report Iter
			if (atto.getReportsIter() != null && atto.getReportsIter().size() > 0) {
				List<ICmisDoc> iters = DocVersionUtil.getLatestVersionList(atto.getReportsIter());
				if(iters!=null) {
					for (ICmisDoc report : iters) {
						DocumentoPdf doc = (DocumentoPdf)report;
						// Allegato - versione integrale
						DmsPermanentProperties reportProps = dmsMetadataConverter.getAllImmutablePropertiesReportIter(doc);
						if (reportProps == null) {
							throw new DmsException("Impossibile estrarre i metadati del report iter! DocumentoPdf Id: " + doc.getId());
						}
						
						String reportId = reportProps.getTargetDocumentId();
						updateDocument(reportId, reportProps);
					}
				}
			}
			
			if(Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice())) {
				List<ICmisDoc> adoziones = DocVersionUtil.getLatestVersionList(atto.getDocumentiPdfAdozione());
				if(adoziones!=null) {
					for(ICmisDoc cm :adoziones) {
						DocumentoPdf doc = (DocumentoPdf)cm;
						if(doc.getCompleto()!=null && doc.getCompleto()) {
							DmsPermanentProperties props = dmsMetadataConverter.getAllImmutablePropertiesAsAllegato(doc, "PROVVEDIMENTO_COMPLETO");
							if (props == null) {
								throw new DmsException("Impossibile estrarre i metadati del doc completo! DocumentoPdf Id: " + doc.getId());
							}
							String docId = props.getTargetDocumentId();
							updateDocument(docId, props);
							break;
						}
					}
				}
				List<ICmisDoc> adozionesOm = DocVersionUtil.getLatestVersionList(atto.getDocumentiPdfAdozioneOmissis());
				if(adozionesOm!=null) {
					for(ICmisDoc cm : adozionesOm) {
						DocumentoPdf doc = (DocumentoPdf)cm;
						if(doc.getCompleto()!=null && doc.getCompleto()) {
							DmsPermanentProperties props = dmsMetadataConverter.getAllImmutablePropertiesAsAllegato(doc, "PROVVEDIMENTO_OMISSIS_COMPLETO");
							if (props == null) {
								throw new DmsException("Impossibile estrarre i metadati del doc completo con omissis! DocumentoPdf Id: " + doc.getId());
							}
							String docId = props.getTargetDocumentId();
							updateDocument(docId, props);
							break;
						}
					}
				}
				List<ICmisDoc> proposte = DocVersionUtil.getLatestVersionList(atto.getDocumentiPdf());
				if(proposte!=null) {
					for(ICmisDoc cm : proposte) {
						DocumentoPdf doc = (DocumentoPdf)cm;
						DmsPermanentProperties props = null;
						if(doc.getTipoDocumento()!=null && doc.getTipoDocumento().getCodice().startsWith("PARERE_REG_CONTABILE_") && doc.getFirmato()!=null && doc.getFirmato()) {
							props = dmsMetadataConverter.getAllImmutablePropertiesAsAllegato(doc, "PARERE_REG_CONTABILE");
						}else if(doc.getTipoDocumento()!=null && doc.getTipoDocumento().getCodice().startsWith("PARERE_REG_TECNICA_") && doc.getFirmato()!=null && doc.getFirmato()) {
							props = dmsMetadataConverter.getAllImmutablePropertiesAsAllegato(doc, "PARERE_REG_TECNICA");
						}else if(doc.getTipoDocumento()!=null && doc.getTipoDocumento().getCodice().startsWith("PROPOSTA_DELIBERA_")) {
							props = dmsMetadataConverter.getAllImmutablePropertiesAsAllegato(doc, "PROPOSTA");
						}
						if(props!=null) {
							String docId = props.getTargetDocumentId();
							updateDocument(docId, props);
						}
					}
				}
			}
			
			// Salvataggio metadati atto con omissis, inserito come allegato
			if (atto.getDocumentiPdfAdozioneOmissis()!=null && !atto.getDocumentiPdfAdozioneOmissis().isEmpty()) {
				DocumentoPdf attoOmissis = attoService.getDocumentoPrincipale(atto, false, true);
				
				DmsPermanentProperties attoOmissisProps = dmsMetadataConverter.getAllImmutableProperties(attoOmissis);		
				if (attoOmissisProps == null) {
					throw new DmsException("Impossibile estrarre i metadati del file atto con omissis! Documento PDF Id: " 
							+ attoOmissis.getId());
				}
				
				String attoOmissisId = attoOmissisProps.getTargetDocumentId();
				updateDocument(attoOmissisId, attoOmissisProps);
			}
					
			// Conservazione documento principale atto
			String documentId = consProps.getTargetDocumentId();
			updateDocument(documentId, consProps);
			
			// TODO: rimuovere, consente di effettuare TEST
			// throw new DmsException("TEST METADATI!");
		}catch(Exception e) {
			log.error("Errore in salvataggio metadati conservazione", e);
			if(e instanceof RuntimeException) {
				throw (RuntimeException)e;
			}else {
				throw new RuntimeException(e);
			}
		}
	}
	
	
	private void updateAllegato(DocumentoInformatico allEnt, String tipoDefault) {
		// Allegato - versione integrale
		DmsPermanentProperties allegProps = dmsMetadataConverter.getAllImmutableProperties(allEnt, tipoDefault, false);
		if (allegProps == null) {
			throw new DmsException("Impossibile estrarre i metadati del file allegato! Documento Informatico Id: " + allEnt.getId());
		}
		
		String allegatoId = allegProps.getTargetDocumentId();
		updateDocument(allegatoId, allegProps);
		
		// Allegato - eventuale versione con omissis
		if (allEnt.getFileomissis() != null) {
			
			DmsPermanentProperties omissisProps = dmsMetadataConverter.getAllImmutableProperties(allEnt, tipoDefault, true);		
			if (omissisProps == null) {
				throw new DmsException("Impossibile estrarre i metadati del file allegato (omissis)! Documento Informatico Id: " + allEnt.getId());
			}
			
			String omissisId = omissisProps.getTargetDocumentId();
			updateDocument(omissisId, omissisProps);
		}
	}
	
	
	private void updateDocument(String documentId, DmsPermanentProperties consProps) {
		List<String> secondaryTypes = consProps.getSecondaryObjectTypes();
		dmsService.addSecondaryTypes(documentId, secondaryTypes);
		
		Map<String, Object> docProperties = consProps.getDocumentProperties();
		dmsService.updateDocumentMetadata(documentId, docProperties);
		
		List<CmisAssociationDTO> associations = consProps.getAssociations();
		dmsService.addAssociations(documentId, associations);
	}
	
}
