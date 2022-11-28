package it.linksmt.assatti.service;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Assessorato;
import it.linksmt.assatti.datalayer.domain.AttoHasDestinatario;
import it.linksmt.assatti.datalayer.domain.IDestinatarioInterno;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.DestinatarioInternoRepository;
import it.linksmt.assatti.datalayer.repository.TipoDestinatarioRepository;
import it.linksmt.assatti.service.util.PaginationUtil;


@Service
public class DestinatarioInternoService {

	@Inject
	private DestinatarioInternoRepository destinatarioInternoRepository;

	@Inject
	private TipoDestinatarioRepository tipoDestinatarioRepository;
	
	@Inject
	private AooService aooService;
	
	@Inject
	private NewsService newsService;
	
	@Inject
	private AssessoratoService assessoratoService;
	
	@Inject
	private UtenteService utenteService;
	
	private final Logger log = LoggerFactory.getLogger(DestinatarioInternoService.class);
	
	@Transactional(readOnly = true)
	public Set<IDestinatarioInterno> findAllByTipo(String tipoDestinatario, Integer offset, Integer limit){
		Set<IDestinatarioInterno> destinatari = new HashSet<IDestinatarioInterno>();
		if(tipoDestinatario.equalsIgnoreCase("aoo")){
			for(IDestinatarioInterno destinatario : aooService.findAll(PaginationUtil.generatePageRequest(offset, limit))){
				destinatari.add(destinatario);
			}
		}else if(tipoDestinatario.equalsIgnoreCase("utente")){
			for(IDestinatarioInterno destinatario : utenteService.findAll(PaginationUtil.generatePageRequest(offset, limit))){
				destinatari.add(destinatario);
			}
		}else if(tipoDestinatario.equalsIgnoreCase("assessorato")){
			for(IDestinatarioInterno destinatario : assessoratoService.findAll(PaginationUtil.generatePageRequest(offset, limit))){
				destinatari.add(destinatario);
			}
		}else{
			destinatari = new HashSet<IDestinatarioInterno>();
		}
		return destinatari;
	}
	
	@Transactional(readOnly = true)
	public Set<AttoHasDestinatario> getDestinatariInterniByAttoId(Long attoId, boolean atto){
		Set<AttoHasDestinatario> destinatari = null;
		if(atto) destinatari = destinatarioInternoRepository.findByAttoId(attoId);
		else destinatari = destinatarioInternoRepository.findBySedutaId(attoId);
		for(AttoHasDestinatario destinatario : destinatari){
			if(destinatario.getTipoDestinatario().getDescrizione().equalsIgnoreCase("aoo")){
				destinatario.setDestinatario(aooService.findOne(destinatario.getDestinatarioId()));
			}else if(destinatario.getTipoDestinatario().getDescrizione().equalsIgnoreCase("utente")){
				destinatario.setDestinatario(utenteService.findOne(destinatario.getDestinatarioId()));
			}else if(destinatario.getTipoDestinatario().getDescrizione().equalsIgnoreCase("assessorato")){
				destinatario.setDestinatario(assessoratoService.findOne(destinatario.getDestinatarioId()));
			}else{
				log.error("tipo di destinatario interno non gestito", destinatario.getTipoDestinatario().getDescrizione());
			}
		}
		return destinatari;
	}
	
	@Transactional(readOnly = true)
	public Set<AttoHasDestinatario> getDestinatariInterniByADocumentoId(Long documentoId){
		Set<AttoHasDestinatario> destinatari = destinatarioInternoRepository.findByDocumentoPdfId(documentoId);
		for(AttoHasDestinatario destinatario : destinatari){
			if(destinatario.getTipoDestinatario().getDescrizione().equalsIgnoreCase("assessorato")){
				destinatario.setDestinatario(assessoratoService.findOne(destinatario.getDestinatarioId()));
			}else{
				log.error("tipo di destinatario interno non gestito", destinatario.getTipoDestinatario().getDescrizione());
			}
		}
		return destinatari;
	}
	
	public void fillDestinatarioInterno(AttoHasDestinatario destinatario){
		if(destinatario.getTipoDestinatario().getDescrizione().equalsIgnoreCase("aoo")){
			destinatario.setDestinatario(aooService.findOne(destinatario.getDestinatarioId()));
		}else if(destinatario.getTipoDestinatario().getDescrizione().equalsIgnoreCase("utente")){
			destinatario.setDestinatario(utenteService.findOne(destinatario.getDestinatarioId()));
		}else if(destinatario.getTipoDestinatario().getDescrizione().equalsIgnoreCase("assessorato")){
			destinatario.setDestinatario(assessoratoService.findOne(destinatario.getDestinatarioId()));
		}else{
			log.error("tipo di destinatario interno non gestito", destinatario.getTipoDestinatario().getDescrizione());
		}
	}
	
	@Transactional(readOnly = false)
	public void salvaDestinatariInterni(Set<AttoHasDestinatario> destinatariInterni, Long attoId,boolean atto){
		Integer notificheAtto = null;
		if(atto) notificheAtto = newsService.getNotificheByIdAtto(attoId);
		else notificheAtto = 0;
		try{
			if(notificheAtto!=null && notificheAtto == 0){
				if(atto)destinatarioInternoRepository.deleteByAttoId(attoId);
				else destinatarioInternoRepository.deleteBySedutaId(attoId);
				if(destinatariInterni!=null && destinatariInterni.size() > 0){
					for(AttoHasDestinatario attodest : destinatariInterni){
						if(attodest.getDestinatario()!=null && attodest.getDestinatario().getId()!=null){
							attodest.setDestinatarioId(attodest.getDestinatario().getId());
							
							if(attodest.getDestinatario().getClassName().equalsIgnoreCase(Aoo.class.getCanonicalName())) 
								attodest.setTipoDestinatario(tipoDestinatarioRepository.findByDescrizione("Aoo"));
							else if(attodest.getDestinatario().getClassName().equalsIgnoreCase(Assessorato.class.getCanonicalName())) 
								attodest.setTipoDestinatario(tipoDestinatarioRepository.findByDescrizione("Assessorato"));
							else if(attodest.getDestinatario().getClassName().equalsIgnoreCase(Utente.class.getCanonicalName())) 
								attodest.setTipoDestinatario(tipoDestinatarioRepository.findByDescrizione("Utente"));
							
						}
						destinatarioInternoRepository.save(attodest);
					}
				}
			}else{
				log.warn("Sono già state inviate notifiche e-mail per cui non è più possibile aggiornare la lista dei destinatari");
			}
		}catch(ConstraintViolationException e){
			log.warn("Sono già state inviate notifiche e-mail per cui non è più possibile aggiornare la lista dei destinatari");
		}
	}
	
	@Transactional(readOnly = false)
	public AttoHasDestinatario salvaDestinatarioInterno(AttoHasDestinatario destinatarioToBeSaved){
		return destinatarioInternoRepository.save(destinatarioToBeSaved);
	}
	
	@Transactional(readOnly = true)
	public AttoHasDestinatario getDestinatarioInternoByDestinatarioIdAndDocumentoPdfId(Long destinatarioId, Long documentoPdfId){
		AttoHasDestinatario destinatario = destinatarioInternoRepository.findByDestinatarioIdAndDocumentoPdfId(destinatarioId, documentoPdfId);
		if (destinatario != null){
			if(destinatario.getTipoDestinatario().getDescrizione().equalsIgnoreCase("aoo")){
				destinatario.setDestinatario(aooService.findOne(destinatario.getDestinatarioId()));
			}else if(destinatario.getTipoDestinatario().getDescrizione().equalsIgnoreCase("utente")){
				destinatario.setDestinatario(utenteService.findOne(destinatario.getDestinatarioId()));
			}else if(destinatario.getTipoDestinatario().getDescrizione().equalsIgnoreCase("assessorato")){
				destinatario.setDestinatario(assessoratoService.findOne(destinatario.getDestinatarioId()));
			}else{
				log.error("tipo di destinatario interno non gestito", destinatario.getTipoDestinatario().getDescrizione());
			}
		}
		
		return destinatario;
	}
	 
}
