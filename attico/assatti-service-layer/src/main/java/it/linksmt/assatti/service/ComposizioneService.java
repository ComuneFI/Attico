package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Composizione;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.ProfiloComposizione;
import it.linksmt.assatti.datalayer.domain.QComposizione;
import it.linksmt.assatti.datalayer.domain.QModelloHtml;
import it.linksmt.assatti.datalayer.domain.QTipoAtto;
import it.linksmt.assatti.datalayer.repository.ComposizioneRepository;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * Service class for managing Composizione.
 */
@Service
@Transactional
public class ComposizioneService {
	private final Logger log = LoggerFactory
			.getLogger(ComposizioneService.class);

	@Inject
	private ComposizioneRepository composizioneRepository;
	

	@Transactional(readOnly = true)
	public Composizione findOne(final Long id) {
		log.debug("getProfiloComposizione id" + id);
		Composizione domain = composizioneRepository.findOne(id);
		if (domain != null) {
			domain.getHasProfiloComposizione().size();

			if(domain.getHasProfiloComposizione() != null){
				for (ProfiloComposizione profiloComposizione: domain.getHasProfiloComposizione()) {
					profiloComposizione.getProfilo();
					profiloComposizione.getOrdine();
					profiloComposizione.getQualificaProfessionale();
					profiloComposizione.getValido();
					profiloComposizione.setComposizione(null);
				}
			}
			
			Collections.sort(domain.getHasProfiloComposizione(), new Comparator<ProfiloComposizione>() {
			    @Override
			    public int compare(ProfiloComposizione a1, ProfiloComposizione a2) {
			        return (a1.getOrdine()!=null?a1.getOrdine().intValue():0) - (a2.getOrdine()!=null?a2.getOrdine().intValue():0);
			    }
			});
		}
		return domain;
	}
	
	public Composizione findPredefinitaByOrgano(String organo){
		return composizioneRepository.findOneByPredefinitaAndOrgano(organo);
	}
	
	@Transactional(readOnly = true)
	public List<Composizione> findAllComposizioni(String organo) {
		return composizioneRepository.findComposizioniByOrgano(organo);
	}
	
	@Transactional
	public Composizione createNuovaComposizione(Composizione composizione) throws GestattiCatchedException{
		Composizione nuovaComposizione = composizioneRepository.save(composizione);
		
		BooleanExpression predicate = QComposizione.composizione.id.isNotNull();
		predicate = predicate.and(QComposizione.composizione.id.ne(nuovaComposizione.getId()));
		predicate = predicate.and(QComposizione.composizione.organo.containsIgnoreCase(composizione.getOrgano()));
		predicate = predicate.and(QComposizione.composizione.predefinita.eq(true));
		Iterable<Composizione> composizioni = composizioneRepository.findAll(predicate);
		
		if(nuovaComposizione.getPredefinita() != null && nuovaComposizione.getPredefinita().booleanValue()) {
			for (Composizione composizioneVecchiaPredefinita : composizioni) {
				composizioneVecchiaPredefinita.setPredefinita(false);
				//composizioneVecchiaPredefinita.setHasProfiloComposizione(null);
				composizioneRepository.save(composizioneVecchiaPredefinita);
			}
		}
		
		
		return nuovaComposizione;
	}
	
	@Transactional
	public void saveOrupdate(Composizione composizione) throws GestattiCatchedException{
		composizioneRepository.save(composizione);
		
		BooleanExpression predicate = QComposizione.composizione.id.isNotNull();
		predicate = predicate.and(QComposizione.composizione.id.ne(composizione.getId()));
		predicate = predicate.and(QComposizione.composizione.organo.containsIgnoreCase(composizione.getOrgano()));
		predicate = predicate.and(QComposizione.composizione.predefinita.eq(true));
		Iterable<Composizione> composizioni = composizioneRepository.findAll(predicate);
		if(composizione.getPredefinita() != null && composizione.getPredefinita().booleanValue()) {
			for(Composizione composizioneVecchiaPredefinita : composizioni) {
				composizioneVecchiaPredefinita.setPredefinita(false);
				//composizioneVecchiaPredefinita.setHasProfiloComposizione(null);
				composizioneRepository.save(composizioneVecchiaPredefinita);
			}
		}
	}
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<Composizione>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicate = QComposizione.composizione.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicate = predicate.and(QComposizione.composizione.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
			if(search.get("versione")!=null && !search.get("versione").isJsonNull() && search.get("versione").getAsString()!=null && !"".equals(search.get("versione").getAsString())){
				predicate = predicate.and(QComposizione.composizione.version.eq(search.get("versione").getAsInt()));
			}
			if(search.get("predefinita")!=null && !search.get("predefinita").isJsonNull()){
				predicate = predicate.and(QComposizione.composizione.predefinita.eq(search.get("predefinita").getAsBoolean()));
			}
			if(search.get("organo")!=null && !search.get("organo").isJsonNull() && search.get("organo").getAsString()!=null && !"".equals(search.get("organo").getAsString())){
				predicate = predicate.and(QComposizione.composizione.organo.containsIgnoreCase(search.get("organo").getAsString()));
			}
		}
		Page<Composizione> page = composizioneRepository.findAll(predicate, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/versioneComposizioneConsiglios", offset, limit);
        return new ResponseEntity<List<Composizione>>(page.getContent(), headers, HttpStatus.OK);
	}

}
