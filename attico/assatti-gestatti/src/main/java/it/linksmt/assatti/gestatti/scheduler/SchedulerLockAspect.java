package it.linksmt.assatti.gestatti.scheduler;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.linksmt.assatti.datalayer.domain.SchedulerChecking;
import it.linksmt.assatti.datalayer.domain.SchedulerLock;
import it.linksmt.assatti.datalayer.domain.util.ISchedulerGestatti;
import it.linksmt.assatti.service.SchedulerCheckingService;
import it.linksmt.assatti.service.SchedulerLockService;
import it.linksmt.assatti.utility.configuration.SchedulerProps;

@Component
@Aspect
public class SchedulerLockAspect {
	
	@Autowired
	private SchedulerLockService schedulerLockService;
	
	@Autowired
	private SchedulerCheckingService schedulerCheckingService;

	private final Logger log = LoggerFactory.getLogger(SchedulerLock.class);

	@Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
	public Object lockTask(ProceedingJoinPoint joinPoint) throws Throwable {
		String jobSignature = joinPoint.getSignature().toString();
		ISchedulerGestatti scheduler = (ISchedulerGestatti)joinPoint.getTarget();
		
		if(scheduler==null || scheduler.getNomeScheduler()==null || scheduler.getNomeScheduler().trim().isEmpty()) {
			log.error("scheduler name not identify - signature " + jobSignature);
			return null;
		}else if(!isSchedulerEnabled(scheduler.getNomeScheduler(), scheduler.getEnabledPropertyName())){
			try {
				log.warn("scheduler "+scheduler.getNomeScheduler()+" not enabled for this host " + InetAddress.getLocalHost().getHostName());
			}catch(Exception e) {};
			return null;
		}
		
		Object proceed = null;
		try {
			schedulerLockService.insertLock(jobSignature);
			
			try {
				proceed = joinPoint.proceed();
			}catch (Exception e) {
				log.error("error in " + jobSignature + " occurred", e);
			}
			
			try {
				schedulerLockService.deleteLock(jobSignature);
			}catch (Exception e) {
				log.error("error in deleting lock " + jobSignature + " occurred", e);
			}
			return proceed;
		}
		catch (Exception e) {
			log.warn("Job is currently locked: " + jobSignature);
			try {
				Integer maxLockMinutes = 60; //default 60 minutes
				String minutes = SchedulerProps.getProperty("scheduler.max.time.lock.minutes", maxLockMinutes + "").trim();
				try {
					maxLockMinutes = Integer.parseInt(minutes);
				}catch(Exception e3) {}
				
				Calendar c = new GregorianCalendar();
				c.add(Calendar.MINUTE, ((-1) * maxLockMinutes));
				
				SchedulerLock lock = schedulerLockService.getLock(jobSignature);
				if(lock!=null && lock.getStartDate().before(c.getTime())) {
					try {
						log.warn("Job lock timeout - Lock will be deleted for job " + jobSignature);
						schedulerLockService.deleteLock(jobSignature);
					}catch (Exception e3) {
						log.error("error in deleting lock " + jobSignature + " occurred", e);
					}
				}
			}catch(Exception e2) {
				log.warn("error in checking lock timeout " + jobSignature, e2);
			}
			return null;
		}
	}
	
	private boolean isSchedulerEnabled(String nomeScheduler, String enabledPropertyName) {
		Boolean schedulerAbilitato = false;
		if(enabledPropertyName!=null && !enabledPropertyName.isEmpty()) {
			try {
				String schedulerEnable = SchedulerProps.getProperty(enabledPropertyName);
				schedulerAbilitato = Boolean.parseBoolean(schedulerEnable);
			}catch(Exception e) {};
		}
		if(schedulerAbilitato!=null && schedulerAbilitato) {
			SchedulerChecking sc = schedulerCheckingService.getSettingsForThisHost(nomeScheduler);
			
			if (sc != null && sc.getEnabled() != null) {
				schedulerAbilitato = sc.getEnabled();
			}
			else {
				schedulerAbilitato = false;
				schedulerCheckingService.setSettingsForThisHost(nomeScheduler, false, null);
			}
		}
		if(schedulerAbilitato==null) {
			schedulerAbilitato = false;
		}
		return schedulerAbilitato;
	}
}
