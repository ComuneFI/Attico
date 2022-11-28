package it.linksmt.assatti.bpm.service;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.service.exception.GestattiNumerazioneCatchedException;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.ProgressivoAdozione;
import it.linksmt.assatti.datalayer.domain.ProgressivoOrdineGiorno;
import it.linksmt.assatti.datalayer.domain.ProgressivoProposta;
import it.linksmt.assatti.datalayer.domain.ProgressivoSeduta;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoProgressivo;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.ProgressivoAdozioneRepository;
import it.linksmt.assatti.datalayer.repository.ProgressivoOrdineGiornoRepository;
import it.linksmt.assatti.datalayer.repository.ProgressivoPropostaRepository;
import it.linksmt.assatti.datalayer.repository.ProgressivoSedutaRepository;
import it.linksmt.assatti.datalayer.repository.TipoProgressivoRepository;
import it.linksmt.assatti.utility.StringUtil;

/**
 * Service class for managing profile.
 */
@Service
@Transactional
public class CodiceProgressivoService {

	public static final String DELIMITATORE = "/";

	private final Logger log = LoggerFactory.getLogger(CodiceProgressivoService.class);

	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private AooRepository aooRepository;

	@Inject
	private TipoProgressivoRepository tipoProgressivoRepository;

	@Inject
	private ProgressivoAdozioneRepository progressivoAdozioneRepository;

	@Inject
	private ProgressivoPropostaRepository progressivoPropostaRepository;

	@Inject
	private ProgressivoOrdineGiornoRepository progressivoOdgRepository;

	@Inject
	private ProgressivoSedutaRepository progressivoSedutaRepository;

	@Transactional(readOnly = false)
	public void generaProgressiviPropostaInizioAnno() {
		List<TipoProgressivo> tipiProgressivo = tipoProgressivoRepository.findAll();
		List<Aoo> aoos = aooRepository.findAll();
		Calendar cal = Calendar.getInstance();
		Integer annoCorrente = cal.get(Calendar.YEAR);
		for (TipoProgressivo tipoProgressivo : tipiProgressivo) {
			for (Aoo aoo : aoos) {
				ProgressivoProposta progressivo = new ProgressivoProposta();
				progressivo.setAnno(annoCorrente);
				progressivo.setAoo(aoo);
				progressivo.setProgressivo(0);
				progressivo.setTipoProgressivo(tipoProgressivo);
				if (this.getCurrentProgressivoProposta(annoCorrente, aoo, tipoProgressivo) == null) {
					progressivo = progressivoPropostaRepository.save(progressivo);
				}
			}
		}
	}

	@Transactional(readOnly = false)
	public void generaProgressiviAdozioneDetermineDirigenzialiInizioAnno() {
		List<TipoProgressivo> tipiProgressivo = tipoProgressivoRepository.findAll();
		List<Aoo> aoos = aooRepository.findAll();
		Calendar cal = Calendar.getInstance();
		Integer annoCorrente = cal.get(Calendar.YEAR);
		for (TipoProgressivo tipoProgressivo : tipiProgressivo) {

			for (Aoo aoo : aoos) {
				ProgressivoAdozione progressivo = new ProgressivoAdozione();
				progressivo.setAnno(annoCorrente);
				progressivo.setAoo(aoo);
				progressivo.setProgressivo(0);
				progressivo.setTipoProgressivo(tipoProgressivo);
				if (this.getCurrentProgressivoAdozione(annoCorrente, aoo, tipoProgressivo) == null) {
					progressivo = progressivoAdozioneRepository.save(progressivo);
				}
			}
		}
	}

	@Transactional(readOnly = false)
	public void generaProgressiviPropostaPerNuovaAoo(Aoo aoo) {
		List<TipoProgressivo> tipiProgressivo = tipoProgressivoRepository.findAll();
		Calendar cal = Calendar.getInstance();
		Integer annoCorrente = cal.get(Calendar.YEAR);
		for (TipoProgressivo tipoProgressivo : tipiProgressivo) {
			ProgressivoProposta progressivo = new ProgressivoProposta();
			progressivo.setAnno(annoCorrente);
			progressivo.setAoo(aoo);
			progressivo.setProgressivo(0);
			progressivo.setTipoProgressivo(tipoProgressivo);
			progressivo = progressivoPropostaRepository.save(progressivo);
		}
	}

	@Transactional(readOnly = false)
	public void generaProgressiviAdozionePerNuovaAoo(Aoo aoo) {
		List<TipoProgressivo> tipiProgressivo = tipoProgressivoRepository.findAll();
		Calendar cal = Calendar.getInstance();
		Integer annoCorrente = cal.get(Calendar.YEAR);
		for (TipoProgressivo tipoProgressivo : tipiProgressivo) {
			ProgressivoAdozione progressivo = new ProgressivoAdozione();
			progressivo.setAnno(annoCorrente);
			progressivo.setAoo(aoo);
			progressivo.setProgressivo(0);
			progressivo.setTipoProgressivo(tipoProgressivo);
			progressivo = progressivoAdozioneRepository.save(progressivo);
		}
	}

	@Transactional(readOnly = true)
	public List<ProgressivoProposta> findAllProgressivoPropostaByAooId(Integer anno, Long aooId) {
		log.debug("getProgressivoProposta anno:" + anno);
		log.debug("getProgressivoProposta aooId:" + aooId);
		List<ProgressivoProposta> progressivi = progressivoPropostaRepository.findByAooIdAndAnno(aooId, anno);
		return progressivi;
	}

	@Transactional(readOnly = true)
	public List<ProgressivoAdozione> findAllProgressivoAdozioneByAooId(Integer anno, Long aooId) {
		log.debug("ProgressivoAdozione anno:" + anno);
		log.debug("ProgressivoAdozione aooId:" + aooId);
		List<ProgressivoAdozione> progressivi = progressivoAdozioneRepository.findByAooIdAndAnno(aooId, anno);
		return progressivi;
	}

	public ProgressivoProposta getCurrentProgressivoProposta(Integer anno, Aoo aoo, TipoProgressivo tipoProgressivo) {
		log.debug("getProgressivoProposta anno:" + anno);
		if (aoo != null) {
			log.debug("getProgressivoProposta aooId:" + aoo.getId());
		}
		log.debug("getProgressivoProposta tipoProgressivoId:" + tipoProgressivo.getId());
		ProgressivoProposta progressivo = null;
		if (aoo != null) {
			progressivo = progressivoPropostaRepository.getByAnnoAndAooIdAndTipoProgressivoId(anno, aoo.getId(), tipoProgressivo.getId());
		}
		else {
			progressivo = progressivoPropostaRepository.getByAnnoAndTipoProgressivoIdAndAooIdIsNull(anno, tipoProgressivo.getId());
		}
		if (progressivo == null) {
			progressivo = creaProgressivoProposta(aoo, tipoProgressivo);
		}
		return progressivo;
	}

	public ProgressivoOrdineGiorno getCurrentProgressivoOdg(Integer anno) {
		log.debug("getCurrentProgressivoOdg anno:" + anno);
		ProgressivoOrdineGiorno progressivo = progressivoOdgRepository.getByAnnoAndTipoOdgId(anno, null);

		return progressivo;
	}

	public ProgressivoAdozione getCurrentProgressivoAdozione(Integer anno, Aoo aoo, TipoProgressivo tipoProgressivo) {
		log.debug("getProgressivoProposta anno:" + anno);
		if (aoo != null) {
			log.debug("getProgressivoProposta aooId:" + aoo.getId());
		}
		log.debug("getProgressivoProposta tipoProgressivoId:" + tipoProgressivo.getId());
		ProgressivoAdozione progressivo = null;
		if (aoo != null) {
			progressivo = progressivoAdozioneRepository.getByAnnoAndAooIdAndTipoProgressivoId(anno, aoo.getId(), tipoProgressivo.getId());
		}
		else {
			progressivo = progressivoAdozioneRepository.getByAnnoAndTipoProgressivoIdAndAooIsNull(anno, tipoProgressivo.getId());
		}
		if (progressivo == null) {
			progressivo = creaProgressivoAdozione(aoo, tipoProgressivo);
		}
		return progressivo;
	}

	@Transactional
	public ProgressivoProposta creaProgressivoProposta(Aoo aoo, TipoProgressivo tipoProgressivo) {
		synchronized (CodiceProgressivoService.class) {
			Integer anno = LocalDate.now().getYear();
			ProgressivoProposta progressivoProposta = new ProgressivoProposta();
			progressivoProposta.setAnno(anno);
			progressivoProposta.setProgressivo(0);
			progressivoProposta.setAoo(aoo);
			progressivoProposta.setTipoProgressivo(tipoProgressivo);
			return progressivoPropostaRepository.saveAndFlush(progressivoProposta);
		}
	}

	@Transactional
	public ProgressivoOrdineGiorno creaProgressivoOdg() {// TipoOdg tipoOdg) {
		Integer anno = LocalDate.now().getYear();
		ProgressivoOrdineGiorno progressivoProposta = new ProgressivoOrdineGiorno();
		progressivoProposta.setAnno(anno);
		progressivoProposta.setProgressivo(0);
		progressivoProposta.setTipoOdg(null);
		return progressivoOdgRepository.save(progressivoProposta);

	}

	public synchronized ProgressivoAdozione creaProgressivoAdozione(Aoo aoo, TipoProgressivo tipoProgressivo) {
		Integer anno = LocalDate.now().getYear();
		ProgressivoAdozione adozione = new ProgressivoAdozione();
		adozione.setAnno(anno);
		adozione.setProgressivo(0);
		adozione.setAoo(aoo);
		adozione.setTipoProgressivo(tipoProgressivo);
		return progressivoAdozioneRepository.saveAndFlush(adozione);
	}

	@Transactional
	public void saveProgressivoAdozione(ProgressivoAdozione progressivoAdozione) {
		progressivoAdozioneRepository.save(progressivoAdozione);
	}

	@Transactional
	public void saveProgressivoProposta(ProgressivoProposta progressivoProposta) {
		progressivoPropostaRepository.save(progressivoProposta);
	}

	public synchronized ProgressivoProposta getProgressivoProposta(Integer anno, Aoo aoo, TipoProgressivo tipoProgressivo) {
		ProgressivoProposta progressivo = getCurrentProgressivoProposta(anno, aoo, tipoProgressivo);
		if (progressivo == null) {
			String message = "";
			if (aoo != null) {
				message = messageException(anno, aoo.getId(), tipoProgressivo.getId());
			}
			else {
				message = messageException(anno, tipoProgressivo.getId());
			}
			throw new RuntimeException("ProgressivoProposta " + message);
		}
		progressivo.setProgressivo(progressivo.getProgressivo()+1);
		log.debug("getProgressivoProposta:" + progressivo);
		progressivo = progressivoPropostaRepository.save(progressivo);
		return progressivo;
	}

	public synchronized ProgressivoOrdineGiorno getProgressivoOdg(Integer anno) {
		ProgressivoOrdineGiorno progressivo = getCurrentProgressivoOdg(anno);
		if (progressivo == null) {
			progressivo = creaProgressivoOdg();
		}
		progressivo.setProgressivo(progressivo.getProgressivo()+1);
		log.debug("getProgressivoOdg:" + progressivo);
		progressivo = progressivoOdgRepository.save(progressivo);
		return progressivo;
	}

	public synchronized ProgressivoAdozione getProgressivoAdozione(Integer anno, Aoo aoo, TipoProgressivo tipoProgressivo) {
		ProgressivoAdozione progressivo = getCurrentProgressivoAdozione(anno, aoo, tipoProgressivo);

		if (progressivo == null) {
			String message = "";
			if (aoo != null) {
				message = messageException(anno, aoo.getId(), tipoProgressivo.getId());
			}
			else {
				message = messageException(anno, tipoProgressivo.getId());
			}
			throw new RuntimeException("ProgressivoAdozione " + message);
		}
		progressivo.setProgressivo(progressivo.getProgressivo()+1);
		log.debug("progressivo:" + progressivo);
		progressivo = progressivoAdozioneRepository.save(progressivo);
		return progressivo;
	}

	private String messageException(Integer anno, Long aooId, Long tipoProgressivoId) {
		return "  per anno " + anno + " aoo " + aooId + " tipoProgressivoId" + tipoProgressivoId + " non trovata. Contattare amministratore sistema per inizializzazione.";
	}

	private String messageException(Integer anno, Long tipoProgressivoId) {
		return "  per anno " + anno + " tipoProgressivoId" + tipoProgressivoId + " non trovata. Contattare amministratore sistema per inizializzazione.";
	}

	/**
	 * @param servizio
	 * @param tipoAtto
	 * @param tipoProgressivo
	 * @return Formato Codice Cifra: Codice Aoo proponente + "/" + Tipo Atto + "/" + anno Data di
	 *         Creazione + "/" + Progressivo Proposta (18 caratteri)
	 *
	 */
	private static DecimalFormat codiceFormatter = new DecimalFormat(StringUtil.CODICE_PATTERN);

	public synchronized String generaCodiceCifraProposta(Aoo aoo, Integer anno, TipoAtto tipoAtto, TipoProgressivo tipoProgressivo) {
		Aoo aooDB = null;
		if (aoo != null && aoo.getId() != null) {
			aooDB = aooRepository.findOne(aoo.getId());
			if (aooDB.getValidita() != null && aooDB.getValidita().getValidoal() != null) {
				throw new RuntimeException("Genera Codice Atto Proposta non possibile aoo non attiva!");
			}
		}

		String codiceCifra = new String();
		ProgressivoProposta progressivo = getProgressivoProposta(anno, aooDB, tipoProgressivo);
		String progressivoStr = codiceFormatter.format(progressivo.getProgressivo());
		if (aoo != null && aoo.getId() != null) {
			codiceCifra = aooDB.getCodice() + DELIMITATORE + tipoAtto.getCodice() + DELIMITATORE + anno + DELIMITATORE + progressivoStr;
		}
		else {
			codiceCifra = tipoAtto.getCodice() + DELIMITATORE + anno + DELIMITATORE + progressivoStr;
		}

		return codiceCifra;
	}

	public synchronized String generaCodiceOrdineGiorno(Integer anno) {

		ProgressivoOrdineGiorno progressivo = getProgressivoOdg(anno);
		String progressivoStr = progressivo.getProgressivo() + "";
		return progressivoStr;
	}

	public synchronized String generaProgressivoSeduta(Integer anno, String organo) {

		if (StringUtil.isNull(organo) || (anno == null) || (anno.intValue() < 1)) {
			throw new RuntimeException("Occorre specificare anno e organo!");
		}

		ProgressivoSeduta progressivo = progressivoSedutaRepository.getByAnnoAndOrgano(anno, organo);
		if (progressivo == null) {
			progressivo = new ProgressivoSeduta();
			progressivo.setAnno(anno);
			progressivo.setProgressivo(0);
			progressivo.setOrgano(organo);

			progressivo = progressivoSedutaRepository.save(progressivo);
		}
		progressivo.setProgressivo(progressivo.getProgressivo()+1);
		log.debug("getProgressivoOdg:" + progressivo);

		progressivo = progressivoSedutaRepository.save(progressivo);
		
		return String.valueOf(progressivo.getProgressivo());
	}

	public String generaCodiceCifraAdozione(Aoo aoo, Integer anno, TipoProgressivo tipoProgressivo) {
		Aoo aooDB = null;
		if (aoo != null && aoo.getId() != null) {
			aooDB = aooRepository.findOne(aoo.getId());
			if (aooDB.getValidita() != null && aooDB.getValidita().getValidoal() != null) {
				throw new RuntimeException("Genera Codice Atto Adozione non possibile aoo non attiva!");
			}
		}
		ProgressivoAdozione progressivo = getProgressivoAdozione(anno, aooDB, tipoProgressivo);
		String progressivoStr = codiceFormatter.format(progressivo.getProgressivo());
		return progressivoStr;
	}

	public synchronized void annullaCodiceCifraAdozione(Aoo aoo, Integer anno, TipoProgressivo tipoProgressivo, String numAdozioneAtto) {
		if (aoo.getValidita() != null && aoo.getValidita().getValidoal() != null) {
			throw new RuntimeException("Annulla Codice Atto Adozione non possibile aoo non attiva!");
		}
		ProgressivoAdozione progressivo = getCurrentProgressivoAdozione(anno, aoo, tipoProgressivo);

		if (progressivo == null) {
			String message = messageException(anno, aoo.getId(), tipoProgressivo.getId());
			throw new RuntimeException("ProgressivoAdozione " + message);
		}
		log.debug("progressivo:" + progressivo);
		String progressivoStr = codiceFormatter.format(progressivo.getProgressivo());

		if (progressivoStr.equals(numAdozioneAtto)) {
			progressivo.setProgressivo(progressivo.getProgressivo()-1);
			progressivo = progressivoAdozioneRepository.save(progressivo);
		}
		else {
			throw new RuntimeException("Annullamento Progressivo Adozione non permessa per anno " + anno + " aoo " + aoo.getId() + " tipoProgressivoId " + tipoProgressivo.getId()
					+ " numero progressivo " + numAdozioneAtto + " in quanto la numerazione attuale è successiva.");
		}
	}

	public synchronized void annullaCodiceCifraAdozioneAttiGiunta(Integer anno, TipoProgressivo tipoProgressivo, String numAdozioneAtto) throws GestattiNumerazioneCatchedException {
		ProgressivoAdozione progressivo = getCurrentProgressivoAdozione(anno, null, tipoProgressivo);

		if (progressivo == null) {
			String message = messageException(anno, null, tipoProgressivo.getId());
			throw new RuntimeException("ProgressivoAdozione " + message);
		}
		log.debug("progressivo:" + progressivo);
		String progressivoStr = codiceFormatter.format(progressivo.getProgressivo());

		if (progressivoStr.equals(numAdozioneAtto)) {
			progressivo.setProgressivo(progressivo.getProgressivo()-1);
			progressivo = progressivoAdozioneRepository.save(progressivo);
		}
		else {
			throw new GestattiNumerazioneCatchedException(
					"Annullamento numerazione non permessa per tipo progressivo " + tipoProgressivo.getDescrizione() + " con progressivo adozione " + numAdozioneAtto + ".");
		}
	}
}
