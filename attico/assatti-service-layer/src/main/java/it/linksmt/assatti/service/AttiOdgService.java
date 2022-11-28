package it.linksmt.assatti.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.QAttiOdg;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepository;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class AttiOdgService {
	private final Logger log = LoggerFactory.getLogger(AttiOdgService.class);
	
	@Inject
	private AttiOdgRepository attiOdgRepository;
	
	public AttiOdg bloccoModificaVotazioni(AttiOdg aOdg) {
		if(aOdg!=null) {
			aOdg.setBloccoModifica(true);
			return attiOdgRepository.save(aOdg);
		}else {
			return null;
		}
	}
	
	public void bloccoModificaVotazioni(Long attoId) {
		if(attoId!=null) {
			Iterable<AttiOdg> aOdgs = attiOdgRepository.findAll(QAttiOdg.attiOdg.atto.id.eq(attoId));
			if(aOdgs!=null) {
				for(AttiOdg aOdg : aOdgs) {
					aOdg.setBloccoModifica(true);
					attiOdgRepository.save(aOdg);
				}
			}
		}
	}
	
	
// SOSPESO in attesa di chiarimenti... ticket Redmine 
//	@Transactional(readOnly=true)
//	public Page<AttiOdg> findAll(final Pageable generatePageRequest, final AttoCriteriaDTO criteria) {
//		TipoEvento eventoEsecutivita = tipoEventoRepository.findByCodice("ESECUTIVITA");
//		BooleanExpression expression = QAttiOdg.atto.id.isNotNull();
//		
//		if(criteria.getAooId()!=null){
//			BooleanExpression predicate = QAttiOdg.atto.aoo.id.eq( Long.parseLong(criteria.getAooId(), 10) );
//			for(String stato : StatoAttoEnum.descrizioneConclusi()){
//				predicate = predicate.or(QAttiOdg.atto.stato.equalsIgnoreCase(stato));
//			}
//			expression = expression.and(predicate);
//		}
//		
//		BooleanExpression tipiAttoExpression = null;
//		for(Long idTipoAtto : criteria.getTipiAttoIds()){
//			if(tipiAttoExpression == null){
//				tipiAttoExpression = QAtto.atto.tipoAtto.id.eq(idTipoAtto);
//			}else{
//				tipiAttoExpression = tipiAttoExpression.or(QAtto.atto.tipoAtto.id.eq(idTipoAtto));
//			}
//		}
//		if(tipiAttoExpression!=null){
//			expression = expression.and( tipiAttoExpression );
//		}
//
//		if(criteria.getArea()  != null && !"".equals(criteria.getArea()) ){
//			BooleanExpression internalPredicate = ((QAtto.atto.aoo.codice.concat(" - ").concat(QAtto.atto.aoo.descrizione)).containsIgnoreCase(criteria.getArea())).or(
//					(QAtto.atto.aoo.codice.concat("-").concat(QAtto.atto.aoo.descrizione)).containsIgnoreCase(criteria.getArea()));
//			if(internalPredicate!=null){
//				expression = expression.and(internalPredicate);
//			}
//		}
//
//		
//		
//		if(criteria.getStato() != null && !"".equals(criteria.getStato()) ){
//			if(!criteria.getStato().contains(";") || criteria.getStato().split(";").length < 2){
//				expression = expression.and( QAtto.atto.stato.equalsIgnoreCase(criteria.getStato().split(";")[0]) );
//			}else{
//				String stati[] = criteria.getStato().split(";");
//				BooleanExpression internalExp = null;
//				for(int i = 0; i<stati.length; i++){
//					if(i == 0){
//						internalExp = QAtto.atto.stato.equalsIgnoreCase(stati[i]);
//					}else{
//						internalExp = internalExp.or(QAtto.atto.stato.equalsIgnoreCase(stati[i]));
//					}
//				}
//				expression = expression.and(internalExp);
//			}
//		}
//		
//		if(criteria.getCodiceCifra()  != null && !"".equals(criteria.getCodiceCifra()) ){
//			expression = expression.and( QAttiOdg.atto.codiceCifra.containsIgnoreCase(criteria.getCodiceCifra()  ) );
//		}
//		if(criteria.getPrimaConvInizioDa() != null && !"".equals(criteria.getPrimaConvInizioDa())){
//			expression = expression.and(QSedutaGiunta.sedutaGiunta.primaConvocazioneInizio.goe(criteria.getPrimaConvInizioDa()));
//		}
//		if(criteria.getPrimaConvInizioA() != null && !"".equals(criteria.getPrimaConvInizioA())){
//			expression = expression.and(QSedutaGiunta.sedutaGiunta.primaConvocazioneInizio.loe(criteria.getPrimaConvInizioA()));
//		}
//		if(criteria.getDataCreazioneDa()  != null && !"".equals(criteria.getDataCreazioneDa()) ){
//			expression = expression.and( QAtto.atto.dataCreazione.goe(criteria.getDataCreazioneDa()  ) );
//		}
//		if(criteria.getDataCreazioneA()  != null && !"".equals(criteria.getDataCreazioneA()) ){
//			expression = expression.and( QAtto.atto.dataCreazione.loe(criteria.getDataCreazioneA()  ) );
//		}
//
//		if((criteria.getDataAdozione()  != null && !"".equals(criteria.getDataAdozione())) && (criteria.getDataAdozioneA()  != null && !"".equals(criteria.getDataAdozioneA()))  ){
//			expression = expression.and( QAtto.atto.dataAdozione.goe(criteria.getDataAdozione()  ) );
//			expression = expression.and( QAtto.atto.dataAdozione.loe(criteria.getDataAdozioneA()  ) );
//		}
//		else{
//			if(criteria.getDataAdozione()  != null && !"".equals(criteria.getDataAdozione())){
//				expression = expression.and( QAtto.atto.dataAdozione.goe(criteria.getDataAdozione()  ) );
//			}
//			
//			if(criteria.getDataAdozioneA()  != null && !"".equals(criteria.getDataAdozioneA())){
//				expression = expression.and( QAtto.atto.dataAdozione.loe(criteria.getDataAdozioneA()  ) );
//			}
//		}
//		
//		
//		if((criteria.getDataEsecutiva() != null && !"".equals(criteria.getDataEsecutiva())) && (criteria.getDataAdozione()  == null || "".equals(criteria.getDataAdozione()))){
////			expression = expression.and( QAtto.atto.dataAdozione.goe(criteria.getDataEsecutiva()  ) );
//			expression = expression.and(QAtto.atto.id.in(eventoService.findAttosByEsecutivita(new DateTime(criteria.getDataEsecutiva().toDate().getTime()))));
//		}
//
//		if(criteria.getOggetto()  != null && !"".equals(criteria.getOggetto()) ){
//			expression = expression.and( QAtto.atto.oggetto.containsIgnoreCase(criteria.getOggetto()  ) );
//		}
//
//		if(criteria.getTipoAtto()  != null && !"".equals(criteria.getTipoAtto()) ){
//			expression = expression.and( QAtto.atto.tipoAtto.descrizione.containsIgnoreCase(criteria.getTipoAtto()  ) );
////			expression = expression.and( QAtto.atto.tipoAtto.codice.equalsIgnoreCase(criteria.getTipoAtto()  ) );
//		}
//
//		if(criteria.getTipoIter()  != null && !"".equals(criteria.getTipoIter()) ){
//			expression = expression.and( QAtto.atto.tipoIter.descrizione.containsIgnoreCase(criteria.getTipoIter()  ) );
//		}
//
//		if(criteria.getTaskStato()  != null && !"".equals(criteria.getTaskStato()) ){
//			expression = expression.and(QAtto.atto.stato.equalsIgnoreCase(criteria.getTaskStato()));
//			//expression = expression.and( QAtto.atto.tipoAtto.codice.equalsIgnoreCase("DEL"));
//			//expression = expression.and( QAtto.atto.avanzamento.any().stato.codice.equalsIgnoreCase(criteria.getTaskStato()));
//
//			/*QAtto qa = QAtto.atto;
//			QAvanzamento qav = QAvanzamento.avanzamento;
//
//			JPAQuery q = new JPAQuery(entityManager);
//			q.from(qa).join(qa.avanzamento, qav);
//			q.where(QAvanzamento.avanzamento.stato.codice.equalsIgnoreCase(criteria.getTaskStato()));
//			q.offset(generatePageRequest.first().getOffset()).limit(generatePageRequest.getOffset());
//			Page<Atto>  lista = q.list(qa);*/
//
//		}
//		if(criteria.getIdOdg()  != null){
//			//expression = expression.and( QAtto.atto.tipoAtto.codice.equalsIgnoreCase("DEL"));
//			//expression = expression.and(QAtto.atto.avanzamento.any().ordineGiorno.id.eq(criteria.getIdOdg()));
//			expression = expression.and(QAtto.atto.ordineGiornos.any().id.eq(criteria.getIdOdg()));
//		}
//		
//		if((criteria.getInizioPubblicazioneAlbo() != null && !"".equals(criteria.getInizioPubblicazioneAlbo())) && (criteria.getFinePubblicazioneAlbo() != null && !"".equals(criteria.getFinePubblicazioneAlbo()))){
//			expression = expression.and( QAtto.atto.inizioPubblicazioneAlbo.goe(criteria.getInizioPubblicazioneAlbo()  ) );
//			expression = expression.and( QAtto.atto.finePubblicazioneAlbo.loe(criteria.getFinePubblicazioneAlbo()  ) );
//		}
//		else{
//			if(criteria.getInizioPubblicazioneAlbo() != null && !"".equals(criteria.getInizioPubblicazioneAlbo())){
//				expression = expression.and( QAtto.atto.inizioPubblicazioneAlbo.goe(criteria.getInizioPubblicazioneAlbo() ) );
//			}
//			
//			if(criteria.getFinePubblicazioneAlbo() != null && !"".equals(criteria.getFinePubblicazioneAlbo())){
//				expression = expression.and( QAtto.atto.finePubblicazioneAlbo.loe(criteria.getFinePubblicazioneAlbo()  ) );
//			}
//		}
//		
//
//		Page<Atto> l = attoRepository.findAll(expression, generatePageRequest);
//		log.debug("Lunghezza Pagina:" + l.getSize());
//		List<String> ids = new ArrayList<String>();
//		
//		for (Atto atto : l) {
//			ids.add(atto.getId().toString());
//			log.debug("Atto:"+atto.getId().toString()+" - "+atto.getId());
//		}
//		
//		AssegnazioniAtto assegnazioni = null;
//		if(criteria.getProfiloId()!=null){
//			assegnazioni = workflowService.getElencoTaskByAttiIds(ids, criteria.getProfiloId());
//		}else{
//			assegnazioni = null;
//		}
//		
//		if(assegnazioni!=null){
//			List<AttoUtentiORuoliAssegnazione> lista = new ArrayList<AttoUtentiORuoliAssegnazione>();
//			
//			log.debug("ASSEGNAZIONI WORKFLOW: {}",assegnazioni);
//			log.debug("criteria.getViewtype() {}",criteria.getViewtype());
//			if(criteria.getViewtype().equals("itinere") && assegnazioni != null){
//				lista = assegnazioni.getAssegnazioni();
//				log.debug("lista = assegnazioni.getAssegnazioni()");
//			}
//			
//			log.debug("List<AttoUtentiORuoliAssegnazione>:"+ lista.size());
//			
//			for (Atto atto : l) {
//				if(criteria.getProfiloId() != null && criteria.getViewtype().equals("itinere")){
//					atto.setAvanzamento(null);
//					Set<Avanzamento> avanzamenti = new HashSet<Avanzamento>();
//					Avanzamento ava = new Avanzamento();
//					ava.setCreatedBy("");
//					ava.setNote("");
//					
//					for(AttoUtentiORuoliAssegnazione attoUtente : lista){
//						log.debug("attoUtente.getAttoId():"+ attoUtente.getAttoId());
//						if(atto.getId().toString().equals(attoUtente.getAttoId())){
//							log.debug("attoUtente.getInfo():"+ attoUtente.getInfo());
//							if(attoUtente.getInfo() != null){
//								List<UtenteORuoloAssegnazione> listUt = attoUtente.getInfo().getDettagli();
//								log.debug("attoUtente.getAttoId() size:"+ listUt.size());
//								if(listUt.size() > 0){
//									UtenteORuoloAssegnazione or = listUt.get(0);
//									log.debug("Utente:" + or.getUtente());
//									log.debug("Ruolo:" + or.getRuolo());
//									
//									if(or.getRuolo() != null){
//										ava.setCreatedBy(or.getRuolo());
//									}
//									if(or.getUtente() != null){
//										Pattern pattern = Pattern.compile("([\\D]*)([0-9]*)");
//										Matcher matcher = pattern.matcher(or.getUtente());
//										Utente temp = null;
//										if (matcher.find())
//										{
//										    System.out.println(matcher.group(1));
//										    temp = utenteService.findByUsername(matcher.group(1));
//										}
//										
//										if(temp != null){
//											ava.setCreatedBy(temp.getCognome() + " " + temp.getNome());
//										}
//										else{
//											ava.setCreatedBy("");
//										}
//										
//									}
//									ava.setNote(or.getTimeAssegnazione());
//								}
//							}
//							
//						}
//					}
//					
//					avanzamenti.add(ava);
//					atto.setAvanzamento(avanzamenti);
//					
//				}
//				minimalInfoLoad(atto);
//				Evento ev = eventoService.findByTipoEventoAndAtto(eventoEsecutivita, atto);
//				if(ev!=null){
//					atto.setDataEsecutivita(ev.getDataCreazione());
//				}
//				log.debug("idAtto:" + atto.getCodiceCifra());
//			}
//		}
//		return l;
//	}
}
