package it.linksmt.assatti.service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Evento;
import it.linksmt.assatti.datalayer.domain.EventoEnum;
import it.linksmt.assatti.datalayer.domain.QEvento;
import it.linksmt.assatti.datalayer.domain.TipoEvento;
import it.linksmt.assatti.datalayer.repository.EventoRepository;
import it.linksmt.assatti.datalayer.repository.TipoEventoRepository;

/**
 * Service class for managing events.
 */
@Service
@Transactional
public class EventoService {
	private final Logger log = LoggerFactory.getLogger(EventoService.class);
	
	@Inject
	private EventoRepository eventoRepository;

	@Inject
	private TipoEventoRepository tipoEventoRepository;
	
	@Transactional
	public void delete(Long eventoId){
		eventoRepository.delete(eventoId);
	}
	
	@Transactional
	public void annullamentoAutomatico(Long attoId){
		eventoRepository.annullamentoAutomatico(attoId);
	}
	
	@Transactional(readOnly=true)
	public List<Evento> findByAtto(Atto atto) {
		List<Evento> eventi = eventoRepository.findByAttoOrderByDataCreazioneAsc(atto);
		for(Evento evento : eventi){
			evento.setAtto(null);
		}
		return eventi;
	}
	
	@Transactional(readOnly=true)
	public List<Evento> findByAttoId(Long attoId) {
		
		List<Evento> eventi = Lists.newArrayList(eventoRepository.findAll(QEvento.evento.atto.id.eq(attoId)));
		if(eventi!=null) {
			Collections.sort(eventi, new Comparator<Evento>() {
				  public int compare(Evento o1, Evento o2) {
				      return o1.getDataCreazione().compareTo(o2.getDataCreazione());
				  }
				});
		}
		return eventi;
	}
	
	@Transactional(readOnly=true)
	public Set<Long> findAttosByEsecutivita(DateTime dataEsecutivita){
		TipoEvento eventoEsecutivita = tipoEventoRepository.findByCodice("ESECUTIVITA");
		Set<Long> ids = new HashSet<Long>();
		BooleanExpression ex = QEvento.evento.tipoEvento.eq(eventoEsecutivita).and(QEvento.evento.dataCreazione.goe(dataEsecutivita));
		Iterable<Evento> eventi = eventoRepository.findAll(ex);
		for(Evento evento : eventi){
			ids.add(evento.getAtto().getId());
		}
		return ids;
	}
	
	@Transactional
	public Set<Long> getAttiEsecutivi() {
		Set<Long> attoIds = new HashSet<Long>();
		List<BigInteger> attiBI = eventoRepository.getAttiEsecutivi();
		for(BigInteger bi : attiBI){
			attoIds.add(bi.longValue());
		}
		return attoIds;
	}	
	
	@Transactional
	public Evento pubblicazioneTerminata(Atto atto) {
		return saveEvento(EventoEnum.EVENTO_PUBBLICAZIONE_TERMINATA.getDescrizione(), atto);
	}
	
	@Transactional
	public Evento saveEventoIterTerminato(Atto atto) {
		return saveEvento(EventoEnum.EVENTO_ITER_TERMINATO.getDescrizione(), atto);
	}
	
	
	@Transactional
	public Evento saveEvento(String codiceEvento, Atto atto) {
		Evento evento = new Evento();
		evento.setTipoEvento(tipoEventoRepository.findByCodice(codiceEvento));
		evento.setAtto(atto);
		evento.setDataCreazione(new DateTime());
		return eventoRepository.save(evento);
	}
	
	@Transactional
	public Evento saveEventoAttoCollegato(String descrizione, Atto atto) {
		Evento evento = new Evento();
		evento.setTipoEvento(tipoEventoRepository.findByCodice(EventoEnum.EVENTO_PROVVEDIMENTO_COLLEGATO.getDescrizione()));
		evento.setInfoAggiuntive(descrizione);
		evento.setAtto(atto);
		evento.setDataCreazione(new DateTime());
		return eventoRepository.save(evento);
	}
}
