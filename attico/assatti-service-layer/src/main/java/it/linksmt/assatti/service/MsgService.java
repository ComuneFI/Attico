package it.linksmt.assatti.service;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Msg;
import it.linksmt.assatti.datalayer.domain.PrioritaMsgEnum;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QAoo;
import it.linksmt.assatti.datalayer.domain.QMsg;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.MsgRepository;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.util.PaginationUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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

import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

@Service
@Transactional
public class MsgService {
	private final Logger log = LoggerFactory.getLogger(MsgService.class);
	
	@Inject
	private MsgRepository msgRepository;
	
	@Inject
	private AooRepository aooRepository;
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private UtenteService utenteService;
	
	public MsgService() {
		
	}
	
	@Transactional
	public Msg create(Msg msg) {
        DateTime now = DateTime.now();
        msg.setDataInserimento(now);
        return msgRepository.save(msg);
	}
	
	@Transactional
	public Msg update(Msg msg) {
        return msgRepository.save(msg);
	}
	
	@Transactional
	public void forceExpire(Long id) {
        msgRepository.forceExpire(id);
	}
	
	@Transactional
	public Msg readOne(Long id) {
		Msg msg = msgRepository.findOne(id);
		if(msg.getDestinatari()!=null){
			Set<Aoo> minimalAoos = new HashSet<Aoo>();
			for(Aoo aoo : msg.getDestinatari()){
				minimalAoos.add(DomainUtil.minimalAoo(aoo));
			}
			msg.setDestinatari(minimalAoos);
		}
		Long utenteId = utenteService.findByUsername(SecurityUtils.getCurrentLogin()).getId();
		Integer count = msgRepository.checkMessaggioLetto(utenteId, msg.getId());
		if(count==null || count < 1){
			msgRepository.setMessaggioLetto(utenteId, msg.getId());
		}
		return msg;
	}
	
	@Transactional
	public Page<Msg> findAllUtente(Pageable generatePageRequest, JsonObject searchJson) throws ParseException{
		Set<Aoo> aoos = new HashSet<Aoo>();
		searchJson.addProperty("enabled", true);
		List<Profilo> profiliOfCurrentUser = profiloService.findActiveByUsername(SecurityUtils.getCurrentLogin());
		Long utenteId = null;
		if(profiliOfCurrentUser!=null && profiliOfCurrentUser.size()>0){
			for(Profilo profilo : profiliOfCurrentUser){
				if(utenteId==null){
					utenteId = profilo.getUtente().getId();
				}
				aoos.add(profilo.getAoo());
			}
		}
		Page<Msg> risultati = findAll(generatePageRequest, searchJson, aoos);
		for(Msg msg : risultati){
			if(utenteId==null){
				msg.setLetto(false);
			}else{
				Integer count = msgRepository.checkMessaggioLetto(utenteId, msg.getId());
				if(count==null || count < 1){
					msg.setLetto(false);
				}else{
					msg.setLetto(true);
				}
			}
		}
		return risultati;
	}
	
	@Transactional(readOnly=true)
	public Page<Msg> findAll(Pageable generatePageRequest, JsonObject searchJson, Set<Aoo> aoos) throws ParseException{
		Page<Msg> page = new PageImpl<Msg>(new ArrayList<Msg>());
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dfHour = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String toDayStart = df.format(new Date());
		toDayStart += " 00:00:00";
		String toDayEnd = df.format(new Date());
		toDayEnd += " 23:59:59";
		
		BooleanExpression predicate = QMsg.msg.id.isNotNull();
		
		if(aoos!=null){
			BooleanExpression predicateAoo = QMsg.msg.id.isNull();
			for(Aoo aoo : aoos){
				predicateAoo = predicateAoo.or(QMsg.msg.destinatari.contains(aoo)); 
			}
			predicate = predicate.and(predicateAoo);
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("letto") && !searchJson.get("letto").isJsonNull() && !searchJson.get("letto").getAsString().isEmpty()){
			Long utenteId = utenteService.findByUsername(SecurityUtils.getCurrentLogin()).getId();
			List<Long> msgIds = msgRepository.getMsgIdsLetti(utenteId);
			if(searchJson.get("letto").getAsBoolean()){
				predicate = predicate.and(QMsg.msg.id.in(msgIds));
			}else{
				predicate = predicate.and(QMsg.msg.id.notIn(msgIds));
			}
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("enabled") && !searchJson.get("enabled").isJsonNull() && !searchJson.get("enabled").getAsString().isEmpty()){
			BooleanExpression validoAlNull = QMsg.msg.validita.validoal.isNull();
			BooleanExpression validoAlKo = QMsg.msg.validita.validoal.before(new LocalDate(dfHour.parse(toDayStart).getTime()));
			BooleanExpression validoDalKo = QMsg.msg.validita.validodal.after(new LocalDate(dfHour.parse(toDayEnd).getTime()));
			
			BooleanExpression validoAlNotNull = QMsg.msg.validita.validoal.isNotNull();
			BooleanExpression validoAlOk = QMsg.msg.validita.validoal.after(new LocalDate(dfHour.parse(toDayEnd).getTime()));
			BooleanExpression validoDalOk = QMsg.msg.validita.validodal.before(new LocalDate(dfHour.parse(toDayStart).getTime())).or(QMsg.msg.validita.validodal.eq(new LocalDate(dfHour.parse(toDayStart).getTime())));
			
			if(searchJson.get("enabled").getAsBoolean()){
				predicate = predicate.and(validoAlNull.or(validoDalOk.and(validoAlOk)));
			}else{
				predicate = predicate.and(validoDalKo.or(validoAlNotNull.and(validoAlKo)));
			}
		}
		
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("dataStart") && !searchJson.get("dataStart").isJsonNull() && !searchJson.get("dataStart").getAsString().trim().equalsIgnoreCase("")){
			Date data = df.parse(searchJson.get("dataStart").getAsString().trim());
			Date dataH = dfHour.parse(df.format(data) + " 00:00:00");
			predicate = predicate.and(QMsg.msg.dataInserimento.after(new DateTime(dataH.getTime())));
			
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("dataEnd") && !searchJson.get("dataEnd").isJsonNull() && !searchJson.get("dataEnd").getAsString().trim().equalsIgnoreCase("")){
			Date data = df.parse(searchJson.get("dataEnd").getAsString().trim());
			Date dataH = dfHour.parse(df.format(data) + " 23:59:59");
			predicate = predicate.and(QMsg.msg.dataInserimento.before(new DateTime(dataH.getTime())));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("priorita") && !searchJson.get("priorita").isJsonNull() && !searchJson.get("priorita").getAsString().isEmpty()){
			predicate = predicate.and(QMsg.msg.priorita.eq(PrioritaMsgEnum.getByString(searchJson.get("priorita").getAsString())));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("categoria") && !searchJson.get("categoria").isJsonNull() && searchJson.get("categoria").getAsJsonObject().has("id") && !searchJson.get("categoria").getAsJsonObject().get("id").getAsString().isEmpty()){
			predicate = predicate.and(QMsg.msg.categoriaMsg.id.eq(searchJson.get("categoria").getAsJsonObject().get("id").getAsLong()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("destinatario") && !searchJson.get("destinatario").isJsonNull() && !searchJson.get("destinatario").getAsString().isEmpty()){
			BooleanExpression aooPred = ((QAoo.aoo.codice.concat(" - ").concat(QAoo.aoo.descrizione)).containsIgnoreCase(searchJson.get("destinatario").getAsString())).or(
							(QAoo.aoo.codice.concat("-").concat(QAoo.aoo.descrizione)).containsIgnoreCase(searchJson.get("destinatario").getAsString()));
			BooleanExpression destPred = null;
			Iterable<Aoo> aoosIt = aooRepository.findAll(aooPred);
			for(Aoo aoo : aoosIt){
				if(destPred == null){
					destPred = QMsg.msg.destinatari.contains(aoo);
				}else{
					destPred = destPred.or(QMsg.msg.destinatari.contains(aoo));
				}
			}
			if(destPred == null){
				destPred = QMsg.msg.id.isNull();
			}
			predicate = predicate.and(destPred);
		}

		Sort sort = new Sort(new Order(Direction.DESC, "dataInserimento" ));
		page = msgRepository.findAll(predicate , PaginationUtil.generatePageRequest(generatePageRequest, sort));
		for(Msg msg : page){
			if(msg.getDestinatari()!=null){
				Set<Aoo> minimalAoos = new HashSet<Aoo>();
				for(Aoo aoo : msg.getDestinatari()){
					minimalAoos.add(DomainUtil.minimalAoo(aoo));
				}
				msg.setDestinatari(minimalAoos);
			}
		}
		return page;
	}
}
