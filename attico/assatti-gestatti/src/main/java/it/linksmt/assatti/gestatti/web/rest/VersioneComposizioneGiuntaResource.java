package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.datalayer.domain.Composizione;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.ProfiloComposizione;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.repository.RuoloRepository;
import it.linksmt.assatti.gestatti.web.rest.dto.ComposizioneDTO;
import it.linksmt.assatti.gestatti.web.rest.dto.ProfiloComposizioneDTO;
import it.linksmt.assatti.service.ComposizioneService;
import it.linksmt.assatti.service.ProfiloComposizioneService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.VersioneComposizioneException;
import it.linksmt.assatti.utility.RoleCodes;

/**
 * REST controller for managing Composizione.
 */
@RestController
@RequestMapping("/api")
public class VersioneComposizioneGiuntaResource{

    private final Logger log = LoggerFactory.getLogger(VersioneComposizioneGiuntaResource.class);
  
    @Inject
    private ComposizioneService composizioneService;
    
    @Inject
    private ProfiloComposizioneService profiloComposizioneService;
    
    @Inject RuoloRepository ruoloRepository;
    
    @Inject
    private ProfiloService profiloService;
    
    private static final String organo = "G";

    
      
    
    /**
     * POST  /versioneComposizioneGiuntas -> Create a new versioneComposizioneGiunta.
     * @throws IOException 
     */
    @RequestMapping(value = "/versioneComposizioneGiuntas",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Composizione> create(@RequestBody Composizione composizione) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Composizione : {}", composizione);
	        if (composizione.getId() != null) {
	            //return ResponseEntity.badRequest().header("Failure", "A new composizione cannot already have an ID").build();
	        	new ResponseEntity<>(HttpStatus.BAD_REQUEST).getHeaders().set("Failure", "A new composizione cannot already have an ID");
	        }
	        composizione.setDataCreazione(new DateTime());
	        composizione.setOrgano(organo);
	        
	        Composizione nuovaComposizione = composizioneService.createNuovaComposizione(composizione);
	        
	        ArrayList<Long> listIdRuolo = new ArrayList<Long>();
			List<Ruolo> foundComponenteGiunta = ruoloRepository.findByCodice(RoleCodes.ROLE_COMPONENTE_GIUNTA);
			if (foundComponenteGiunta != null && foundComponenteGiunta.size() > 0) {
				for (Ruolo ruolo : foundComponenteGiunta) {
					listIdRuolo.add(ruolo.getId());
				}
				
				List<Profilo> profComponentiGiunta = profiloService.findByRuoloAoo(listIdRuolo, null);
				
				Collections.sort(profComponentiGiunta, new Comparator<Profilo>() {
				    @Override
				    public int compare(Profilo a1, Profilo a2) {
				    	return (a1.getOrdineGiunta()!=null?a1.getOrdineGiunta().intValue():0) - (a2.getOrdineGiunta()!=null?a2.getOrdineGiunta().intValue():0);
				    }
				});
				
				if(profComponentiGiunta != null) {
					for (Profilo componenteGiunta : profComponentiGiunta) {
						ProfiloComposizione profiloComposizione = new ProfiloComposizione();
						
						profiloComposizione.setComposizione(nuovaComposizione);
						profiloComposizione.setOrdine(componenteGiunta.getOrdineGiunta()!=null?componenteGiunta.getOrdineGiunta():99);
						profiloComposizione.setProfilo(componenteGiunta);
						profiloComposizione.setValido(true);
						profiloComposizione.setQualificaProfessionale(componenteGiunta.getQualificaProfessionaleGiunta()!=null?componenteGiunta.getQualificaProfessionaleGiunta():null);
						profiloComposizioneService.saveOrupdate(profiloComposizione);
						
					}
				}
				
			}
			
//			if(nuovaComposizione != null && nuovaComposizione.getPredefinita()!=null && nuovaComposizione.getPredefinita().booleanValue()) {
//        		//se viene richiesto il salvataggio come predefinita rendo non predefinita la precedente
//        		
//        		composizioneService.annullaPredefinita(nuovaComposizione.getId(), organo);
//        	}
	        
	        return new ResponseEntity<>(composizione, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /versioneComposizioneGiuntas -> Updates an existing versioneComposizioneGiunta.
     * @throws IOException 
     */
    @RequestMapping(value = "/versioneComposizioneGiuntas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Composizione> update(@RequestBody Composizione composizione) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Composizione : {}", composizione);
	    	if(validazioneProgressivi(composizione)) {
	    		if(validazioneQualifiche(composizione)) {
			    	Composizione composizioneAggiornata = null;
			        if (composizione.getId() == null) {
			            return create(composizione);
			        }else{
			        	List<Long> profiliAggiornati = new ArrayList<Long>();
			        	List<ProfiloComposizione> profiliComposizione = composizione.getHasProfiloComposizione();
			        	if(profiliComposizione!=null) {
			        		for (ProfiloComposizione profiloComposizione : profiliComposizione) {
			        			if(composizione!=null && composizione.getPredefinita()!=null && composizione.getPredefinita().equals(true)) {
			        				profiliAggiornati.add(profiloComposizione.getProfilo().getId());
			        				if(profiloComposizione.getValido()!=null && profiloComposizione.getValido().equals(true) && profiloComposizione.getOrdine()!=null) {
			        					profiloService.updateInfoSeduta(profiloComposizione.getOrdine(), profiloComposizione.getProfilo().getId(), true, profiloComposizione.getValido());
			        				}else {
			        					profiloService.updateInfoSeduta(9999, profiloComposizione.getProfilo().getId(), true, profiloComposizione.getValido());
			        				}
			        			}
								profiloComposizione.setComposizione(composizione);
							}
			        	}
			        	if(composizione!=null && composizione.getPredefinita()!=null && composizione.getPredefinita().equals(true)) {
				        	List<Long> listIdRuolo = new ArrayList<Long>();
				        	List<Ruolo> ruoli = ruoloRepository.findByCodice(RoleCodes.ROLE_COMPONENTE_GIUNTA);
				        	ruoli.addAll(ruoloRepository.findByCodice(RoleCodes.ROLE_PRESIDENTE_SEDUTA_GIUNTA));
							for (Ruolo ruolo : ruoli) {
								listIdRuolo.add(ruolo.getId());
							}
							List<Profilo> profili = profiloService.findByRuoloAoo(listIdRuolo, null);
							for(Profilo p : profili) {
								if(!profiliAggiornati.contains(p.getId())) {
									profiloService.updateValiditaSeduta(p, true, false);
								}
							}
			        	}
			        	
			        	composizioneService.saveOrupdate(composizione);
		//	        	
		//	        	composizioneAggiornata = composizioneService.findOne(composizione.getId());
		//	        	
		//	        	if(composizioneAggiornata != null && composizioneAggiornata.getPredefinita()!=null && composizioneAggiornata.getPredefinita().booleanValue()) {
		//	        		//se viene richiesto il salvataggio come predefinita rendo non predefinita la precedente
		//	        		
		//	        		composizioneService.annullaPredefinita(composizioneAggiornata.getId(), organo);
		//	        	}
			        	
			        }
			        return new ResponseEntity<>(composizioneAggiornata, HttpStatus.OK);
	    		}else {
	    			throw new GestattiCatchedException( new VersioneComposizioneException("Valorizzare le qualifiche professionali di ogni componente della Giunta.") );
	    		}
		     }else {
		    		throw new GestattiCatchedException( new VersioneComposizioneException("Controllare i valori inseriti nel campo ordine per ogni componente della Giunta.") );
		     }
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private boolean validazioneProgressivi(Composizione composizione) {
		if(composizione != null && composizione.getHasProfiloComposizione() != null) {
			List<ProfiloComposizione> profiliComposizione = composizione.getHasProfiloComposizione();
			HashMap<Integer, Integer> posizioni = new HashMap<Integer, Integer>();
       		for (ProfiloComposizione profiloComposizione : profiliComposizione) {
       			try {
					if(profiloComposizione.getValido()!=null && profiloComposizione.getValido().booleanValue()) {
						if(profiloComposizione.getOrdine()!=null && profiloComposizione.getOrdine().intValue()>0) {
							if(posizioni.containsKey(profiloComposizione.getOrdine())){
								return false;
							}else {
								posizioni.put(profiloComposizione.getOrdine(), profiloComposizione.getOrdine());
							}
						}else {
							return false;
						}
					}
				} catch (Exception e) {
					return false;
				}
       			
			}
		}
		return true;
	}
    
    private boolean validazioneQualifiche(Composizione composizione) {
		if(composizione != null && composizione.getHasProfiloComposizione() != null) {
			List<ProfiloComposizione> profiliComposizione = composizione.getHasProfiloComposizione();
       		for (ProfiloComposizione profiloComposizione : profiliComposizione) {
       			try {
					if(profiloComposizione.getValido()!=null && profiloComposizione.getValido().booleanValue()) {
						if(profiloComposizione.getQualificaProfessionale()==null || profiloComposizione.getQualificaProfessionale().getId()==null) {
							return false;
						}
					}
				} catch (Exception e) {
					return false;
				}
       			
			}
		}
		return true;
	}

    /**
     * GET  /versioneComposizioneGiuntas -> get all the versioneComposizioneGiunta.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/versioneComposizioneGiuntas",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Composizione>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "descrizione", required = false) String descrizione,
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "predefinita", required = false) Boolean predefinita) throws GestattiCatchedException {
    	try{
    		
	    	log.debug("REST request to get all Composizione");
	        JsonObject jsonSearch = this.buildSearchObject(descrizione, version, predefinita);
	        ResponseEntity<List<Composizione>> list = composizioneService.search(jsonSearch, offset, limit);
	        
	        return list;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
	 * GET /versioneComposizioneConsiglios/caricaComposizioni
	 * @throws IOException
	 */
	@RequestMapping(value = "/versioneComposizioneGiuntas/caricaComposizioni", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ComposizioneDTO>> caricaComposizioni() throws GestattiCatchedException {
		try{
			List<ComposizioneDTO> composizioniDto = new ArrayList<ComposizioneDTO>();
			List<Composizione >composizioni = composizioneService.findAllComposizioni(organo);
			if(composizioni!=null) {
				for (Composizione composizione : composizioni) {
					ComposizioneDTO dto = new ComposizioneDTO();
					dto.setDataCreazione(composizione.getDataCreazione());
					dto.setDescrizione(composizione.getDescrizione());
					dto.setId(composizione.getId());
					dto.setOrgano(composizione.getOrgano());
					dto.setPredefinita(composizione.getPredefinita());
					dto.setVersion(composizione.getVersion());
					List<ProfiloComposizioneDTO> profiliComposizione = new ArrayList<ProfiloComposizioneDTO>();
					for (int i = 0; i < composizione.getHasProfiloComposizione().size(); i++) {
						
						ProfiloComposizione profiloComposizione = composizione.getHasProfiloComposizione().get(i);

						ProfiloComposizioneDTO profiloComposizioneDTO = new ProfiloComposizioneDTO();
						profiloComposizioneDTO.setIdProfiloComposizione(profiloComposizione.getId());
						profiloComposizioneDTO.setProfilo(profiloComposizione.getProfilo());
						profiloComposizioneDTO.setOrdine(profiloComposizione.getOrdine());
						if(profiloComposizione.getQualificaProfessionale()!=null && profiloComposizione.getQualificaProfessionale().getId()>0) {
							profiloComposizioneDTO.setQualificaProfessionale(profiloComposizione.getQualificaProfessionale());
						}
						profiloComposizioneDTO.setValido(profiloComposizione.getValido());
						
						profiliComposizione.add(profiloComposizioneDTO );
					}
					dto.setProfiliComposizione(profiliComposizione);
					
					composizioniDto.add(dto);
				}
			}
			
			
			return new ResponseEntity<>(composizioniDto!=null ? composizioniDto : new ArrayList<ComposizioneDTO>(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    private JsonObject buildSearchObject(String descrizione, String version, Boolean predefinita) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("descrizione", descrizione);
	    	json.addProperty("version", version);
	    	if(predefinita!=null){
	    		json.addProperty("predefinita", predefinita);
	    	}
	    	json.addProperty("organo", organo);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /versioneComposizioneGiuntas/:id -> get the "id" versioneComposizioneGiunta.
     */
    @RequestMapping(value = "/versioneComposizioneGiuntas/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Composizione> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get versioneComposizioneGiunta : {}", id);
	        Composizione composizione = composizioneService.findOne(id);
	        if (composizione == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	       
	        return new ResponseEntity<>(composizione, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

}
