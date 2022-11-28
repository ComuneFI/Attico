package it.linksmt.assatti.fdr.security;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class FdrWsSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private String username;
	private String password;
	
    public FdrWsSecurityHandler(String username, String password) {
    	this.username = username;
    	this.password = password;
    }

    @Override
	public boolean handleMessage(SOAPMessageContext soapMessageContext) {

		Boolean outboundProperty = (Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outboundProperty.booleanValue()) {
			try {
				SOAPEnvelope soapEnvelope = soapMessageContext.getMessage().getSOAPPart().getEnvelope();
				SOAPHeader header = soapEnvelope.getHeader();
				if (header == null) {
					header = soapEnvelope.addHeader();
				}
                SOAPFactory factory = SOAPFactory.newInstance();
                String prefix = "wsse";
                String uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
                SOAPElement securityElem = factory.createElement("Security", prefix, uri);
                securityElem.addAttribute(QName.valueOf("xmlns:wsu"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
                SOAPElement tokenElem = factory.createElement("UsernameToken", prefix, uri);
                tokenElem.addAttribute(QName.valueOf("wsu:Id"), "UsernameToken-5473A5BE2BF6C0AEA9146660353283724");
                SOAPElement userElem = factory.createElement("Username", prefix, uri);
                userElem.addTextNode(username);
                SOAPElement pwdElem = factory.createElement("Password", prefix, uri);
                pwdElem.addTextNode(password);
                pwdElem.addAttribute(QName.valueOf("Type"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
                tokenElem.addChildElement(userElem);
                tokenElem.addChildElement(pwdElem);
                securityElem.addChildElement(tokenElem);
                header.addChildElement(securityElem);
			} catch (Exception e) {
				throw new RuntimeException("Error on wsSecurityHandler: " + e.getMessage());
			}
		}
		return true;
	}

    @Override
    public Set<QName> getHeaders() {
        return new TreeSet();
    }
    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }
    @Override
    public void close(MessageContext context) {
        //
    }
}
