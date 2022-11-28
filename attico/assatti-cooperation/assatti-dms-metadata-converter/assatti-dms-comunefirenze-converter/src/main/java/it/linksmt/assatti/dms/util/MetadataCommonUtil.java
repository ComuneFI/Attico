package it.linksmt.assatti.dms.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;

import it.linksmt.assatti.cmis.client.model.CmisAssociationDTO;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.ICmisDoc;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.service.ConfigurazioneIncaricoService;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoProfiloDto;
import it.linksmt.assatti.service.dto.DmsPermanentProperties;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.service.util.DocVersionUtil;
import it.linksmt.assatti.utility.StringUtil;

public class MetadataCommonUtil {
	
	public static final SimpleDateFormat DATA_ESECUTIVITA_DF = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final Logger log = LoggerFactory.getLogger(MetadataCommonUtil.class);

	public static void addCommonProperties(DmsPermanentProperties originalProps, Atto atto, DocumentoPdf docPrincipale) {
		if ( (atto == null) || (atto.getDocumentiPdfAdozione() == null) ||
				atto.getDocumentiPdfAdozione().isEmpty()) {
			log.error("Errore di lettura del documento di adozione dell'atto!");
			throw new DmsException("Errore di lettura del documento di adozione dell'atto!");
		}
		
		if (docPrincipale.getFile() == null) {
			log.error("File relativo al documento principale non presente!");
			throw new DmsException("File relativo al documento principale non presente!");
			
		}
		originalProps.setTargetDocumentId(docPrincipale.getFile().getCmisObjectId());
		
		Map<String, Object> docProps = originalProps.getDocumentProperties();
		if (docProps == null) {
			docProps = new HashMap<String, Object>();
		}
		
		docProps.put(DmsComuneFirenzeConstants.NUMERO_REGISTRAZIONE, Long.parseLong(atto.getNumeroAdozione()));
		docProps.put(DmsComuneFirenzeConstants.ANNO_REGISTRAZIONE, atto.getDataAdozione().getYear());
		docProps.put(DmsComuneFirenzeConstants.SIGLA_REGISTRO, atto.getTipoAtto().getCodice());
		docProps.put(DmsComuneFirenzeConstants.OGGETTO, StringUtil.trimStr(atto.getOggetto()));
		
		if (atto.getAoo() != null) {
			docProps.put(DmsComuneFirenzeConstants.STRUTTURA_PROPONENTE, 
					"(" + atto.getAoo().getCodice() + ") " +
					atto.getAoo().getDescrizione());
		}
		
		originalProps.setDocumentProperties(docProps);
	}
	
	
	public static void addAutoreByCodiceIncarico(DmsPermanentProperties originalProps, Atto atto, ApplicationContext appContext, String codiceIncarico) {
		Map<String, Object> docProps = originalProps.getDocumentProperties();
		if (docProps == null) {
			docProps = new HashMap<String, Object>();
		}
		
		try {
			ConfigurazioneIncaricoService confIncaricoService = appContext.getBean(ConfigurazioneIncaricoService.class);
			List<ConfigurazioneIncaricoDto> listConfigurazioneIncarico = confIncaricoService.findByAtto(atto.getId());
			if(listConfigurazioneIncarico!=null) {				
				for(ConfigurazioneIncaricoDto c : listConfigurazioneIncarico) {
					if(c.getConfigurazioneTaskCodice()!=null && c.getConfigurazioneTaskCodice().equals(codiceIncarico)) {
						List<ConfigurazioneIncaricoProfiloDto> profili = c.getListConfigurazioneIncaricoProfiloDto();
						if(profili!=null) {
							for(ConfigurazioneIncaricoProfiloDto p : profili) {
								String responsabileTecnico = p.getUtenteCognome() + " " + p.getUtenteNome();
								docProps.put(DmsComuneFirenzeConstants.AUTORE, responsabileTecnico);
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			log.error("Errore di lettura del Responsabile Tecnico.", e);
			throw new DmsException("Errore di lettura del Responsabile Tecnico.");
		}
		
		originalProps.setDocumentProperties(docProps);
	}
	
	
	public static void addPubblicazioneProperties(DmsPermanentProperties originalProps, Atto atto) {
		List<String> aspects = originalProps.getSecondaryObjectTypes();
		if (aspects == null) {
			aspects = new ArrayList<String>();
		}
		
		String aspectPub = DmsComuneFirenzeConstants.ASPECT_PREFIX + DmsComuneFirenzeConstants.PUBBLICAZIONE_ASPECT;
		if (!aspects.contains(aspectPub)) {
			aspects.add(aspectPub);
		}
		originalProps.setSecondaryObjectTypes(aspects);
		
		Map<String, Object> docProps = originalProps.getDocumentProperties();
		if (docProps == null) {
			docProps = new HashMap<String, Object>();
		}
		
		// come da Excel condiviso, il flag pubblicazione presente solo nelle delibere
		if (atto.getInizioPubblicazioneAlbo() != null) {			
			docProps.put(DmsComuneFirenzeConstants.ANNO_PUBBLICAZIONE, 
					atto.getInizioPubblicazioneAlbo().getYear());
			docProps.put(DmsComuneFirenzeConstants.DATA_INIZIO_PUBBLICAZIONE,
					atto.getInizioPubblicazioneAlbo().toDate());
			docProps.put(DmsComuneFirenzeConstants.REGISTRO_PUBBLICAZIONE, 
					DmsComuneFirenzeConstants.REGISTRO_PUBBLICAZIONE_AOL);
			if (atto.getFinePubblicazioneAlbo() != null) {
				docProps.put(DmsComuneFirenzeConstants.DATA_FINE_PUBBLICAZIONE,
						atto.getFinePubblicazioneAlbo().toDate());
			}
			if (atto.getProgressivoPubblicazioneAlbo() != null && 
					(atto.getProgressivoPubblicazioneAlbo().longValue() > 0)) {
				docProps.put(DmsComuneFirenzeConstants.NUMERO_PUBBLICAZIONE,
						atto.getProgressivoPubblicazioneAlbo());
			}
		}
		originalProps.setDocumentProperties(docProps);
	}
	
	
	public static void addConservazioneProperties(DmsPermanentProperties originalProps, Atto atto) {
		List<String> aspects = originalProps.getSecondaryObjectTypes();
		if (aspects == null) {
			aspects = new ArrayList<String>();
		}
		
		String aspectCons = DmsComuneFirenzeConstants.ASPECT_PREFIX + DmsComuneFirenzeConstants.CONSERVAZIONE_SOSTITUITIVA_ASPECT;
		if (!aspects.contains(aspectCons)) {
			aspects.add(aspectCons);
		}
		originalProps.setSecondaryObjectTypes(aspects);
		
		Map<String, Object> docProps = originalProps.getDocumentProperties();
		if (docProps == null) {
			docProps = new HashMap<String, Object>();
		}
		
		docProps.put(DmsComuneFirenzeConstants.STATO_CONSERVAZIONE, 
				DmsComuneFirenzeConstants.STATO_CONSERVAZIONE_DA_INVIARE_VAL);
		
		originalProps.setDocumentProperties(docProps);
	}
	
	
	public static void addAttachments(DmsPermanentProperties originalProps, Atto atto) {
		List<CmisAssociationDTO> associations = originalProps.getAssociations();
		if (associations == null) {
			associations = new ArrayList<CmisAssociationDTO>();
		}
		
		if (atto.getAllegati() != null) {
			List<ICmisDoc> docs = DocVersionUtil.getLatestVersionList(atto.getAllegati());
			for (ICmisDoc doc : docs) {
				DocumentoInformatico allegato = (DocumentoInformatico)doc;
				addAttachment(allegato, associations);
			}
		}
		
		// Aggancio gli allegati ai pareri come allegati
		if (atto.getPareri() != null) {
			for (Parere attoPar : atto.getPareri()) {
				if (attoPar.getAllegati() != null) {
					List<ICmisDoc> docs = DocVersionUtil.getLatestVersionList(attoPar.getAllegati());
					for (ICmisDoc doc : docs) {
						DocumentoInformatico allParere = (DocumentoInformatico)doc;
						addAttachment(allParere, associations);
					}
				}
			}
		}
		
		// Aggancio la versione con omissis dell'Atto come allegato
		if (atto.getDocumentiPdfAdozioneOmissis()!=null && !atto.getDocumentiPdfAdozioneOmissis().isEmpty()) {
			DocumentoPdf allegatoOmissis = null;
			
			List<ICmisDoc> docs = DocVersionUtil.getLatestVersionList(atto.getDocumentiPdfAdozioneOmissis());
			for (ICmisDoc doc : docs) {
				DocumentoPdf d = (DocumentoPdf)doc;
				if(d.getCompleto()==null || d.getCompleto().equals(false)) {
					allegatoOmissis = d;
					break;
				}
			}
			
			CmisAssociationDTO assoc = new CmisAssociationDTO();
			
			assoc.setAssociationType(DmsComuneFirenzeConstants.RELATION_PREFIX +
					DmsComuneFirenzeConstants.ASSOCIAZIONE_ALLEGATI_ATTO);
			assoc.setTargetNodeId(allegatoOmissis.getFile().getCmisObjectId());
			
			associations.add(assoc);
		}
		
		//invio in conservazione del documento completo
		if(Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice())) {
			List<ICmisDoc> docs = DocVersionUtil.getLatestVersionList(atto.getDocumentiPdfAdozione());
			for(ICmisDoc d : docs) {
				DocumentoPdf doc = (DocumentoPdf)d;
				if(doc.getCompleto()!=null && doc.getCompleto()) {
					CmisAssociationDTO assoc = new CmisAssociationDTO();
					assoc.setAssociationType(DmsComuneFirenzeConstants.RELATION_PREFIX +
							DmsComuneFirenzeConstants.ASSOCIAZIONE_ALLEGATI_ATTO);
					assoc.setTargetNodeId(doc.getFile().getCmisObjectId());
					associations.add(assoc);
					break;
				}
			}
			docs = DocVersionUtil.getLatestVersionList(atto.getDocumentiPdfAdozioneOmissis());
			for(ICmisDoc d : docs) {
				DocumentoPdf doc = (DocumentoPdf)d;
				if(doc.getCompleto()!=null && doc.getCompleto()) {
					CmisAssociationDTO assoc = new CmisAssociationDTO();
					assoc.setAssociationType(DmsComuneFirenzeConstants.RELATION_PREFIX +
							DmsComuneFirenzeConstants.ASSOCIAZIONE_ALLEGATI_ATTO);
					assoc.setTargetNodeId(doc.getFile().getCmisObjectId());
					associations.add(assoc);
					break;
				}
			}
		}
		
		if(Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice())) {
			List<ICmisDoc> docs = DocVersionUtil.getLatestVersionList(atto.getDocumentiPdf());
			DocumentoPdf docParereRegContabile = null; DateTime dataParereRegContabile =null;
			DocumentoPdf docParereRegTecnica = null; DateTime dataParereRegTecnica =null;
			DocumentoPdf docPropostaDelibera = null; DateTime dataPropostaDelibera =null;
			for(ICmisDoc d : docs) {
				DocumentoPdf doc = (DocumentoPdf)d;
				if(doc.getTipoDocumento()!=null && doc.getTipoDocumento().getCodice().startsWith("PARERE_REG_CONTABILE_") && doc.getFirmato()!=null && doc.getFirmato()) {
					if(dataParereRegContabile == null || doc.getCreatedDate().isAfter(dataParereRegContabile)) {
						docParereRegContabile = doc;
						dataParereRegContabile = doc.getCreatedDate();
					}
				}else if(doc.getTipoDocumento()!=null && doc.getTipoDocumento().getCodice().startsWith("PARERE_REG_TECNICA_") && doc.getFirmato()!=null && doc.getFirmato()) {
					if(dataParereRegTecnica == null || doc.getCreatedDate().isAfter(dataParereRegTecnica)) {
						docParereRegTecnica = doc;
						dataParereRegTecnica = doc.getCreatedDate();
					}
					
				}else if(doc.getTipoDocumento()!=null && doc.getTipoDocumento().getCodice().startsWith("PROPOSTA_DELIBERA_")) {
					if(dataPropostaDelibera == null || doc.getCreatedDate().isAfter(dataPropostaDelibera)) {
						docPropostaDelibera = doc;
						dataPropostaDelibera = doc.getCreatedDate();
					}
				}
			}
			
			if(docParereRegContabile!=null) {
				CmisAssociationDTO assoc = new CmisAssociationDTO();
				assoc.setAssociationType(DmsComuneFirenzeConstants.RELATION_PREFIX +
						DmsComuneFirenzeConstants.ASSOCIAZIONE_ALLEGATI_ATTO);
				assoc.setTargetNodeId(docParereRegContabile.getFile().getCmisObjectId());
				associations.add(assoc);
			}
			
			if(docParereRegTecnica!=null) {
				CmisAssociationDTO assoc = new CmisAssociationDTO();
				assoc.setAssociationType(DmsComuneFirenzeConstants.RELATION_PREFIX +
						DmsComuneFirenzeConstants.ASSOCIAZIONE_ALLEGATI_ATTO);
				assoc.setTargetNodeId(docParereRegTecnica.getFile().getCmisObjectId());
				associations.add(assoc);
			}
			
			if(docPropostaDelibera!=null) {
				CmisAssociationDTO assoc = new CmisAssociationDTO();
				assoc.setAssociationType(DmsComuneFirenzeConstants.RELATION_PREFIX +
						DmsComuneFirenzeConstants.ASSOCIAZIONE_ALLEGATI_ATTO);
				assoc.setTargetNodeId(docPropostaDelibera.getFile().getCmisObjectId());
				associations.add(assoc);
			}
		}
		
		// Aggancio la relata di pubblicazione come allegato
		if (atto.getRelatePubblicazione() != null && atto.getRelatePubblicazione().size() > 0) {
			List<ICmisDoc> docs = DocVersionUtil.getLatestVersionList(atto.getRelatePubblicazione());
			for (ICmisDoc doc : docs) {
				DocumentoPdf relata = (DocumentoPdf)doc;
				CmisAssociationDTO assoc = new CmisAssociationDTO();
				
				assoc.setAssociationType(DmsComuneFirenzeConstants.RELATION_PREFIX +
						DmsComuneFirenzeConstants.ASSOCIAZIONE_ALLEGATI_ATTO);
				assoc.setTargetNodeId(relata.getFile().getCmisObjectId());
				
				associations.add(assoc);
			}
		}
		
		// Aggancio il report iter come allegato
		if (atto.getReportsIter() != null && atto.getReportsIter().size() > 0) {
			List<ICmisDoc> docs = DocVersionUtil.getLatestVersionList(atto.getReportsIter());
			for (ICmisDoc doc : docs) {
				DocumentoPdf report = (DocumentoPdf)doc;
				CmisAssociationDTO assoc = new CmisAssociationDTO();
				
				assoc.setAssociationType(DmsComuneFirenzeConstants.RELATION_PREFIX +
						DmsComuneFirenzeConstants.ASSOCIAZIONE_ALLEGATI_ATTO);
				assoc.setTargetNodeId(report.getFile().getCmisObjectId());
				
				associations.add(assoc);
			}
		}
		
		originalProps.setAssociations(associations);
	}
	
	private static void addAttachment(DocumentoInformatico allegato, List<CmisAssociationDTO> associations) {
		CmisAssociationDTO allegInt = new CmisAssociationDTO();
		allegInt.setAssociationType(DmsComuneFirenzeConstants.RELATION_PREFIX +
				DmsComuneFirenzeConstants.ASSOCIAZIONE_ALLEGATI_ATTO);
		allegInt.setTargetNodeId(allegato.getFile().getCmisObjectId());
		
		associations.add(allegInt);
		
		if (allegato.getFileomissis() != null) {
			CmisAssociationDTO allegOmiss = new CmisAssociationDTO();
			allegOmiss.setAssociationType(DmsComuneFirenzeConstants.RELATION_PREFIX +
					DmsComuneFirenzeConstants.ASSOCIAZIONE_ALLEGATI_ATTO);
			allegOmiss.setTargetNodeId(allegato.getFileomissis().getCmisObjectId());
			
			associations.add(allegOmiss);
		}
	}
	
}
