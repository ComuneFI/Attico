package it.linksmt.checkdb.service.impl;


import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import it.linksmt.checkdb.exception.BasicException;
import it.linksmt.checkdb.exception.ServiceException;
import it.linksmt.checkdb.service.ConnectionService;
import it.linksmt.checkdb.util.ConnectionConstants;
import it.linksmt.checkdb.util.MysqlConnect;

@Service
public class ConnectionServiceImpl implements ConnectionService {
	
	@Value("${db.host.vip}")
    private String hostVip;
	
	@Value("${db.host.nodo1}")
    private String hostNodo1;
	
	@Value("${db.host.nodo2}")
    private String hostNodo2;
	
	@Value("${db.name.check}")
    private String nameDb;
	
	@Value("${db.username}")
    private String username;
	
	@Value("${db.password}")
    private String password;
	

	private Logger logger = LoggerFactory.getLogger(ConnectionServiceImpl.class);

	
	@Override
	public String checkDB(String port, String host) throws BasicException {
		
		MysqlConnect mysqlConnect = new MysqlConnect();
		try {
			String hostCheck = null;
			if(host != null && !host.isEmpty()) {
				if(host.equals(ConnectionConstants.DB_NODO_1)) {
					hostCheck = hostNodo1;
				}
				else if(host.equals(ConnectionConstants.DB_NODO_2)){
					hostCheck = hostNodo2;
				}
				else if(host.equals(ConnectionConstants.DB_VIP)){
					hostCheck = hostVip;
				}
			}
			PreparedStatement statement = mysqlConnect.connect(username, password, hostCheck, port, nameDb).prepareStatement(ConnectionConstants.TEST_QUERY);
			ResultSet rs= statement.executeQuery();  
			while(rs.next()){  
				if(rs.getInt(1) == 1) {
					return HttpStatus.OK.name();
				}
			}
		}
		catch (Exception e) {
			logger.error("ConnectionServiceImpl :: checkDB :: ", e);
			throw new ServiceException(e);
		}
		finally {
		    mysqlConnect.disconnect();
		}
		throw new BasicException("KO", "ERRORE NEL RISULTATO");
	}


}
