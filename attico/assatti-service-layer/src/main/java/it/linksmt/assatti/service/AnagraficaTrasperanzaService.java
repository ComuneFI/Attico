package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.AmbitoDl33;
import it.linksmt.assatti.datalayer.domain.Cat_obbligo_dl33;
import it.linksmt.assatti.datalayer.domain.Dato;
import it.linksmt.assatti.datalayer.domain.Macro_cat_obbligo_dl33;
import it.linksmt.assatti.datalayer.domain.MateriaDl33;
import it.linksmt.assatti.datalayer.domain.Obbligo_DL33;
import it.linksmt.assatti.datalayer.domain.Scheda;
import it.linksmt.assatti.datalayer.domain.SchedaDato;
import it.linksmt.assatti.datalayer.domain.TipoDatoEnum;
import it.linksmt.assatti.datalayer.repository.AmbitoDl33Repository;
import it.linksmt.assatti.datalayer.repository.Macro_cat_obbligo_dl33Repository;

/**
 * Service class for managing profile.
 */
@Service
@Transactional
public class AnagraficaTrasperanzaService {

	private final Logger log = LoggerFactory.getLogger(AnagraficaTrasperanzaService.class);

	@Inject
	private AmbitoDl33Repository ambitoDl33Repository;
	
	
	@Inject
	private Macro_cat_obbligo_dl33Repository  macro_cat_obbligo_dl33Repository;
	
	@Transactional(readOnly=true)
	public List<AmbitoDl33> getAllAmbitoAttivo(){
		log.debug("getAllAmbitoAttivo");
		 List<AmbitoDl33>  ambiti =  ambitoDl33Repository.findAllByOrderByDenominazioneAsc();
		 for (AmbitoDl33 ambitoDl33 : ambiti) {
			 ambitoDl33.getMaterie();
			 AmbitoDl33 minAmbito = new AmbitoDl33();
			 minAmbito.setId(ambitoDl33.getId());
			 
			 for (MateriaDl33 materia :  ambitoDl33.getMaterie()) {
				 materia.getDenominazione();
				 materia.setAmbitoDl33(minAmbito);
			 }
		 }
		 
		 return ambiti;
	}
	
	@Transactional(readOnly=true)
	public List<Macro_cat_obbligo_dl33> getAllAttivo(){
		log.debug("getAllAttivo");
		List<Macro_cat_obbligo_dl33> list = macro_cat_obbligo_dl33Repository.findAll();
		List<Macro_cat_obbligo_dl33> listaFiltrata = new ArrayList<Macro_cat_obbligo_dl33>();
		for (Macro_cat_obbligo_dl33 macro : list) {
			if(macro.getAttiva()!=null && macro.getAttiva() == true){
				listaFiltrata.add(macro);
			}else{
				continue;
			}
			macro.getDescrizione();
			Set<Cat_obbligo_dl33> listaCategorieFiltrate = new HashSet<Cat_obbligo_dl33>();
			for ( Cat_obbligo_dl33 categoria : macro.getCategorie()) {
				if(categoria.getAttiva()==null || categoria.getAttiva() == false){
					continue;
				}else{
					listaCategorieFiltrate.add(categoria);
				}
				categoria.getDescrizione();
				categoria.setFk_cat_obbligo_macro_cat_obbligo_idx( new Macro_cat_obbligo_dl33(macro.getId()));
				Set<Obbligo_DL33> listaObblighiFiltrati = new HashSet<Obbligo_DL33>();
				for (  Obbligo_DL33 obbligo : categoria.getObblighi() ) {
					if(obbligo.getAttivo() == null || obbligo.getAttivo() == false){
						continue;
					}else{
						listaObblighiFiltrati.add(obbligo);
					}
					obbligo.setCat_obbligo_DL33( new Cat_obbligo_dl33(categoria.getId()));
					obbligo.getDescrizione();
					for (  Scheda scheda : obbligo.getSchedas() ) {
						scheda.getEtichetta();
						Scheda minScheda = new Scheda(scheda.getId());
						for (  SchedaDato campo : scheda.getCampi()  ) {
							campo.setScheda(minScheda);
							campo.getDato().getEtichetta();
							createOptions(campo.getDato());
						}
					}
				}
				categoria.setObblighi(listaObblighiFiltrati);
			}
			macro.setCategorie(listaCategorieFiltrate);
		}
		return listaFiltrata;
	}

	private void createOptions(Dato dato) {
		
		if(dato != null &&  TipoDatoEnum.select.equals( dato.getTipoDato() ) ){
			if( dato.getMultivalore()!= null  ){
				String[] options = dato.getMultivalore().split("\n");
				SortedSet<String> optionsSet = new TreeSet<String>();
				for (String option : options) {
					optionsSet.add(option);
				}
				dato.setOptions(optionsSet);
				dato.setMultivalore(null);
			}
		}
	}
}
