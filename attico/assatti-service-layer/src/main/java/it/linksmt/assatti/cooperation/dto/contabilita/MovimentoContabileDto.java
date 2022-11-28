/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.dto.contabilita;

import java.io.Serializable;

/**
 * @author Gianluca Pindinelli
 *
 */
public class MovimentoContabileDto implements Serializable, Comparable<MovimentoContabileDto> {

	private static final long serialVersionUID = 3512322531823548907L;

	private ImpAccertamentoDto movImpAcce;
	private MovimentoLiquidazioneDto liquidazione;
	private DettaglioLiquidazioneDto dettaglioLiquidazione;
    
	public ImpAccertamentoDto getMovImpAcce() {
		return movImpAcce;
	}
	public void setMovImpAcce(ImpAccertamentoDto movImpAcce) {
		this.movImpAcce = movImpAcce;
	}
	public MovimentoLiquidazioneDto getLiquidazione() {
		return liquidazione;
	}
	public void setLiquidazione(MovimentoLiquidazioneDto liquidazione) {
		this.liquidazione = liquidazione;
	}
	public DettaglioLiquidazioneDto getDettaglioLiquidazione() {
		return dettaglioLiquidazione;
	}
	public void setDettaglioLiquidazione(DettaglioLiquidazioneDto dettaglioLiquidazione) {
		this.dettaglioLiquidazione = dettaglioLiquidazione;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dettaglioLiquidazione == null) ? 0 : dettaglioLiquidazione.hashCode());
		result = prime * result + ((liquidazione == null) ? 0 : liquidazione.hashCode());
		result = prime * result + ((movImpAcce == null) ? 0 : movImpAcce.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MovimentoContabileDto other = (MovimentoContabileDto) obj;
		if (dettaglioLiquidazione == null) {
			if (other.dettaglioLiquidazione != null)
				return false;
		} else if (!dettaglioLiquidazione.equals(other.dettaglioLiquidazione))
			return false;
		if (liquidazione == null) {
			if (other.liquidazione != null)
				return false;
		} else if (!liquidazione.equals(other.liquidazione))
			return false;
		if (movImpAcce == null) {
			if (other.movImpAcce != null)
				return false;
		} else if (!movImpAcce.equals(other.movImpAcce))
			return false;
		return true;
	}
	@Override
	public int compareTo(MovimentoContabileDto o) {
		if(o.hashCode() == this.hashCode()) {
			return 0;
		}else if(o.hashCode() > this.hashCode()) {
			return 1;
		}else {
			return -1;
		}
	}
	
}
