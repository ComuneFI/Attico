package it.linksmt.assatti.service.dto;

import org.springframework.data.domain.Page;

import it.linksmt.assatti.datalayer.domain.dto.AooDto;

public class TaskDesktopGroupDTO{
	
	private Page<TaskDesktopDTO> page;
	private AooDto ufficio;
	public Page<TaskDesktopDTO> getPage() {
		return page;
	}
	public void setPage(Page<TaskDesktopDTO> page) {
		this.page = page;
	}
	public AooDto getUfficio() {
		return ufficio;
	}
	public void setUfficio(AooDto ufficio) {
		this.ufficio = ufficio;
	}
}

