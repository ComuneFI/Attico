package it.linksmt.assatti.bpm.util;


public class BpmThreadLocalUtil {

	// IMPORTANTE: LE VARIABILI VANNO RIPULITE NELLA CLASSE "CleanBpmThreadLocalListener"	
	
	private static ThreadLocal<Long> profiloIdLocal = new ThreadLocal<Long>();
	private static ThreadLocal<Long> deleganteIdLocal = new ThreadLocal<Long>();
	private static ThreadLocal<Long> profiloOriginarioIdLocal = new ThreadLocal<Long>();
	private static ThreadLocal<String> nomeAttivitaLocal = new ThreadLocal<String>();
	private static ThreadLocal<String> codiceDecisioneLocal = new ThreadLocal<String>();
	private static ThreadLocal<String> motivazioneLocal = new ThreadLocal<String>();
	
	public static void setProfiloId(long profiloId) {
		profiloIdLocal.set(profiloId);
	}
	
	public static long getProfiloId() {
		if (profiloIdLocal.get() != null) {
			return profiloIdLocal.get().longValue();
		}
		return 0;
	}
	
	public static void setProfiloOriginarioId(long profiloOriginarioId) {
		profiloOriginarioIdLocal.set(profiloOriginarioId);
	}
	
	public static long getProfiloOriginarioId() {
		if (profiloOriginarioIdLocal.get() != null) {
			return profiloOriginarioIdLocal.get().longValue();
		}
		return 0;
	}
	
	public static void setProfiloDeleganteId(long profiloDeleganteId) {
		deleganteIdLocal.set(profiloDeleganteId);
	}
	
	public static long getProfiloDeleganteId() {
		if (deleganteIdLocal.get() != null) {
			return deleganteIdLocal.get().longValue();
		}
		return 0;
	}

	public static void setNomeAttivita(String nomeAttivita) {
		nomeAttivitaLocal.set(nomeAttivita);
	}
	
	public static String getNomeAttivita() {
		return nomeAttivitaLocal.get();
	}
	
	public static void setMotivazione(String motivazione) {
		motivazioneLocal.set(motivazione);
	}
	
	public static String getMotivazione() {
		return motivazioneLocal.get();
	}

	public static String getCodiceDecisioneLocal() {
		return codiceDecisioneLocal.get();
	}

	public static void setCodiceDecisioneLocal(String codice) {
		codiceDecisioneLocal.set(codice);
	}
}
