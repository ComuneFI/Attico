package it.linksmt.assatti.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneRiversamento;
import it.linksmt.assatti.datalayer.domain.QAoo;
import it.linksmt.assatti.datalayer.domain.QConfigurazioneRiversamento;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoSerieEnum;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneRiversamentoRepository;
import it.linksmt.assatti.service.exception.RiversamentoPoolException;


/**
 * Service class for managing ConfigurazioneRiversamento.
 */
@Service
@Transactional
public class ConfigurazioneRiversamentoService {

	private final Logger log = LoggerFactory.getLogger(ConfigurazioneRiversamentoService.class);

	@Inject
	private ConfigurazioneRiversamentoRepository configurazioneRiversamentoRepository;
	
	@Inject
	private AooService aooService;
	
	@Inject
	private AooRepository aooRepository;
	
	@Inject
	private TipoAttoService tipoAttoService;
	
	@Transactional //(readOnly=true)
	public List<ConfigurazioneRiversamento> findConfigurazioniByTipoDocumento(TipoDocumento tipoDocumento) throws RiversamentoPoolException{
		return this.findConfigurazioniByTipoDocumento(tipoDocumento, null);
	}
	
	@Transactional //(readOnly=true)
	public List<ConfigurazioneRiversamento> findConfigurazioniByTipoDocumento(TipoDocumento tipoDocumento, Aoo aoo) throws RiversamentoPoolException{
		List<ConfigurazioneRiversamento> list = null;
		if(tipoDocumento==null || tipoDocumento.getId()==null || tipoDocumento.getId() < 1L){
			throw new RiversamentoPoolException("findConfigurazioniByTipoDocumento :: tipodocumento non presente");
		}
		BooleanExpression p = QConfigurazioneRiversamento.configurazioneRiversamento.tipoDocumento.eq(tipoDocumento).and(QConfigurazioneRiversamento.configurazioneRiversamento.validoAl.isNull());
		list = Lists.newArrayList(configurazioneRiversamentoRepository.findAll(p));
		if(aoo!=null){
			Map<Long, List<ConfigurazioneRiversamento>> map = new HashMap<Long, List<ConfigurazioneRiversamento>>();
			for(ConfigurazioneRiversamento cr : list){
				if(!map.containsKey(cr.getTipoDocumentoSerie().getId())){
					map.put(cr.getTipoDocumentoSerie().getId(), new ArrayList<ConfigurazioneRiversamento>());
				}
				map.get(cr.getTipoDocumentoSerie().getId()).add(cr);
			}
			list = new ArrayList<ConfigurazioneRiversamento>();
			for(Long tipoDocSerieId : map.keySet()){
				List<ConfigurazioneRiversamento> l = map.get(tipoDocSerieId);
				ConfigurazioneRiversamento confTutteLeAoo = null;
				ConfigurazioneRiversamento confAooSpecifica = null;
				ConfigurazioneRiversamento confNessunaAoo = null;
				for(ConfigurazioneRiversamento c : l){
					if(c.getAooId().equals(0L)){
						confTutteLeAoo = c;
					}else if(c.getAooId().equals(aoo.getId())){
						confAooSpecifica = c;
						break;
					}else if(c.getAooId() < 0L){
						confNessunaAoo = c;
					}
				}
				if(confAooSpecifica!=null){
					list.add(confAooSpecifica);
				}else if(confTutteLeAoo!=null){
					list.add(confTutteLeAoo);
				}else if(confNessunaAoo!=null){
					list.add(confNessunaAoo);
				}
			}
		}
		if(list == null || list.size() < 1){
			log.warn("findConfigurazioniByTipoDocumento :: nessuna configurazione trovata per il tipodocumento con codice :: " + tipoDocumento.getCodice());
		}
		return list;
	}
	
	@Transactional //(readOnly=true)
	public ConfigurazioneRiversamento findConfigurazione(TipoDocumento tipoDocumento, TipoAtto tipoAtto, Aoo aoo) throws RiversamentoPoolException{
		ConfigurazioneRiversamento configurazioneRiversamento = null;
		if(tipoDocumento==null || tipoDocumento.getId()==null || tipoDocumento.getId() < 1L){
			throw new RiversamentoPoolException("ConfigurazioneRiversamentoService :: findConfigurazione :: tipodocumento non presente");
		}
		if(tipoAtto==null || tipoAtto.getId()==null || tipoAtto.getId() < 1L){
			throw new RiversamentoPoolException("ConfigurazioneRiversamentoService :: findConfigurazione :: tipoAtto non presente");
		}
		if(aoo==null || aoo.getId()==null || aoo.getId() < 1L){
			throw new RiversamentoPoolException("ConfigurazioneRiversamentoService :: findConfigurazione :: aoo non presente");
		}
		configurazioneRiversamento = configurazioneRiversamentoRepository.find(tipoDocumento.getId(), tipoAtto.getId(), aoo.getId());
		return configurazioneRiversamento;
	}
	
	@Transactional //(readOnly=true)
	public ConfigurazioneRiversamento findActiveConfigurazione(Long tipoDocumentoId, Long tipoAttoId, Long aooId) throws RiversamentoPoolException{
		ConfigurazioneRiversamento configurazioneRiversamento = null;
		if(tipoDocumentoId==null || tipoDocumentoId < 1L){
			throw new RiversamentoPoolException("ConfigurazioneRiversamentoService :: findConfigurazione :: tipodocumentoId non presente");
		}
		configurazioneRiversamento = configurazioneRiversamentoRepository.findActive(tipoDocumentoId, tipoAttoId, aooId);
		return configurazioneRiversamento;
	}
	
	@Transactional(readOnly=false)
	public ConfigurazioneRiversamento disableConfigurazioneRiversamento(final Long id){
		log.debug("disableConfigurazioneSerie configurazioneRiversamento" + id);
		ConfigurazioneRiversamento configurazioneRiversamento = configurazioneRiversamentoRepository.findOne(id);
		if(configurazioneRiversamento!=null){
			Calendar cal = Calendar.getInstance();
			configurazioneRiversamento.setValidoAl(new LocalDate(cal.getTimeInMillis()));
			configurazioneRiversamentoRepository.save(configurazioneRiversamento);
		}
		return configurazioneRiversamento;
	}

	@Transactional(readOnly=false)
	public ConfigurazioneRiversamento enableConfigurazioneSerie(final Long id){
		log.debug("enableConfigurazioneRiversamento configurazioneRiversamento" + id);
		ConfigurazioneRiversamento configurazioneRiversamento = configurazioneRiversamentoRepository.findOne(id);
		if(configurazioneRiversamento!=null){
			configurazioneRiversamento.setValidoAl(null);
			configurazioneRiversamentoRepository.save(configurazioneRiversamento);
		}
		return configurazioneRiversamento;
	}

	public void save(ConfigurazioneRiversamento configurazioneRiversamento) {
		Calendar cal = Calendar.getInstance();
		configurazioneRiversamento.setValidoDal(new LocalDate(cal.getTimeInMillis()));
		configurazioneRiversamentoRepository.save(configurazioneRiversamento);		
	}

	public boolean isAvailable(ConfigurazioneRiversamento configurazioneRiversamento) {
		BooleanExpression p = QConfigurazioneRiversamento.configurazioneRiversamento.aooId.eq(configurazioneRiversamento.getAooId());
		if(configurazioneRiversamento.getTipoAttoId()!=null){
			p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.tipoAttoId.eq(configurazioneRiversamento.getTipoAttoId()));
		}
		p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.tipoDocumento.id.eq(configurazioneRiversamento.getTipoDocumento().getId()));
		p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.tipoDocumentoSerie.id.eq(configurazioneRiversamento.getTipoDocumentoSerie().getId()));
		p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.validoAl.isNull());
		boolean pubblicabili = configurazioneRiversamento.getOnlyPubblicabili() == null ? false : configurazioneRiversamento.getOnlyPubblicabili();
		if(!pubblicabili){
			p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.onlyPubblicabili.eq(false).or(QConfigurazioneRiversamento.configurazioneRiversamento.onlyPubblicabili.isNull()));
		}else{
			p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.onlyPubblicabili.eq(true));
		}
		boolean available = false;
		if(configurazioneRiversamento.getId()!=null && configurazioneRiversamento.getId().longValue() > 0L){
			available = !(configurazioneRiversamentoRepository.count(p) > 1);
		}else{
			available = !(configurazioneRiversamentoRepository.count(p) > 0);
		}
		if(available){
			p = QConfigurazioneRiversamento.configurazioneRiversamento.tipoDocumento.id.eq(configurazioneRiversamento.getTipoDocumento().getId());
			if(configurazioneRiversamento.getTipoAttoId()!=null){
				p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.tipoAttoId.eq(configurazioneRiversamento.getTipoAttoId()));
			}
			p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.tipoDocumentoSerie.id.eq(configurazioneRiversamento.getTipoDocumentoSerie().getId()));
			if(!pubblicabili){
				p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.onlyPubblicabili.eq(false).or(QConfigurazioneRiversamento.configurazioneRiversamento.onlyPubblicabili.isNull()));
			}else{
				p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.onlyPubblicabili.eq(true));
			}
			p = p.and(QConfigurazioneRiversamento.configurazioneRiversamento.validoAl.isNull());
			List<ConfigurazioneRiversamento> list = Lists.newArrayList(configurazioneRiversamentoRepository.findAll(p));
			if(list!=null){
				if(configurazioneRiversamento.getAooId() > 0L || configurazioneRiversamento.getAooId().equals(0L)){
					for(ConfigurazioneRiversamento c : list){
						if(configurazioneRiversamento.getId() == null || !configurazioneRiversamento.getId().equals(c.getId())){
							if(c.getAooId() < 0L){
								available = false;
								break;
							}
						}
					}
				}else{
					for(ConfigurazioneRiversamento c : list){
						if(configurazioneRiversamento.getId() == null || !configurazioneRiversamento.getId().equals(c.getId())){
							if(c.getAooId() > 0L || c.getAooId().equals(0L)){
								available = false;
								break;
							}
						}
					}
				}
			}
		}
		return available;
	}

	public List<ConfigurazioneRiversamento> findAll() {
		return configurazioneRiversamentoRepository.findAll();
	}

	public ConfigurazioneRiversamento findOne(Long id) {
		return configurazioneRiversamentoRepository.findOne(id);
	}
	
	public List<ConfigurazioneRiversamento> findAll(String annoIstruzione, Long tipoDocumento, Boolean aoo) {
		BooleanExpression predicateConfigurazioneRiversamento = QConfigurazioneRiversamento.configurazioneRiversamento.id.isNotNull();
		if(tipoDocumento!=null){
			predicateConfigurazioneRiversamento = predicateConfigurazioneRiversamento.and(QConfigurazioneRiversamento.configurazioneRiversamento.tipoDocumentoSerie.id.eq(tipoDocumento));
		}
		Iterator<ConfigurazioneRiversamento> iterator = configurazioneRiversamentoRepository.findAll(predicateConfigurazioneRiversamento).iterator();
		List<ConfigurazioneRiversamento> result = new ArrayList<ConfigurazioneRiversamento>();
	    while (iterator.hasNext()) {
	    	result.add(iterator.next());
	    }
	    
		return result;
	}
	
	@Transactional(readOnly=true)
	public Page<ConfigurazioneRiversamento> findAll(Pageable generatePageRequest, JsonObject searchJson) throws ParseException {
		log.debug("Enter:"+searchJson);
		BooleanExpression predicate = QConfigurazioneRiversamento.configurazioneRiversamento.id.isNotNull();
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("aoo") && !searchJson.get("aoo").isJsonNull() && !searchJson.get("aoo").getAsString().isEmpty()){
			BooleanExpression orP = null;
			if("tutte".contains(searchJson.get("aoo").getAsString().toLowerCase())){
				orP = QConfigurazioneRiversamento.configurazioneRiversamento.aooId.eq(0L);
			}
			if("nessuna".contains(searchJson.get("aoo").getAsString().toLowerCase())){
				if(orP == null){
					orP = QConfigurazioneRiversamento.configurazioneRiversamento.aooId.eq(-1L);
				}else{
					orP = orP.or(QConfigurazioneRiversamento.configurazioneRiversamento.aooId.eq(-1L));
				}
			}
			BooleanExpression aooEx = ((QAoo.aoo.codice.concat(" - ").concat(QAoo.aoo.descrizione)).containsIgnoreCase(searchJson.get("aoo").getAsString())).or(
					(QAoo.aoo.codice.concat("-").concat(QAoo.aoo.descrizione)).containsIgnoreCase(searchJson.get("aoo").getAsString()));
			List<Aoo> aoos = Lists.newArrayList(aooRepository.findAll(aooEx));
			List<Long> aooIds = new ArrayList<Long>();
			for(Aoo aoo : aoos){
				aooIds.add(aoo.getId());
			}
			if(orP == null){
				orP = QConfigurazioneRiversamento.configurazioneRiversamento.aooId.in(aooIds);
			}else{
				orP = orP.or(QConfigurazioneRiversamento.configurazioneRiversamento.aooId.in(aooIds));
			}
			predicate = predicate.and(orP);
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("tipoDocumento") && !searchJson.get("tipoDocumento").isJsonNull()){
			predicate = predicate.and(QConfigurazioneRiversamento.configurazioneRiversamento.tipoDocumento.id.eq(searchJson.get("tipoDocumento").getAsJsonObject().get("id").getAsLong()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("tipoAtto") && !searchJson.get("tipoAtto").isJsonNull()){
			predicate = predicate.and(QConfigurazioneRiversamento.configurazioneRiversamento.tipoAttoId.eq(searchJson.get("tipoAtto").getAsJsonObject().get("id").getAsLong()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("tipoDocumentoSerie") && !searchJson.get("tipoDocumentoSerie").isJsonNull()){
			predicate = predicate.and(QConfigurazioneRiversamento.configurazioneRiversamento.tipoDocumentoSerie.id.eq(searchJson.get("tipoDocumentoSerie").getAsJsonObject().get("id").getAsLong()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("onlyPubblicabili") && !searchJson.get("onlyPubblicabili").isJsonNull()){
			predicate = predicate.and(QConfigurazioneRiversamento.configurazioneRiversamento.onlyPubblicabili.eq(searchJson.get("onlyPubblicabili").getAsBoolean()));
		}
		Page<ConfigurazioneRiversamento> page = configurazioneRiversamentoRepository.findAll(predicate , generatePageRequest);
		for(ConfigurazioneRiversamento conf : page){
			if(conf.getAooId()!=null && conf.getAooId() > 0L){
				conf.setAoo(aooService.findMinimalAooById(conf.getAooId()));
			}else if(conf.getAooId()!=null){
				Aoo aooFake = new Aoo();
				aooFake.setId(conf.getAooId());
				conf.setAoo(aooFake);
			}
			if(conf.getTipoAttoId()!=null && conf.getTipoAttoId() > 0L){
				TipoAtto tipoAtto = tipoAttoService.findOneSimple(conf.getTipoAttoId());
				conf.setTipoAtto(tipoAtto);
			}
		}
		return page;
	}
	
}
