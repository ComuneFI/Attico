package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.SchedulerLock;

public interface SchedulerLockRepository {

	/**
	 *
	 * @param schedulerLock
	 */
	void save(SchedulerLock schedulerLock);

	/**
	 *
	 * @param signature
	 * @return
	 */
	SchedulerLock findOne(String signature);

	/**
	 * @param signature
	 */
	void delete(String signature);
}
