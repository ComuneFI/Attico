package it.linksmt.assatti.service;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.QRuntimeOperation;
import it.linksmt.assatti.datalayer.domain.RuntimeOperation;
import it.linksmt.assatti.datalayer.domain.RuntimeOperationTaskEnum;
import it.linksmt.assatti.datalayer.repository.RuntimeOperationRepository;
import it.linksmt.assatti.service.exception.RuntimeOperationNotAlloweException;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * Service class for managing RuntimeOperations.
 */
@Service
@Transactional
public class RuntimeOperationService {
	
	@Inject
	private RuntimeOperationRepository runtimeOperationRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public RuntimeOperation add(RuntimeOperationTaskEnum task, Long profiloId) throws RuntimeOperationNotAlloweException {
		RuntimeOperation op = runtimeOperationRepository.findOne(QRuntimeOperation.runtimeOperation.task.eq(task).and(QRuntimeOperation.runtimeOperation.profiloId.eq(profiloId)));
		if(op!=null) {
			Integer maxSecondsLock = 300;
			try {
				maxSecondsLock = Integer.parseInt(WebApplicationProps.getProperty("runtimeTaskTimeLockLimitSeconds", "300"));
			}catch(Exception e) {};
			
			LocalDateTime limitTime = op.getTime().plusSeconds(maxSecondsLock);
			if(LocalDateTime.now().isBefore(limitTime)) {
				throw new RuntimeOperationNotAlloweException();
			}else {
				op.setTime(LocalDateTime.now());
			}
		}else {
			op = new RuntimeOperation();
			op.setTime(LocalDateTime.now());
			op.setProfiloId(profiloId);
			op.setTask(task);
		}
		return runtimeOperationRepository.save(op);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void end(RuntimeOperationTaskEnum task, Long profiloId) throws RuntimeOperationNotAlloweException {
		RuntimeOperation op = runtimeOperationRepository.findOne(QRuntimeOperation.runtimeOperation.task.eq(task).and(QRuntimeOperation.runtimeOperation.profiloId.eq(profiloId)));
		if(op!=null) {
			runtimeOperationRepository.delete(op);
		}
	}
	
}
