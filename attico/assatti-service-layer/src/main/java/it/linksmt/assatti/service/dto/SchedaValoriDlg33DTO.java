package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedaValoriDlg33DTO  implements Serializable{
	
	private	Map<Long,List<Map<Long,Object>>> elementiSchede = new HashMap<Long,List<Map<Long,Object>>>();
	
	private	String activeTab;
	private	Long macroId;
	private	Long  categoriaId;
	private	Long obbligoId;
	private	Long attoId;
	
	
	
	public Map<Long, List<Map<Long, Object>>> getElementiSchede() {
		return elementiSchede;
	}
	public void setElementiSchede(Map<Long, List<Map<Long, Object>>> elementiSchede) {
		this.elementiSchede = elementiSchede;
	}
	public String getActiveTab() {
		return activeTab;
	}
	
	public void setActiveTab(String activeTab) {
		this.activeTab = activeTab;
	}
 
	public Long getMacroId() {
		return macroId;
	}
	public void setMacroId(Long macroId) {
		this.macroId = macroId;
	}
	public Long getCategoriaId() {
		return categoriaId;
	}
	public void setCategoriaId(Long categoriaId) {
		this.categoriaId = categoriaId;
	}
	public Long getObbligoId() {
		return obbligoId;
	}
	public void setObbligoId(Long obbligoId) {
		this.obbligoId = obbligoId;
	}
	public Long getAttoId() {
		return attoId;
	}
	public void setAttoId(Long attoId) {
		this.attoId = attoId;
	}
	@Override
	public String toString() {
		return "SchedaValoriDlg33DTO [elementiSchede=" + elementiSchede + ", activeTab=" + activeTab + ", macroId="
				+ macroId + ", categoriaId=" + categoriaId + ", obbligoId=" + obbligoId + ", attoId=" + attoId + "]";
	}
	  
	
	
}
