package it.linksmt.assatti.service;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.TipoAllegato;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.domain.Verbale;
import it.linksmt.assatti.datalayer.repository.DocumentoInformaticoRepository;
import it.linksmt.assatti.datalayer.repository.FileRepository;
import it.linksmt.assatti.datalayer.repository.TipoAllegatoRepository;
import it.linksmt.assatti.service.converter.DmsMetadataConverter;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.utility.FileChecksum;
import it.linksmt.assatti.utility.StringUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class DocumentoInformaticoService {
	private final Logger log = LoggerFactory
			.getLogger(DocumentoInformaticoService.class);

	@Autowired
	private ServiceUtil serviceUtil;

	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private DmsMetadataConverter dmsMetadataConverter;
	
	@Inject
	private FileRepository fileRepository;

	@Inject
	private DocumentoInformaticoRepository documentoInformaticoRepository;
	
	@Inject
	private TipoDocumentoService tipoDocumentoService;
	
	@Autowired
	private TipoAllegatoRepository tipoAllegatoRepository;
	
	@Transactional
	public List<DocumentoInformatico> getAllegatiByAtto(Long id) {
		return documentoInformaticoRepository.findAllByAttoId(id);
	}
	
	public List<DocumentoInformatico> getAll() {
		return documentoInformaticoRepository.findAll();
	}
	
	public List<DocumentoInformatico> findByAtto(Long idAtto) {
		return documentoInformaticoRepository.findAllByAttoId(idAtto);
	}
	
	public List<DocumentoInformatico> findByAttoCodiceTipoAllegato(Long idAtto, String codiceTipoAllegato) throws ServiceException {
		List<DocumentoInformatico> listAllegato = documentoInformaticoRepository.findAllByAttoId(idAtto);
		if(codiceTipoAllegato!=null && listAllegato!=null) {
			Iterator<DocumentoInformatico> it = listAllegato.iterator();
			while(it.hasNext()) {
				DocumentoInformatico d = it.next();
				if(d.getTipoAllegato() == null || !d.getTipoAllegato().getCodice().equals(codiceTipoAllegato)) {
					it.remove();
				}
			}
		}
		return listAllegato;
	}
	
	public List<Object[]> findByAttoDaRiversareInAlbo() {
		return documentoInformaticoRepository.findByAttoDaRiversareInAlbo();
	}
	
	@Transactional
	public File saveFile(Atto atto, MultipartFile multipartFile ) throws IOException, GestattiCatchedException {
		log.debug("save atto" + atto);
		
		/*
		 * Salvo su repository documentale
		 */
		String objectId = "";
		try {
			TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.allegato.name());
			String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, atto.getAoo(), atto.getTipoAtto(), atto.getCodiceCifra());
			
			Map<String, Object> createProps = dmsMetadataConverter.getPropertiesForCreate(atto, tipoDocumento, false);
			
			DateFormat dfmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String prefixTimeStamp = dfmt.format(new Date()) + "_";
			
			objectId = dmsService.save(attoFolderPath, multipartFile.getBytes(), 
					prefixTimeStamp + multipartFile.getOriginalFilename(), multipartFile.getContentType(), createProps);
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e, e.getMessage());
		}

		File file = new File(multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getSize());
		file.setCmisObjectId(objectId);
		file.setSha256Checksum(FileChecksum.calcolaImpronta(multipartFile.getBytes()));
		file = fileRepository.save(file);
		
		return file;
	}

	@Transactional
	public void deleteDocumentoInformaticoAndFile(Long documentoInformaticoId) throws GestattiCatchedException {
		DocumentoInformatico docInfo = documentoInformaticoRepository.findOne(documentoInformaticoId);
		if(docInfo==null) {
			return;
		}
		File fileP = null;
		File fileO = null;
		if (docInfo.getFile() != null) {
			fileP = docInfo.getFile();
		}
		if (docInfo.getFileomissis() != null) {
			fileO = docInfo.getFileomissis();
		}
		
		if(fileP != null){
			fileRepository.delete(fileP.getId());
		}
		if(fileO != null){
			fileRepository.delete(fileO.getId());
		}
		
		documentoInformaticoRepository.delete(documentoInformaticoId);
		/*
		try {
			if(fileP != null){
				dmsService.remove(fileP.getCmisObjectId());
			}
			if(fileO != null){
				dmsService.remove(fileO.getCmisObjectId());
			}
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e, e.getMessage());
		}
		*/
	}
	
	@Transactional
	public void deleteOmissis(Long documentoInformaticoId) throws GestattiCatchedException {
		DocumentoInformatico docInfo = documentoInformaticoRepository.findOne(documentoInformaticoId);
		
		File fileO = null;
		if (docInfo.getFileomissis() != null) {
			fileO = docInfo.getFileomissis();
		}
		
		docInfo.setFileomissis(null);
		docInfo.setOmissis(false);
		
		documentoInformaticoRepository.save(docInfo);
		
		if(fileO != null){
			fileRepository.delete(fileO.getId());
		}
		/*
		try {
			if(fileO != null){
				dmsService.remove(fileO.getCmisObjectId());
			}
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e, e.getMessage());
		}
		*/
	}
	
	@Transactional(readOnly=true)
	public boolean isAllegatoProvvedimento(BigInteger documentoInformaticoId){
		BigInteger count = documentoInformaticoRepository.isAllegatoProvvedimento(documentoInformaticoId);
		return count != null && count.intValue() > 0;
	}
	
	@Transactional
	public DocumentoInformatico save (
			Atto atto,
			String fileName,
			String fileContentType,
			long fileSize,
			byte[] fileContent,
			Integer ordineInclusione,
			boolean isAllegatiProposta,
			String tipoAllegato,
			Boolean pubblicabile) 
					throws IOException, DmsException, ServiceException, GestattiCatchedException {
		log.debug("save atto" + atto);
		
		List<DocumentoInformatico> allegatiEsistenti = getAllegatiByAtto(atto.getId());
		
		if (allegatiEsistenti != null) {
			for (DocumentoInformatico documentoInformatico : allegatiEsistenti) {
				if (StringUtil.trimStr(documentoInformatico.getNomeFile())
						.equalsIgnoreCase(StringUtil.trimStr(fileName))) {
					throw new GestattiCatchedException("Un allegato con lo stesso nome file " + GestattiCatchedException.NEW_LINE
							+ "risulta essere presente per questo atto. " + GestattiCatchedException.NEW_LINE
							+ "Si prega di caricare un file con diverso nome.");
				}
			}
		}
		
		/*
		 * Salvo allegato su repository documentale
		 */
		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.allegato.name());
		String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, atto.getAoo(), atto.getTipoAtto(), atto.getCodiceCifra());
		
		Map<String, Object> createProps = dmsMetadataConverter.getPropertiesForCreate(atto, tipoDocumento, false);
		DateFormat dfmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String prefixTimeStamp = dfmt.format(new Date()) + "_";
		String cmisObjectId = dmsService.save(attoFolderPath, fileContent, prefixTimeStamp + fileName, fileContentType, createProps);

		File file = new File(fileName, fileContentType, fileSize);
		file.setCmisObjectId(cmisObjectId);
		file.setSha256Checksum(FileChecksum.calcolaImpronta(fileContent));
		fileRepository.save(file);

		DocumentoInformatico entity = new DocumentoInformatico(file, atto, ordineInclusione);
		if(entity.getNomeFile().length()>255) {
			entity.setTitolo(entity.getNomeFile().substring(0, 254));
			entity.setOggetto(entity.getNomeFile().substring(0, 254));
		}else {
			entity.setTitolo(entity.getNomeFile());
			entity.setOggetto(entity.getNomeFile());
		}
		
		if(tipoAllegato!=null && !tipoAllegato.trim().isEmpty()) {
			TipoAllegato tipoAll = tipoAllegatoRepository.findByCodice(tipoAllegato);
			if(tipoAll!=null) {
				entity.setTipoAllegato(tipoAll);
				if(tipoAll.getCodice().equalsIgnoreCase("GENERICO")) {
					entity.setPubblicabile(false);
				}else {
					entity.setPubblicabile(pubblicabile);
					if(pubblicabile!= null && pubblicabile) {
						entity.setOmissis(false);
					}
				}
				
			}
		}
		entity.setAllegatoProvvedimento(!isAllegatiProposta);
		entity = documentoInformaticoRepository.save(entity);
		
		return entity;
	}

	@Transactional
	public DocumentoInformatico save(Atto atto, MultipartFile multipartFile,
			Integer ordineInclusione, boolean isAllegatiProposta, String tipoAllegato, Boolean pubblicabile) 
					throws IOException, DmsException, ServiceException, GestattiCatchedException {
		return save(atto, multipartFile.getOriginalFilename(), 
				multipartFile.getContentType(), multipartFile.getSize(), 
				multipartFile.getBytes(), ordineInclusione, isAllegatiProposta, tipoAllegato, pubblicabile);
	}
	
	@Transactional
	public void save(DocumentoInformatico documentoInformatico){
		if(documentoInformatico.getFile()!=null){
			fileRepository.save(documentoInformatico.getFile());
		}else if(documentoInformatico.getFileomissis()!=null){
			fileRepository.save(documentoInformatico.getFileomissis());
		}
		documentoInformaticoRepository.save(documentoInformatico);
	}
	
	/*
	 * TODO: In ATTICO non previsto
	 * 
	@Transactional
	public void save(
			Parere parere,
			MultipartFile multipartFile,
			Integer ordineInclusione
			) throws IOException, DmsException {
		log.debug("save parere" + parere);
		String originalFilename = multipartFile.getOriginalFilename();
		String nome = "";
		
		if(parere.getTipoParere() != null && !"".equals(parere.getTipoParere())) {
			nome = "Allegato_" + parere.getTipoParere().replace(" Organo di ", "").replace(" ", "") + "_" + originalFilename;
		} else {
			nome = "Allegato-" + parere.getAoo().getCodice() + "-" + parere.getAtto().getTipoAtto().getCodice() + "-" + originalFilename;
		}
		
		// Salvo documento su repository documentale
		
		String cmisObjectId = null;
		try {
			TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.parere.name());
			String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, parere.getAoo(), null, null);
			cmisObjectId = dmsService.save(attoFolderPath, multipartFile.getBytes(), nome, multipartFile.getContentType(), null);
		}
		catch (Exception e) {
			throw new DmsException(e);
		}
		
		File file = new File(nome, multipartFile.getContentType(), multipartFile.getSize());
		file.setCmisObjectId(cmisObjectId);
		file.setSha256Checksum(FileChecksum.calcolaImpronta(multipartFile.getBytes()));
		fileRepository.save(file);

		DocumentoInformatico entity = new DocumentoInformatico(file, parere, ordineInclusione);
		entity = documentoInformaticoRepository.save(entity);
	}
	*/
	
	@Transactional
	public Set<DocumentoInformatico> save(Atto atto, MultipartFile[] files, boolean isAllegatiProposta, String tipoAllegato, Boolean pubblicabile) 
			throws GestattiCatchedException {
		
		Set<DocumentoInformatico> allegati = new HashSet<DocumentoInformatico>();
		if (files != null && files.length > 0) {
			int ordine = 0;
			if (atto.getAllegati() != null) {
				ordine = atto.getAllegati().size();
			}
			for (MultipartFile multipartFile : files) {
				try {
					allegati.add(save(atto, multipartFile, ordine++, isAllegatiProposta, tipoAllegato, pubblicabile));
				}
				catch (IOException e) {
					log.debug("IOException", e);
					throw new FileUploadException(e.getMessage(), e);
				}
				catch (DmsException e) {
					log.debug("DmsException", e);
					throw new FileUploadException(e.getMessage(), e);
				}
				catch (ServiceException e) {
					log.debug("ServiceException", e);
					throw new FileUploadException(e.getMessage(), e);
				}
			}
		}
		return allegati;
	}
	
	@Transactional(readOnly=true)
	public DocumentoInformatico findOne(Long id) {
		return documentoInformaticoRepository.findOne(id);
	}

	public DocumentoInformatico saveOmissis(Atto atto, DocumentoInformatico allegato, MultipartFile multipartFile) throws DmsException {
		if (multipartFile != null ) {
			try {
				/*
				 * Salvo documento su repository documentale
				 */
				String cmisObjectId = null;
				try {
					
					TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.allegato.name());

					String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, 
							atto.getAoo(), atto.getTipoAtto(), atto.getCodiceCifra());
					
					Map<String, Object> createProps = dmsMetadataConverter.getPropertiesForCreate(
							atto, tipoDocumento, false);
					
					DateFormat dfmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
					String prefixTimeStamp = dfmt.format(new Date()) + "_";
					
					cmisObjectId = dmsService.save(attoFolderPath, 
							multipartFile.getBytes(), prefixTimeStamp + multipartFile.getOriginalFilename(), 
							multipartFile.getContentType(), createProps);
				}
				catch (Exception e) {
					throw new DmsException(e);
				}

				File fileomissis = new File(multipartFile.getOriginalFilename(),
						multipartFile.getContentType(), multipartFile.getSize());
				fileomissis.setCmisObjectId(cmisObjectId);
				fileomissis.setSha256Checksum(FileChecksum.calcolaImpronta(multipartFile.getBytes()));
				
				fileomissis = fileRepository.save(fileomissis);
				
				if ((allegato.getTipoAllegato() != null) && 
						!StringUtil.isNull(allegato.getTipoAllegato().getCodice())) {
					TipoAllegato tipoAllegato = tipoAllegatoRepository.findByCodice(allegato.getTipoAllegato().getCodice());
					allegato.setTipoAllegato(tipoAllegato);
				}
								
				allegato.setFileomissis(fileomissis);
				allegato = documentoInformaticoRepository.save(allegato);
			}
			catch (IOException e) {
				log.debug("IOException", e);
				throw new FileUploadException(e.getMessage());
			}
		}
		
		return allegato;
		
	}

	@Transactional
	public DocumentoInformatico save(Verbale verbale, MultipartFile multipartFile, Integer ordineInclusione) throws IOException, GestattiCatchedException {
		if (verbale != null && verbale.getId() != null){
			log.debug(String.format("save verbale id:%s", verbale.getId()));
		}
		
		/*
		 * Salvo allegato su repository documentale
		 */
		String cmisObjectId = null;
		try {
//			it.linksmt.assatti.datalayer.domain.File df = documentoInformatico.getFile();
			String tipoDocCodice = "";
			if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
					verbale.getSedutaGiunta().getOrgano())) {
				tipoDocCodice = TipoDocumentoEnum.verbalegiunta.name();
			}
			else {
				tipoDocCodice = TipoDocumentoEnum.verbaleconsiglio.name();
			}
			
			TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(tipoDocCodice);
			
			String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, null, null, null);
			
			DateFormat dfmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String prefixTimeStamp = dfmt.format(new Date()) + "_";
			cmisObjectId = prefixTimeStamp + dmsService.save(attoFolderPath, multipartFile.getBytes(), 
					multipartFile.getOriginalFilename(), multipartFile.getContentType(), null);
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e, e.getMessage());
		}

		File file = new File(multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getSize());
		file.setCmisObjectId(cmisObjectId);
		file.setSha256Checksum(FileChecksum.calcolaImpronta(multipartFile.getBytes()));
		fileRepository.save(file);

//		verbale.setSedutaGiunta(new SedutaGiunta(
//				verbale.getSedutaGiunta().getId(), 
//				verbale.getSedutaGiunta().getLuogo(), 
//				verbale.getSedutaGiunta().getPrimaConvocazioneInizio()));
		DocumentoInformatico entity = new DocumentoInformatico(file, verbale, ordineInclusione);
		return documentoInformaticoRepository.save(entity);
	}
	
	
	@Transactional
	public DocumentoInformatico save(Parere parere, MultipartFile multipartFile,
			Integer ordineInclusione) throws IOException,GestattiCatchedException {
		
		if (parere != null && parere.getId() != null){
			log.debug(String.format("save parere id:%s", parere.getId()));
		}
		
		/*
		 * Salvo allegato su repository documentale
		 */
		String cmisObjectId = null;
		try {

			String tipoDocCodice = TipoDocumentoEnum.allegato.name();
			TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(tipoDocCodice);
			String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, null, null, parere.getAtto().getCodiceCifra());
			
			Map<String, Object> createProps = dmsMetadataConverter.getPropertiesForCreate(
					parere.getAtto(), tipoDocumento, false);
			
			DateFormat dfmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String prefixTimeStamp = dfmt.format(new Date()) + "_";
			cmisObjectId = dmsService.save(attoFolderPath, multipartFile.getBytes(), 
					prefixTimeStamp + multipartFile.getOriginalFilename(), multipartFile.getContentType(), createProps);
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e, e.getMessage());
		}

		File file = new File(multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getSize());
		file.setCmisObjectId(cmisObjectId);
		file.setSha256Checksum(FileChecksum.calcolaImpronta(multipartFile.getBytes()));
		fileRepository.save(file);
		
		DocumentoInformatico entity = new DocumentoInformatico(file, parere, ordineInclusione);
		if(entity.getPubblicabile()==null) {
			entity.setPubblicabile(true);
		}

		return documentoInformaticoRepository.save(entity);
		
	}

	@Transactional
	public Set<DocumentoInformatico> save(Verbale verbale, MultipartFile[] files) throws GestattiCatchedException {
		Set<DocumentoInformatico> allegati = new HashSet<DocumentoInformatico>();
		if (files != null && files.length > 0) {
			int ordine = 0;
			if (verbale.getAllegati() != null) {
				ordine = verbale.getAllegati().size();
			}
			for (MultipartFile multipartFile : files) {
				try {
					allegati.add(save(verbale, multipartFile, ordine++));
				} catch (IOException e) {
					log.debug("IOException", e);
					throw new FileUploadException(e.getMessage());
				}
			}
		}
		return allegati;
	}
	
	@Transactional
	public Set<DocumentoInformatico> save(Parere parere, MultipartFile[] files)  throws GestattiCatchedException{
		Set<DocumentoInformatico> allegati = new HashSet<DocumentoInformatico>();
		allegati.addAll(parere.getAllegati());
		if (files != null && files.length > 0) {
			int ordine = 0;
			if (parere.getAllegati() != null) {
				ordine = parere.getAllegati().size();
			}
			for (MultipartFile multipartFile : files) {
				try {
					allegati.add(save(parere, multipartFile, ordine++));
				} 
				catch (IOException e) {
					log.debug("IOException", e);
					throw new FileUploadException(e.getMessage());
				}
			}
		}
		return allegati;
	}
	

}
