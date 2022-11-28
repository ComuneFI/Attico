package it.linksmt.checkdb.service;


import it.linksmt.checkdb.exception.BasicException;

public interface ConnectionService {

	public String checkDB(String port, String host) throws BasicException;
}
