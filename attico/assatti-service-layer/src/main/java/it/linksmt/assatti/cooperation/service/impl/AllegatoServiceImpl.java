/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.cooperation.dto.AllegatoDto;
import it.linksmt.assatti.cooperation.service.AllegatoService;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.service.exception.ServiceException;

/**
 * @author Gianluca Pindinelli
 *
 */
@Service
@Transactional
public class AllegatoServiceImpl implements AllegatoService {

	private final Logger log = LoggerFactory.getLogger(AllegatoServiceImpl.class);

	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private AttoService attoService;

	/*
	 * (non-Javadoc)
	 *
	 * @see it.linksmt.assatti.cooperation.service.AllegatoService#getDocumenoPrincipale(it.linksmt.
	 * assatti.datalayer.domain.Atto)
	 */
	@Override
	public AllegatoDto getDocumenoPrincipaleAsAllegatoDto(Atto atto, boolean completo) throws ServiceException {
		
		AllegatoDto allegatoDto = new AllegatoDto();
		if(completo) {
			log.info("Cerco documento principale per atto:"+atto.getId() + " "+atto.getCodiceCifra()+" completo con omissis null");
		}else {
			log.info("Cerco documento principale per atto:"+atto.getId() + " "+atto.getCodiceCifra()+" non completo con omissis null");
		}
		DocumentoPdf docEntity = attoService.getDocumentoPrincipale(atto, completo, null);

		if (docEntity == null) {
			log.info("non trovato");
			if(completo) {
				log.info("Cerco documento principale per atto:"+atto.getId() + " "+atto.getCodiceCifra()+" completo con omissis true");
			}else {
				log.info("Cerco documento principale per atto:"+atto.getId() + " "+atto.getCodiceCifra()+" non completo con omissis true");
			}
			docEntity = attoService.getDocumentoPrincipale(atto, completo, true);
			if(docEntity == null) {
				throw new ServiceException("Non è stato trovato nessun documento principale " + (completo ? "completo" : "firmato") + " per l'atto con id : " + atto.getId());
			}
		}else {
			log.info("trovato");
		}

		File file = docEntity.getFile();
		allegatoDto.setContentType(file.getContentType());
		allegatoDto.setPrincipale(true);
		allegatoDto.setNome(file.getNomeFile());
		allegatoDto.setDescrizione(atto.getTipoAtto().getDescrizione());
		try {
			String cmisObjectId = file.getCmisObjectId();
			byte[] content = dmsService.getContent(cmisObjectId);
			allegatoDto.setContenuto(content);
		}
		catch (DmsException e) {
			log.error("getDocumenoPrincipale :: " + e.getMessage(), e);
			throw new ServiceException("Impossibile caricare il contenuto del file allegato con id : " + file.getId() + " :: messaggio eccezione : " + e.getMessage(), e);
		}

		return allegatoDto;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.linksmt.assatti.cooperation.service.AllegatoService#getAllegati(it.linksmt.assatti.
	 * datalayer.domain.Atto)
	 */
	@Override
	public List<AllegatoDto> getAllegati(Atto atto, boolean includiNonPubblicabili) throws ServiceException {

		List<AllegatoDto> allegati = null;

		if (atto.getAllegati() != null) {
			allegati = new ArrayList<>();
			for (DocumentoInformatico allEnt : atto.getAllegati()) {
				if(allEnt.getTipoAllegato()!=null && allEnt.getTipoAllegato().getCodice().equalsIgnoreCase("PARTE_INTEGRANTE")) {
					if ((includiNonPubblicabili || (allEnt.getPubblicabile() != null && allEnt.getPubblicabile().booleanValue()))) {
						File fileAll = null;
						if (allEnt.getOmissis() == null || allEnt.getOmissis() == false) {
							fileAll = allEnt.getFile();
						}
						else {
							fileAll = allEnt.getFileomissis();
						}
	
						AllegatoDto allegatoDto = new AllegatoDto();
						allegatoDto.setContentType(fileAll.getContentType());
						allegatoDto.setPrincipale(false);
						allegatoDto.setNome(fileAll.getNomeFile());
						allegatoDto.setTitolo(allEnt.getTitolo());
						allegatoDto.setDescrizione(atto.getTipoAtto().getDescrizione());
						allegatoDto.setRiservato(allEnt.getPubblicabile() == null || !allEnt.getPubblicabile());
						String cmisObjectId = fileAll.getCmisObjectId();
						try {
							byte[] content = dmsService.getContent(cmisObjectId);
							allegatoDto.setContenuto(content);
							allegati.add(allegatoDto);
						}
						catch (DmsException e) {
							throw new ServiceException("Impossibile caricare il contenuto del file allegato con id : " + fileAll.getId() + " :: messaggio eccezione : " + e.getMessage(), e);
						}
					}
				}
			}
		}

		return allegati;
	}

}
