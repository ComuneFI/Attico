package it.linksmt.assatti.datalayer.domain;

import java.util.HashSet;
import java.util.Set;

public class DomainUtil {
	
	/**
	 * Crea un Aoo con i dati da tornare minimali
	 * @param aoo
	 * @return
	 */
	public static Aoo minimalAoo(Aoo aoo) {
		if(aoo!= null ){
			return new Aoo(aoo.getId(),aoo.getDescrizione(),aoo.getCodice(), aoo.getIdentitavisiva(), aoo.getLogo());
		}
		
		return null;
	}
	
	public static Aoo simpleAoo(Aoo aoo) {
		Aoo simple = null;
		if(aoo!= null ){
			simple = new Aoo(aoo.getId(),aoo.getDescrizione(),aoo.getCodice(), aoo.getIdentitavisiva(), aoo.getLogo());
			simple.setValidita(aoo.getValidita());
			simple.setProfiloResponsabileId(aoo.getProfiloResponsabileId());
			simple.setTipoAoo(aoo.getTipoAoo());
			simple.setUo(aoo.getUo());
			if(aoo.getAooPadre()!=null) {
				simple.setAooPadre(DomainUtil.simpleAoo(aoo.getAooPadre()));
			}
		}
		
		return simple;
	}
	
	public static Profilo minimalProfilo(Profilo p, boolean includiQualifiche) {
		Profilo n = new Profilo();
		if(p.getAoo()!=null) {
			n.setAoo(new Aoo(p.getAoo().getId(), p.getAoo().getDescrizione(), p.getAoo().getCodice(), null));
		}
		n.setId(p.getId());
		n.setDescrizione(p.getDescrizione());
		n.setValidita(p.getValidita());
		n.setOrdineGiunta(p.getOrdineGiunta());
		if(p.getUtente()!=null) {
			n.setUtente(new Utente(p.getUtente().getId(), p.getUtente().getCodicefiscale(), p.getUtente().getUsername(), p.getUtente().getCognome(), p.getUtente().getNome()));
		}
		if(includiQualifiche) {
			if(p.getHasQualifica()!=null && p.getHasQualifica().size() > 0) {
				n.setHasQualifica(p.getHasQualifica());
			}
		}
		return n;
	}
	
	public static Profilo minimalProfilo(Profilo p) {
		return DomainUtil.minimalProfilo(p, false);
	}
	
	public static void nameCerca(RubricaDestinatarioEsterno dest) {
		if(dest!= null ){
			String nameCerca = "";
			
			if("AZIENDA".equals( dest.getTipo()) ){
				nameCerca = dest.getDenominazione();
			}else{
				nameCerca = dest.getCognome()+ " " + dest.getNome() ;
			}
			dest.setNameCerca(nameCerca.toLowerCase() );
			
			
			dest.setImage( "assets/images/"+dest.getTipo() +".png");
			
			
//			
//			 if( rub.tipo === 'AZIENDA'){
//		          rub.nameCerca=rub.denominazione ;
//		        }else{
//		          rub.nameCerca= rub.cognome+ " "+rub.nome;
//		        }
//		      rub.nameCerca =angular.lowercase(rub.nameCerca ); 
		}
		 
	}

	public static Atto minimalAtto(Atto atto) {
		if(atto != null  )
			return new Atto(atto.getId());
		return null;
	}

	public static SchedaDato minimalSchedaDato(Long schedaDatoId) {
		SchedaDato schedaDato = new SchedaDato();
		schedaDato.setId( schedaDatoId  );
		return schedaDato;
	}
	
	/**
	 * Crea una SedutaGiunta con i dati da tornare minimali
	 * @param seduta
	 * @return
	 */
	public static SedutaGiunta minimalSedutaGiunta(SedutaGiunta seduta) {
		SedutaGiunta retValue = null;
		// Dati base della seduta
		if(seduta!= null ){
			retValue = new SedutaGiunta(seduta.getId(),
										seduta.getLuogo(),
										seduta.getDataOra());
			retValue.setNumero(seduta.getNumero());
			retValue.setPrimaConvocazioneInizio(seduta.getPrimaConvocazioneInizio());
			retValue.setSecondaConvocazioneInizio(seduta.getSecondaConvocazioneInizio());
			retValue.setInizioLavoriEffettiva(seduta.getInizioLavoriEffettiva());
		}
		// Dati base del Presidente...
		if (seduta!=null && seduta.getPresidente()!=null && seduta.getPresidente().getUtente()!=null){
			Utente u_pres = new Utente(seduta.getPresidente().getUtente().getId(), 
								  seduta.getPresidente().getUtente().getCodicefiscale(),
								  seduta.getPresidente().getUtente().getUsername(),
								  seduta.getPresidente().getUtente().getCognome(),
								  seduta.getPresidente().getUtente().getNome());
			Profilo p_pres = new Profilo(seduta.getPresidente().getId());
			p_pres.setUtente(u_pres);
			retValue.setPresidente(p_pres);
		}
		// Dati base del Segretario...
		if (seduta!=null && seduta.getSegretario()!=null && seduta.getSegretario().getUtente()!=null){
			Utente u_segr = new Utente(seduta.getSegretario().getUtente().getId(), 
								  seduta.getSegretario().getUtente().getCodicefiscale(),
								  seduta.getSegretario().getUtente().getUsername(),
								  seduta.getSegretario().getUtente().getCognome(),
								  seduta.getSegretario().getUtente().getNome());
			Profilo p_segr = new Profilo(seduta.getSegretario().getId());
			p_segr.setUtente(u_segr);
			retValue.setSegretario(p_segr);
		}
		
		return retValue;
	}
	
	public static Parere minimalParere(Parere parere) {
		
		Parere par = new Parere();
		
//		par.setAllegati(allegati);
		par.setAnnullato(parere.getAnnullato());
		par.setAoo(minimalAoo(parere.getAoo()));
		par.setAtto(parere.getAtto());
		par.setCreatedBy(parere.getCreatedBy());
		par.setCreatedDate(parere.getCreatedDate());
		par.setCreateUser(parere.getCreateUser());
		par.setData(parere.getData());
		par.setDataInvio(parere.getDataInvio());
		par.setDataScadenza(parere.getDataScadenza());
		par.setDataSollecito(parere.getDataSollecito());
		par.setDescrizioneQualifica(parere.getDescrizioneQualifica());
//		par.setDocumentiPdf(documentiPdf);
		par.setId(parere.getId());
		par.setLastModifiedBy(parere.getLastModifiedBy());
		par.setLastModifiedDate(parere.getLastModifiedDate());
		par.setOrigine(parere.getOrigine());
		par.setParere(parere.getParere());
		par.setParerePersonalizzato(parere.getParerePersonalizzato());
		par.setParereSintetico(parere.getParereSintetico());
		par.setProfilo(parere.getProfilo());
		if(parere.getProfilo()!=null && parere.getProfilo().getAoo()!=null) {
			parere.getProfilo().setAoo(DomainUtil.minimalAoo(parere.getProfilo().getAoo()));
		}
		par.setProfiloRelatore(parere.getProfiloRelatore());
		par.setStatoRisposta(parere.getStatoRisposta());
		par.setTerminiPresentazione(parere.getTerminiPresentazione());
		par.setTipoAzione(parere.getTipoAzione());
		par.setTitolo(parere.getTitolo());
		par.setVersion(parere.getVersion());

//		if (parere.getDocumentiPdf() != null) {
//			for (DocumentoPdf docParere : parere.getDocumentiPdf()) {
//				if (docParere.getFile() != null) {
//					docParere.getFile().getNomeFile();
//				}
//			}
//		}
//
//		if (parere.getAllegati() != null) {
//			for (DocumentoInformatico allegato : parere.getAllegati()) {
//				allegato.setAtto(null);
//				allegato.setParere(null);
//				if (allegato.getFile() != null) {
//					allegato.getFile().getNomeFile();
//				}
//			}
//		}
		return par;
	}
	
	public static Set<Parere> minimalPareri(Set<Parere> lst) {
		Set<Parere> pareri = null;
		if(lst != null && !lst.isEmpty()) {
			pareri = new HashSet<Parere>();
			for (Parere par : lst) {
				pareri.add(minimalParere(par));
			}
		}
		return pareri;
	}

	
	//TODO: Verificare se serve...
//	public static Atto StoricoAttoDirigenzialeToAtto(StoricoAttoDirigenziale attoStorico) {
//		Atto retValue = null;
//		if(attoStorico != null  ){
//			retValue = new Atto(attoStorico.getId());
//			
//			retValue.setCodiceCifra(attoStorico.getCodiceCifra());
//			retValue.setDataCreazione(attoStorico.getDataCreazione());
//			retValue.setOggetto(attoStorico.getOggetto());
//		}
//		return retValue;
//	}

}
