package it.linksmt.assatti.service;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.QAvanzamento;
import it.linksmt.assatti.datalayer.repository.AvanzamentoRepository;
import it.linksmt.assatti.service.dto.AvanzamentoCriteriaDTO;
import com.google.common.collect.Lists;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing avanzamenti.
 */
@Service
@Transactional
public class AvanzamentoService {
	private final Logger log = LoggerFactory.getLogger(AvanzamentoService.class);
	
	@Inject
	private AvanzamentoRepository avanzamentoRepository;
	
	@Transactional
	public Avanzamento salva(Avanzamento avanzamento) {
		return avanzamentoRepository.save(avanzamento);
	}
	
	@Transactional
	public void delete(Long id){
		avanzamentoRepository.delete(id);
	}
	
    @Transactional(readOnly=true)
	public List<Avanzamento> findByAtto_id(Long id){
		List<Avanzamento> list = null;
		BooleanExpression b = QAvanzamento.avanzamento.atto.id.eq(id);
		list = Lists.newArrayList(avanzamentoRepository.findAll(b, new OrderSpecifier<>(Order.ASC, QAvanzamento.avanzamento.dataAttivita)));
		return list;
	}
    
    @Transactional(readOnly=true)
	public Avanzamento findLastByAtto_id(Long id){
		List<Avanzamento> list = null;
		BooleanExpression b = QAvanzamento.avanzamento.atto.id.eq(id);
		list = Lists.newArrayList(avanzamentoRepository.findAll(b, new OrderSpecifier<>(Order.DESC, QAvanzamento.avanzamento.dataAttivita)));
		return list!=null&&list.size()>0?list.get(0):null;
	}
    
    @Transactional(readOnly=true)
    public boolean profiloWorkedAtto(Long attoId, Long profiloId) {
    	boolean worked = false;
    	if(attoId!=null && profiloId!=null) {
	    	BooleanExpression b = QAvanzamento.avanzamento.atto.id.eq(attoId).and(QAvanzamento.avanzamento.profilo.id.eq(profiloId));
	    	worked = avanzamentoRepository.count(b) > 0L;
    	}
    	return worked;
    }
	
	@Transactional(readOnly=true)
	public Page<Avanzamento> findAll(final Pageable generatePageRequest,final AvanzamentoCriteriaDTO criteria){
		BooleanExpression expression = QAvanzamento.avanzamento.id.isNotNull().and(QAvanzamento.avanzamento.atto.isNotNull());
		
		if(criteria.getStato() != null && !"".equals(criteria.getStato()) ){
			expression = expression.and(QAvanzamento.avanzamento.stato.equalsIgnoreCase(criteria.getStato()));
		}
		
		if(criteria.getAttivita() != null && !"".equals(criteria.getAttivita()) ){
			expression = expression.and(QAvanzamento.avanzamento.attivita.containsIgnoreCase(criteria.getAttivita()));
		}
		
		if(criteria.getUtente() != null && !"".equals(criteria.getUtente()) ){
			expression = expression.and(QAvanzamento.avanzamento.createdBy.containsIgnoreCase(criteria.getUtente()));
		}
		
		if(criteria.getDataAttivita()  != null){
			DateTime dataAttivita = criteria.getDataAttivita().toDateTimeAtStartOfDay().toDateTime();
			expression = expression.and(QAvanzamento.avanzamento.dataAttivita.goe(dataAttivita));
		}
		
		if(criteria.getAtto()  != null && !"".equals(criteria.getAtto()) ){
			BooleanExpression expression2 = QAvanzamento.avanzamento.atto.codiceCifra.containsIgnoreCase(criteria.getAtto()).or(QAvanzamento.avanzamento.atto.oggetto.containsIgnoreCase(criteria.getAtto()));
			expression = expression.and(expression2);
		}
		
		Page<Avanzamento> lista = avanzamentoRepository.findAll(expression,generatePageRequest);
		
		return lista;
	}
	
}
