package it.linksmt.assatti.bpm.util;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanBpmThreadLocalListener implements ServletRequestListener {
	
	private final Logger log = LoggerFactory.getLogger(CleanBpmThreadLocalListener.class);

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		
		BpmThreadLocalUtil.setProfiloId(0);
		BpmThreadLocalUtil.setProfiloDeleganteId(0);
		BpmThreadLocalUtil.setProfiloOriginarioId(0);
		BpmThreadLocalUtil.setNomeAttivita(null);
		BpmThreadLocalUtil.setMotivazione(null);
		BpmThreadLocalUtil.setCodiceDecisioneLocal(null);
		
		log.debug("CLEAN THREAD LOCAL!");
	}

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		log.debug("THREAD LOCAL DATA: p-" + String.valueOf(BpmThreadLocalUtil.getProfiloId()) + 
				" d-"+ String.valueOf(BpmThreadLocalUtil.getProfiloDeleganteId()) + 
				" a-"+ String.valueOf(BpmThreadLocalUtil.getNomeAttivita()) + 
				" m-"+ String.valueOf(BpmThreadLocalUtil.getMotivazione()) +
				" c-"+ String.valueOf(BpmThreadLocalUtil.getCodiceDecisioneLocal()));
	}
}
