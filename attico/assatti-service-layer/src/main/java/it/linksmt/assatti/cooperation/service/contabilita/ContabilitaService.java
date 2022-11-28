/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.service.contabilita;

import java.util.Date;
import java.util.List;

import it.linksmt.assatti.cooperation.dto.contabilita.ContabilitaDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoContabileDto;
import it.linksmt.assatti.service.exception.ServiceException;

/**
 * Servizi per l'interazione con il sistema di contabilità esterno.
 *
 * @author Gianluca Pindinelli
 *
 */
public interface ContabilitaService {

	/**
	 * Invia o modifica una bozza nel sistema di contabilità esterno.
	 *
	 * @param contabilitaDto
	 * @throws ServiceException
	 */
	void sendBozza(ContabilitaDto contabilitaDto) throws ServiceException;

	/**
	 * Carica la lista dei movimenti contabili.
	 *
	 * @param codiceTipoAtto
	 * @param numeroProposta
	 * @param annoCreazioneProposta
	 * @return
	 * @throws ServiceException
	 */
	List<MovimentoContabileDto> getMovimentiContabili(
			String codiceTipoAtto, String numero, int anno,
			boolean isBozza) throws ServiceException;

	/**
	 * Conferma l'atto sul sistema di contabilità.
	 *
	 * @param contabilitaDto
	 * @throws ServiceException
	 */
	void confirmAtto(ContabilitaDto contabilitaDto) throws ServiceException;
	
	/**
	 * Aggiorna la data di esecutività di un atto
	 *
	 * @param contabilitaDto
	 * @throws ServiceException
	 */
	void dataEsecutivitaAtto(String codiceTipoAtto, String numeroAdozione, int annoAdozione, Date dataEsecutivita) throws ServiceException;
	
	
	/**
	 * Elimina una bozza dal sistema di contabilità.
	 * 
	 * @param codiceTipoAtto
	 * @param numeroProposta
	 * @param annoCreazioneProposta
	 * @return
	 * @throws ServiceException
	 */
	void eliminaBozza(String codiceTipoAtto, String numeroProposta, int annoCreazioneProposta) throws ServiceException;
	
	/**
	 * Converte l'atto in bozza sul sistema di contabilità
	 *
	 * @param contabilitaDto
	 * @throws ServiceException
	 */
	void revertBozza(ContabilitaDto contabilitaDto) throws ServiceException;

	
	/**
	 * Verifica esistenza dell'atto o bozza sul sistema di contabilità
	 *
	 * @param codiceTipoAtto
	 * @param numero
	 * @param anno
	 * @param isBozza
	 */
	public boolean esisteBozzaAtto(
			final String codiceTipoAtto, final String numero, 
			final int anno, boolean isBozza) throws ServiceException;
}
