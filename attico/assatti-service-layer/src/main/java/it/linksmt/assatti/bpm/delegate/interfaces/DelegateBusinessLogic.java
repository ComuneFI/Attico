package it.linksmt.assatti.bpm.delegate.interfaces;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public interface DelegateBusinessLogic {
	void executeBusinessLogic(DelegateExecution execution) throws RuntimeException;
}
