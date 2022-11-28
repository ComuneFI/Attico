package it.linksmt.assatti.service.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.path.PathBuilder;

import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneRiversamento;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QProfilo;
import it.linksmt.assatti.datalayer.domain.QRuolo;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoSerie;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.RuoloRepository;
import it.linksmt.assatti.service.ConfigurazioneRiversamentoService;
import it.linksmt.assatti.service.TipoDocumentoSerieService;
import it.linksmt.assatti.service.exception.RiversamentoPoolException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.CmisProps;
import it.linksmt.assatti.utility.configuration.GlobalProps;


@Service
public class ServiceUtil {

	private final Logger log = LoggerFactory.getLogger(ServiceUtil.class);
	
	@Autowired
	private ConfigurazioneRiversamentoService configurazioneRiversamentoService;
	
	@Autowired
	private TipoDocumentoSerieService tipoDocumentoSerieService;

	@Autowired
	private ProfiloRepository profiloRepository;
	
	@Autowired
	private WorkflowServiceWrapper workflowService;


	@Transactional(readOnly=true)
	public boolean hasRuolo(final Long profiloId, final String codice) {

		BooleanExpression predicate =  QProfilo.profilo.id.isNotNull();
		predicate = predicate.and(QProfilo.profilo.id.eq(profiloId));

		Profilo prof = profiloRepository.findOne(predicate);
		return hasRuolo(prof,codice);
	}
	
	@Transactional(readOnly=true)
	public boolean hasOneOfThisRuoli(final String username, final List<String> codiciRuolo) {
		boolean ret = false;
		if(codiciRuolo!=null && codiciRuolo.size() > 0) {
			BooleanExpression predicate =  QProfilo.profilo.id.isNotNull().and(QProfilo.profilo.validita.validoal.isNull());
			predicate = predicate.and(QProfilo.profilo.utente.username.toLowerCase().eq(username.toLowerCase().trim()));
			predicate = predicate.and(QProfilo.profilo.grupporuolo.hasRuoli.any().codice.in(codiciRuolo));
			long count = profiloRepository.count(predicate);
			ret = count > 0L;
		}else {
			ret = true;
		}
		return ret;
	}


	@Transactional(readOnly=true)
	public boolean hasRuolo(final Profilo prof, final String codice) {
		boolean result =false;

		if(prof != null ){
			log.debug(prof.getGrupporuolo()+"");

			if(prof.getGrupporuolo()!= null){
				prof.getGrupporuolo().getDenominazione();
				if(prof.getGrupporuolo().getHasRuoli() != null ){
					for (Ruolo ruolo : prof.getGrupporuolo().getHasRuoli()) {
						String tmpCodicce = ruolo.getCodice();
						if(tmpCodicce.equalsIgnoreCase(codice)){
							result =true;
						}
					}
				}
			}
		}
		return result;
	}
	
	public List<DecisioneWorkflowDTO> getDecisioneMassiva(String taskBpmId, final Long profiloId) throws Exception {
    	List<String> lstValCodiceDecisione = workflowService.getCodiceAzioneMassiva(taskBpmId);
    	if (lstValCodiceDecisione == null || lstValCodiceDecisione.isEmpty()) {
    		return null;
    	}
		
		Iterable<DecisioneWorkflowDTO> decisioni = workflowService.getPulsantiWorkflow(taskBpmId, profiloId );
		if(decisioni!= null) {
			List<DecisioneWorkflowDTO> lstDecisioni = new ArrayList<DecisioneWorkflowDTO>();
			for (DecisioneWorkflowDTO dec : decisioni) {
				if (lstValCodiceDecisione.contains(dec.getCodiceAzioneUtente())) {
					lstDecisioni.add(dec);
				}
			}
			return lstDecisioni;
		}
		
		return null;
    }

	public byte[] getByteDecodeBase64DataImg(final String img){
		if(img ==null) {
			return new byte[0];
		}
		String tmpLogo = img;
		tmpLogo = tmpLogo.substring(22);
  		log.debug("logo:"+tmpLogo);
  		byte[] imgByteArray = Base64.decode(tmpLogo.getBytes());
  		return imgByteArray;
	}

	public String trimStr(final String original) {

		String retVal = "";
		if (original != null) {
			retVal = original.trim();
		}

		return retVal;
	}

	public boolean isNull(final String original) {
		return ( (original == null) || (original.trim().length() == 0) );
	}

	@SuppressWarnings("finally")
	public byte[] getByteLogoEnte(){
		String logo="data:image/png;base64,";
		try {
			ClassPathResource resource = new ClassPathResource(GlobalProps.getProperty(ConfigPropNames.GLOBAL_LOGO_ENTE_FILENAME));
			byte[] img =FileUtils.readFileToByteArray(resource.getFile());
			img = Base64.encode(img);
			logo = new String(img);
			logo = "data:image/png;base64,"+logo ;

		} catch (IOException e) {
			log.error(e.getMessage() );
		} finally{
			return logo.getBytes();
		}

	}

	@SuppressWarnings("finally")
	public byte[] getLogoPalazzoEnte(){
		String logo="data:image/png;base64,";
		try {
			ClassPathResource resource = new ClassPathResource("bannerodgnew.png");
			byte[] img =FileUtils.readFileToByteArray(resource.getFile());
			img = Base64.encode(img);
			logo = new String(img);
			logo = "data:image/png;base64,"+logo ;
			
		} catch (IOException e) {
			log.error(e.getMessage() );
		} finally{
			return logo.getBytes();
		}
		
	}
	
	/**
	 * Costruisce il percorso in cui salvare il documento in base ai parametri
	 * passati in input.
	 * 
	 * Aoo e tipoAtto non sono obbligatori.
	 * 
	 * @param tipoDocumento
	 * @param aoo
	 * @param tipoAtto
	 * @return
	 * @throws ServiceException
	 */
	public String buildDocumentPath(TipoDocumento tipoDocumento, Aoo aoo, TipoAtto tipoAtto, String codiceAtto) throws ServiceException {
		String attoFolderPath = null;
		
		if(tipoDocumento==null) {
			throw new ServiceException("ServiceUtil :: buildDocumentPath() :: tipoDocumento non può essere null!");
		}

		/*
		 * Ricerca/creazione directory su DMS
		 */
		Calendar gc = new GregorianCalendar();
		gc.setTime(new Date());
		int currentYear = gc.get(Calendar.YEAR);
		
		try {
			ConfigurazioneRiversamento configurazioneRiversamento = null;
			
			if(tipoAtto!=null && aoo!=null && aoo.getId()!=null && aoo.getId()>=0L) {
				configurazioneRiversamento = configurazioneRiversamentoService.findConfigurazione(tipoDocumento, tipoAtto, aoo);
			} else if(tipoAtto==null && aoo!=null && aoo.getId()!=null && aoo.getId()>=0L) {
				List<ConfigurazioneRiversamento> listConfigurazioneRiversamento = configurazioneRiversamentoService.findConfigurazioniByTipoDocumento(tipoDocumento, aoo);
				if(listConfigurazioneRiversamento!=null && listConfigurazioneRiversamento.size()>0) {
					configurazioneRiversamento = listConfigurazioneRiversamento.get(0);
				}
			} else {
				List<ConfigurazioneRiversamento> listConfigurazioneRiversamento = configurazioneRiversamentoService.findConfigurazioniByTipoDocumento(tipoDocumento);
				if(listConfigurazioneRiversamento!=null && listConfigurazioneRiversamento.size()>0) {
					configurazioneRiversamento = listConfigurazioneRiversamento.get(0);
				}
			}

			if(configurazioneRiversamento==null) {
				String errorMessage = "ServiceUtil :: buildDocumentPath() :: configurazioneRiversamento non trovata! - tipoDocumento:" + tipoDocumento.getCodice();
				if(aoo!=null) {
					errorMessage += " - aoo:" + aoo.getCodice();
				}
				if(tipoAtto!=null) {
					errorMessage += " - tipoAtto:" + tipoAtto.getCodice();
				}
				throw new ServiceException(errorMessage);
			}
			
			if(configurazioneRiversamento.getTipoDocumentoSerie()==null
					|| configurazioneRiversamento.getTipoDocumentoSerie().getId()==null || configurazioneRiversamento.getTipoDocumentoSerie().getId()<=0L) {
				String errorMessage = "ServiceUtil :: buildDocumentPath() :: configurazioneRiversamento senza tipoDocumentoSerie! - tipoDocumento:" + tipoDocumento.getCodice();
				if(aoo!=null) {
					errorMessage += " - aoo:" + aoo.getCodice();
				}
				if(tipoAtto!=null) {
					errorMessage += " - tipoAtto:" + tipoAtto.getCodice();
				}
				throw new ServiceException(errorMessage);
			}

			TipoDocumentoSerie tipoDocumentoSerie = tipoDocumentoSerieService.findOne(configurazioneRiversamento.getTipoDocumentoSerie().getId());
			
			String cmisYearFolderFirst = CmisProps.getProperty(ConfigPropNames.CMIS_YEAR_FOLDER_FIRST);
			
			if(cmisYearFolderFirst!=null && cmisYearFolderFirst.equals("true")) {
				attoFolderPath = "/" + currentYear + "/" + tipoDocumentoSerie.getCodice();
			} else {
				attoFolderPath = "/" + tipoDocumentoSerie.getCodice() + "/" + currentYear;
			}
			
			if(aoo!=null) {
				attoFolderPath += "/" + aoo.getCodice();
			}
			
			if (TipoDocumentoEnum.allegato.name().equalsIgnoreCase(tipoDocumento.getCodice()) || TipoDocumentoEnum.relata.name().equalsIgnoreCase(tipoDocumento.getCodice())) {
				if (StringUtil.isNull(codiceAtto)) {
					throw new ServiceException("Per il salvataggio degli allegati risulta necessario specificare il codice identificativo dell'atto");
				}
				
				attoFolderPath += "/" + StringUtil.trimStr(codiceAtto).replace('/', '_');
			}
			
			log.debug("ServiceUtil :: save() :: processFolderPath: " + attoFolderPath);
		} 
		catch (RiversamentoPoolException e) {
			throw new ServiceException(e);
		}
		catch (Exception e) {
			throw new ServiceException(e);
		}
		
		return attoFolderPath;
	}

	public String getStringLogoEnte(){
		return new String(getByteLogoEnte());
	}

	public String getStringLogoPalazzoEnte(){
		return new String(getLogoPalazzoEnte());
	}

	public String getStringLogoEnteIntestazioneConDicitura(){
		return new String(getByteLogoEnteIntestazioneConDicitura());
	}
	
	public byte[] getByteLogoEnteIntestazioneConDicitura(){
		byte[] logoBytes = null;
		String logo="data:image/png;base64,";
		try {
			ClassPathResource resource = new ClassPathResource(GlobalProps.getProperty(ConfigPropNames.GLOBAL_LOGO_ENTE_FULL_FILENAME));
			byte[] img =FileUtils.readFileToByteArray(resource.getFile());
			img = Base64.encode(img);
			logo = new String(img);
			logo = "data:image/png;base64,"+logo ;

		} catch (IOException e) {
		} finally{
			logoBytes = logo.getBytes();
		}
		return logoBytes;
	}
	
	@SuppressWarnings("finally")
	public byte[] getByteDefaultBanner(){
		String logo="data:image/png;base64,";
		try {
			ClassPathResource resource = new ClassPathResource("bannerlaterale.png");
			byte[] img =FileUtils.readFileToByteArray(resource.getFile());
			img = Base64.encode(img);
			logo = new String(img);
			logo = "data:image/png;base64,"+logo ;

		} catch (IOException e) {
			log.error(e.getMessage() );
		} finally{
			return logo.getBytes();
		}

	}

	public String getStringDefaultBanner(){
		return new String(getByteDefaultBanner());
	}
	
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public OrderSpecifier<?>[] toOrderSpecifiers(Class tableClass, String tableName, Sort sort){
		
		int numOrders=0;
		for (Sort.Order o : sort){
			numOrders++;
		}
		log.debug(String.format("toOrderSpecifiers - numOrders : %s", numOrders));
		OrderSpecifier<?>[] orders = new OrderSpecifier<?>[numOrders];
		
		int idx=0;
		for (Sort.Order o : sort){
			log.debug(String.format("o.getProperty() : %s", o.getProperty()));
			PathBuilder orderByExpression = new PathBuilder(tableClass, tableName);
			orders[idx] = new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,	orderByExpression.get(o.getProperty()));
			idx++;
		}
		log.debug(String.format("orders - length : %s", orders.length));
		
		return orders;
	}
	
	/*
	 * TODO: dipende dal Protocollo, fare eventuale DTO
	 * 
	public String getStringSegnaturaProtocollo(SegnaturaProtocollo sp){
		String spString = "";
		
		if (sp != null){
			spString = String.format("%s/%s/%s/%s/%s", 
					sp.getCodiceAmministrazione(), 
					sp.getCodiceAOO(),
					sp.getCodiceRegistro(),
					sp.getDataRegistrazione(),
					sp.getNumeroProtocollo());
			
			log.debug(String.format("Segnatura protocollo = %s", spString));
		} else {
			log.error("SegnaturaProtocollo is null!");
		}
		
		return spString;
	}
	*/
}
