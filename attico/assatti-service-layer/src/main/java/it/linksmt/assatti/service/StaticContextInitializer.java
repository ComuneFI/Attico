package it.linksmt.assatti.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.repository.DelegaTaskRepository;
import it.linksmt.assatti.service.converter.AttoSearchConverter;
import it.linksmt.assatti.service.converter.ConfigurazioneIncaricoProfiloConverter;
import it.linksmt.assatti.service.converter.DelegaTaskConverter;

@Component
public class StaticContextInitializer {
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private WorkflowServiceWrapper workflowService;
	
	@Inject
	private DelegaTaskRepository delegaTaskRepository;
	
	@Inject
	private AttoService attoService;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@PostConstruct
	public void init() {
		ConfigurazioneIncaricoProfiloConverter.init(profiloService);
		AttoSearchConverter.init(workflowService, bpmWrapperUtil);
		DelegaTaskConverter.init(delegaTaskRepository, bpmWrapperUtil, attoService);
	}
}
