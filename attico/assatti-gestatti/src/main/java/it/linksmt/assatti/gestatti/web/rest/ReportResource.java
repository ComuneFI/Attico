package it.linksmt.assatti.gestatti.web.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.itextpdf.text.DocumentException;

import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.TipoResocontoEnum;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.DocumentoPdfService;
import it.linksmt.assatti.service.ReportService;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.dto.TipoDocumentoDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing ModelloHtml.
 */
@RestController
@RequestMapping("/api")
public class ReportResource {

	private final Logger log = LoggerFactory.getLogger(ReportResource.class);

	@Inject
	private ReportService reportService;
	
	@Inject
	private AttoService attoService;
	
	@Inject
	private DocumentoPdfService documentoPdfService;
	
	@Inject
	private WorkflowServiceWrapper workflowService;
	
	
	/**
	 * Restituisce l'elenco dei tipi di documento per i quali si vuole visualizzare
	 * l'anteprima relativa al task corrente.
	 * 
	 * L'elenco dei tipi di documenti per i quali si vuole l'anteprima deve essere
	 * fornito dal processo mediante la variabile di processo "ELENCO_CODICE_TIPO_DOCUMENTO_PREVIEW".
	 *  
  	 * GET /preview/tipodocumento/elenco/{idAtto} -> elenco tipi di documento per preview
  	 * 
	 * @throws IOException
  	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/preview/elencotipodocumento/{taskBpmId}", method = RequestMethod.GET)
	@Timed
	public ResponseEntity getElencoTipoDocumentoPreview(
			@PathVariable final String taskBpmId,
			final HttpServletResponse response) throws GestattiCatchedException {
		try {
			log.debug("REST request to get tipoDocumento list : {}", taskBpmId);

			List<TipoDocumentoDto> listTipoDocumento = workflowService.getElencoTipoDocumentoPreview(taskBpmId);

			if (listTipoDocumento == null) {
				return new ResponseEntity<>(new ArrayList<TipoDocumentoDto>(), HttpStatus.OK);
			}
			return new ResponseEntity<>(listTipoDocumento, HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}

	/**
	 * GET /report -> Create a new report.
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/preview/{idModelloHtml}/atto{idAtto}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> previewAtto(
			@PathVariable("idAtto") Long idAtto,
			@PathVariable("idModelloHtml") Long idModelloHtml,
			@RequestParam("omissis") Boolean omissis
			) throws GestattiCatchedException {
		try{
			log.debug("preview:" + idAtto);
			
			return executeAttoPreview(idAtto, idModelloHtml, omissis, Boolean.FALSE);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	@RequestMapping(value = "/preview/reportIterAtto{idAtto}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> previewReportIter(@PathVariable("idAtto") Long idAtto) throws GestattiCatchedException {
		try{
			return executeReportIterPreview(idAtto);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	private ResponseEntity<ByteArrayResource> executeReportIterPreview(Long idAtto) throws GestattiCatchedException{

		try{
			Object obj = documentoPdfService.generaReportIter(idAtto, false);
			if(obj instanceof java.io.File) {
				return returnResponseEntity((java.io.File)obj);
			}else {
				return null;
			}
		}catch(Exception e){
			throw new GestattiCatchedException(e);
		}
	}
	
	/**
	 * GET /report -> Create a new report.
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/previewAdottato/{idModelloHtml}/atto{idAtto}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> previewAttoAdottato(@PathVariable("idAtto") Long idAtto,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws GestattiCatchedException {
		try{
			log.debug("preview:" + idAtto);
			
			return executeAttoPreview(idAtto, idModelloHtml, Boolean.FALSE, Boolean.TRUE);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
		
	/**
	 * GET /report ->  Create a new "parere" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	/*
	 * TODO: In ATTICO non previsti
	 *
	@RequestMapping(value = "/preview/{idModelloHtml}/parere{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> createParere(@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws CifraCatchedException {
		try{
			log.debug(String.format("preview:createParere:%s - idModelloHtml:%s", id, idModelloHtml));
				
			Parere parere = parereService.findOne(id);
			
			ModelloHtml modello = modelloService.findOne(idModelloHtml);
			log.debug(String.format("Modello id:%s - TipoDocumento:[codice:%s - descr:%s]", 
					modello.getId(), 
					modello.getTipoDocumento().getCodice(),
					modello.getTipoDocumento().getDescrizione()));
			
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setIdAtto(parere.getAtto().getId());
			
			Atto atto = attoService.findOneWithOrdineGiorno(parere.getAtto().getId());
//			Object a = atto.getOrdineGiornos()!=null ? atto.getOrdineGiornos() : atto.getOrdineGiornos();
			File result = null;
			if(modello.getTipoDocumento().getDescrizione().toLowerCase().contains("ritiro")){
				// Set<AttiOdg> ordineGiornos = parere.getAtto().getOrdineGiornos()!=null ? parere.getAtto().getOrdineGiornos() : null;
				reportDto.setTipoDoc("ritiro");
				List<AttiOdg> attiOdgs = new ArrayList<AttiOdg>();
				if (atto.getOrdineGiornos() != null){
					for(AttiOdg a :atto.getOrdineGiornos()){
						attiOdgs.add(a);
					}
				}
				
				result = reportService.previewParereRitiro(reportDto, parere, atto,attiOdgs ); 
			}else{
				reportDto.setTipoDoc("parere");
				result = reportService.previewParere(reportDto, parere); 
			}
			
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
	}
	*/
	
	/**
	 * GET /report ->  Create a new "parere" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	/*
	 * TODO: In ATTICO non previsto
	 * 
	@RequestMapping(value = "/preview/relata{attoId}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> previewRelataPubblicazione(@PathVariable("attoId") Long attoId) throws CifraCatchedException {
		try{
			log.debug("preview:previewRelataPubblicazione attoid " + attoId);
			return returnResponseEntity(attoService.getPdfRelataForPreview(attoId));
		}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
	}
	*/
	
	/**
	 * GET /report ->  Create a new "scheda anagrafico contabile" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	/*
	 * TODO: In ATTICO non previsti
	 *
	@RequestMapping(value = "/preview/{idModelloHtml}/schedaAnagraficoContabile{idatto}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> schedaAnagraficoContabile(@PathVariable("idatto") Long id, @PathVariable("idModelloHtml") Long idModelloHtml) throws CifraCatchedException {
		try{
			ReportDTO reportDto = new ReportDTO();
			reportDto.setTipoDoc("scheda_anagrafico_contabile");
			reportDto.setIdModelloHtml(idModelloHtml);
			
			File result = reportService.previewSchedaAnagraficoContabile(reportDto, attoService.findOne(id)); 
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
	}
	*/
	
	/**
	 * GET /preview ->  Create a preview resoconto report
	 *  idModelloHtml
	 *  id: id sedutaGiunta
	 * @throws IOException
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/preview/{idModelloHtml}/resoconto{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> createResocontoEsito(@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws GestattiCatchedException{
		try{
			log.debug("preview:createResoconto idSeduta:" + id);
				
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdSeduta(id);
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setTipoDoc(TipoResocontoEnum.DOCUMENTO_DEF_ESITO.name());
			File result = reportService.previewResoconto(reportDto); 
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /preview ->  Create a preview elencoVerbali report
	 *  idModelloHtml
	 *  id: id sedutaGiunta
	 * @throws IOException
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/preview/{idModelloHtml}/elencoVerbali{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> createResocontoVerbali(@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws GestattiCatchedException{
		try{
			log.debug("preview:createResoconto idSeduta:" + id);
				
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdSeduta(id);
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setTipoDoc(TipoResocontoEnum.DOCUMENTO_DEF_ELENCO_VERBALI.name());
			File result = reportService.previewResoconto(reportDto); 
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /report ->  Create  a preview resoconto report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/preview/{idModelloHtml}/verbale{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> createVerbale(@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws GestattiCatchedException {
		try{
			log.debug("preview:createVerbale:" + id);
				
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdSeduta(id);
			reportDto.setIdModelloHtml(idModelloHtml);
			File result = reportService.previewVerbale(reportDto); 
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /report ->  Create a new "PresenzeAssenze" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	/*
	 * TODO: In ATTICO non previsti
	 *
	@RequestMapping(value = "/preview/{idModelloHtml}/previewPresenzeAssenze{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> previewPresenzeAssenze(
			@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml,
			@RequestParam(value = "profiloId"  ,required=false ) final Long idProfilo) throws CifraCatchedException{
		try{
			log.debug("preview:PresenzeAssenze:" + id);
				
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdAtto(id);
			reportDto.setIdModelloHtml(idModelloHtml);
			File result = reportService.previewPresenzeAssenze(reportDto, idProfilo);
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
	}
	*/
	
	/**
	 * GET /report ->  Create a new "lettera" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	/*
	 * TODO: In ATTICO non previsti
	 *
	@RequestMapping(value = "/preview/{idModelloHtml}/lettera{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> createLettera(@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws CifraCatchedException{
		try{
			log.debug("preview:createLettera:" + id);
				
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdAtto(id);
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setTipoDoc("lettera");
			File result = reportService.previewLettera(reportDto); 
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
	}
	*/

	/**
	 * GET /report ->  Create a new "ordinegiorno" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/preview/{idModelloHtml}/ordinegiorno{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> createOrdinegiorno(@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws GestattiCatchedException {
		try{
			log.debug("preview:createOrdinegiorno:" + id);
				
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdAtto(id);
			reportDto.setIdModelloHtml(idModelloHtml);
			
			File result = reportService.previewOrdinegiorno(reportDto); 
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /report ->  Create a new "ordinegiorno" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/preview/{idModelloHtml}/ordinegiorno{id}.docx", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> createOrdinegiornoDocx(@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws GestattiCatchedException {
		try{
			log.debug("preview:createOrdinegiorno:" + id);
				
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdAtto(id);
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setIsDoc(true);
			File result = reportService.previewOrdinegiorno(reportDto); 
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /report ->  Create a new "variazioneestremiseduta" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/preview/{idModelloHtml}/variazioneestremiseduta{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> createVariazioneEstremiSeduta(
			@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml,
			@RequestParam("idProfilo") Long idProfilo) throws GestattiCatchedException{
		try{
			log.debug("preview:createVerbale:" + id);
				
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdSeduta(id);
			reportDto.setIdModelloHtml(idModelloHtml);
			File result = reportService.previewVariazioneEstremiSeduta(reportDto); 
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}	
	
	/**
	 * GET /report ->  Create a new "annullamentoseduta" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/preview/{idModelloHtml}/annullamentoseduta{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> createAnnullamentoSeduta(
			@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml,
			@RequestParam("idProfilo") Long idProfilo) throws GestattiCatchedException{
		try{
			log.debug("preview:createVerbale:" + id);
			
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdSeduta(id);
			reportDto.setIdModelloHtml(idModelloHtml);
			File result = reportService.previewVariazioneEstremiSeduta(reportDto); 
	
			return returnResponseEntity(result);
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	
	/**
	 * GET /report -> Create a new "parere omissis" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	/*
	 * TODO: In ATTICO non previsti
	 *
	@RequestMapping(value = "/omissis/{idModelloHtml}/parere{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> omissisParere(@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws CifraCatchedException{
		try{
			log.debug("omissisParere:" + id);
	
			Parere parere = parereService.findOne(id);
			
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdAtto(parere.getAtto().getId());
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setOmissis(Boolean.TRUE);
			
			File result = reportService.previewParere(reportDto, parere); 
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
	}
	*/
	
	/**
	 * GET /report -> Create a new "resoconto omissis" report
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	/*
	 * TODO: In ATTICO non previsti
	 *
	@RequestMapping(value = "/omissis/{idModelloHtml}/resoconto{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> omissisResoconto(@PathVariable("id") Long id,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws CifraCatchedException {
		try{
			log.debug("omissisResoconto:" + id);
	
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdAtto(id);
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setOmissis(Boolean.TRUE);
			File result = reportService.previewResoconto(reportDto); 
	
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
	}
	*/

	private ResponseEntity<ByteArrayResource> executeAttoPreview(Long idAtto, Long idModelloHtml, Boolean omissis, Boolean adottato) throws GestattiCatchedException,
			FileNotFoundException {
		try{
			Atto atto = attoService.findOneWithOrdineGiorno(idAtto);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdAtto(idAtto);
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setOmissis(omissis);

			File result = reportService.previewAtto(atto, reportDto);
			
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	


	@RequestMapping(value = "/preview/{idModelloHtml}/test/{tipo}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> test(@PathVariable("idModelloHtml") Long idModelloHtml,
			 									  @PathVariable("tipo") String tipo) throws GestattiCatchedException {
		try{
			log.debug("test:" + idModelloHtml);
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setTipoDoc(tipo);
			File result = reportService.test(reportDto);
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	@RequestMapping(value = "/omissis/{idModelloHtml}/test/{tipo}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> testOmissis(@PathVariable("idModelloHtml") Long idModelloHtml,
														 @PathVariable("tipo") String tipo)	throws GestattiCatchedException{
		try{
			log.debug("test-omissis:" + idModelloHtml);
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setTipoDoc(tipo);
			File result = reportService.testOmissis(reportDto);
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	@RequestMapping(value = "/previewword/{idModelloHtml}/test/{tipo}.docx", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> testWord(@PathVariable("idModelloHtml") Long idModelloHtml,
			 									  @PathVariable("tipo") String tipo) throws GestattiCatchedException {
		try{
			log.debug("test:" + idModelloHtml);
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setTipoDoc(tipo);
			reportDto.setIsDoc(true);
			File result = reportService.test(reportDto);
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	private ResponseEntity<ByteArrayResource> returnResponseEntity(File result)	throws GestattiCatchedException {
		try{
			log.info("returnResponseEntity starts");
			FileInputStream inputend = new FileInputStream(result);
			ByteArrayResource resultByte = new ByteArrayResource(IOUtils.toByteArray(inputend));
			IOUtils.closeQuietly(inputend);
			ResponseEntity<ByteArrayResource> responseEntity = new ResponseEntity<ByteArrayResource>(resultByte,
					HttpStatus.OK);
			
			result.deleteOnExit();
			log.info("returnResponseEntity ends");
			return responseEntity;
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}	
}
