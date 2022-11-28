package it.linksmt.assatti.bpm.delegate.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;

@Service
@Transactional
public class MultiTransactionalDelegatesExecutionImpl implements DelegateBusinessLogic {
	
	private final Logger log = LoggerFactory.getLogger(MultiTransactionalDelegatesExecutionImpl.class);
	
	@Autowired
	private ApplicationContext context;
	
	@Inject
	private PlatformTransactionManager transactionManager;
	
	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		
		@SuppressWarnings("unchecked")
		List<String> delegateList = (List<String>)execution.getVariableLocal(AttoProcessVariables.MULTI_DELEGATES_LIST);
		final String MULTI_DELEGATES_VAR_LIST = "#varList#";
		final String MULTI_DELEGATES_VAR_SEPERATOR = "#!v#";
		final String MULTI_DELEGATES_VAR_VALUE_SEPERATOR = "::";
		final String DELEGATES_PACKAGE = "it.linksmt.assatti.bpm.delegate.impl";
		
		if(delegateList!=null) {
			for(String delegateExpression : delegateList) {
				if(delegateExpression!=null && !delegateExpression.trim().isEmpty()) {
					String delegateClass = delegateExpression.split(MULTI_DELEGATES_VAR_LIST)[0];
					delegateClass = StringUtils.capitalize(delegateClass) + "Impl";
					Map<String, Object> varMap = null;
					if(delegateExpression.split(MULTI_DELEGATES_VAR_LIST).length > 1) {
						String varListStr = delegateExpression.split(MULTI_DELEGATES_VAR_LIST)[1];
						varMap = new HashMap<String, Object>();
						for(int i = 0; i < varListStr.split(MULTI_DELEGATES_VAR_SEPERATOR).length; i++) {
							String varExpression = varListStr.split(MULTI_DELEGATES_VAR_SEPERATOR)[i];
							varMap.put(varExpression.split(MULTI_DELEGATES_VAR_VALUE_SEPERATOR)[0], varExpression.split(MULTI_DELEGATES_VAR_VALUE_SEPERATOR)[1]);
						}
					}
					execution.setVariablesLocal(varMap);
					DelegateBusinessLogic delegate;
					try {
						delegate = (DelegateBusinessLogic)context.getBean(Class.forName(DELEGATES_PACKAGE + "." + delegateClass));
						delegate.executeBusinessLogic(execution);
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}else {
			log.warn("nothing to do");
		}
		
	}
}
