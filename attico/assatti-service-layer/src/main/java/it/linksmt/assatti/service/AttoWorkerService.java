package it.linksmt.assatti.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.AttoWorker;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QAttoWorker;
import it.linksmt.assatti.datalayer.repository.AttoWorkerRepository;
import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

@Service
@Transactional
public class AttoWorkerService {
	private final Logger log = LoggerFactory.getLogger(AttoWorkerService.class);
	
    @Inject
    private AttoWorkerRepository attoWorkerRepository;
    
	@Transactional
	public void checkAndInsertWorker(Long attoId, Long profiloId){
		BooleanExpression p = QAttoWorker.attoWorker.atto.id.eq(attoId).and(QAttoWorker.attoWorker.profilo.id.eq(profiloId));
		if(attoWorkerRepository.count(p) < 1){
			AttoWorker aw = new AttoWorker();
			Profilo profilo = new Profilo();
			profilo.setId(profiloId);
			Atto atto = new Atto();
			atto.setId(attoId);
			aw.setAtto(atto);
			aw.setProfilo(profilo);
			attoWorkerRepository.save(aw);
		}
	}
	
	@Transactional
	public void deleteByAttoIdAndProfiloId(Long attoId, Long profiloId){
		AttoWorker attoWorker = attoWorkerRepository.findByAttoIdAndProfiloId(attoId, profiloId);
		if(attoWorker!=null) {
			attoWorkerRepository.delete(attoWorker);
		}
	}
	
	/**
	 * Quando l'iter di un atto termina, le informazioni su chi ci ha lavorato vengono eliminate da questa tabella
	 * 
	 * @param attoId
	 */
	@Transactional
	public void iterTerminato(Long attoId){
		attoWorkerRepository.iterTerminato(attoId);
	}
	
	/**
	 * Verifica se esistono atti lavorati dal profilo il cui iter non è ancora terminato
	 * @param profiloId
	 * @return
	 */
	@Transactional(readOnly=true)
	public boolean hasAttoWorkNotEnd(Long profiloId){
		BooleanExpression p = QAttoWorker.attoWorker.profilo.id.eq(profiloId).and(QAttoWorker.attoWorker.atto.fineIterDate.isNull());
		return attoWorkerRepository.count(p) > 0;
	}
	
	/**
	 * Restituisce gli atti lavorati dal profilo il cui iter non è ancora terminato
	 * @param profiloId
	 * @return
	 */
	@Transactional(readOnly=true)
	public Set<Atto> getAttiNotEndWorkedByProfilo(Long profiloId){
		BooleanExpression p = QAttoWorker.attoWorker.profilo.id.eq(profiloId);
		List<AttoWorker> awList = Lists.newArrayList(attoWorkerRepository.findAll(p));
		Set<Atto> atti = new HashSet<Atto>();
		for(AttoWorker aw : awList){
			atti.add(aw.getAtto());
		}
		return atti;
	}
}
