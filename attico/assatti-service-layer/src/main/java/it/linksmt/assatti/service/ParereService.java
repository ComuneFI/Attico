package it.linksmt.assatti.service;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.bpm.util.BpmThreadLocalUtil;
import it.linksmt.assatti.bpm.wrapper.ProfiloQualificaBean;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.ParereSintenticoEnum;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.TipoAzione;
import it.linksmt.assatti.datalayer.repository.ParereRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.TipoAzioneRepository;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
@Transactional
public class ParereService {
	private final Logger log = LoggerFactory.getLogger(ParereService.class);

	@Inject
	private ParereRepository parereRepository;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private TipoAzioneRepository tipoAzioneRepository;
	
	@Inject
	private DocumentoInformaticoService documentoInformaticoService;
	
	@Inject
	private DocumentoPdfService documentoPdfService;
	
	@Inject
	private WorkflowServiceWrapper workflowService;
	

	@Transactional(readOnly=true)
	public Parere findOne(final Long id){
		Parere parere = parereRepository.findOne(id);
		loadParere(parere);
		return parere;

	}
	
	@Transactional
	public void rimuoviParere(Long idParere) throws GestattiCatchedException {
		Parere p = parereRepository.findOne(idParere);
		if(p.getAllegati()!=null) {
			Set<DocumentoInformatico> allegati = p.getAllegati();
			p.getAllegati().clear();
			for(DocumentoInformatico allegato : allegati) {
				documentoInformaticoService.deleteDocumentoInformaticoAndFile(allegato.getId());
			}
		}
		
		if(p.getDocumentiPdf()!=null) {
			List<DocumentoPdf> documenti = p.getDocumentiPdf();
			p.getDocumentiPdf().clear();
			for(DocumentoPdf documento : documenti) {
				documentoPdfService.deleteDocumentoPdfAndFile(documento.getId());
			}
			
		}
		
		parereRepository.delete(p.getId());
	}
	
	@Transactional
	public Parere nonEspresso(Long idParere) throws GestattiCatchedException {
		Parere p = parereRepository.findOne(idParere);
		if(p.getAllegati()!=null) {
			Set<DocumentoInformatico> allegati = p.getAllegati();
			p.getAllegati().clear();
			for(DocumentoInformatico allegato : allegati) {
				documentoInformaticoService.deleteDocumentoInformaticoAndFile(allegato.getId());
			}
		}
		
		if(p.getDocumentiPdf()!=null) {
			List<DocumentoPdf> documenti = p.getDocumentiPdf();
			p.getDocumentiPdf().clear();
			for(DocumentoPdf documento : documenti) {
				documentoPdfService.deleteDocumentoPdfAndFile(documento.getId());
			}
			
		}
		
		p.setTitolo(WebApplicationProps.getProperty(ConfigPropNames.PARERE_QUARTIERE_REVISORE_CODICE_TIPO));
		p.setParereSintetico(ParereSintenticoEnum.NON_ESPRESSO.getCodice());
		return parereRepository.save(p);
	}
	
	@Transactional(readOnly=true)
	public Long getAooIdByParereId(Long parereId){
		return parereRepository.getAooIdByParereId(parereId);
	}

	private void loadParere(final Parere parere) {
		if(parere != null ){
			Parere minParere = new  Parere(parere.getId());
			Aoo aooMin = DomainUtil.minimalAoo( parere.getAoo() );
			parere.setAoo(aooMin);
			parere.setAnnullato(parere.getAnnullato());

			for (DocumentoInformatico allegato : parere.getAllegati() ) {
				allegato.setAvanzamento(   null );
				allegato.setParere( null );
			}


			for (DocumentoPdf allegato : parere.getDocumentiPdf()  ) {
				allegato.setParereId( minParere.getId() );
			}

//			for (SottoscrittoreParere sootscrittori : parere.getSottoscrittori()  ) {
//				sootscrittori.setParere(   minParere );
//				sootscrittori.getProfilo().setAoo(null);
//				sootscrittori.getProfilo().getDescrizione();
//
//				for (QualificaProfessionale qua : sootscrittori.getProfilo().getHasQualifica()) {
////					INNOVCIFRA-187
////					qua.setAoo(aooMin );
//					qua.getDenominazione();
//				}
//				sootscrittori.getQualificaProfessionale().getDenominazione();
//
//			}
		}
	}
	

	@Transactional
	public Parere save(final String taskBpmId, final Long profiloId, 
			Parere parere, Atto atto, DecisioneWorkflowDTO decisione) throws Exception {
		
		if (parere == null) {
			return null;
		}
		
		// Imposto valori non modificabili
		if (parere.getData() == null) {
			parere.setData(new DateTime());
		}
		
		if ((profiloId != null) && (profiloId.longValue() > 0)) {
			Profilo prof = profiloRepository.findOne(profiloId);
			if (prof != null) {
				parere.setProfilo(prof);
				if (parere.getAoo() == null && prof.getAoo() != null) {
					parere.setAoo(prof.getAoo());
				}
			}
		}
		
		if ( (atto != null) && (atto.getId() != null) ) {
			parere.setAtto(atto);
		}
		
		// Tipo e origine parere dalla configurazione pulsante
		if (decisione != null) {
			if ((parere.getTipoAzione() == null) && (!StringUtil.isNull(decisione.getTipoParere()))) {
				TipoAzione tipoPar = tipoAzioneRepository.findOne(decisione.getTipoParere());
				parere.setTipoAzione(tipoPar);
			}
			if (StringUtil.isNull(parere.getOrigine()) &&
				!StringUtil.isNull(decisione.getOrigineParere())) {
				parere.setOrigine(StringUtil.trimStr(decisione.getOrigineParere()));
			}
		}
		
		int giorniScadenza = -1;
		
		// Lettura dati task e assegnatario/aoo
		ProfiloQualificaBean profQualifica = workflowService.getProfiloQualificaEsecutore(taskBpmId);
		if (profQualifica != null) {
			if (profQualifica.getConfigurazioneIncaricoProfilo() != null) {
				if (profQualifica.getConfigurazioneIncaricoProfilo().getGiorniScadenza() != null) {
					giorniScadenza = profQualifica.getConfigurazioneIncaricoProfilo().getGiorniScadenza().intValue();
				}
			}
			if ((giorniScadenza < 1) && (profQualifica.getConfigurazioneIncaricoAoo() != null)) {
				if (profQualifica.getConfigurazioneIncaricoAoo().getGiorniScadenza() != null) {
					giorniScadenza = profQualifica.getConfigurazioneIncaricoAoo().getGiorniScadenza().intValue();
				}
			}
		}
		
		if (profQualifica.getDataAvvioTask() != null) {
			parere.setDataInvio(new DateTime(profQualifica.getDataAvvioTask()));
		}
		
		if ((parere.getDataScadenza() == null) && 
			(profQualifica.getDataIncarico() != null) && (giorniScadenza > 0)) {
						
			DateTime dataScadenza = new DateTime(profQualifica.getDataIncarico()).plusDays(giorniScadenza);
			parere.setDataScadenza(dataScadenza);
		}
		
		if (!StringUtil.isNull(profQualifica.getDescrizioneQualifica())) {
			parere.setDescrizioneQualifica(profQualifica.getDescrizioneQualifica());
		}
		
		parere = parereRepository.save(parere);
		
		// Al fine di salvare la motivazione all'interno delle note dell'avanzamento
		if (!StringUtil.isNull(parere.getParere())) {
			BpmThreadLocalUtil.setMotivazione(parere.getParere());
		}
		
		return parere;
	}
}
