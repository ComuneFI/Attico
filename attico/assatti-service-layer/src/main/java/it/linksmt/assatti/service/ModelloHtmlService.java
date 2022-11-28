package it.linksmt.assatti.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.QModelloHtml;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.repository.ModelloHtmlRepository;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ModelloHtmlService {
	private final Logger log = LoggerFactory
			.getLogger(ModelloHtmlService.class);

	@Inject
	private ModelloHtmlRepository modelloHtmlRepository;
	
	@Inject
	private TipoAttoService tipoAttoService;
	
	@Inject
	private TipoDocumentoService tipoDocumentoService;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<ModelloHtml>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateModelloHtml = QModelloHtml.modelloHtml.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("titolo")!=null && !search.get("titolo").isJsonNull() && search.get("titolo").getAsString()!=null && !"".equals(search.get("titolo").getAsString())){
				predicateModelloHtml = predicateModelloHtml.and(QModelloHtml.modelloHtml.titolo.containsIgnoreCase(search.get("titolo").getAsString()));
			}
			if(search.get("tipoDocumento")!=null && !search.get("tipoDocumento").isJsonNull() && search.get("tipoDocumento").getAsString()!=null && !"".equals(search.get("tipoDocumento").getAsString())){
					predicateModelloHtml = predicateModelloHtml.and(QModelloHtml.modelloHtml.tipoDocumento.codice.eq(search.get("tipoDocumento").getAsString()));
			}
		}
		Page<ModelloHtml> page = modelloHtmlRepository.findAll(predicateModelloHtml, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/modelloHtmls", offset, limit);
        return new ResponseEntity<List<ModelloHtml>>(page.getContent(), headers, HttpStatus.OK);
	}

	@Transactional
	public void save(ModelloHtml modelloHtml) throws IOException {
		if (modelloHtml.getId() != null) {
			//reportService.test(modelloHtml.getId());
		}

		modelloHtmlRepository.save(modelloHtml);

	}

	@Transactional
	public void deleteAll() throws IOException {
		
		modelloHtmlRepository.deleteAll();
	}
	
	@Transactional(readOnly = true)
	public List<ModelloHtml> findAll() {
		List<ModelloHtml> l = modelloHtmlRepository.findAll();
		for (ModelloHtml modelloHtml : l) {
			modelloHtml.setHtml(null);

		}

		return l;
	}
	
	@Transactional(readOnly = true)
	public List<ModelloHtml> findByTipoDocumento(String tipoDocumento) {
		List<ModelloHtml> l = modelloHtmlRepository.findAllByTipoDocumento(tipoDocumentoService.findByCodice(tipoDocumento));
		for (ModelloHtml modelloHtml : l) {
			modelloHtml.getHtml();
		}
		
		return l;
	}
	
	@Transactional(readOnly = true)
	public List<ModelloHtml> findByTitolo(String titoloModello) {
		List<ModelloHtml> l = modelloHtmlRepository.findAllByTitolo(titoloModello);
		for (ModelloHtml modelloHtml : l) {
			modelloHtml.getHtml();
		}
		return l;
	}
	
	@Transactional(readOnly = true)
	public List<Long> findIdModelloByCodiciTipiAtto(List<String> codTipiDocumento) {
		List<ModelloHtml> l = Lists.newArrayList(modelloHtmlRepository.findAll(QModelloHtml.modelloHtml.tipoDocumento.codice.toLowerCase().in(codTipiDocumento)));
		List<Long> ids = new ArrayList<Long>();
		for(ModelloHtml m : l) {
			if(m.getId()!=null) {
				ids.add(m.getId());
			}
		}
		return ids;
	}
	
	
	/*
	 * TODO: In ATTICO non previsto 
	 *
	@Transactional
	public void createAttoDirigenziale() throws IOException{
		log.info("createAttoDirigenziale");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/determinadirigenziale.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("DETERMINA DIRIGENZIALE");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.tipo_proposta_determinazione.name()));
		
		modelloHtmlRepository.save(modelloHtml);
		
	}
	*/
	
	@Transactional
	public void createReportRicerca() throws IOException{
		log.info("createReportRicerca");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/report_ricerca.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("REPORT RICERCA");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.report_ricerca.name()));
		
		modelloHtmlRepository.save(modelloHtml);
		
	}
	
	/*
	 * TODO: In ATTICO non previsti
	 *
	@Transactional
	public void createRelataPubblicazione() throws IOException{
		log.debug("createRelataPubblicazione");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/relatadipubblicazione.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1);
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("RELATA PUBBLICAZIONE");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.relata_pubblicazione.name()));
		
		modelloHtmlRepository.save(modelloHtml);
		
	}
	
	@Transactional
	public void createDeliberaGiunta() throws IOException{
		log.info("createDeliberaGiunta");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/deliberagiunta.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("DELIBERA GIUNTA");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.tipo_proposta_delibera_giunta.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createDeliberaConsiglio() throws IOException{
		log.info("createDeliberaConsiglio");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/deliberaconsiglio.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("DELIBERA CONSIGLIO");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.tipo_proposta_delibera_consiglio.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createSchemaDisegnoLegge() throws IOException{
		log.info("createSchemaDisegnoLegge");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/schemadisegnolegge.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("SCHEMA DISEGNO LEGGE");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.tipo_proposta_schema_disegno_legge.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createComunicazione() throws IOException{
		log.info("createComunicazione");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/comunicazione.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("COMUNICAZIONE");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.tipo_proposta_comunicazione.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createDisegnoLegge() throws IOException{
		log.info("createDisegnoLegge");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/disegnolegge.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("DISEGNO DI LEGGE");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.tipo_proposta_disegno_legge.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createRefertoTecnico() throws IOException{
		log.info("createRefertoTecnico");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/refertotecnico.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("REFERTO TECNICO");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.referto_tecnico.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createOrdinanza() throws IOException{
		log.info("createOrdinanza");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/ordinanza.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("ORDINANZA");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.atto.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createDecretoPresidente() throws IOException{
		log.info("createDecretoPresidente");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/decretopresidente.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("DECRETO DEL PRESIDENTE");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.atto.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	*/
	
	/*
	 * TODO: In ATTICO non previsti
	 * 
	@Transactional
	public void createResoconto() throws IOException{
		log.info("createResoconto");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/resoconto.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("RESOCONTO");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.resoconto.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	*/
	
	@Transactional
	public void createVerbaleGiunta() throws IOException{
		log.info("createVerbaleGiunta");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/verbalegiunta.html");
		str = FileUtils.readFileToString(resource.getFile(), StandardCharsets.ISO_8859_1 );
		log.debug(str);
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("VERBALE GIUNTA");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.verbalegiunta.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createVerbaleConsiglio() throws IOException{
		log.info("createVerbaleConsiglio");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/verbaleconsiglio.html");
		str = FileUtils.readFileToString(resource.getFile(), StandardCharsets.ISO_8859_1 );
		log.debug(str);
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("VERBALE CONSIGLIO");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.verbaleconsiglio.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	/*
	 * TODO: In ATTICO non previsto
	 * 
	@Transactional
	public void createLettera() throws IOException{
		log.info("createLettera");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/lettera.html");
		str = FileUtils.readFileToString(resource.getFile(), StandardCharsets.ISO_8859_1 );
		log.debug(str);
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("LETTERA");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.lettera.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createParere() throws IOException{
		log.info("createParere");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/parere.html");
		str = FileUtils.readFileToString(resource.getFile(), StandardCharsets.ISO_8859_1 );
		log.debug(str);
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("PARERE");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.parere.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createSchedaAnagraficoContabile() throws IOException{
		log.info("createSchedaAnagraficoContabile");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/scheda_anagrafico_contabile.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("SCHEDA ANAGRAFICO CONTABILE");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.scheda_anagrafico_contabile.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}

	@Transactional
	public void createAttoInesistente() throws IOException{
		log.info("createPropostaInesistente");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/atto_inesistente.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("ATTO INESISTENTE");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.atto_inesistente.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	@Transactional
	public void createRestituzioneSuIstanzaUfficioProponente() throws IOException{
		log.info("createRestituzioneSuIstanzaUfficioProponente");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/restituzioneIstanzaUfficioProponente.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("RESTITUZIONE UFFICIO PROPONENTE");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.restituzione_su_istanza_ufficio_proponente.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	*/
	
	@Transactional
	public void createOdgGiunta() throws IOException{
		log.info("createOdgGiunta");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/ordinegiornogiunta.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("ORDINE del GIORNO GIUNTA");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.ordinegiornogiunta.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional
	public void createOdgConsiglio() throws IOException{
		log.info("createOdgConsiglio");
		String str = null;
		
		ClassPathResource resource = new ClassPathResource("/modelli/ordinegiornoconsiglio.html");
		str = FileUtils.readFileToString(resource.getFile(),  StandardCharsets.ISO_8859_1 );
		log.debug(str);
		ModelloHtml modelloHtml = new ModelloHtml();
		modelloHtml.setHtml(str);
		modelloHtml.setTitolo("ORDINE del GIORNO CONSIGLIO");
		modelloHtml.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.ordinegiornoconsiglio.name()));
		
		modelloHtmlRepository.save(modelloHtml);
	}
	
	@Transactional(readOnly = true)
	public ModelloHtml findOne(Long id) {
		return modelloHtmlRepository.findOne(id);
	}
	
	@Transactional
	public void delete(Long id) {
		modelloHtmlRepository.delete(id);
	}

}
