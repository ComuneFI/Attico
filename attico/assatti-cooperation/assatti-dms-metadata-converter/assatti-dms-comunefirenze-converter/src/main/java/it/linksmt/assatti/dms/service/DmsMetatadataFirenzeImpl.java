package it.linksmt.assatti.dms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.cmis.client.model.CmisDocumentDTO;
import it.linksmt.assatti.cmis.client.model.CmisMetadataDTO;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.dms.util.DmsComuneFirenzeConstants;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.TipoDocumentoService;
import it.linksmt.assatti.service.converter.DmsMetadataConverter;
import it.linksmt.assatti.service.converter.DmsMetadataExtractor;
import it.linksmt.assatti.service.dto.DmsPermanentProperties;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.CmisProps;

@Service
public class DmsMetatadataFirenzeImpl implements DmsMetadataConverter {
	
	private final static Logger log = LoggerFactory.getLogger(DmsMetatadataFirenzeImpl.class.getName());
	
	public static final String METADATA_EXTRACTOR_PREFIX = "cmis.comunefirenze.metadata.extractor.";
	
	public static final String TIPO_ALLEGATO_PROVVEDIMENTO_OMISSIS = "PROVVEDIMENTO_OMISSIS";
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private TipoDocumentoService tipoDocumentoService;
	
	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private AttoService attoService;

	@Override
	public Map<String, Object> getPropertiesForCreate(Atto atto, TipoDocumento tipoDocumento, boolean omissis) {
		if ((tipoDocumento == null) || StringUtil.isNull(tipoDocumento.getDmsContentType()) ) {
			return null;
		}
		
		String valTipoDocumento = StringUtil.trimStr(tipoDocumento.getDmsContentType());
		if (omissis) {
			TipoDocumento tipoAllegato = tipoDocumentoService
					.findByCodice(TipoDocumentoEnum.allegato.name());
			
			valTipoDocumento = StringUtil.trimStr(tipoAllegato.getDmsContentType());
		}
		
		HashMap<String, Object> documentProperties = new HashMap<String, Object>();
		documentProperties.put(PropertyIds.OBJECT_TYPE_ID,
			DmsComuneFirenzeConstants.CONTENT_TYPE_PREFIX + valTipoDocumento);

		return documentProperties;
	}

	@Override
	public DmsPermanentProperties getAllImmutableProperties(Atto atto) {
		if ((atto == null) || (atto.getTipoAtto() == null) || 
			StringUtil.isNull(atto.getTipoAtto().getCodice())) {
			return null;
		}
		
		String codiceTipoAtto = atto.getTipoAtto().getCodice();
		String extractorClassname = CmisProps.getProperty(METADATA_EXTRACTOR_PREFIX + codiceTipoAtto);
		
		if (StringUtil.isNull(extractorClassname)) {
			return null;
		}
		
		try {
			Class<?> c = Class.forName(extractorClassname);
			if (!DmsMetadataExtractor.class.isAssignableFrom(c)) {
				log.error("La classe istanziata non risulta compatibile con " + DmsMetadataExtractor.class.getName());
				throw new DmsException("La classe istanziata non risulta compatibile con " + DmsMetadataExtractor.class.getName());
			}
			
			DmsMetadataExtractor concreteExtractor = (DmsMetadataExtractor) c.newInstance();
			return concreteExtractor.extractMetadataByTipoAtto(atto, attoService.getDocumentoPrincipale(atto, false, false), applicationContext);
		}
		catch (Exception e) {
			log.error("Errore durante l'estrazione dei metadati", e);
			throw new DmsException("Errore durante l'estrazione dei metadati: " + e.getMessage());
		}
	}

	@Override
	public DmsPermanentProperties getAllImmutableProperties(DocumentoInformatico allegato, String tipoDefault, boolean forOmissis) {
		
		DmsPermanentProperties props = new DmsPermanentProperties();
		if (allegato == null) {
			return null;
		}
		
		// TODO: verificare il corretto mapping dei dati!
		String nomeFile = StringUtil.trimStr(allegato.getTitolo());
		String tipoAllegato = null;
		
		if(forOmissis) {
			props.setTargetDocumentId(allegato.getFileomissis().getCmisObjectId());
			if (StringUtil.isNull(nomeFile) && allegato.getFileomissis() != null) {
				nomeFile = StringUtil.trimStr(allegato.getFileomissis().getNomeFile());
			}
		}
		else {
			props.setTargetDocumentId(allegato.getFile().getCmisObjectId());
			if (StringUtil.isNull(nomeFile) && allegato.getFile() != null) {
				nomeFile = StringUtil.trimStr(allegato.getFile().getNomeFile());
			}
		}
		
		if (StringUtil.isNull(nomeFile)) {
			nomeFile = StringUtil.trimStr(allegato.getNomeFile());
		}
		
		if (allegato.getTipoAllegato() != null) {
			tipoAllegato = StringUtil.trimStr(allegato.getTipoAllegato().getCodice());
		}
		
		if (StringUtil.isNull(tipoAllegato)) {
			tipoAllegato = StringUtil.trimStr(tipoDefault);
		}
		
		Map<String, Object> allProps = new HashMap<String, Object>();
		if (!StringUtil.isNull(nomeFile)) {
			allProps.put(DmsComuneFirenzeConstants.NOME_ALLEGATO, nomeFile);
		}
		if (!StringUtil.isNull(tipoAllegato)) {
			allProps.put(DmsComuneFirenzeConstants.TIPO_ALLEGATO, tipoAllegato);
		}
		
		props.setDocumentProperties(allProps);	
		return props;
	}
	
	@Override
	public DmsPermanentProperties getAllImmutablePropertiesRelata(DocumentoPdf relata) {
		
		DmsPermanentProperties props = new DmsPermanentProperties();
		if (relata == null) {
			return null;
		}
		
		String nomeFile = StringUtil.trimStr(relata.getFile().getNomeFile());
		String tipoAllegato = "Relata di pubblicazione";
		
		props.setTargetDocumentId(relata.getFile().getCmisObjectId());
		if (StringUtil.isNull(nomeFile) && relata.getFile() != null) {
			nomeFile = StringUtil.trimStr(relata.getFile().getNomeFile());
		}
		
		Map<String, Object> allProps = new HashMap<String, Object>();
		if (!StringUtil.isNull(nomeFile)) {
			allProps.put(DmsComuneFirenzeConstants.NOME_ALLEGATO, nomeFile);
		}
		if (!StringUtil.isNull(tipoAllegato)) {
			allProps.put(DmsComuneFirenzeConstants.TIPO_ALLEGATO, tipoAllegato);
		}
		
		props.setDocumentProperties(allProps);	
		return props;
	}
	
	@Override
	public DmsPermanentProperties getAllImmutablePropertiesReportIter(DocumentoPdf report) {
		return this.getAllImmutablePropertiesAsAllegato(report, "Report Iter");
	}
	
	@Override
	public DmsPermanentProperties getAllImmutablePropertiesAsAllegato(DocumentoPdf doc, String tipoAllegato) {
		
		DmsPermanentProperties props = new DmsPermanentProperties();
		if (doc == null) {
			return null;
		}
		
		String nomeFile = StringUtil.trimStr(doc.getFile().getNomeFile());
		
		props.setTargetDocumentId(doc.getFile().getCmisObjectId());
		if (StringUtil.isNull(nomeFile) && doc.getFile() != null) {
			nomeFile = StringUtil.trimStr(doc.getFile().getNomeFile());
		}
		
		Map<String, Object> allProps = new HashMap<String, Object>();
		if (!StringUtil.isNull(nomeFile)) {
			allProps.put(DmsComuneFirenzeConstants.NOME_ALLEGATO, nomeFile);
		}
		if (!StringUtil.isNull(tipoAllegato)) {
			allProps.put(DmsComuneFirenzeConstants.TIPO_ALLEGATO, tipoAllegato);
		}
		
		props.setDocumentProperties(allProps);	
		return props;
	}
	
	@Override
	public DmsPermanentProperties getAllImmutableProperties(DocumentoPdf attoOmissis) {
		DmsPermanentProperties props = new DmsPermanentProperties();
		if (attoOmissis == null) {
			return null;
		}
		
		Map<String, Object> allProps = new HashMap<String, Object>();
		props.setTargetDocumentId(attoOmissis.getFile().getCmisObjectId());
		
		String nomeFile = attoOmissis.getFile().getNomeFile();
		if (!StringUtil.isNull(nomeFile)) {
			allProps.put(DmsComuneFirenzeConstants.NOME_ALLEGATO, nomeFile);
		}
		allProps.put(DmsComuneFirenzeConstants.TIPO_ALLEGATO, TIPO_ALLEGATO_PROVVEDIMENTO_OMISSIS);
		
		props.setDocumentProperties(allProps);	
		return props;
	}


	@Override
	public void checkDocumentoDaInviare(String documentId) {
		CmisDocumentDTO docDTO = dmsService.getDocumentMetadata(documentId);
		List<CmisMetadataDTO> docProps = docDTO.getMetadata();
		for (CmisMetadataDTO meta : docProps) {
			if (DmsComuneFirenzeConstants.STATO_CONSERVAZIONE.equalsIgnoreCase(
				meta.getPrefix() + DmsComuneFirenzeConstants.PREFIX_SEPARATOR + meta.getName() )) {
				if (DmsComuneFirenzeConstants.STATO_CONSERVAZIONE_DA_INVIARE_VAL
						.equalsIgnoreCase(meta.getValue())) {
					log.info("Stato Conservazione Documento: " + meta.getValue());
					return;
				}
				else {
					log.error("Stato Conservazione Documento: " + meta.getValue());
					throw new DmsException("Errore Stato Conservazione Documento: " + meta.getValue());
				}
			}
		}
		log.info("Metadati relativi alla Conservazione sostitutiva non presenti in: " + documentId);
	}
	
}
