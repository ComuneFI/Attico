package it.linksmt.assatti.service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.dm.mds.customerws.ServiceResponse;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Assessorato;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.AttoHasDestinatario;
import it.linksmt.assatti.datalayer.domain.Beneficiario;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.News;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QNews;
import it.linksmt.assatti.datalayer.domain.RubricaDestinatarioEsterno;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.StatoJob;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoNotificaEnum;
import it.linksmt.assatti.datalayer.domain.TipoInvioNotificaEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.NewsRepository;
import it.linksmt.assatti.datalayer.repository.TipoDestinatarioRepository;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.dto.PaginaDTO;
import it.linksmt.assatti.service.exception.MailNotificationException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
@Transactional
public class NewsService{
	
	private final Logger log = LoggerFactory.getLogger(NewsService.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
    private MessageSource messageSource;
	
	@Inject
	private NewsRepository newsRepository;
	
	@Inject
	private DestinatarioInternoService destinatarioInternoService; 
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private AssessoratoService assessoratoService;
	
	@Inject
	private UtenteService utenteService;
	
	@Inject
	private SedutaGiuntaService sedutaGiuntaService;
	
	@Inject
	private OrdineGiornoService ordineGiornoService;
	
	@Inject
	private TipoDestinatarioRepository tipoDestinatarioRepository;
	
	//TODO @Inject
	//private SmsService smsService;
		
	private static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy' ore 'HH:mm");
	
	public News createNews(News news){		
		return newsRepository.save(news);	
	}
	
	public void updateNews(News newsToUpdate){
		newsRepository.save(newsToUpdate);
	}
	
	
	public void deleteNews(Long newsId){
		News news = newsRepository.findOne(newsId);	    
	    newsRepository.delete(news);	
	}
	
	public void cleanPreviousError(News originalNews){
		
		if (originalNews == null) {
			return;
		}
		
		BooleanExpression pred = QNews.news.id.isNotNull();
		pred = pred.and(QNews.news.originalNews.id.eq(originalNews.getId()));
		Iterable<News> newss = newsRepository.findAll(pred);
		for (News news : newss) {
			if(news.getStato().equals(StatoJob.ERROR)){
				newsRepository.delete(news);
			}
		}
		
//		if(originalNews.getStato().equals(StatoJob.ERROR)){
//			newsRepository.delete(originalNews);
//		}
	}
	
	@Transactional(readOnly=true)
	public Page<News> getStoricoTentativi(Long newsId, Integer offset, Integer limit){
		BooleanExpression predicate = QNews.news.id.isNotNull();
		predicate = predicate.and(QNews.news.originalNews.id.eq(newsId).or(QNews.news.id.eq(newsId)));
		Page<News> page = newsRepository.findAll(predicate, PaginationUtil.generatePageRequest(offset, limit,new Sort(Sort.Direction.DESC, "dataCreazione")));
		return page;
	}
	
	@Transactional
	public PaginaDTO getReportNews(JsonObject criteria, Integer limit, Integer offset) throws ParseException{
		String where = "1=1";
		if(criteria!=null && !criteria.isJsonNull()){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			if(criteria.has("stato") && criteria.getAsJsonObject("stato").has("target") && !criteria.getAsJsonObject("stato").get("target").getAsString().trim().equals("")){
				where += " and stato = '" + criteria.getAsJsonObject("stato").get("target").getAsString().trim() + "'";
			}
			if(criteria.has("dataStart") && !criteria.get("dataStart").getAsString().trim().equalsIgnoreCase("")){
				Date data = df.parse(criteria.get("dataStart").getAsString().trim());
				where += " and data_creazione >= '" + df.format(data) + " 00:00:00" + "'";
			}
			if(criteria.has("dataEnd") && !criteria.get("dataEnd").getAsString().trim().equalsIgnoreCase("")){
				Date data = df.parse(criteria.get("dataEnd").getAsString().trim());
				where += " and data_creazione <= '" + df.format(data) + " 23:59:59" + "'";
			}
			if(criteria.has("tipoInvio") && !criteria.get("tipoInvio").getAsString().trim().equalsIgnoreCase("")){
				where += " and tipo_invio = '" + criteria.get("tipoInvio").getAsString().trim() + "'";
			}
			if(criteria.has("motivazione") && criteria.getAsJsonObject("motivazione").has("target") && !criteria.getAsJsonObject("motivazione").get("target").getAsString().trim().equals("")){
				where += " and tipo_documento = '" + criteria.getAsJsonObject("motivazione").get("target").getAsString().trim() + "'";
			}
			if(criteria.has("oggetto") && !criteria.get("oggetto").getAsString().trim().equalsIgnoreCase("")){
				where += " and oggetto like '%" + criteria.get("oggetto").getAsString().trim() + "%'";
			}
			if(criteria.has("destinatario") && !criteria.get("destinatario").getAsString().trim().equalsIgnoreCase("")){
				where += " and destinazione_notifica like '%" + criteria.get("destinatario").getAsString().trim() + "%'";
			}
		}
		
		Query query = entityManager.createNativeQuery("SELECT * FROM (SELECT * FROM (select * from news order by data_creazione desc) AS sub group by tipo_documento, atto_id, dest_interno_id, dest_esterno_id, beneficiario_id, tipo_invio order by data_creazione desc) as sub2 where "+where+" limit "+offset+","+limit, News.class);
		@SuppressWarnings("unchecked")
		List<News> lista = (List<News>)query.getResultList();
		
		for(Object obj : lista){
			News notifica = (News)obj;
			if(notifica.getStato()!=null && (notifica.getStato().equals(StatoJob.IN_PROGRESS) || notifica.getStato().equals(StatoJob.NEW))){
				if(notifica.getOriginalNews()!=null){
					notifica.setTentativi(newsRepository.countTentativiInvioFalliti(notifica.getOriginalNews().getId(), 4L));
				}else{
					notifica.setTentativi(newsRepository.countTentativiInvioFalliti(notifica.getId(), 4L));
				}
			}
		}
		Query queryCount = entityManager.createNativeQuery("SELECT count(*) FROM (SELECT * FROM (select * from news order by data_creazione desc) AS sub group by tipo_documento, atto_id, dest_interno_id, dest_esterno_id, beneficiario_id, tipo_invio order by data_creazione desc) as sub2 where "+where);
		BigInteger totaleRisultati = (BigInteger)queryCount.getResultList().get(0);
		PaginaDTO page = new PaginaDTO();
		page.setListaOggetti(lista);
		page.setTotaleElementi(totaleRisultati.longValue());
		return page;
	}
	
	@Transactional(readOnly = true)
	public News getLastTentativoByNewsId(Long newsid){
		BooleanExpression predicate = QNews.news.id.isNotNull();
		predicate = predicate.and(QNews.news.id.eq(newsid).or(QNews.news.originalNews.id.eq(newsid)));
		Page<News> page = newsRepository.findAll(predicate, PaginationUtil.generatePageRequest(0, 1,new Sort(Sort.Direction.DESC, "dataCreazione")));
		if(page!=null && page.getContent()!=null && page.getContent().size()>0){
			return page.getContent().get(0);
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public News getNotificaById(Long notificaId){
		News notifica = newsRepository.findOne(notificaId);
		if(notifica.getDestinatarioInterno()!=null){
			destinatarioInternoService.fillDestinatarioInterno(notifica.getDestinatarioInterno());
		}
		
		//DOCUMENTI
		if(notifica.getAtto() != null){
			if(notifica.getAtto().getDocumentiPdf()  != null  ){
				for (DocumentoPdf documentoPdf : notifica.getAtto().getDocumentiPdf() ) {

				}
			}

			//Documenti Adozione
			if(notifica.getAtto().getDocumentiPdfAdozione()   != null  ){
				for (DocumentoPdf documentoPdf : notifica.getAtto().getDocumentiPdfAdozione() ) {

				}

			}
		} else if(notifica.getDocumento()!=null){
			DocumentoPdf documentoPdf = notifica.getDocumento();

		}
		return notifica;
	}
	
	@Transactional(readOnly = true)
	public Page<News> getAllNotificheDestinatariInterni(Integer offset, Integer limit, Long profiloid){
		List<Profilo> profiliAttivi = null;
		Profilo profiloLoggato = null;
		Utente utenteLoggato = null;
		List<Long> profiliIds = new ArrayList<Long>();
		List<Long> aooIds = new ArrayList<Long>();
		BooleanExpression predicate = null;
		boolean nessunProfilo = false;
		if(profiloid==null){
			predicate = QNews.news.id.isNull();
		}else{
			if(profiloid.equals(0L)){
				profiliAttivi = profiloService.findActiveByUsername(SecurityUtils.getCurrentLogin());
				for(Profilo profilo : profiliAttivi){
					if(profilo!=null){
						profiliIds.add(profilo.getId());
						if(profilo.getAoo()!=null){
							aooIds.add(profilo.getAoo().getId());
						}
					}
				}
				if(profiliAttivi!=null && profiliAttivi.size()>0){
					utenteLoggato = profiliAttivi.get(0).getUtente();
				}else{
					nessunProfilo = true;
				}
			}else{
				profiliIds.add(profiloid);
				profiloLoggato = profiloService.findOne(profiloid);
				aooIds.add(profiloLoggato.getAoo().getId());
				utenteLoggato = profiloLoggato.getUtente();
			}
			if(!nessunProfilo){ //nessunProfilo true se è stato selezionato tutti i profili, ma non si ha alcun profilo
				predicate = QNews.news.destinatarioInterno.isNotNull().and(QNews.news.originalNews.isNull());
				BooleanExpression predicatoUtente = QNews.news.destinatarioInterno.tipoDestinatario.descrizione.equalsIgnoreCase("utente").and(QNews.news.destinatarioInterno.destinatarioId.eq(utenteLoggato.getId()));
				List<Assessorato> assessorati = assessoratoService.findByProfiloResponsabileIds(profiliIds);
				List<Long> assessoratiIds = new ArrayList<Long>();
				for(Assessorato assessorato : assessorati){
					assessoratiIds.add(assessorato.getId());
				}
				BooleanExpression predicatoAssessorato = QNews.news.destinatarioInterno.tipoDestinatario.descrizione.equalsIgnoreCase("assessorato").and(QNews.news.destinatarioInterno.destinatarioId.in(assessoratiIds));
				
				BooleanExpression predicateAoo = QNews.news.destinatarioInterno.tipoDestinatario.descrizione.equalsIgnoreCase("aoo").and(QNews.news.destinatarioInterno.destinatarioId.in(aooIds));
				
				predicate = predicate.and(predicatoUtente.or(predicatoAssessorato).or(predicateAoo));
			}else{
				utenteLoggato = utenteService.findByUsername(SecurityUtils.getCurrentLogin());
				predicate = QNews.news.originalNews.isNull().and(QNews.news.destinatarioInterno.tipoDestinatario.descrizione.equalsIgnoreCase("utente").and(QNews.news.destinatarioInterno.destinatarioId.eq(utenteLoggato.getId())));
			}
		}		
		return newsRepository.findAll(predicate, PaginationUtil.generatePageRequest(offset, limit,new Sort(Sort.Direction.DESC, "id")));
	}
	
	public Integer getNotificheByIdAtto(Long attoId){
		return newsRepository.countNewsOfAttoId(attoId);
	}
	
	
	@Transactional(readOnly = true)
	public List<News> getNotificheByStatoList(List<StatoJob> stati, boolean widthBlob){
		if(widthBlob){
			return newsRepository.findByStatoIn(stati);
		}else{
			List<News> lista = newsRepository.findByStatoIn(stati);
			for(News news : lista){
				if(news.getDocumento()!=null){
					if(news.getDocumento().getFile()!=null){
						news.getDocumento().setFile(null);
					}
				}
			}
			return lista;
		}
	}
	
	@Transactional(readOnly=true)
	public List<News> getNewsPecDoneBySubjectAndTo(String subject, List<String> toList) {
		
		return newsRepository.getNewsPecDoneBySubjectAndTo(subject, toList);
	}
	
	private boolean configuraMailPecBeneficiario(News notifica, Atto atto, Beneficiario beneficiario, Long profiloId, String subject, boolean isPec){
		boolean toSave = false;
		notifica.setStato(StatoJob.NEW);
		if(atto.getDocumentiPdfAdozione()!=null && atto.getDocumentiPdfAdozione().size() >0){
			notifica.setDocumento(atto.getDocumentiPdfAdozione().get(0));
			if(isPec && beneficiario.getPec()!=null && !beneficiario.getPec().isEmpty()){
				notifica.setTipoInvio(TipoInvioNotificaEnum.PEC.getDescrizione());
				notifica.setDestinazioneNotifica(beneficiario.getPec());
				toSave = true;
			}else if(!isPec && beneficiario.getEmail()!=null && !beneficiario.getEmail().isEmpty()){
				notifica.setTipoInvio(TipoInvioNotificaEnum.MAIL.getDescrizione());
				notifica.setDestinazioneNotifica(beneficiario.getEmail());
				toSave = true;
			}else if((beneficiario.getEmail()==null || beneficiario.getEmail().isEmpty()) && (beneficiario.getPec()==null || beneficiario.getPec().isEmpty())){
				notifica.setStato(StatoJob.ERROR);
				notifica.setDettaglioErrore("Al beneficiario destinatario della notifica non risulta associata nè un indirizzo PEC, nè un indirizzo e-mail.");
				toSave = true;
			}
		}else{
			notifica.setStato(StatoJob.ERROR);
			notifica.setDettaglioErrore("Impossibile recuperare il documento dell'atto da notificare. :: ID Beneficiario " + beneficiario.getId());
			toSave = true;
		}
		notifica.setAtto(atto);
		Profilo profilo = new Profilo();
		profilo.setId(profiloId);
		notifica.setAutore(profilo);
		notifica.setDataCreazione(new DateTime());
		notifica.setBeneficiario(beneficiario);
		notifica.setTipoDocumento(TipoDocumentoNotificaEnum.ATTO_NOTIFICATO);
        notifica.setOggetto(subject);
        return toSave;
	}

	@Transactional
	public void richiediNotificaAtto(Atto atto, Long profiloId){
		Locale locale = Locale.ITALY;
		Object[] codiceCifraParam = {atto.getCodiceCifra()};
		String subject = messageSource.getMessage("email.notifica.title", codiceCifraParam, locale);
		
		if(atto.getNotificaBeneficiari()!=null && atto.getNotificaBeneficiari() == true && atto.getBeneficiari()!=null && atto.getBeneficiari().size()>0){
			for(Beneficiario beneficiario : atto.getBeneficiari()){
				News notifica = new News();
				if(this.configuraMailPecBeneficiario(notifica, atto, beneficiario, profiloId, subject, true)){
					this.createNews(notifica);
				}
				notifica = new News();
				if(this.configuraMailPecBeneficiario(notifica, atto, beneficiario, profiloId, subject, false)){
					this.createNews(notifica);
				}
			}
		}
		Set<AttoHasDestinatario> destinatariInterni = destinatarioInternoService.getDestinatariInterniByAttoId(atto.getId(),true);
		for(AttoHasDestinatario destinatarioInterno : destinatariInterni){
			News notifica = new News();
			notifica.setStato(StatoJob.NEW);
			if(atto.getDocumentiPdfAdozione()!=null && atto.getDocumentiPdfAdozione().size() >0){
				notifica.setDocumento(atto.getDocumentiPdfAdozione().get(0));
				if(destinatarioInterno != null && destinatarioInterno.getDestinatarioId()!=null){
	    			if(destinatarioInterno.getDestinatario()==null){
	    				destinatarioInternoService.fillDestinatarioInterno(destinatarioInterno);
	    			}
	    		}
	    		if(destinatarioInterno!=null && destinatarioInterno.getDestinatario()!=null && destinatarioInterno.getDestinatario().getClassName().equalsIgnoreCase(Assessorato.class.getCanonicalName())){
					Assessorato assessorato = (Assessorato)destinatarioInterno.getDestinatario();
					if(assessorato!=null && assessorato.getProfiloResponsabileId()!=null){
						Profilo profiloAssessorato = profiloService.findOne(assessorato.getProfiloResponsabileId());
						if(profiloAssessorato.getUtente() != null && profiloAssessorato.getUtente().getEmail()!=null){
							assessorato.setEmail(profiloAssessorato.getUtente().getEmail());
						}
					}
				}
		    	if(destinatarioInterno!=null && destinatarioInterno.getDestinatario()!=null && destinatarioInterno.getDestinatario().getEmail()!=null && !destinatarioInterno.getDestinatario().getEmail().trim().equals("")){
		    		notifica.setTipoInvio(TipoInvioNotificaEnum.MAIL.getDescrizione());
		    		notifica.setDestinazioneNotifica(destinatarioInterno.getDestinatario().getEmail());
		    	}else{
		    		notifica.setStato(StatoJob.ERROR);
		    		notifica.setDettaglioErrore("Il destinatario interno non risulta provvisto di e-mail.");
		    	}
			}else{
				notifica.setStato(StatoJob.ERROR);
				notifica.setDettaglioErrore("Impossibile recuperare il documento dell'atto da notificare.");
			}
			notifica.setAtto(atto);
			Profilo profilo = new Profilo();
			profilo.setId(profiloId);
			notifica.setDataCreazione(new DateTime());
			notifica.setAutore(profilo);
			notifica.setDestinatarioInterno(destinatarioInterno);
			notifica.setTipoDocumento(TipoDocumentoNotificaEnum.ATTO_NOTIFICATO);
			notifica.setOggetto(subject);
			this.createNews(notifica);
		}
		for(RubricaDestinatarioEsterno destinatarioEsterno : atto.getDestinatariEsterni()){
			News notifica = new News();
			notifica.setStato(StatoJob.NEW);
			notifica.setTipoInvio(TipoInvioNotificaEnum.PEC.getDescrizione());
			if(atto.getDocumentiPdfAdozione()!=null && atto.getDocumentiPdfAdozione().size() >0){
				notifica.setDocumento(atto.getDocumentiPdfAdozione().get(0));
				if(destinatarioEsterno!=null && destinatarioEsterno.getPec()!=null && !destinatarioEsterno.getPec().trim().equals("")){
						notifica.setDestinazioneNotifica(destinatarioEsterno.getPec());
	    		}else{
	    			notifica.setStato(StatoJob.ERROR);
	    			notifica.setDettaglioErrore("Al destinatario esterno non è associato un indirizzo PEC a cui inviare la notifica.");
	    		}
			}else{
				notifica.setStato(StatoJob.ERROR);
				notifica.setDettaglioErrore("Impossibile recuperare il documento dell'atto da notificare.");
			}
			notifica.setAtto(atto);
			Profilo profilo = new Profilo();
			profilo.setId(profiloId);
			notifica.setDataCreazione(new DateTime());
			notifica.setAutore(profilo);
			notifica.setTipoDocumento(TipoDocumentoNotificaEnum.ATTO_NOTIFICATO);
			notifica.setDestinatarioEsternoId(destinatarioEsterno.getId());
			notifica.setOggetto(subject);
			this.createNews(notifica);
		}
	}
	
	
	/**
	 * Gestisce le notifiche da inviare nella gestione dell'Ordine del Giorno 
	 * a fronte di un DocumetoPdf firmato dal presidente di giunta, sia esso
	 * il documento dell' OdG Base, di un OdG Suppletivo o di una variazione all'OdG.
	 * @param doc
	 * @param tipoDocumento
	 * @param autore
	 */
	public void notificaDocumentoOdG(DocumentoPdf doc, TipoDocumentoNotificaEnum tipoDocumento, Long idProfiloAutore){
		
		if (doc !=null && doc.getId() != null)
			log.info(String.format("Notifica del DocumentoPdf con id:%s di tipo:%s agli Assessori....", doc.getId(), tipoDocumento));
		notificaDocumentoOdGAssessori(doc, tipoDocumento, idProfiloAutore);
		log.info("Notifica agli assessori inviata!!");
		
		if (doc !=null && doc.getId() != null)
			log.info(String.format("Notifica del DocumentoPdf con id:%s di tipo:%s ai Consiglieri....", doc.getId(), tipoDocumento));
		notificaDocumentoOdGConsiglieri(doc, tipoDocumento, idProfiloAutore);
		log.info("Notifica ai Consiglieri inviata!!");
		
		if (doc !=null && doc.getId() != null)
			log.info(String.format("Notifica del DocumentoPdf con id:%s di tipo:%s alle altre Strutture Regionali....", doc.getId(), tipoDocumento));
		notificaDocumentoOdGAltreStrutture(doc, tipoDocumento, idProfiloAutore);
		log.info("Notifica ai Consiglieri inviata!!");
		
		if (doc !=null && doc.getId() != null)
			log.info(String.format("Notifica del DocumentoPdf con id:%s di tipo:%s agli Estra....", doc.getId(), tipoDocumento));
		notificaDocumentoOdGExtra(doc, tipoDocumento, idProfiloAutore);
		log.info("Notifica agli extra inviata!!");
	}
	
	/**
	 * Gestione delle notifiche agli Assessori...
	 * @param doc
	 * @param tipoDocumento
	 * @param idProfiloAutore
	 */
	public void notificaDocumentoOdGAssessori(DocumentoPdf doc, TipoDocumentoNotificaEnum tipoDocumento, Long idProfiloAutore){
		
		try{
			// recupero i destinatari di tipo Assessori...
			OrdineGiorno odg = ordineGiornoService.findOne(doc.getOrdineGiornoId());
			List<Assessorato> assessorati = recuperaAssessoriBySeduta(odg.getSedutaGiunta());
			if (assessorati != null){
				log.debug(String.format("Recuperati %s assessorati...", assessorati.size()));

				for (Assessorato assessorato : assessorati){
					fillEmailAndTelefono(assessorato);
					
					AttoHasDestinatario destInterno = destinatarioInternoService.getDestinatarioInternoByDestinatarioIdAndDocumentoPdfId(assessorato.getId(), doc.getId());
					
					if (destInterno == null){
						destInterno = new AttoHasDestinatario();
						destInterno.setDestinatarioId(assessorato.getId());
						destInterno.setDestinatario(assessorato);
						destInterno.setTipoDestinatario(tipoDestinatarioRepository.findByDescrizione("Assessorato"));
						destInterno.setDocumentoPdfId(doc.getId());
						
						destInterno = destinatarioInternoService.salvaDestinatarioInterno(destInterno);
					}

					// Invio la Notifica...
					inviaNotificaOdG(doc, tipoDocumento, idProfiloAutore, destInterno, null); 
				}
			}
			else{
				log.error("Nessun assessorato recuperato!!");
			}
		}catch(Exception ex){
			log.error("Errore in notifica ordine del giorno agli assessori sul documentoPdf id:" + doc.getId());
		}
	}
//	public void notificaDocumentoOdGConsiglieri(DocumentoPdf doc, TipoDocumentoNotificaEnum tipoDocumento, Long idProfiloAutore){
//		try{
//			// recupero i destinatari di tipo Consiglieri...
//			Pageable paginazione = PaginationUtil.generatePageRequest(PaginationUtil.MIN_OFFSET, PaginationUtil.MAX_LIMIT);
//			JsonObject statoJson = new JsonObject();
//			statoJson.addProperty("id", 0);
//			
//			JsonObject searchJson = new JsonObject();
//			searchJson.add("stato", statoJson);
//			searchJson.addProperty("tipo", "PRIVATO");
//			searchJson.addProperty("notificagiunta", new Boolean(true));
//			
//			Page<RubricaDestinatarioEsterno> searchResult = rubricaService.search(searchJson, paginazione);
//			if (searchResult != null){
//				log.debug(String.format("Recuperati %s consiglieri...", searchResult.getTotalElements()));
//				
//				// Invio la Notifica...
//				for (RubricaDestinatarioEsterno rde : searchResult){
//					log.debug(String.format("Notifica consigliere id:%s - tipo:%s - notificagiunta:%s...", 
//							rde.getId(), rde.getTipo(), rde.getNotificaGiuntaAutomatica()));
//					
//					inviaNotificaOdG(doc, tipoDocumento, idProfiloAutore, null, rde);
//				}
//			} else{
//				log.error("Nessun consigliere recuperato!!");
//			}
//			
//		}catch(Exception ex){
//			log.error("Errore in notifica ordine del giorno ai consiglieri sul documentoPdf id:" + doc.getId());
//		}
//	}
	public void notificaDocumentoOdGConsiglieri(DocumentoPdf doc, TipoDocumentoNotificaEnum tipoDocumento, Long idProfiloAutore){
		String gruppoRuoloConsiglieri = WebApplicationProps.getProperty(ConfigPropNames.NOTIFICA_GRUPPORUOLO_CONSIGLIERI);
		
		try{
			// recupero i destinatari di tipo Consiglieri...
			OrdineGiorno odg = ordineGiornoService.findOne(doc.getOrdineGiornoId());
			List<Profilo> consiglieri = recuperaProfiliByGR(gruppoRuoloConsiglieri, odg.getSedutaGiunta().getNotificaTuttiConsiglieri());
			if (consiglieri != null){
				for (Profilo p : consiglieri){
					AttoHasDestinatario destInterno = destinatarioInternoService.getDestinatarioInternoByDestinatarioIdAndDocumentoPdfId(
							p.getUtente().getId(), doc.getId());
					
					if (destInterno == null){
						destInterno = new AttoHasDestinatario();
						destInterno.setDestinatarioId(p.getUtente().getId());
						destInterno.setDestinatario(p.getUtente());
						destInterno.setTipoDestinatario(tipoDestinatarioRepository.findByDescrizione("Utente"));
						destInterno.setDocumentoPdfId(doc.getId());
						
						destInterno = destinatarioInternoService.salvaDestinatarioInterno(destInterno);
					}

					// Invio la Notifica...
					inviaNotificaOdG(doc, tipoDocumento, idProfiloAutore, destInterno, null); 
				}
			} else{
				log.error("Nessun consigliere recuperato!!");
			}
			
		}catch(Exception ex){
			log.error("Errore in notifica ordine del giorno ai consiglieri sul documentoPdf id:" + doc.getId(), ex);
		}
	}
	public void notificaDocumentoOdGAltreStrutture(DocumentoPdf doc, TipoDocumentoNotificaEnum tipoDocumento, Long idProfiloAutore){
		
		String gruppoRuoloAltreStrutture = WebApplicationProps.getProperty(ConfigPropNames.NOTIFICA_GRUPPORUOLO_ALTRESTRUTTURE);
		
		try{
			// recupero i destinatari di tipo Altra Struttura Regionale...
			OrdineGiorno odg = ordineGiornoService.findOne(doc.getOrdineGiornoId());
			List<Profilo> profiliAltraStruttura = recuperaProfiliByGR(
					gruppoRuoloAltreStrutture, 
					odg.getSedutaGiunta().getNotificaTuttiAltreStrutture());
			
			if (profiliAltraStruttura != null){
				for (Profilo p : profiliAltraStruttura){
					AttoHasDestinatario destInterno = destinatarioInternoService.getDestinatarioInternoByDestinatarioIdAndDocumentoPdfId(
							p.getUtente().getId(), doc.getId());
					
					if (destInterno == null){
						destInterno = new AttoHasDestinatario();
						destInterno.setDestinatarioId(p.getUtente().getId());
						destInterno.setDestinatario(p.getUtente());
						destInterno.setTipoDestinatario(tipoDestinatarioRepository.findByDescrizione("Utente"));
						destInterno.setDocumentoPdfId(doc.getId());
						
						destInterno = destinatarioInternoService.salvaDestinatarioInterno(destInterno);
					}

					// Invio la Notifica...
					inviaNotificaOdG(doc, tipoDocumento, idProfiloAutore, destInterno, null); 
				}
			} else{
				log.error("Nessun utente di Altra Struttura Regionale recuperato!!");
			}
			
		}catch(Exception ex){
			log.error("Errore in notifica ordine del giorno ai consiglieri sul documentoPdf id:" + doc.getId());
		}
	}
	public void notificaDocumentoOdGExtra(DocumentoPdf doc, TipoDocumentoNotificaEnum tipoDocumento, Long idProfiloAutore){
		try{
			// recupero i destinatari extra dalla seduta...
			OrdineGiorno odg = ordineGiornoService.findOne(doc.getOrdineGiornoId());
			if (doc != null && odg != null && 
					odg.getSedutaGiunta() != null){
				
				Long idSeduta = odg.getSedutaGiunta().getId();
				
				// Gestione Deswtinatari INTERNI....
				Set<AttoHasDestinatario> rubricaDestInterni = destinatarioInternoService.getDestinatariInterniByAttoId(idSeduta, false);
				if (rubricaDestInterni != null){
					log.debug(String.format("Recuperati %s destinatari extra...", rubricaDestInterni.size()));

					// Invio la Notifica...
					for (AttoHasDestinatario d_int : rubricaDestInterni){
						
						AttoHasDestinatario destInterno = destinatarioInternoService.getDestinatarioInternoByDestinatarioIdAndDocumentoPdfId(d_int.getDestinatarioId(), doc.getId());
						
						if (destInterno == null){
							destInterno = new AttoHasDestinatario();
							destInterno.setDestinatarioId(d_int.getDestinatarioId());
							destInterno.setDocumentoPdfId(doc.getId());
							destInterno.setTipoDestinatario(d_int.getTipoDestinatario());
							destInterno = destinatarioInternoService.salvaDestinatarioInterno(destInterno);

							destinatarioInternoService.fillDestinatarioInterno(destInterno);
							inviaNotificaOdG(doc, tipoDocumento, idProfiloAutore, destInterno, null);
						} else {
							log.info(String.format("DocumentoPdf con id:%s gia notificato all'utente [%s]!!",
									doc.getId(), d_int.getDestinatario().getDescrizioneAsDestinatario()));
						}
					}
				} else{
					log.error("Nessun destinatario interno extra recuperato!!");
				}
				// FINE Gestione Destinatari INTERNI....
				
				// Gestione Deswtinatari ESTERNI....
				Set<RubricaDestinatarioEsterno> rubricaDestEsterni = sedutaGiuntaService.getRubricaDestinatariEsterni(idSeduta);
				if (rubricaDestEsterni != null){
					log.debug(String.format("Recuperati %s destinatari extra...", rubricaDestEsterni.size()));

					// Invio la Notifica...
					for (RubricaDestinatarioEsterno d_est : rubricaDestEsterni){
						if (!isNotificaOdgPresente(d_est.getId(), doc)){
							inviaNotificaOdG(doc, tipoDocumento, idProfiloAutore, null, d_est);
						} else {
							log.info(String.format("DocumentoPdf con id:%s gia notificato all'utente [%s %s]!!",
									doc.getId(), d_est.getCognome(), d_est.getNome()));
						}
					}
				} else{
					log.error("Nessun destinatario esterno extra recuperato!!");
				}
				// FINE Gestione Deswtinatari ESTERNI....
			} else{
				log.error("Nessun destinatario extra recuperato!!");
			}
		}catch(Exception ex){
			log.error("Errore in notifica ordine del giorno ai destinatari extra della seduta sul documentoPdf id: " + doc.getId(), ex);
		}
	}
	
	private boolean isNotificaOdgPresente(Long destinatarioEsternoId, DocumentoPdf doc){
		boolean result = false;
		
		List<News> notificheOdg = newsRepository.findByDestinatarioEsternoIdAndDocumento(destinatarioEsternoId, doc);
		
		if (notificheOdg != null && notificheOdg.size() > 0)
			result = true;
		
		return result;
	}
	
	/**
	 * Gestione delle Notifiche per un Ordine del Giorno
	 * @param doc
	 * @param tipoDocumento
	 * @param autore
	 * @param destInterno
	 * @param destEsterno
	 * @throws MailNotificationException
	 */
	private void inviaNotificaOdG(
			DocumentoPdf doc, 
			TipoDocumentoNotificaEnum tipoDocumento, 
			Long idProfiloAutore,
			AttoHasDestinatario destInterno,
			RubricaDestinatarioEsterno destEsterno){
		
		// TODO : DA RIVEDERE... MANCA AL MOMENTO COME DETERMINARE SE SI DEVE RICEVERE O NO L'SMS
		// PER ORA VIENE CONTROLLATA SOLO LA PRESENZA DEL NUMERO DI TELEFONO
		
		// Gestione della notifica SMS
		log.debug("NewsService - notificaConvocazioneSMS tipoDocumento" + tipoDocumento.toString());
		if ((tipoDocumento == TipoDocumentoNotificaEnum.ODG_BASE || 
				 tipoDocumento == TipoDocumentoNotificaEnum.VARIAZIONE ||
				 tipoDocumento == TipoDocumentoNotificaEnum.ANNULLAMENTO)){
			log.debug("NewsService - notificaConvocazioneSMS :: richiesta inviata...");
			notificaConvocazioneSMS(tipoDocumento, doc, idProfiloAutore, destInterno, destEsterno);
		}
		
		// Gestione della notifica E-MAIL
		richiediNotificaOdGViaMail(doc, tipoDocumento, idProfiloAutore, destInterno, destEsterno);
		log.debug("NewsService - notificaOdGViaMail :: richiesta inviata...");
	}
	
	/**
	 * Gestione della notifica di un Ordine del Giorno via E-MAIL
	 * @param doc
	 * @param tipoDocumento
	 * @param seduta
	 * @param autore
	 * @param destId
	 * @param destNominativo
	 * @param destIsEsterno
	 * @param destEmail
	 * @throws MailNotificationException 
	 */
	private void richiediNotificaOdGViaMail(
			DocumentoPdf doc, 
			TipoDocumentoNotificaEnum tipoDocumento, 
			Long idProfiloAutore,
			AttoHasDestinatario destInterno,
			RubricaDestinatarioEsterno destEsterno) {
		Locale locale = Locale.ITALY;
		News notificaMail = new News();		
		notificaMail.setStato(StatoJob.NEW);
		notificaMail.setDataCreazione(new DateTime());
		
		OrdineGiorno odg = ordineGiornoService.findOne(doc.getOrdineGiornoId());
		
		// TODO: Rivedere la generazione dell'oggetto della notifica...
		if(tipoDocumento.equals(TipoDocumentoNotificaEnum.ODG_BASE) || tipoDocumento.equals(TipoDocumentoNotificaEnum.ODG_SUPPLETIVO) || tipoDocumento.equals(TipoDocumentoNotificaEnum.ODG_FUORISACCO)){
			if(doc!=null && odg!=null ){
				String strOggetto = String.format(messageSource.getMessage("email.notificaOdG.title", null, locale), 
						odg.getTipoOdg().getDescrizione(),
						odg.getSedutaGiunta().getNumero(),
						formatter.print(odg.getSedutaGiunta().getPrimaConvocazioneInizio()));
				notificaMail.setOggetto(strOggetto);
			}
		}else if(tipoDocumento.equals(TipoDocumentoNotificaEnum.VARIAZIONE)){
			if(doc!=null && odg!=null && odg.getSedutaGiunta()!=null && odg.getSedutaGiunta().getLuogo()!=null){
				String strOggetto = String.format(messageSource.getMessage("email.notificaVS.title", null, locale), 
						odg.getSedutaGiunta().getNumero(),
						formatter.print(odg.getSedutaGiunta().getPrimaConvocazioneInizio()));
				notificaMail.setOggetto(strOggetto);
			}
		}else if(tipoDocumento.equals(TipoDocumentoNotificaEnum.ANNULLAMENTO)){
			if(doc!=null && odg!=null && odg.getSedutaGiunta()!=null && odg.getSedutaGiunta().getLuogo()!=null){
				String strOggetto = String.format(messageSource.getMessage("email.notificaAS.title", null, locale), 
						odg.getSedutaGiunta().getNumero(),
						formatter.print(odg.getSedutaGiunta().getPrimaConvocazioneInizio()));
				notificaMail.setOggetto(strOggetto);
			}
		}
		
		Profilo profilo = profiloService.findOne(idProfiloAutore);
		if (profilo!=null)
			notificaMail.setAutore(profilo);

		notificaMail.setDocumento(doc);
		log.info(String.format("DocumentoPdf : %s", doc.toString()));
		if (destInterno != null){
			if(destInterno!=null && destInterno.getDestinatario()!=null && destInterno.getDestinatario().getClassName().equalsIgnoreCase(Assessorato.class.getCanonicalName())){
				Assessorato assessorato = (Assessorato)destInterno.getDestinatario();
				if(assessorato!=null && assessorato.getProfiloResponsabileId()!=null){
					Profilo profiloAssessorato = profiloService.findOne(assessorato.getProfiloResponsabileId());
					if(profiloAssessorato.getUtente() != null && profiloAssessorato.getUtente().getEmail()!=null){
						assessorato.setEmail(profiloAssessorato.getUtente().getEmail());
					}
				}
			}
	    	if(destInterno!=null && destInterno.getDestinatario()!=null && destInterno.getDestinatario().getEmail()!=null && !destInterno.getDestinatario().getEmail().trim().equals("")){
	    		notificaMail.setDestinazioneNotifica(destInterno.getDestinatario().getEmail());
	    	}else{
	    		notificaMail.setStato(StatoJob.ERROR);
	    		notificaMail.setDettaglioErrore("Il destinatario interno non risulta provvisto di e-mail.");
	    	}
			notificaMail.setDestinatarioInterno(destInterno);
			notificaMail.setTipoInvio(TipoInvioNotificaEnum.MAIL.getDescrizione());
		}else if (destEsterno != null && destEsterno.getId() != null){
			if(destEsterno!=null && destEsterno.getPec()!=null && !destEsterno.getPec().trim().equals("")){
				notificaMail.setDestinazioneNotifica(destEsterno.getPec());
    		}else{
    			notificaMail.setStato(StatoJob.ERROR);
    			notificaMail.setDettaglioErrore("Al destinatario esterno non è associato un indirizzo PEC a cui inviare la notifica.");
    		}
			notificaMail.setDestinatarioEsternoId(destEsterno.getId());
			notificaMail.setTipoInvio(TipoInvioNotificaEnum.PEC.getDescrizione());
		}
		notificaMail.setTipoDocumento(tipoDocumento);
		
		this.createNews(notificaMail);
	}
	
	/**
	 * Gestione della notifica SMS
	 * @param seduta
	 * @param autore
	 * @param destId
	 * @param destNominativo
	 * @param destIsEsterno
	 * @param destTelefono
	 */
	private void notificaConvocazioneSMS(
			TipoDocumentoNotificaEnum tipoDocumento,
			DocumentoPdf documento, 
			Long idProfiloAutore,
			AttoHasDestinatario destInterno,
			RubricaDestinatarioEsterno destEsterno){
		log.debug("NewsService - notificaConvocazioneSMS - start");
		Locale locale = Locale.ITALY;
		News notificaSMS = new News();
		notificaSMS.setTipoInvio(TipoInvioNotificaEnum.SMS.getDescrizione());
		notificaSMS.setStato(StatoJob.NEW);
		notificaSMS.setDataCreazione(new DateTime());
		
		String numTelefono = "";
		String smsMessage = "";
		
		if(destInterno != null){
			log.debug("NewsService - notificaConvocazioneSMS - destInterno not null");

			if (destInterno.getTipoDestinatario().getDescrizione().equalsIgnoreCase("assessorato"))
				numTelefono = ((Assessorato) destInterno.getDestinatario()).getTelefono();
			else if (destInterno.getTipoDestinatario().getDescrizione().equalsIgnoreCase("utente"))
				numTelefono = ((Utente) destInterno.getDestinatario()).getTelefono();
		}
		
		// TODO: DA RIVEDERE... MANCA AL MOMENTO IL TELEFONO COME DATO PER LA RUBRICA DEST ESTERNI
		if(destEsterno != null){
			numTelefono = null;
		}
		
		if ((numTelefono != null && !numTelefono.equals(""))){
			Profilo profilo = new Profilo();
			profilo.setId(idProfiloAutore);
			notificaSMS.setAutore(profilo);
			notificaSMS.setDocumento(documento);
			
			if (destEsterno != null && destEsterno.getId() != null){
				notificaSMS.setDestinatarioEsternoId(destEsterno.getId());
				notificaSMS.setNominativoDestinatario(String.format("%s %s", destEsterno.getCognome(), destEsterno.getNome()));
				
			}
			if (destInterno != null){
				notificaSMS.setDestinatarioInterno(destInterno);
				notificaSMS.setNominativoDestinatario(destInterno.getDestinatario().getDescrizioneAsDestinatario());
			}
			
			notificaSMS.setTipoDocumento(tipoDocumento);
			
			String strDataOraSeduta = "";
			String strNumSeduta = "";
			String strTipoSeduta = "";
			try{
				OrdineGiorno odg = null;
				if(documento.getOrdineGiornoId()!=null) {
					odg = ordineGiornoService.findOne(documento.getOrdineGiornoId());
				}
				
				strNumSeduta = odg.getSedutaGiunta().getNumero();
				strDataOraSeduta = formatter.print(odg.getSedutaGiunta().getPrimaConvocazioneInizio());
				strTipoSeduta = odg.getTipoOdg().getDescrizione();
				log.debug(String.format("strNumSeduta:%s - strDataOraSeduta:%s - strTipoSeduta:%s", strNumSeduta, strDataOraSeduta, strTipoSeduta));
			} catch (Exception exp){
				log.error(exp.getMessage(), exp);
			}
			
			
			if(tipoDocumento.equals(TipoDocumentoNotificaEnum.ODG_BASE) || tipoDocumento.equals(TipoDocumentoNotificaEnum.ODG_SUPPLETIVO) || tipoDocumento.equals(TipoDocumentoNotificaEnum.ODG_FUORISACCO)){
				smsMessage = String.format(messageSource.getMessage("sms.notificaOdG.corpo", null, locale), strTipoSeduta, strNumSeduta, strDataOraSeduta);
			}else if(tipoDocumento.equals(TipoDocumentoNotificaEnum.VARIAZIONE)){
				smsMessage = String.format(messageSource.getMessage("sms.notificaVS.corpo", null, locale), strNumSeduta, strDataOraSeduta);
			}else if(tipoDocumento.equals(TipoDocumentoNotificaEnum.ANNULLAMENTO)){
				smsMessage = String.format(messageSource.getMessage("sms.notificaAS.corpo", null, locale), strNumSeduta, strDataOraSeduta);
			}
			JsonObject send = new JsonObject();
			send.addProperty("nome", notificaSMS.getNominativoDestinatario());
			send.addProperty("numero", numTelefono); 
			send.addProperty("testo", smsMessage);
			
			notificaSMS.setOggetto(smsMessage);
			notificaSMS.setDestinazioneNotifica(numTelefono + " - " + notificaSMS.getNominativoDestinatario());

			this.createNews(notificaSMS);
			
//			TODO gestire invio sms 
			//try {
//				// INVIO SMS
//				notificaSMS.setDataInvio(new DateTime());
//				ServiceResponse smsServiceResp =  smsService.invio(send);
//				log.debug(String.format("SMS Inviato :: nome = %s - numero = %s - testo = %s - Status code = %s - message = %s", 
//						send.get("nome"), send.get("numero"), send.get("testo"), 
//						smsServiceResp.getStatusCode(), smsServiceResp.getStatusMessage()));
//				
//				if (smsServiceResp.getStatusCode() == 0) {
//					// SMS INVIATO CORRETTAMENTE
//					log.info(String.format("notificaConvocazioneSMS - SMS inviato correttamente. [seduta id:%s, destinatario:%s]", seduta.getId(), notificaSMS.getNominativoDestinatario()));
//					notificaSMS.setStato(StatoJob.DONE);
//				} else {
//					// GESTIONE ERRORE INVIO
//					log.error(String.format("notificaConvocazioneSMS - ERRORE in invio SMS...[seduta id:%s, destinatario:%s, SMS status code:%s, SMS status msg:%s]", 
//							seduta.getId(), notificaSMS.getNominativoDestinatario(), smsServiceResp.getStatusCode(), smsServiceResp.getStatusMessage()));
//					log.error(smsServiceResp.getStatusMessage());
//					notificaSMS.setDataErrore(new DateTime());
//					notificaSMS.setDataInvio(null);
//					notificaSMS.setStato(StatoJob.ERROR);
//					notificaSMS.setDettaglioErrore(smsServiceResp.getStatusMessage().substring(0, smsServiceResp.getStatusMessage().length() > 255 ? 254 : smsServiceResp.getStatusMessage().length()-1 ));
//				}
//				this.updateNews(notificaSMS);
//			} catch (Exception e) {
//				// GESTIONE ERRORE INVIO
//				log.error(e.getMessage(), e);
//				notificaSMS.setDataErrore(new DateTime());
//				notificaSMS.setDataInvio(null);
//				notificaSMS.setStato(StatoJob.ERROR);
//				try{
//					notificaSMS.setDettaglioErrore(e.getMessage().substring(0, e.getMessage().length() > 255 ? 254 : e.getMessage().length()-1 ));
//				} catch(Exception exo){
//					notificaSMS.setDettaglioErrore("NON PERVENUTO");
//				}
//				this.updateNews(notificaSMS);
//			}
		} else {
			log.error("NewsService - notificaConvocazioneSMS - telefono null, impossibile inviare la notifica via SMS!!");
		}
	}
	
	/**
	 * Restituisce la lista di tutte le mails dei destinatari delle notifiche 
	 * per la data seduta di giunta....
	 * @param seduta
	 * @return
	 */
	public List<String> getDestinatariNotificheSeduta(SedutaGiunta seduta){

		List<String> retValue = new ArrayList<String>();

		String gruppoRuoloConsiglieri = WebApplicationProps.getProperty(ConfigPropNames.NOTIFICA_GRUPPORUOLO_CONSIGLIERI);
		String gruppoRuoloAltreStrutture = WebApplicationProps.getProperty(ConfigPropNames.NOTIFICA_GRUPPORUOLO_ALTRESTRUTTURE);
		
		try{
			// Recupero degli indirizzi email per gli assessori...
			List<Assessorato> assessorati = recuperaAssessoriBySeduta(seduta);
			if (assessorati != null){
				for (Assessorato ass : assessorati){
					fillEmailAndTelefono(ass);
					if (ass.getEmail()!= null)
						retValue.add(ass.getEmail());
				}
			}
			log.debug("Assessorati recuperati!!");

			// Recupero degli indirizzi email per i consiglieri...
			boolean isDaNotificareConsiglieri = false;
			if (seduta != null && seduta.getNotificaTuttiConsiglieri() != null)
				isDaNotificareConsiglieri = seduta.getNotificaTuttiConsiglieri();
			List<Profilo> consiglieri = recuperaProfiliByGR(gruppoRuoloConsiglieri, isDaNotificareConsiglieri);
			if (consiglieri != null){
				for (Profilo p : consiglieri){
					if (p.getUtente().getEmail() != null && !retValue.contains(p.getUtente().getEmail()))
						retValue.add(p.getUtente().getEmail());
				}
			}
			log.debug("Consiglieri recuperati!!");

			// Recupero degli indirizzi email per gli utenti delle altre strutture...
			boolean isDaNotificareAltreStrutture = false;
			if (seduta != null && seduta.getNotificaTuttiAltreStrutture() != null)
				isDaNotificareAltreStrutture = seduta.getNotificaTuttiAltreStrutture();
			List<Profilo> altreStrutture = recuperaProfiliByGR(gruppoRuoloAltreStrutture, isDaNotificareAltreStrutture);
			if (altreStrutture != null){
				for (Profilo p : altreStrutture){
					if (p.getUtente().getEmail() != null && !retValue.contains(p.getUtente().getEmail()))
						retValue.add(p.getUtente().getEmail());
				}
			}
			log.debug("Altre strutture recuperate!!");

			// Recupero degli indirizzi email per la rubrica interni...
			Set<AttoHasDestinatario> rubricaDestInterni = destinatarioInternoService.getDestinatariInterniByAttoId(seduta.getId(), false);
			if (rubricaDestInterni != null){
				for (AttoHasDestinatario rd_int : rubricaDestInterni){
					destinatarioInternoService.fillDestinatarioInterno(rd_int);
					if (rd_int.getTipoDestinatario().getDescrizione().equalsIgnoreCase("assessorato")){
						Assessorato ass = (Assessorato) rd_int.getDestinatario();
						fillEmailAndTelefono(ass);
						if (!retValue.contains(ass.getEmail()))
							retValue.add(ass.getEmail());
					} else if (rd_int.getTipoDestinatario().getDescrizione().equalsIgnoreCase("utente")){
						if (!retValue.contains(rd_int.getDestinatario().getEmail()))
							retValue.add(rd_int.getDestinatario().getEmail());
					}
				}
			}
			log.debug("Dest. Interni recuperati!!");

			// Recupero degli indirizzi email per la rubrica esterni...
			Set<RubricaDestinatarioEsterno> rubricaDestEsterni = sedutaGiuntaService.getRubricaDestinatariEsterni(seduta.getId());
			if (rubricaDestEsterni != null){
				for (RubricaDestinatarioEsterno rd_est : rubricaDestEsterni){
					if (rd_est.getEmail() != null && !retValue.contains(rd_est.getEmail()))
						retValue.add(rd_est.getEmail());
				}
			}
			log.debug("Dest. Esterni recuperati!!");

			log.debug(String.format("getDestinatariNotificheSeduta :: recuperat %s destinatari notifiche per la seduta con id:%s", 
					retValue.size(), seduta.getId()));
		} catch (Exception exp){
			log.error(exp.getMessage(), exp);
		}

		return retValue;
	}
	
	
	public void gestisciErroreNotifica(News notifica, boolean retry, Integer numeroMassimoTentativi, String dettaglio){
		
		boolean clean = false;
		notifica.setDataErrore(new DateTime());
		notifica.setDettaglioErrore(dettaglio);
		notifica.setDataInvio(null);
		
		int valNumeroMassimoTentativi = 0;
		if (numeroMassimoTentativi != null) {
			valNumeroMassimoTentativi = numeroMassimoTentativi.intValue();
		}
		
		if((valNumeroMassimoTentativi > 0) && (notifica.getProgressivoTentativo() < valNumeroMassimoTentativi)){
			notifica.setStato(StatoJob.ERROR);
		}
		else{
			notifica.setStato(StatoJob.FAILED);
			retry = false;
			clean = true;
		}
		updateNews(notifica);
		
		if(retry){
			// CIFRAESINNOV-120: non inserire un nuovo record
			/*
			News copyOfNews = (News)SerializationUtils.clone(notifica);
			if(copyOfNews.getOriginalNews()==null){
				copyOfNews.setOriginalNews(notifica);
			}
			copyOfNews.setId(null);
			copyOfNews.setStato(StatoJob.NEW);
			copyOfNews.setDettaglioErrore(null);
			copyOfNews.setDataStartInvio(null);
			copyOfNews.setDataErrore(null);
			copyOfNews.setDataCreazione(new DateTime());
			int progressivoTentativo = copyOfNews.getProgressivoTentativo();
			progressivoTentativo++;
			copyOfNews.setProgressivoTentativo(progressivoTentativo);
			*/
			int progressivoTentativo = notifica.getProgressivoTentativo();
			progressivoTentativo++;
			notifica.setProgressivoTentativo(progressivoTentativo);
			// createNews(copyOfNews);
			updateNews(notifica);
			log.info("ERRORE TENTATIVO INVIO NOTIFICA - ID NEWS " + notifica.getId());
			// log.info("CREATA NOTIFICA PER NUOVO TENTATIVO - ID NEWS " + copyOfNews.getId() + " (Notifica originale: " + notifica.getId() + ")");
		}
		
		if(clean){
			cleanPreviousError(notifica.getOriginalNews());
		}
	}
	
	
	private List<Assessorato> recuperaAssessoriBySeduta(final SedutaGiunta seduta){
		// recupero i destinatari di tipo Assessori...
		List<Assessorato> assessorati = null;
		
		if (seduta.getNotificaTuttiAssessori() != null && seduta.getNotificaTuttiAssessori())
			assessorati = assessoratoService.findAll();
		
		return assessorati;
	}
	private void fillEmailAndTelefono(Assessorato assessorato){
		Profilo profiloAssessorato = profiloService.findOne(assessorato.getProfiloResponsabileId());
		if(profiloAssessorato.getUtente() != null && profiloAssessorato.getUtente().getEmail()!=null){
			assessorato.setEmail(profiloAssessorato.getUtente().getEmail());
		}
		
		if(profiloAssessorato.getUtente() != null && profiloAssessorato.getUtente().getTelefono()!=null && (assessorato.getTelefono() == null || assessorato.getTelefono().equals(""))){
			assessorato.setTelefono(profiloAssessorato.getUtente().getTelefono());
		}
	}
	private List<Profilo> recuperaProfiliByGR(String nomeGR, boolean isDaNotificare){
		// recupero i destinatari di tipo Assessori...
		List<Profilo> profili = null;
		
		if (isDaNotificare){
			profili = profiloService.findActiveByGruppoRuolo(nomeGR);
		}
		
		return profili;
	}
}
