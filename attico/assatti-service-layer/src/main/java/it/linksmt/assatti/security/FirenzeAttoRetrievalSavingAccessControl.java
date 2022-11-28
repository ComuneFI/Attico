package it.linksmt.assatti.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.dto.TipoTaskDTO;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.AvanzamentoService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
@EnableTransactionManagement(proxyTargetClass = true)
public class FirenzeAttoRetrievalSavingAccessControl implements IAttoRetrievalSavingAccessControl{

	@Autowired
	private ProfiloService profiloService;
	
	@Autowired
	private AttoService attoService;
	
	@Autowired
	private AvanzamentoService avanzamentoServie;
	
	@Autowired
	private WorkflowServiceWrapper camunda;
	
	@Autowired
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Autowired
	private ServiceUtil serviceUtil;
	
	@Override
    @Transactional(readOnly=true)
    public boolean canReadAtto(Long attoId, Long profiloId, String taskBpmId, Long sedutaId) {
		boolean trusted = false;
		try {
			//verifica che l'utente è loggato
	    	if(profiloId !=null && SecurityUtils.isAuthenticated()) {
	    		if((SecurityUtils.isUserInRole(AuthoritiesConstants.ADMIN) || SecurityUtils.isUserInRole(AuthoritiesConstants.AMMINISTRATORE_RP)) && (taskBpmId==null || taskBpmId.isEmpty())) {
	    			trusted = true;
	    		}else {
	    			String username = SecurityUtils.getCurrentLogin();
		    		if(username!=null && !username.isEmpty()) {
		    			List<Profilo> profiliOfUser = profiloService.findActiveByUsername(username);
		    			//verifica che il profilo id utilizzato sia tra quelli disponibili per l'utente
		    			if(profiliOfUser!=null && profiliOfUser.size() > 0) {
		    				for(Profilo profilo : profiliOfUser) {
		    					if(profilo.getId().equals(profiloId)) {
		    						Atto atto = attoService.findOneSimple(attoId);
		    						if((taskBpmId==null || taskBpmId.isEmpty()) && 
		    								(
			    								(profilo.getAoo()!=null && profilo.getAoo().getId()!=null && atto.getAoo()!=null && atto.getAoo().getId()!=null && //il profilo è dirigente dell'aoo che ha istruito l'atto
			    								atto.getAoo().getProfiloResponsabileId()!=null && atto.getAoo().getId().equals(profilo.getAoo().getId()) &&
			    								profilo.getId().equals(atto.getAoo().getProfiloResponsabileId()))
		    								)
		    						) {
		    							trusted = true;
		    				  		}else {	
			    						//verifica che il profilo usato sia abilitato a gestire quel tipo di atto
			    						if(profilo.getTipiAtto().contains(atto.getTipoAtto())) {
		    								//se il profilo ha lavorato l'atto può vederlo
		    								if((taskBpmId==null || taskBpmId.isEmpty()) && avanzamentoServie.profiloWorkedAtto(attoId, profiloId)) {
		    									trusted = true;
		    								}else{
		    									if(taskBpmId!=null && taskBpmId.length() > 0L) {
		    										//il profilo ha in carico l'atto
		    										TipoTaskDTO task = camunda.getDettaglioTask(taskBpmId.toString());
		    										if(task!=null && task.getBusinessKey().trim().equals(atto.getId().toString().trim())) {
		    											String bpmUsername = bpmWrapperUtil.getUsernameForBpm(profiloId, -1L);
		    											if(task.getIdAssegnatario() !=null && (task.getIdAssegnatario().equalsIgnoreCase(bpmUsername) || task.getIdAssegnatario().startsWith(bpmUsername + Constants.BPM_INCARICO_SEPARATOR))) {
				    										trusted = true;
				    									}else {
				    										//il profilo ha il ruolo previsto come candidategroup dal task
				    										if(task.getCandidateGroups()!=null && !task.getCandidateGroups().isEmpty()) {
				    											String[] candateGroups = task.getCandidateGroups().split(",");
				    											String aooCodice = profilo != null && profilo.getAoo() != null && profilo.getAoo().getCodice() != null ? profilo.getAoo().getCodice() : null;
						    									if(aooCodice!=null && profilo.getGrupporuolo()!=null) {
							    									for(Ruolo r : profilo.getGrupporuolo().getHasRuoli()) {
							    										if(r!=null && r.getEnabled()!=null && r.getEnabled() && r.getCodice()!=null && !r.getCodice().isEmpty()) {
							    											for(int i = 0; i < candateGroups.length; i++) {
								    											if(candateGroups[i].equalsIgnoreCase(r.getCodice()) ||candateGroups[i].equalsIgnoreCase(r.getCodice() + Constants.BPM_ROLE_SEPARATOR + aooCodice)) {
								    												trusted = true;
								    												break;
								    											}
							    											}
							    											if(trusted) {
							    												break;
							    											}
							    										}
							    									}
						    									}
				    										}
				    									}
		    										}
		    									}
		    								}
			    						}
		    						}
		    						break;
		    					}
		    				}
		    			}
		    		}
	    		}
	    	}
		}catch(Exception e) {
			e.printStackTrace();
		}
    	return trusted;
    }

	@Override
	public boolean canSaveAtto(Long attoId, Long profiloId, String taskBpmId, Long sedutaId) {
		boolean trusted = false;
		try {
			//verifica che l'utente è loggato
	    	if(attoId!=null && profiloId !=null && SecurityUtils.isAuthenticated()) {
				String username = SecurityUtils.getCurrentLogin();
	    			List<Profilo> profiliOfUser = profiloService.findActiveByUsername(username);
	    			//verifica che il profilo id utilizzato sia tra quelli disponibili per l'utente
	    			if(profiliOfUser!=null && profiliOfUser.size() > 0) {
	    				for(Profilo profilo : profiliOfUser) {
							if(profilo.getId().equals(profiloId) && username!=null && !username.isEmpty()) {
								
								if(taskBpmId!=null && taskBpmId.length() > 0L) {
									TipoTaskDTO task = camunda.getDettaglioTask(taskBpmId.toString());
									if(task!=null && task.getBusinessKey().trim().equals(attoId + "")) {
										String bpmUsername = bpmWrapperUtil.getUsernameForBpm(profiloId, -1L);
										if(task.getIdAssegnatario() !=null && (task.getIdAssegnatario().equalsIgnoreCase(bpmUsername) || task.getIdAssegnatario().startsWith(bpmUsername + Constants.BPM_INCARICO_SEPARATOR))) {
											trusted = true;
										}
									}
									break;
								}else {
									if(isPubblicatoreAbilitato(profilo,attoId)) {
										return true;
									}
								}
							}
	    				}
	    			}
	    	}
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return trusted;
	}
	
	private boolean isPubblicatoreAbilitato(Profilo profilo, Long idAtto) {
		List<Object> ruoliProp = WebApplicationProps.getPropertyList("ruoli.pubblicazione");
		List<String> ruoliPubblicatore = null;
		if(ruoliProp!=null && ruoliProp.size() > 0) {
			ruoliPubblicatore = new ArrayList<String>();
			for(Object ruolo : ruoliProp) {
				ruoliPubblicatore.add((String)ruolo);
			}
		}else {
			ruoliPubblicatore = Arrays.asList("ROLE_RESPONSABILE_PUBBLICAZIONE_GIUNTA","ROLE_RESPONSABILE_PUBBLICAZIONE_CONSIGLIO","ROLE_RESPONSABILE_PUBBLICAZIONE_AD","ROLE_RESPONSABILE_PUBBLICAZIONE_ORD","ROLE_RESPONSABILE_PUBBLICAZIONE_DEC");
		}

		return serviceUtil.hasOneOfThisRuoli(profilo.getUtente().getUsername(), ruoliPubblicatore);
		/*
		Atto atto = attoService.findOne(idAtto);
		
		if(atto!=null && atto.getTipoAtto() != null) {
			String codiceRuolo = "";
			
			String codiceTipoAtto = atto.getTipoAtto().getCodice();
			if(codiceTipoAtto.equals("DG") || codiceTipoAtto.equals("DIG"))
			{
				codiceRuolo = "ROLE_RESPONSABILE_PUBBLICAZIONE_GIUNTA";
			}else if(codiceTipoAtto.equals("DC") || codiceTipoAtto.equals("DIC") || codiceTipoAtto.equals("DPC") || codiceTipoAtto.equals("ODG") || 
					codiceTipoAtto.equals("RIS") || codiceTipoAtto.equals("MZ") || codiceTipoAtto.equals("COM") || codiceTipoAtto.equals("DAT") ||
					codiceTipoAtto.equals("INT") || codiceTipoAtto.equals("QT") || codiceTipoAtto.equals("VERB")) {
				codiceRuolo = "ROLE_RESPONSABILE_PUBBLICAZIONE_CONSIGLIO";
			}else if(codiceTipoAtto.equals("DD") || codiceTipoAtto.equals("DL")) {
				codiceRuolo = "ROLE_RESPONSABILE_PUBBLICAZIONE_AD";
			}else if(codiceTipoAtto.equals("ORD")) {
				codiceRuolo = "ROLE_RESPONSABILE_PUBBLICAZIONE_ORD";
			}else if(codiceTipoAtto.equals("DEC")) {
				codiceRuolo = "ROLE_RESPONSABILE_PUBBLICAZIONE_DEC";
			}
				
			return serviceUtil.hasRuolo(profilo, codiceRuolo);
		}
		*/
	}

}
