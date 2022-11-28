package it.linksmt.assatti.datalayer.domain;

import java.util.ArrayList;
import java.util.List;

public enum TipoOdgBaseEnum {
	ORDINARIO(1L),
	STRAORDINARIO(2L);
	
	private TipoOdgBaseEnum(Long id){
		this.id = id;
	}
	
	private Long id;

	public Long getId() {
		return id;
	}
	
	static public List<Long> getIdsOdgBase(){
		List<Long> ids = new ArrayList<Long>();
		for(TipoOdgBaseEnum en : TipoOdgBaseEnum.values()){
			ids.add(en.getId());
		}
		return ids;
	}

}
