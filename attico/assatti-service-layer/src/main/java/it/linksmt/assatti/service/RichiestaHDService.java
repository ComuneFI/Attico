package it.linksmt.assatti.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.QRichiestaHD;
import it.linksmt.assatti.datalayer.domain.RichiestaHD;
import it.linksmt.assatti.datalayer.domain.RispostaHD;
import it.linksmt.assatti.datalayer.domain.StatoRichiestaHD;
import it.linksmt.assatti.datalayer.domain.StatoRichiestaHDEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.RichiestaHDRepository;
import it.linksmt.assatti.datalayer.repository.RispostaHDRepository;
import it.linksmt.assatti.datalayer.repository.StatoRichiestaHDRepository;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

@Service
@Transactional
public class RichiestaHDService {
	private final Logger log = LoggerFactory.getLogger(RichiestaHDService.class);
	
    @Inject
    private RichiestaHDRepository richiestaHDRepository;
    
    @Inject
    private RispostaHDRepository rispostaHDRepository;
    
    @Inject
    private StatoRichiestaHDRepository statoRichiestaHDRepository;
    
    @Inject
    private UtenteService utenteService;
    
    @Inject
    private AooService aooService;
    
	public RichiestaHDService() {
		
	}
	
	@Transactional
	public void updateTestoRisposta(RispostaHD risposta){
		rispostaHDRepository.updateTestoRisposta(risposta.getTestoRisposta(), risposta.getId());
	}
	
	@Transactional
	public void updateTestoRichiesta(RichiestaHD richiesta){
		richiestaHDRepository.updateTestoRichiesta(richiesta.getTestoRichiesta(), richiesta.getId());
	}
	
	@Transactional
	public void updateStatoRichiesta(Long richiestaId, Long statoId) throws GestattiCatchedException{
		StatoRichiestaHD statoRichiestaDB = statoRichiestaHDRepository.findOne(statoId);
		if(statoRichiestaDB == null){
			throw new GestattiCatchedException("statorichiesta non presente sul db statoId: " + statoId);
		}else{
			if(statoRichiestaDB.getDescrizione().equalsIgnoreCase(StatoRichiestaHDEnum.RICHIESTA_CHIUSA.getStatoDB())){
				this.registraDataChiusura(richiestaId);
			}else if(statoRichiestaDB.getDescrizione().equalsIgnoreCase(StatoRichiestaHDEnum.RICHIESTA_SOSPESA.getStatoDB())){
				this.registraDataSospensione(richiestaId);
			}else if(statoRichiestaDB.getDescrizione().equalsIgnoreCase(StatoRichiestaHDEnum.RICHIESTA_APERTA.getStatoDB())){
				richiestaHDRepository.nullToDataSospensioneChiusura(richiestaId);
			}
			richiestaHDRepository.updateStatoRichiestaHD(statoRichiestaDB.getId(), richiestaId);
		}
	}
	
	@Transactional
	public void updateStatoRichiesta(Long richiestaId, String stato) throws GestattiCatchedException{
		if(stato == null || stato.isEmpty() || richiestaId == null || richiestaId < 1L){
			throw new GestattiCatchedException("statorichiesta o idrichiesta non validi");
		}else{
			StatoRichiestaHD statoRichiestaDB = statoRichiestaHDRepository.findByDescrizione(stato);
			if(statoRichiestaDB == null){
				throw new GestattiCatchedException("statorichiesta non presente sul db: " + stato);
			}else{
				if(statoRichiestaDB.getDescrizione().equalsIgnoreCase(StatoRichiestaHDEnum.RICHIESTA_CHIUSA.getStatoDB())){
					this.registraDataChiusura(richiestaId);
				}else if(statoRichiestaDB.getDescrizione().equalsIgnoreCase(StatoRichiestaHDEnum.RICHIESTA_SOSPESA.getStatoDB())){
					this.registraDataSospensione(richiestaId);
				}
				richiestaHDRepository.updateStatoRichiestaHD(statoRichiestaDB.getId(), richiestaId);
			}
		}
	}
	
	@Transactional
	public void presaVisione(Long richiestaId) throws GestattiCatchedException{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		richiestaHDRepository.presaVisione(df.format(new Date()), richiestaId);
	}
	
	@Transactional
	private void registraDataChiusura(Long richiestaId) throws GestattiCatchedException{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		richiestaHDRepository.registraDataChiusura(df.format(new Date()), richiestaId);
	}
	
	@Transactional
	private void registraDataSospensione(Long richiestaId) throws GestattiCatchedException{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		richiestaHDRepository.registraDataSospensione(df.format(new Date()), richiestaId);
	}
	
	@Transactional
	public void addRispostaOperatore(RispostaHD risposta, String statoRichiesta) throws GestattiCatchedException{
		if(statoRichiesta != null && !statoRichiesta.isEmpty()){
			StatoRichiestaHD statoRichiestaDB = statoRichiestaHDRepository.findByDescrizione(statoRichiesta);
			if(statoRichiestaDB == null || risposta == null || risposta.getRichiesta() == null || risposta.getRichiesta().getId() == null){
				throw new GestattiCatchedException("statorichiesta non presente sul db: " + statoRichiesta + " oppure risposta non contiene la richiesta");
			}else{
				RichiestaHD richiesta = richiestaHDRepository.findOne(risposta.getRichiesta().getId());
				richiesta.setStato(statoRichiestaDB);
				richiestaHDRepository.save(richiesta);
			}
		}
		risposta.setDataInvio(DateTime.now());
		risposta.setOperatore(utenteService.findByUsername(SecurityUtils.getCurrentLogin()));
		rispostaHDRepository.save(risposta);
	}
	
	@Transactional
	public void addRispostaUtente(RispostaHD risposta) throws GestattiCatchedException{
		this.updateStatoRichiesta(risposta.getRichiesta().getId(), StatoRichiestaHDEnum.RICHIESTA_APERTA.getStatoDB());
		risposta.setDataInvio(DateTime.now());
		risposta.setOperatore(null);
		rispostaHDRepository.save(risposta);
	}

	@Transactional
	public RichiestaHD create(RichiestaHD richiestaHD) {
        DateTime now = DateTime.now();
        richiestaHD.setDataInvio(now);
        richiestaHD.setStato(statoRichiestaHDRepository.findByDescrizione(StatoRichiestaHDEnum.RICHIESTA_APERTA.getStatoDB()));
        richiestaHD.setAutore(utenteService.findByUsername(SecurityUtils.getCurrentLogin()));
        return richiestaHDRepository.save(richiestaHD);
	}
	
	@Transactional
	public RichiestaHD save(RichiestaHD richiestaHD) {
		return richiestaHDRepository.save(richiestaHD);
	}
	
	@Transactional(readOnly=true)
	public Page<RichiestaHD> findAllOfCurrentUser(Pageable generatePageRequest, JsonObject searchJson) throws ParseException {
		Utente utente = utenteService.findByUsername(SecurityUtils.getCurrentLogin());
		if(searchJson == null || searchJson.isJsonNull()){
			searchJson = new JsonObject();
		}
		searchJson.add("autore", new JsonObject());
		searchJson.get("autore").getAsJsonObject().addProperty("id", utente.getId());
		return findAll(generatePageRequest, searchJson);		
	}
	
	@Transactional(readOnly=true)
	public Page<RichiestaHD> findAllNotDirigente(Pageable generatePageRequest, JsonObject searchJson) throws ParseException {
		if(searchJson==null){
			searchJson = new JsonObject();
		}
		searchJson.addProperty("dirigente", false);
		return findAll(generatePageRequest, searchJson);
	}
	
	@Transactional(readOnly=true)
	public Page<RichiestaHD> findAllDirigente(Pageable generatePageRequest, JsonObject searchJson) throws ParseException {
		if(searchJson==null){
			searchJson = new JsonObject();
		}
		searchJson.addProperty("dirigente", true);
		return findAll(generatePageRequest, searchJson);
	}

	@Transactional(readOnly=true)
	public Page<RichiestaHD> findAll(Pageable generatePageRequest, JsonObject searchJson) throws ParseException {
		Page<RichiestaHD> page = new PageImpl<RichiestaHD>(new ArrayList<RichiestaHD>());
		
		log.debug("Enter:"+searchJson);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dfHour = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		BooleanExpression predicate = QRichiestaHD.richiestaHD.stato.id.isNotNull();
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("dirigente")){
			predicate = predicate.and(QRichiestaHD.richiestaHD.tipo.dirigente.eq(searchJson.get("dirigente").getAsBoolean()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("aooId")){
			predicate = predicate.and(QRichiestaHD.richiestaHD.aooId.eq(searchJson.get("aooId").getAsLong()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("aoo")){
			predicate = predicate.and(QRichiestaHD.richiestaHD.aooId.in(aooService.searchAooIdsByCodiceDescrizione(searchJson.get("aoo").getAsString())));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("stato") && searchJson.get("stato").getAsJsonObject().has("descrizione") && !searchJson.get("stato").getAsJsonObject().get("id").getAsString().isEmpty()){
			predicate = predicate.and(QRichiestaHD.richiestaHD.stato.id.eq(searchJson.get("stato").getAsJsonObject().get("id").getAsLong()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("autore") && searchJson.get("autore").getAsJsonObject().has("id") && !searchJson.get("autore").getAsJsonObject().get("id").getAsString().isEmpty()){
			predicate = predicate.and(QRichiestaHD.richiestaHD.autore.id.eq(searchJson.get("autore").getAsJsonObject().get("id").getAsLong()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("tipo") && searchJson.get("tipo").getAsJsonObject().has("id") && !searchJson.get("tipo").getAsJsonObject().get("id").getAsString().isEmpty()){
			predicate = predicate.and(QRichiestaHD.richiestaHD.tipo.id.eq(searchJson.get("tipo").getAsJsonObject().get("id").getAsLong()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("oggetto") && !searchJson.get("oggetto").getAsString().isEmpty()){
			predicate = predicate.and(QRichiestaHD.richiestaHD.oggetto.like("%"+searchJson.get("oggetto").getAsString()+"%"));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("risposte") && !searchJson.get("risposte").getAsString().isEmpty()){
			predicate = predicate.and(searchJson.get("risposte").getAsBoolean() ? QRichiestaHD.richiestaHD.risposte.isNotEmpty() : QRichiestaHD.richiestaHD.risposte.isEmpty());
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("presaVisione") && !searchJson.get("presaVisione").getAsString().isEmpty()){
			predicate = predicate.and(searchJson.get("presaVisione").getAsBoolean() ? QRichiestaHD.richiestaHD.dataPresaVisione.isNotNull() : QRichiestaHD.richiestaHD.dataPresaVisione.isNull());
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("dataStart") && !searchJson.get("dataStart").getAsString().trim().equalsIgnoreCase("")){
			Date data = df.parse(searchJson.get("dataStart").getAsString().trim());
			Date dataH = dfHour.parse(df.format(data) + " 00:00:00");
			predicate = predicate.and(QRichiestaHD.richiestaHD.dataInvio.after(new DateTime(dataH.getTime())));
			
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("dataEnd") && !searchJson.get("dataEnd").getAsString().trim().equalsIgnoreCase("")){
			Date data = df.parse(searchJson.get("dataEnd").getAsString().trim());
			Date dataH = dfHour.parse(df.format(data) + " 23:59:59");
			predicate = predicate.and(QRichiestaHD.richiestaHD.dataInvio.before(new DateTime(dataH.getTime())));
		}
		Sort sort = new Sort(new Order(Direction.DESC, "dataInvio" ));
		page = richiestaHDRepository.findAll(predicate , PaginationUtil.generatePageRequest(generatePageRequest, sort));
		for(RichiestaHD ric : page){
			ric.setnRisposte(ric.getRisposte()!=null ? ric.getRisposte().size() : 0);
			ric.setRisposte(null);
			if(ric.getAooId()!=null){
				ric.setAoo(aooService.findMinimalAooById(ric.getAooId()));
			}
		}
		return page;
	}

	@Transactional(readOnly=true)
	public RichiestaHD findOne(Long id) {
		RichiestaHD richiesta = richiestaHDRepository.findOne(id);
		if(richiesta!=null && richiesta.getRisposte()!=null){
			for(RispostaHD risposta : richiesta.getRisposte()){
				risposta.getId(); //load lazy info
				risposta.setRichiesta(null);
			}
			if(richiesta.getAooId()!=null){
				richiesta.setAoo(aooService.findMinimalAooById(richiesta.getAooId()));
			}
		}
		return richiesta;
	}

	@Transactional
	public void delete(Long id) {
		richiestaHDRepository.delete(id);
	}

}
