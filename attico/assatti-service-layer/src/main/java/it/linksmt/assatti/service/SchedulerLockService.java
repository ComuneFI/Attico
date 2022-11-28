package it.linksmt.assatti.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.datalayer.domain.SchedulerLock;
import it.linksmt.assatti.datalayer.repository.SchedulerLockRepository;

@Service("schedulerLockService")
@Transactional
public class SchedulerLockService {

	@Autowired
	private SchedulerLockRepository schedulerLockRepository;

	public void insertLock(String signature) throws UnknownHostException {
		SchedulerLock lock = new SchedulerLock();
		lock.setHostname(InetAddress.getLocalHost().getHostName());
		lock.setSignature(signature);
		lock.setStartDate(new Date());
		schedulerLockRepository.save(lock);
	}

	public void deleteLock(String signature) {
		schedulerLockRepository.delete(signature);
	}
	
	public SchedulerLock getLock(String signature) {
		return schedulerLockRepository.findOne(signature);
	}
}
