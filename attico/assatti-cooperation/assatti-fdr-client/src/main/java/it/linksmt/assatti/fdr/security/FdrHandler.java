package it.linksmt.assatti.fdr.security;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;

public class FdrHandler implements HandlerResolver {

	private SOAPHandler soapHandler;
	
	public FdrHandler(SOAPHandler soapHandler) {
		this.soapHandler = soapHandler;
	}
	
	@Override
	public List<Handler> getHandlerChain(PortInfo portInfo) {
		List<Handler> handlerList = new ArrayList<Handler>();
        handlerList.add(soapHandler);
        return handlerList;
	}
}
