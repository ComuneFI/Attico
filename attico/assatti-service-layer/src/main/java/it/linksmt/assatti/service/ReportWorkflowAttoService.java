package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ReportWorkflowAtto;
import it.linksmt.assatti.datalayer.domain.StatoJob;
import it.linksmt.assatti.datalayer.domain.StatoReportWorkflowAtto;
import it.linksmt.assatti.datalayer.repository.ReportWorkflowAttoRepository;

@Service
@Transactional
public class ReportWorkflowAttoService{
	
	private final Logger log = LoggerFactory.getLogger(ReportWorkflowAttoService.class);
	
	@Autowired
	private ReportWorkflowAttoRepository reportWorkflowAttoRepository;
	
	@Transactional
	public void richiediNuovoReportWorkflowAttoRepository(Atto atto){
		ReportWorkflowAtto reportWorkflowAtto = new ReportWorkflowAtto();
		reportWorkflowAtto.setAtto(atto);
		reportWorkflowAtto.setStato(StatoReportWorkflowAtto.NEW);
		reportWorkflowAttoRepository.save(reportWorkflowAtto);
		log.debug("richiediNuovoReportWorkflowAttoRepository done for atto_id " +  atto.getId());
	}
	
	@Transactional
	public void insertNewReportWorkflowAtto(Long attoId){
		reportWorkflowAttoRepository.insertNewReportWorkflowAtto(attoId,StatoJob.NEW.name(),null);
		log.debug("insertNewReportWorkflowAtto done for attoId " +  attoId);
	}
	
	@Transactional(readOnly = true)
	public List<ReportWorkflowAtto> getDaGenerare(){
		List<StatoReportWorkflowAtto> stati = new ArrayList<StatoReportWorkflowAtto>();
		stati.add(StatoReportWorkflowAtto.ERROR);
		stati.add(StatoReportWorkflowAtto.NEW);

		return reportWorkflowAttoRepository.findByStatoIn(stati);
	}
	
	@Transactional
	public void update(ReportWorkflowAtto reportWorkflowAtto){
		reportWorkflowAttoRepository.save(reportWorkflowAtto);
	}
	
	@Transactional
	public void inProgress(ReportWorkflowAtto reportWorkflowAtto){
		reportWorkflowAttoRepository.updateStato(reportWorkflowAtto.getId(), StatoReportWorkflowAtto.IN_PROGRESS.name(), reportWorkflowAtto.getErrore());
	}
	
	@Transactional
	public void error(ReportWorkflowAtto reportWorkflowAtto){
		reportWorkflowAttoRepository.updateStato(reportWorkflowAtto.getId(), StatoReportWorkflowAtto.ERROR.name(), reportWorkflowAtto.getErrore());
	}
	
	@Transactional
	public void done(ReportWorkflowAtto reportWorkflowAtto){
		reportWorkflowAttoRepository.updateStato(reportWorkflowAtto.getId(), StatoReportWorkflowAtto.DONE.name(), null);
	}
	

	@Transactional
	public void cancellaReportWorkflowAtto(ReportWorkflowAtto reportWorkflowAtto){
		reportWorkflowAtto.setStato(StatoReportWorkflowAtto.CANCELED);
		reportWorkflowAttoRepository.save(reportWorkflowAtto);
	}
}