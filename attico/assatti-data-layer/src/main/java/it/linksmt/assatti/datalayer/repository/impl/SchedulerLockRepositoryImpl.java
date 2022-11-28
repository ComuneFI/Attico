package it.linksmt.assatti.datalayer.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import it.linksmt.assatti.datalayer.domain.SchedulerLock;
import it.linksmt.assatti.datalayer.repository.SchedulerLockRepository;

@Repository
public class SchedulerLockRepositoryImpl implements SchedulerLockRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void save(SchedulerLock schedulerLock) {
		em.persist(schedulerLock);
	}

	@Override
	public SchedulerLock findOne(String signature) {
		SchedulerLock schedulerLock = em.find(SchedulerLock.class, signature);
		return schedulerLock;
	}

	@Override
	public void delete(String signature) {
		SchedulerLock schedulerLockSaved = em.find(SchedulerLock.class, signature);
		em.remove(schedulerLockSaved);
	}

}

