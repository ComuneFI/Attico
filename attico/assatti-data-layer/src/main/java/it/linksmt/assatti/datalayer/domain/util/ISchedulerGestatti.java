package it.linksmt.assatti.datalayer.domain.util;

public interface ISchedulerGestatti {
	/**
	 * Si tratto dello scheduerName della tabella schedulerChecking
	 */
	public String getNomeScheduler();
	
	/**
	 * Si tratta di un eventuale property booleana per l'abilitazione/disabilitazione dello scheduler
	 * Se non è prevista ritornare null
	 */
	public String getEnabledPropertyName();
}
