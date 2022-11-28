package it.linksmt.assatti.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.MonitoraggioAccesso;
import it.linksmt.assatti.datalayer.domain.QMonitoraggioAccesso;
import it.linksmt.assatti.datalayer.repository.MonitoraggioAccessoRepository;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

@Service
@Transactional
public class MonitoraggioAccessoService {
	private final Logger log = LoggerFactory.getLogger(MonitoraggioAccessoService.class);
	
    @Inject
    private MonitoraggioAccessoRepository monitoraggioAccessoRepository;
    
	@Transactional
	public void salvaNuovoAccesso(MonitoraggioAccesso accesso){
		monitoraggioAccessoRepository.save(accesso);
	}
	
	@Transactional(readOnly=true)
	public Page<MonitoraggioAccesso> findAll(Pageable generatePageRequest, JsonObject searchJson) throws ParseException {
		log.debug("Enter:"+searchJson);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		BooleanExpression predicate = QMonitoraggioAccesso.monitoraggioAccesso.id.isNotNull();
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("username") && !searchJson.get("username").isJsonNull() && !searchJson.get("username").getAsString().isEmpty()){
			predicate = predicate.and(QMonitoraggioAccesso.monitoraggioAccesso.username.containsIgnoreCase(searchJson.get("username").getAsString()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("dettaglio") && !searchJson.get("dettaglio").isJsonNull() && !searchJson.get("dettaglio").getAsString().isEmpty()){
			predicate = predicate.and(QMonitoraggioAccesso.monitoraggioAccesso.dettaglio.containsIgnoreCase(searchJson.get("dettaglio").getAsString()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("hostname") && !searchJson.get("hostname").isJsonNull() && !searchJson.get("hostname").getAsString().isEmpty()){
			predicate = predicate.and(QMonitoraggioAccesso.monitoraggioAccesso.hostname.containsIgnoreCase(searchJson.get("hostname").getAsString()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("ipAddress") && !searchJson.get("ipAddress").isJsonNull() && !searchJson.get("ipAddress").getAsString().isEmpty()){
			predicate = predicate.and(QMonitoraggioAccesso.monitoraggioAccesso.ipAddress.containsIgnoreCase(searchJson.get("ipAddress").getAsString()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("status") && !searchJson.get("status").isJsonNull() && !searchJson.get("status").getAsString().isEmpty()){
			predicate = predicate.and(QMonitoraggioAccesso.monitoraggioAccesso.status.containsIgnoreCase(searchJson.get("status").getAsString()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("dataDa") && !searchJson.get("dataDa").isJsonNull() && !searchJson.get("dataDa").getAsString().isEmpty()){
			Date data = df.parse(searchJson.get("dataDa").getAsString() + " 00:00:00");
			predicate = predicate.and(QMonitoraggioAccesso.monitoraggioAccesso.data.goe(new DateTime(data.getTime())));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("dataA") && !searchJson.get("dataA").isJsonNull() && !searchJson.get("dataA").getAsString().isEmpty()){
			Date data = df.parse(searchJson.get("dataA").getAsString() + " 23:59:59");
			predicate = predicate.and(QMonitoraggioAccesso.monitoraggioAccesso.data.loe(new DateTime(data.getTime())));
		}
		return monitoraggioAccessoRepository.findAll(predicate , generatePageRequest);
	}
	
	
}
