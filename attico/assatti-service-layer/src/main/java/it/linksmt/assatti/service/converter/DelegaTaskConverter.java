package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.List;

import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DelegaTask;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoDelegaTaskEnum;
import it.linksmt.assatti.datalayer.repository.DelegaTaskRepository;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.dto.DelegaTaskDto;
import it.linksmt.assatti.utility.Constants;

public class DelegaTaskConverter {

	public static DelegaTaskRepository delegaTaskRepository;
	
	public static BpmWrapperUtil bpmWrapperUtil;
	
	public static AttoService attoService;
	
	public static void init(DelegaTaskRepository delegaTaskRepository, BpmWrapperUtil bpmWrapperUtil, AttoService attoService) {
		DelegaTaskConverter.delegaTaskRepository = delegaTaskRepository;
		DelegaTaskConverter.bpmWrapperUtil = bpmWrapperUtil;
		DelegaTaskConverter.attoService = attoService;
	}
	
	public static DelegaTaskDto convertToDto(DelegaTask delega) {
		DelegaTaskDto delegaDto = null;
		if(delega!=null) {
			delegaDto = new DelegaTaskDto();
			delegaDto.setId(delega.getId());
			Profilo delegante = delega.getProfiloDelegante();
			if(delegante!=null && delegante.getAoo()!=null) {
				delegante.setAoo(new Aoo(delegante.getAoo().getId(), delegante.getAoo().getDescrizione(), delegante.getAoo().getCodice(), null));
			}
			delegaDto.setProfiloDelegante(delegante);
			Profilo delegato = delega.getProfiloDelegato();
			if(delegato!=null && delegato.getAoo()!=null) {
				delegato.setAoo(new Aoo(delegato.getAoo().getId(), delegato.getAoo().getDescrizione(), delegato.getAoo().getCodice(), null));
			}
			delegaDto.setProfiloDelegato(delegato);
			delegaDto.setEnabled(delega.getEnabled());
			if(delega.getAtto()!=null) {
				delegaDto.setCodiceCifra(delega.getAtto().getCodiceCifra());
			}
			delegaDto.setLavorazione(delega.getLavorazione());
			delegaDto.setTaskBpmId(delega.getTaskBpmId());
			if(delega.getAtto()!=null) {
				Atto minAtto = DomainUtil.minimalAtto(delega.getAtto());
				minAtto.setOggetto(delega.getAtto().getOggetto());
				minAtto.setCodiceCifra(delega.getAtto().getCodiceCifra());
				minAtto.setTipoAtto(new TipoAtto(delega.getAtto().getTipoAtto().getId(), delega.getAtto().getTipoAtto().getCodice(), delega.getAtto().getTipoAtto().getDescrizione()));
				delegaDto.setAtto(minAtto);
			}
			if(delega.getTipo()!=null) {
				delegaDto.setTipo(delega.getTipo().name());
			}
		}
		return delegaDto;
	}
	
	public static List<DelegaTaskDto> convertToDto(List<DelegaTask> delega) {
		List<DelegaTaskDto> listDelega = null;
		if(delega!=null) {
			listDelega = new ArrayList<>();
			for(DelegaTask d : delega) {
				listDelega.add(convertToDto(d));
			}
		}
		return listDelega;
	}

	public static DelegaTask convertToModel(DelegaTaskDto delegaDto) {
		DelegaTask delega = null;
		if(delegaDto!=null) {
			if(delegaDto.getId()!=null) {
				delega = DelegaTaskConverter.delegaTaskRepository.findOne(delegaDto.getId());
			}else {
				delega = new DelegaTask();
				delega.setAssigneeOriginario(delegaDto.getAssigneeOriginario());
			}
			delega.setProfiloDelegante(delegaDto.getProfiloDelegante());
			delega.setProfiloDelegato(delegaDto.getProfiloDelegato());
			delega.setEnabled(delegaDto.getEnabled() != null ? delegaDto.getEnabled() : false);
			
			String usernameOriginario = bpmWrapperUtil.getUsernameByUsernameBpm(delega.getAssigneeOriginario());
			Long idProfiloOriginario = bpmWrapperUtil.getIdProfiloByUsernameBpm(delega.getAssigneeOriginario());
			String assigneeDelegato = delega.getAssigneeOriginario().replaceFirst(usernameOriginario, delega.getProfiloDelegato().getUtente().getUsername());
			assigneeDelegato = assigneeDelegato.replaceFirst(Constants.BPM_USERNAME_SEPARATOR + idProfiloOriginario, Constants.BPM_USERNAME_SEPARATOR + delega.getProfiloDelegato().getId());
			
			delega.setAssigneeDelegato(assigneeDelegato);
			Atto atto = DelegaTaskConverter.attoService.findOneSimple(delegaDto.getAtto().getId());
			delega.setAtto(atto);
			delega.setLavorazione(delegaDto.getLavorazione());
			delega.setTaskBpmId(delegaDto.getTaskBpmId());
			if(delegaDto.getTipo()!=null) {
				for(TipoDelegaTaskEnum tipo : TipoDelegaTaskEnum.values()) {
					if(tipo.name().equalsIgnoreCase(delegaDto.getTipo())) {
						delega.setTipo(tipo);
						break;
					}
				}
			}
		}
		return delega;
	}

}
