package it.linksmt.assatti.service;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.QTipoDocumentoSerie;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoSerie;
import it.linksmt.assatti.datalayer.repository.TipoDocumentoSerieRepository;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing tipoDocumentoSerie
 */
@Service
@Transactional
public class TipoDocumentoSerieService {
	
	@Inject
	private TipoDocumentoSerieRepository tipoDocumentoSerieRepository;
	
	@Transactional(readOnly = true)
	public Page<TipoDocumentoSerie> findAll(JsonObject search, Pageable pageInfo){
		
		BooleanExpression predicateTipoDocumentoSerie = QTipoDocumentoSerie.tipoDocumentoSerie.isNotNull();

		if(search!=null && !search.isJsonNull()){
			if(search.has("codice") && !search.get("codice").getAsString().isEmpty()){
				predicateTipoDocumentoSerie = 
						predicateTipoDocumentoSerie.and(QTipoDocumentoSerie.tipoDocumentoSerie.codice.containsIgnoreCase(search.get("codice").getAsString()));
			}
			if(search.has("descrizione") && !search.get("descrizione").getAsString().isEmpty()){
				predicateTipoDocumentoSerie = 
						predicateTipoDocumentoSerie.and(QTipoDocumentoSerie.tipoDocumentoSerie.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
		}
		
		Page<TipoDocumentoSerie> page = tipoDocumentoSerieRepository.findAll(predicateTipoDocumentoSerie, pageInfo);
		
		return page;
	}
	
	@Transactional(readOnly = true)
	public TipoDocumentoSerie findOne(Long id) {
		return getMinimal(tipoDocumentoSerieRepository.findOne(id));
	}
	
	private TipoDocumentoSerie getMinimal(TipoDocumentoSerie obj) {
		TipoDocumentoSerie out = null;
		if(obj != null){
			out = new TipoDocumentoSerie();
			out.setId(obj.getId());
			out.setCodice(obj.getCodice());;
			out.setDescrizione(obj.getDescrizione());
			out.setEnable(obj.getEnable());
//			out.setIsProposta(obj.getIsProposta());
//			TipoAtto tipoAtto = null;
//			if (obj.getIdTipoAtto()!= null) {
//				TipoAtto tipoAttoMain = tipoAttoService.findOne(obj.getIdTipoAtto());
//				tipoAtto = new TipoAtto();
//				tipoAtto.setId(tipoAttoMain.getId());
//				tipoAtto.setCodice(tipoAttoMain.getCodice());
//				tipoAtto.setDescrizione(tipoAttoMain.getDescrizione());
//			}
//			out.setTipoAtto(tipoAtto);
		}
		return out;
	}
	
	@Transactional
	public void save (TipoDocumentoSerie tipoDocumentoSerie) {
		
//		tipoDocumentoSerie.setIdTipoAtto(tipoDocumentoSerie.getTipoAtto()!=null?tipoDocumentoSerie.getTipoAtto().getId():null);
		
		tipoDocumentoSerieRepository.save(tipoDocumentoSerie);

	}
	
	@Transactional(readOnly=false)
	public void setIsAbilitato(final Boolean isAbilitato, final Long id){
		TipoDocumentoSerie tipoDocumentoSerie = tipoDocumentoSerieRepository.findOne(id);
		if(tipoDocumentoSerie!=null){
			tipoDocumentoSerie.setEnable(isAbilitato);
			tipoDocumentoSerieRepository.save(tipoDocumentoSerie);
		}
	}
	
	@Transactional(readOnly=true)
	public TipoDocumentoSerie findByCodice(String codice){
		return tipoDocumentoSerieRepository.findByCodice(codice);
	}
	
}
