package it.linksmt.assatti.datalayer.domain;

public class SedutaGiuntaConstants {
	
	public enum organoSeduta {
		G {
			public String toString() {
				return "Giunta";
			}
		},
		C {
			public String toString() {
				return "Consiglio";
			}
		},
	};
	
	public enum fasiSeduta {
		PREDISPOSIZIONE,
		PREDISPOSTA,
		CONCLUSA,
		ANNULLATA
	};

	public static statiSeduta getStatoSedutaByDescrizione(String statoStr, String organo) {
		for(statiSeduta s : statiSeduta.values()) {
			if(s.toStringByOrgano(organo).equalsIgnoreCase(statoStr)) {
				return s;
			}
		}
		return null;
	}
	
	public static statiSeduta getStatoSedutaByCodice(String codiceStato) {
		for(statiSeduta s : statiSeduta.values()) {
			if(s.name().equalsIgnoreCase(codiceStato)) {
				return s;
			}
		}
		return null;
	}
	
	public enum statiSeduta {
		/*
		 * IN ATTICO NON PREVISTO
		 * 
		sedutaInAttesaDiFirmaOdgOdlBase {
			public String toStringByOrgano(String organo) {
				return "Seduta in attesa di firma " + getLabelByOrgano(organo);
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		*/
		odgOdlBaseInPredisposizione {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Base in predisposizione";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
			public String getFase() {
				return fasiSeduta.PREDISPOSIZIONE.toString();
			}
			public int getOrdine(String organo) {
				return 0;
			}
		},
		
		odgOdlBaseConsolidato {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Base consolidato";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
			public String getFase() {
				return fasiSeduta.PREDISPOSTA.toString();
			}
			public int getOrdine(String organo) {
				return 0;
			}
		},
		
		odgOdlSuppletivoConsolidato {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Suppletivo consolidato";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
			public String getFase() {
				return fasiSeduta.PREDISPOSTA.toString();
			}
			public int getOrdine(String organo) {
				return 0;
			}
		},
		
		odgOdlFuoriSaccoConsolidato {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Fuori Sacco consolidato";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
			public String getFase() {
				return fasiSeduta.PREDISPOSTA.toString();
			}
			public int getOrdine(String organo) {
				return 0;
			}
		},
		
		sedutaInAttesaDocumentoVariazione {
			public String toString() {
				return "Seduta in attesa di generazione documento di variazione";
			}
			public String getFase() {
				return fasiSeduta.PREDISPOSTA.toString();
			}
			public int getOrdine(String organo) {
				return 0;
			}
		},
		
		sedutaDocumentoVariazioneGenerato {
			public String toString() {
				return "Seduta con documento di variazione generato";
			}
			public String getFase() {
				return fasiSeduta.PREDISPOSTA.toString();
			}
			public int getOrdine(String organo) {
				return 0;
			}
		},
		
		sedutaConclusaIndicazioneAttiTrattati {
			public String toString() {
				return "Seduta conclusa-Indicazione atti trattati";
			}
			public String getFase() {
				return fasiSeduta.CONCLUSA.toString();
			}
			public int getOrdine(String organo) {
				return 1;
			}
		},
		
		sedutaConclusaIndicazioneOrdineDiscussione {
			public String toString() {
				return "Seduta conclusa-Indicazione ordine discussione";
			}
			public String getFase() {
				return fasiSeduta.CONCLUSA.toString();
			}
			public int getOrdine(String organo) {
				return 2;
			}
		},
		
		sedutaConclusaIndicazioneNumeroArgomento {
			public String toString() {
				return "Seduta conclusa-Indicazione numero argomento";
			}
			public String getFase() {
				return fasiSeduta.CONCLUSA.toString();
			}
			public int getOrdine(String organo) {
				if(organo.equalsIgnoreCase("C")) {
					return 3;
				}else {
					return -1;
				}
			}
		},
		
		sedutaConclusaRegistrazioneEsiti {
			public String toString() {
				return "Seduta conclusa-Registrazione Esiti";
			}
			public String getFase() {
				return fasiSeduta.CONCLUSA.toString();
			}
			public int getOrdine(String organo) {
				if(organo.equalsIgnoreCase("C")) {
					return sedutaConclusaIndicazioneNumeroArgomento.getOrdine(organo) + 1;
				}else {
					return sedutaConclusaIndicazioneOrdineDiscussione.getOrdine(organo) + 1;
				}
			}
		},
		
		sedutaConclusaEsitiConfermati {
			public String toString() {
				return "Seduta conclusa-Esiti Confermati";
			}
			public String getFase() {
				return fasiSeduta.CONCLUSA.toString();
			}
			public int getOrdine(String organo) {
				return sedutaConclusaRegistrazioneEsiti.getOrdine(organo) + 1;
			}
		},
		
		sedutaConclusaNumerazioneConfermata{
			public String toString() {
				return "Seduta conclusa-Numerazione Confermata";
			}
			public String getFase() {
				return fasiSeduta.CONCLUSA.toString();
			}
			public int getOrdine(String organo) {
				return sedutaConclusaEsitiConfermati.getOrdine(organo) + 1;
			}
		},
		
		sedutaConsolidata {
			public String toString() {
				return "Seduta Consolidata";
			}
			public String getFase() {
				return fasiSeduta.CONCLUSA.toString();
			}
			public int getOrdine(String organo) {
				return 0;
			}
		},
		
		sedutaVerbalizzata {
			public String toString() {
				return "Seduta Conclusa e Verbalizzata";
			}
			public String getFase() {
				return fasiSeduta.CONCLUSA.toString();
			}
			public int getOrdine(String organo) {
				return 0;
			}
		},
		
		sedutaAnnullata {
			public String toString() {
				return "Seduta annullata";
			}
			public String getFase() {
				return fasiSeduta.ANNULLATA.toString();
			}
			public int getOrdine(String organo) {
				return 0;
			}
		},
		
		sedutaProvvisoriaAnnullata {
			public String toString() {
				return "Seduta provvisoria annullata";
			}
			public String getFase() {
				return fasiSeduta.ANNULLATA.toString();
			}
			public int getOrdine(String organo) {
				return 0;
			}
		};
		
		public int getOrdine(String organo) {
			return this.getOrdine(organo);
		}
		public String toStringByOrgano(String organo) {
			return this.toString();
		}
		public String getFase() {
			throw new RuntimeException("Fase non implementata.");
		}
	};

	public enum statiAtto {
		propostaInAttesaDiEsito {
			public String toString() {
				return "In attesa Consolidamento Seduta";
			}
		},
		propostaInseritaInOdgOdl{
			public String toStringByOrgano(String organo) {
				return "Inserita in " + getLabelByOrgano(organo);
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		propostaInseribileInOdgOdl {
			public String toStringByOrgano(String organo) {
				return "Inseribile in " + getLabelByOrgano(organo);
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		propostaSospesa {
			public String toString() {
				return "Atto Sospeso";
			}
		};
		
		/*
		provvedimentoEsecutivo {
			public String toString() {
				return "Provvedimento Esecutivo";
			}
		},
		provvedimentoPresoDatto {
			public String toString() {
				return "Provvedimento Preso D’atto";
			}
		},
		provvedimentoVerbalizzato {
			public String toString() {
				return "Provvedimento Verbalizzato";
			}
		}
		*/
		
		public String toStringByOrgano(String organo) {
			return this.toString();
		}
	};

	public enum statiOdgOdl {
		odgOdlInPredisposizione {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " in predisposizione";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		/*
		 * IN ATTICO NON PREVISTO
		 * 
		odgOdlInAttesaDiFirma {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " in attesa di firma";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		*/
		odgOdlConsolidato {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " consolidato";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		odgOdlAnnullato {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " annullato";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		};
		
		public String toStringByOrgano(String organo) {
			return this.toString();
		}
	};
	
	public enum esitiAttoOdg {
		nonTrattato {
			public String getCodice() {
				return "non_trattato";
			}
			public String toString() {
				return "Non Trattato";
			}
		},
		
		rinviatoConsiglio {
			public String getCodice() {
				return "rinviato";
			}
			public String toString() {
				return "Rinviato";
			}
		},
		
		rinviatoGiunta {
			public String getCodice() {
				return "G_rinviato";
			}
			public String toString() {
				return "Rinviato";
			}
		};
		
		public String getCodice() {
			return this.toString();
		}
	}

	public enum statiVerbale {
		verbaleInPredisposizione {
			public String toString() {
				return "Verbale in predisposizione";
			}
		},
		verbaleInAttesaDiFirma {
			public String toString() {
				return "Verbale in attesa di firma";
			}
		},
		verbaleConsolidato {
			public String toString() {
				return "Verbale consolidato";
			}
		},
		verbaleRifiutato {
			public String toString() {
				return "Verbale non redatto";
			}
		}
	};
	public enum statiResoconto {
		
		resocontoInAttesaDiFirma {
			public String toString() {
				return "Resoconto in attesa di firma";
			}
		},
		resocontoConsolidato {
			public String toString() {
				return "Resoconto firmato";
			}
		}
	};
	public enum statiPresenze {
		
		presenzeInAttesaDiFirma {
			public String toString() {
				return "Doc presenze/assenze in attesa di firma";
			}
		},
		presenzeConsolidato {
			public String toString() {
				return "Doc presenze/assenze firmato";
			}
		}
	};
	public enum statiDocSeduta {
		odgOdlBaseInPredisposizione {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Base in Predisposizione";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		/*
		 * IN ATTICO NON PREVISTO
		 * 
		odgOdlBaseInAttesaDiFirma {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Base in Attesa di Firma";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		*/
		odgOdlBaseConsolidato {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Base Consolidato";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		odgOdlSuppletivoInPredisposizione {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Suppletivo in Predisposizione";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		/*
		 * IN ATTICO NON PREVISTO
		 * 
		odgOdlSuppletivoInAttesaDiFirma {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Suppletivo in Attesa di Firma";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		*/
		odgOdlSuppletivoConsolidato {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Suppletivo Consolidato";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		odgOdlFuoriSaccoInPredisposizione {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Fuori Sacco in Predisposizione";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		/*
		 * IN ATTICO NON PREVISTO
		 * 
		odgOdlFuoriSaccoInAttesaDiFirma {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Fuori Sacco In Attesa di Firma";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		*/
		odgOdlFuoriSaccoConsolidato {
			public String toStringByOrgano(String organo) {
				return getLabelByOrgano(organo) + " Fuori Sacco Consolidato";
			}
			public String toString() {
				throw new RuntimeException("Occorre specificare l'organo della Seduta.");
			}
		},
		docVarInPredisposizione {
			public String toString() {
				return "Documento di variazione in predisposizione";
			}
		},
		/*
		docVarInAttesaDiFirma {
			public String toString() {
				return "Documento di variazione in attesa di firma";
			}
		},
		*/
		docVarGenerato {
			public String toString() {
				return "Documento di variazione generato";
			}
		},
		/*
		docAnnInAttesaDiFirma {
			public String toString() {
				return "Documento di Annullamento in Attesa di Firma";
			}
		},
		*/
		docResInAttesaDiPredisposizione {
			public String toString() {
				return "Resoconto in Attesa di Predisposizione";
			}
		},
		docResInPredisposizione {
			public String toString() {
				return "Resoconto in Predisposizione";
			}
		},
		/*
		docResInAttesaDiFirma {
			public String toString() {
				return "Resoconto in Attesa di Firma";
			}
		},
		docResFirmato {
			public String toString() {
				return "Resoconto Consolidato";
			}
		},
		docPresInAttesaDiFirma {
			public String toString() {
				return "Documento Presenti/Assenti in Attesa di Firma";
			}
		},
		docPresFirmato {
			public String toString() {
				return "Documento Presenti/Assenti Consolidato";
			}
		},
		docAnnFirmato {
			public String toString() {
				return "Documento di Annullamento Firmato";
			}
		},
		provvedimentiInAttesaDiFirma {
			public String toString() {
				return "Provvedimenti in attesa di firma";
			}
		},
		provvedimentiFirmati {
			public String toString() {
				return "Provvedimenti firmati";
			}
		},
		*/
		verbaleInAttesa {
			public String toString() {
				return "Verbale in attesa di predisposizione";
			}
		},
		verbaleInPredisposizione {
			public String toString() {
				return "Verbale in Predisposizione";
			}
		},
		verbaleNonRichiesto {
			public String toString() {
				return "Verbale non richiesto";
			}
		},
		verbaleGenerato {
			public String toString() {
				return "Verbale generato";
			}
		};
		
		/*
		 * IN ATTICO NON PREVISTA FIRMA
		 * ,
		verbaleInAttesaDiFirma {
			public String toString() {
				return "Verbale in attesa di firma";
			}
		},
		verbaleFirmato {
			public String toString() {
				return "Verbale firmato";
			}
		};
		*/
		
		public String toStringByOrgano(String organo) {
			return this.toString();
		}
	};
	
	private static String getLabelByOrgano(String organo) {
		if (organoSeduta.G.name().equalsIgnoreCase(organo)) {
			return "OdG";
		}
		else if (organoSeduta.C.name().equalsIgnoreCase(organo)) {
			return "OdL";
		}
		return "";
	}
}