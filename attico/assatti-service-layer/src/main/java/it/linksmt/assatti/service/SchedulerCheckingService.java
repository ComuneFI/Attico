package it.linksmt.assatti.service;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.QSchedulerChecking;
import it.linksmt.assatti.datalayer.domain.SchedulerChecking;
import it.linksmt.assatti.datalayer.repository.SchedulerCheckingRepository;
import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;


/**
 * Service class for managing users.
 */
@Service
@Transactional
public class SchedulerCheckingService {
	@Inject
    private SchedulerCheckingRepository schedulerCheckingRepository;
	

	private final Logger log = LoggerFactory.getLogger(SchedulerCheckingService.class);

	@Transactional(readOnly=true)
	public SchedulerChecking getSettingsForThisHost(String schedulerName){
		SchedulerChecking schedCheck = null;
		try {
			BooleanExpression predicate = QSchedulerChecking.schedulerChecking.schedulerName.eq(schedulerName).and(QSchedulerChecking.schedulerChecking.hostName.eq(InetAddress.getLocalHost().getHostName()));
			schedCheck = schedulerCheckingRepository.findOne(predicate);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return schedCheck;
	}
	
	@Transactional(readOnly=true)
	public List<SchedulerChecking> getAllSettingsForThisHost(){
		List<SchedulerChecking> schedCheck = null;
		try {
			BooleanExpression predicate = QSchedulerChecking.schedulerChecking.hostName.eq(InetAddress.getLocalHost().getHostName());
			schedCheck = Lists.newArrayList(schedulerCheckingRepository.findAll(predicate));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return schedCheck;
	}
	
	@Transactional(readOnly=true)
	public List<SchedulerChecking> getAllSettings(){
		return schedulerCheckingRepository.findAll();
	}
	
	@Transactional
	public void remove(Long id){
		schedulerCheckingRepository.delete(id);
	}
	
	@Transactional
	public void setSettingsForThisHost(String schedulerName, Boolean enabled, DateTime executionTime){
		try {
			SchedulerChecking sc = new SchedulerChecking();
			sc.setSchedulerName(schedulerName);
			if(enabled==null){
				enabled = false;
			}
			if(executionTime!=null){
				sc.setExecutionTime(executionTime);
			}
			sc.setEnabled(enabled);
			sc.setHostName(InetAddress.getLocalHost().getHostName());
			schedulerCheckingRepository.save(sc);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	public void setSettingsForThisHost(SchedulerChecking schedulerChecking){
		try {
			if(schedulerChecking.getEnabled()==null){
				schedulerChecking.setEnabled(false);
			}
			schedulerChecking.setHostName(InetAddress.getLocalHost().getHostName());
			schedulerCheckingRepository.save(schedulerChecking);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	public void saveSettings(SchedulerChecking schedulerChecking){
		if(schedulerChecking.getEnabled()==null){
			schedulerChecking.setEnabled(false);
		}
		schedulerCheckingRepository.save(schedulerChecking);
	}
}
