package it.linksmt.assatti.service;

import java.util.Map;

import it.linksmt.assatti.datalayer.domain.Atto;

public interface IOperazione {

	public void execute( Atto atto, Map<String,Object> campi );
}
