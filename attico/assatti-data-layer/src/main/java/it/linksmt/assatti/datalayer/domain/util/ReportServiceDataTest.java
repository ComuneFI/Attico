package it.linksmt.assatti.datalayer.domain.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.ArgomentoOdg;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.Lettera;
import it.linksmt.assatti.datalayer.domain.Materia;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.domain.Resoconto;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SezioneTesto;
import it.linksmt.assatti.datalayer.domain.SottoMateria;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreAtto;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreParere;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreSedutaGiunta;
import it.linksmt.assatti.datalayer.domain.TipoAdempimento;
import it.linksmt.assatti.datalayer.domain.TipoAoo;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoAzione;
import it.linksmt.assatti.datalayer.domain.TipoIter;
import it.linksmt.assatti.datalayer.domain.TipoMateria;
import it.linksmt.assatti.datalayer.domain.TipoOdg;
import it.linksmt.assatti.datalayer.domain.Ufficio;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.Verbale;
import it.linksmt.assatti.datalayer.domain.dto.RelataDiPubblicazioneDto;
import liquibase.structure.core.Data;


@Service

public class ReportServiceDataTest {

	private final static Logger log = LoggerFactory.getLogger(ReportServiceDataTest.class);

	public ReportServiceDataTest() {

	}

	public static Profilo getProfiloTest(long id){
		Profilo profilo = new Profilo();
		profilo.setId(-100L);
		Utente utente = new Utente();
		utente.setId(-100L);
		utente.setNome("Nome Emanante");
		utente.setCognome("Cognome Emanante");
		profilo.setUtente(utente);
		Aoo aooPadre = new Aoo();
		aooPadre.setCodice("AOO");
		aooPadre.setDescrizione("AREA POLITICHE PER LO SVILUPPO ECONOMICO, IL LAVORO E L'INNOVAZIONE");
		aooPadre.setIdentitavisiva("#db7012");
		aooPadre.setTipoAoo(new TipoAoo(1L, "AREA", "AREA"));
		Aoo aoo = new Aoo();
		aoo.setCodice("091");
		aoo.setDescrizione("Servizio politiche per il lavoro");
		aoo.setAooPadre(aooPadre);
		aoo.setTipoAoo(new TipoAoo(2L, "SERVIZIO", "SERVIZIO"));
		profilo.setAoo(aoo);
		return profilo;
	}
	public static Atto getSottoscrittoriAttoTest( ) {
		Atto atto = new Atto();
		atto.setId(-1L);
		Aoo aooPadre = new Aoo();
		aooPadre.setCodice("AOO");
		aooPadre.setDescrizione("AREA POLITICHE PER LO SVILUPPO ECONOMICO, IL LAVORO E L'INNOVAZIONE");
		aooPadre.setIdentitavisiva("#db7012");
		aooPadre.setTipoAoo(new TipoAoo(1L, "AREA", "AREA"));
		
		Aoo aoo = new Aoo();
		aoo.setCodice("091");
		aoo.setDescrizione("Servizio politiche per il lavoro");
		aoo.setAooPadre(aooPadre);
		aoo.setTipoAoo(new TipoAoo(2L, "SERVIZIO", "SERVIZIO"));
		
		Utente utente0 = new Utente();
		utente0.setCognome("Antonio");
		utente0.setNome("Bianchi");
		Profilo profilo0 = new Profilo();
		profilo0.setAoo(aoo);
		profilo0.setUtente(utente0);

		QualificaProfessionale qualificaProfessionale = new QualificaProfessionale();
//		qualificaProfessionale.setAoo(aoo);
		qualificaProfessionale.setDenominazione("Capo servizio");
		SottoscrittoreAtto e = new SottoscrittoreAtto();
		e.setOrdineFirma(new Integer(1));
		e.setId(9997L);
		e.setProfilo(profilo0);
		e.setQualificaProfessionale(qualificaProfessionale);


		Utente utente1 = new Utente();
		utente1.setCognome("Mario");
		utente1.setNome("Rossi");

		Profilo profilo1 = new Profilo();
		profilo1.setAoo(aoo);
		profilo1.setUtente(utente1);

		QualificaProfessionale qualificaProfessionale1 = new QualificaProfessionale();
//		qualificaProfessionale1.setAoo(aoo);
		qualificaProfessionale1.setDenominazione("Responsabile");

		SottoscrittoreAtto e1 = new SottoscrittoreAtto();
		e1.setOrdineFirma(new Integer(2));
		e1.setId(9998L);
		e1.setProfilo(profilo1);
		e1.setQualificaProfessionale(qualificaProfessionale1);

		atto.getSottoscrittori().add(e);
		atto.getSottoscrittori().add(e1);
		return atto;
	}
	public static Atto getAttoTest( ) {
		Atto atto = new Atto();
		atto.setId(-1L);

		Aoo aooPadre = new Aoo();
		aooPadre.setCodice("002");
		aooPadre.setDescrizione("Dipartimento Sviluppo economico, Innovazione, Istruzione Formazione e Lavoro");
		aooPadre.setIdentitavisiva("#db7012");
		aooPadre.setTipoAoo(new TipoAoo(1L, "AREA", "AREA"));
		aooPadre.setId(3L);

		Aoo aoo = new Aoo();
		aoo.setCodice("006");
		aoo.setDescrizione("Dipartimento Risorse finanziarie e strumentali, personale e organizzazione");
		aoo.setAooPadre(aooPadre);
		aoo.setTipoAoo(new TipoAoo(2L, "SERVIZIO","SERVIZIO"));
		aoo.setId(4L);

		atto.setAoo(aoo);
		atto.setDescrizioneArea(aooPadre.getDescrizione());
		atto.setDescrizioneServizio("");
		
		Profilo profilo = new Profilo();
		profilo.setId(-100L);
		Utente utente = new Utente();
		utente.setId(-100L);
		utente.setNome("Nome Emanante");
		utente.setCognome("Cognome Emanante");
		profilo.setUtente(utente);
		
		QualificaProfessionale qualifica = new QualificaProfessionale();
		qualifica.setDenominazione("Il Direttore Amministrativo del Gabinetto");
		
		atto.setEmananteProfilo(profilo);
		atto.setQualificaEmanante(qualifica);

		Set<DocumentoInformatico> allegati = new HashSet<DocumentoInformatico>();


		try {
			String str = null;
			String logo=null;
			ClassPathResource resource = new ClassPathResource("bannerlaterale.png");
			byte[] img =FileUtils.readFileToByteArray(resource.getFile());
			img = Base64.encode(img);
			logo = new String(img);
			logo = "data:image/png;base64,"+logo ;

			aooPadre.setLogo(logo);

			resource = new ClassPathResource("/modelli/preambolo.html");
			str =FileUtils.readFileToString(resource.getFile());
			atto.getPreambolo().setTesto(str);

			resource = new ClassPathResource("/modelli/motivazione.html");
			str =FileUtils.readFileToString(resource.getFile());
			atto.getMotivazione().setTesto(str);
			
			resource = new ClassPathResource("/modelli/domanda.html");
			str =FileUtils.readFileToString(resource.getFile());
			atto.getMotivazione().setTesto(str);

			resource = new ClassPathResource("/modelli/garanzieriservatezza.html");
			str =FileUtils.readFileToString(resource.getFile());
			atto.getGaranzieRiservatezza().setTesto(str);

			resource = new ClassPathResource("/modelli/dichiarazioni_adempimenticontabili.html");
			str =FileUtils.readFileToString(resource.getFile());
			atto.getDichiarazioni().setTesto( str);

			resource = new ClassPathResource("/modelli/dispositivo_dd_direttamente_esecutiva_senza_adempimenti_contabili.html");
			str =FileUtils.readFileToString(resource.getFile());
			atto.getDispositivo().setTesto(str);

			resource = new ClassPathResource("/modelli/note.html");
			str =FileUtils.readFileToString(resource.getFile());
			atto.setNote(str);


			resource = new ClassPathResource("/modelli/example/DD_allegato1.pdf");
			byte[] bFile =FileUtils.readFileToByteArray(resource.getFile());

			it.linksmt.assatti.datalayer.domain.File fileD = new it.linksmt.assatti.datalayer.domain.File();
			fileD.setNomeFile("DD_allegato1.pdf");
//			fileD.setContenuto(bFile);
			fileD.setContentType("application/pdf");
			fileD.setSize(new Long(bFile.length));
			fileD.setId(9991L);

			DocumentoInformatico docInf = new DocumentoInformatico();
			docInf.setNomeFile("DD_allegato1.pdf");
			docInf.setOggetto("DD_allegato1.pdf- oggetto");
			docInf.setPubblicabile(new Boolean(true));
			docInf.setParteIntegrante(new Boolean(true));
			docInf.setTitolo("DD_allegato1.pdf - titolo");
			docInf.setOrdineInclusione(new Integer(1));
			docInf.setFile(fileD);
			docInf.setFileomissis(fileD);
			docInf.setId(9991L);

			allegati.add(docInf); // primo doc

			resource = new ClassPathResource("/modelli/example/DD_allegato2.pdf");
			bFile =FileUtils.readFileToByteArray(resource.getFile());

			it.linksmt.assatti.datalayer.domain.File fileD1 = new it.linksmt.assatti.datalayer.domain.File();
			fileD1.setNomeFile("DD_allegato2.pdf");
//			fileD1.setContenuto(bFile);
			fileD1.setContentType("application/pdf");
			fileD1.setSize(new Long(bFile.length));
			fileD1.setId(9992L);

			DocumentoInformatico docInf1 = new DocumentoInformatico();
			docInf1.setNomeFile("DD_allegato2.pdf");
			docInf1.setOggetto("DD_allegato2.pdf - oggetto");
			docInf1.setPubblicabile(new Boolean(true));
			docInf1.setParteIntegrante(new Boolean(true));
			docInf1.setTitolo("DD_allegato2.pdf - titolo");
			docInf1.setOrdineInclusione(new Integer(2));
			docInf1.setFile(fileD1);
			docInf1.setFileomissis(fileD1);
			docInf1.setId(9992L);

			allegati.add(docInf1); // secondo doc

			atto.getAllegati().addAll(allegati);

		} catch (IOException e) {
			log.error(e.getMessage() );
		}


		TipoAtto tipoAtto = new TipoAtto(999L, "999", "TipoAtto");

		atto.setTipoAtto( tipoAtto);
		atto.setTipoAdempimento(new TipoAdempimento(999L,"TipoAdempimento"));
		atto.setTipoIter( new TipoIter(999L, "TipoIter",tipoAtto) );
		atto.setTipoMateria( new TipoMateria(999L,"TipoMateria") );
		atto.setMateria(new Materia(999L,"Materia") );
		atto.setSottoMateria( new SottoMateria(999L,"SottoMateria") );
		atto.setDurataGiorni(10);
		atto.setObbligodlgs332013(new Boolean(true));
		atto.setPubblicazioneIntegrale(new Boolean(true));
		atto.setRiservato(new Boolean(false));

		atto.setCodiceProcedimento("CodiceProcedimento" );
		atto.setCodiceCifra("999/999/0000000999");
		atto.setDataCreazione(new LocalDate() );
		atto.setDataAdozione( new LocalDate() );
		LocalDate inizioPubblicazioneAlbo = new LocalDate(2017, 1, 1);
		atto.setInizioPubblicazioneAlbo(inizioPubblicazioneAlbo );
		LocalDate finePubblicazioneAlbo = new LocalDate(2017, 1, 16);
		atto.setFinePubblicazioneAlbo(finePubblicazioneAlbo);
		atto.setOggetto("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur rhoncus nisi sit amet dui molestie suscipit. Curabitur sed ligula mi. Duis consectetur risus in mollis vehicula. Integer sed congue neque. Curabitur tincidunt malesuada aliquet. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur cursus nec urna eget sollicitudin. Vivamus non nibh pulvinar, facilisis sem quis, fringilla enim. Nullam vitae turpis nec ante facilisis vestibulum sit amet id odio. Nullam vitae ante in arcu condimentum pretium in suscipit orci. Ut eget sodales lorem, vel tincidunt nisi. In vel fermentum tortor." );
		atto.setLuogoCreazione("LuogoCreazione");
		atto.setLuogoAdozione("LuogoAdozione");
		atto.setNumeroAdozione("654");
		atto.setDataNumerazione(LocalDate.now());
		atto.setDescrizioneUfficio("descrizione ufficio");
		atto.setCodiceUfficio("codice ufficio");


		Ufficio ufficio= new Ufficio();
		ufficio.setAoo(aoo);
		ufficio.setCodice("codice uff");
		ufficio.setDescrizione("descr ufficio");

		atto.setUfficio(ufficio);

		atto.getInformazioniAnagraficoContabili().setTesto( "<h4>setInformazioniAnagraficoContabili</h4>");
		atto.getAdempimentiContabili().setTesto(  "<h4>setAdempimentiContabili</h4>");

		Utente utente0 = new Utente();
		utente0.setCognome("Antonio");
		utente0.setNome("Bianchi");

		Profilo profilo0 = new Profilo();
		profilo0.setAoo(aoo);
		profilo0.setUtente(utente0);

		QualificaProfessionale qualificaProfessionale = new QualificaProfessionale();
//		qualificaProfessionale.setAoo(aoo);
		qualificaProfessionale.setDenominazione("Capo servizio");

		SottoscrittoreAtto e = new SottoscrittoreAtto();
		e.setOrdineFirma(new Integer(1));
		e.setId(9997L);
		e.setProfilo(profilo0);
		e.setQualificaProfessionale(qualificaProfessionale);


		Utente utente1 = new Utente();
		utente1.setCognome("Mario");
		utente1.setNome("Rossi");

		Profilo profilo1 = new Profilo();
		profilo1.setAoo(aoo);
		profilo1.setUtente(utente1);

		QualificaProfessionale qualificaProfessionale1 = new QualificaProfessionale();
//		qualificaProfessionale1.setAoo(aoo);
		qualificaProfessionale1.setDenominazione("Responsabile");

		SottoscrittoreAtto e1 = new SottoscrittoreAtto();
		e1.setOrdineFirma(new Integer(2));
		e1.setId(9998L);
		e1.setProfilo(profilo1);
		e1.setQualificaProfessionale(qualificaProfessionale1);

		atto.getSottoscrittori().add(e);
		atto.getSottoscrittori().add(e1);


		//todo da iterare
		for (SottoscrittoreAtto sottoscrittoreAtto : atto.getSottoscrittori()) {
			sottoscrittoreAtto.getProfilo().getDescrizione();
			sottoscrittoreAtto.getProfilo().getUtente().getCognome();
			sottoscrittoreAtto.getProfilo().getUtente().getNome();
			if(sottoscrittoreAtto.getQualificaProfessionale()!=null){
				sottoscrittoreAtto.getQualificaProfessionale().getDenominazione();
			}
		}

		//todo da iterare
		for (DocumentoInformatico docInfo : atto.getAllegati()) {
			docInfo.getTitolo();
		}		
		
		SedutaGiunta sedutaGiunta = getSedutaGiuntaTest(null);
		atto.setSedutaGiunta(sedutaGiunta);
//		atto.setDenominazioneRelatore("Il Vice Presidente");

		return atto;

	}
	
	public static List<ComponentiGiunta> getComponentiGiuntaTest() {
		List<ComponentiGiunta> componenti = new ArrayList<ComponentiGiunta>();
		
		componenti.add(getComponenteGiuntaTest(1,true));
		componenti.add(getComponenteGiuntaTest(2,true));
		componenti.add(getComponenteGiuntaTest(3,true));
		componenti.add(getComponenteGiuntaTest(4,true));
		componenti.add(getComponenteGiuntaTest(5,false));
		componenti.add(getComponenteGiuntaTest(6,false));
		componenti.add(getComponenteGiuntaTest(7,false));
		componenti.add(getComponenteGiuntaTest(8,false));
		
		return componenti;
	}
	
	public static List<ComponentiGiunta> getComponentiGiuntaTestConsecutivi() {
		List<ComponentiGiunta> componenti = new ArrayList<ComponentiGiunta>();
		
		componenti.add(getComponenteGiuntaTest(1,true,100));
		componenti.add(getComponenteGiuntaTest(2,true,101));
		componenti.add(getComponenteGiuntaTest(3,true,102));
		componenti.add(getComponenteGiuntaTest(4,true,103));
		componenti.add(getComponenteGiuntaTest(5,false,104));
		componenti.add(getComponenteGiuntaTest(6,false,104));
		componenti.add(getComponenteGiuntaTest(7,false,104));
		componenti.add(getComponenteGiuntaTest(8,false,104));
		componenti.add(getComponenteGiuntaTest(9,false,104));
		componenti.add(getComponenteGiuntaTest(10,false,104));
		componenti.add(getComponenteGiuntaTest(11,false,104));
		componenti.add(getComponenteGiuntaTest(12,false,104));
		componenti.add(getComponenteGiuntaTest(5,false,105));
		componenti.add(getComponenteGiuntaTest(6,false,105));
		componenti.add(getComponenteGiuntaTest(7,false,105));
		componenti.add(getComponenteGiuntaTest(8,false,105));
		componenti.add(getComponenteGiuntaTest(9,false,105));
		componenti.add(getComponenteGiuntaTest(10,false,105));
		componenti.add(getComponenteGiuntaTest(11,false,105));
		componenti.add(getComponenteGiuntaTest(12,false,105));
		componenti.add(getComponenteGiuntaTest(5,false,106));
		componenti.add(getComponenteGiuntaTest(6,false,107));
		componenti.add(getComponenteGiuntaTest(6,false,108));
		componenti.add(getComponenteGiuntaTest(7,false,109));
		componenti.add(getComponenteGiuntaTest(6,false,110));
		componenti.add(getComponenteGiuntaTest(6,false,111));
		componenti.add(getComponenteGiuntaTest(6,false,112));
		
		return componenti;
	}
	
	private static ComponentiGiunta getComponenteGiuntaTest(int id, boolean presente) {
		
		ComponentiGiunta componente = new ComponentiGiunta();
		componente.setPresente(presente);
		Profilo profilo =new Profilo();
		Utente utente = new Utente(null,null,null,"Nome"+id,"Cognome"+id);
		profilo.setUtente(utente);
		componente.setProfilo(profilo);
		TipoAtto sl = new TipoAtto(2L,"SDL_A","schema dl");
		
		int ordine = 0;
		AttiOdg elem11sl1 = getAttoOdg(sl, ordine++, 1, 1, "presidente", null, "AOO TEST1"+id);
		componente.setAtto(elem11sl1);
		return componente;
	}

	private static ComponentiGiunta getComponenteGiuntaTest(int id, boolean presente, int numAdozione) {
	
	ComponentiGiunta componente = new ComponentiGiunta();
	componente.setPresente(presente);
	Profilo profilo =new Profilo();
	Utente utente = new Utente(null,null,null,"Nome"+id,"Cognome"+id);
	profilo.setUtente(utente);
	componente.setProfilo(profilo);
	TipoAtto sl = new TipoAtto(2L,"SDL_A","schema dl");
	
	int ordine = 0;
	AttiOdg elem11sl1 = getAttoOdg(sl, ordine++, 1, 1, "presidente", null, "AOO TEST1"+id, numAdozione);
	componente.setAtto(elem11sl1);
	return componente;
}

	public static Verbale getVerbaleTest( ) throws IOException {
		Verbale verbale = new Verbale();
		SezioneTesto narrativa = new SezioneTesto();
		narrativa.setTesto("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?");
		verbale.setNarrativa(narrativa );
		SezioneTesto finale = new SezioneTesto();
		finale.setTesto("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur.");
		verbale.setNoteFinali(finale);
//		Set<DocumentoInformatico> allegati = new HashSet<DocumentoInformatico>();
		
		ClassPathResource resource = new ClassPathResource("/modelli/example/DD_allegato2.pdf");
		byte[] bFile = FileUtils.readFileToByteArray(resource.getFile());

		it.linksmt.assatti.datalayer.domain.File fileD1 = new it.linksmt.assatti.datalayer.domain.File();
		fileD1.setNomeFile("DD_allegato2.pdf");
//		fileD1.setContenuto(bFile);
		fileD1.setContentType("application/pdf");
		fileD1.setSize(new Long(bFile.length));
		fileD1.setId(9992L);
		
		/*
		 * TODO: causa errore in CMIS
		 * 
		DocumentoInformatico docInf1 = new DocumentoInformatico();
		docInf1.setNomeFile("DD_allegato2.pdf");
		docInf1.setOggetto("DD_allegato2.pdf - oggetto");
		docInf1.setPubblicabile(new Boolean(true));
		docInf1.setParteIntegrante(new Boolean(true));
		docInf1.setTitolo("DD_allegato2.pdf - titolo");
		docInf1.setOrdineInclusione(new Integer(2));
		docInf1.setFile(fileD1);
		docInf1.setFileomissis(fileD1);
		docInf1.setId(9992L);
		allegati.add(docInf1);
		verbale.setAllegati(allegati);
		*/
		
		// Sottoscrittori
		verbale.setSottoscrittori(new TreeSet<SottoscrittoreSedutaGiunta>());
		// Sottoscrittore segretario
		SottoscrittoreSedutaGiunta segretario = new SottoscrittoreSedutaGiunta();
		Profilo pSegr = new Profilo();
		pSegr.setUtente(new Utente());
		pSegr.getUtente().setNome("Antonio");
		pSegr.getUtente().setCognome("Santomauro");
		segretario.setProfilo(pSegr);
		segretario.setQualificaProfessionale(new QualificaProfessionale());
		segretario.getQualificaProfessionale().setDenominazione("Segretario Generale");
		segretario.setOrdineFirma(1);
		verbale.getSottoscrittori().add(segretario);
		// Sottoscrittore presidente
		SottoscrittoreSedutaGiunta presidente = new SottoscrittoreSedutaGiunta();
		Profilo pPres = new Profilo();
		pPres.setUtente(new Utente());
		pPres.getUtente().setNome("Loredana");
		pPres.getUtente().setCognome("Capone");
		presidente.setProfilo(pPres);
		presidente.setQualificaProfessionale(new QualificaProfessionale());
		presidente.getQualificaProfessionale().setDenominazione("Presidente");
		presidente.setOrdineFirma(2);
		verbale.getSottoscrittori().add(presidente);
		return verbale;
	}

	public static Lettera getLetteraTest( ) {
		return new Lettera();
	}

	public static Parere getParereTest( ) {

		Parere parere = new Parere();
		Atto test = getAttoTest();

		parere.setAtto(test);
		parere.setParereSintetico("Positivo");
		parere.setData(new DateTime());
		
		TipoAzione tipoPar = new TipoAzione();
		tipoPar.setCodice("PARERE-TEST");
		tipoPar.setDescrizione("PARERE-TEST");
		
		parere.setTipoAzione(tipoPar);
		parere.setTitolo("Titolo Documento di Parere");

		Set<SottoscrittoreAtto> sTest = test.getSottoscrittori();
		for (SottoscrittoreAtto sa : sTest) {
			SottoscrittoreParere sp = new SottoscrittoreParere();

			sp.setOrdineFirma(sa.getOrdineFirma());
			sp.setId(sa.getId());
			sp.setProfilo(sa.getProfilo());
			sp.setQualificaProfessionale(sa.getQualificaProfessionale());

			//parere.getSottoscrittori().add(sp);
		}

		return parere;
	}
	
	
	public static Resoconto getResocontoTest( ) {
		
		Resoconto resoconto = new Resoconto();
		resoconto.setOrganoDeliberante("");
		resoconto.setTipo((int)(Math.random()*2)+1);
		resoconto.setSedutaGiunta(getSedutaGiuntaTest(null));
		return resoconto;
	}

	public static SedutaGiunta getSedutaGiuntaTest(Integer tipo) {
		
		Utente utente0 = new Utente();
		utente0.setCognome("Bianchi");
		utente0.setNome("Antonio");
		Profilo profilo0 = new Profilo();
		profilo0.setUtente(utente0);
		Utente utente1 = new Utente();
		utente1.setCognome("Rossi");
		utente1.setNome("Mario");
		Profilo profilo1 = new Profilo();
		profilo1.setUtente(utente1);
		
		SedutaGiunta sedutaGiunta = new SedutaGiunta();
		Random randomGenerator = new Random();
		if(tipo == null)
			sedutaGiunta.setTipoSeduta(randomGenerator.nextBoolean()?1:2);//1 ordinaria, 2 straordinaria
		else
			sedutaGiunta.setTipoSeduta(tipo);//1 ordinaria, 2 straordinaria
		sedutaGiunta.setPrimaConvocazioneInizio(new DateTime());
		if(randomGenerator.nextBoolean()){
			sedutaGiunta.setSecondaConvocazioneInizio(new DateTime().plus(24L*60L*60L*1000L));
		}else{
			sedutaGiunta.setSecondaConvocazioneInizio(new DateTime().minus(24L*60L*60L*1000L));
		}
		
		sedutaGiunta.setLuogo("Bari");
		sedutaGiunta.setSecondaConvocazioneLuogo("Bari Luogo 2");
		sedutaGiunta.setPresidente(profilo0);
		sedutaGiunta.setDataOra(new DateTime());

		Profilo segretario = new Profilo();
		Utente utenteSegretario = new Utente();
		utenteSegretario.setCognome("Moretti");
		utenteSegretario.setNome("Carmela");
		segretario.setUtente(utenteSegretario);
		sedutaGiunta.setSegretario(segretario);
		Set<OrdineGiorno> odgs = new HashSet<OrdineGiorno>();
		
		odgs.add(getOrdineGiornoTest(1L, sedutaGiunta));
		odgs.add(getOrdineGiornoTest(2L, sedutaGiunta));
		odgs.add(getOrdineGiornoTest(3L, sedutaGiunta));
		odgs.add(getOrdineGiornoTest(4L, sedutaGiunta));
		
		sedutaGiunta.setOdgs(odgs);
		
		
//		Set<SottoscrittoreSedutaGiunta> sottoscrittoriresoconto;
		
		QualificaProfessionale qualificaProfessionale = new QualificaProfessionale();
//		qualificaProfessionale.setAoo(aoo);
		qualificaProfessionale.setDenominazione("Capo servizio");
		SottoscrittoreSedutaGiunta e = new SottoscrittoreSedutaGiunta();
		e.setOrdineFirma(new Integer(1));
		e.setId(9997L);
		e.setProfilo(profilo0);
		e.setQualificaProfessionale(qualificaProfessionale);


		Utente utente10 = new Utente();
		utente10.setCognome("Mario");
		utente10.setNome("Rossi");

		Aoo aooPadre = new Aoo();
		aooPadre.setCodice("AOO");
		aooPadre.setDescrizione("AREA POLITICHE PER LO SVILUPPO ECONOMICO, IL LAVORO E L'INNOVAZIONE");
		aooPadre.setIdentitavisiva("#db7012");
		aooPadre.setTipoAoo(new TipoAoo(1L, "AREA", "AREA"));
		
		Aoo aoo = new Aoo();
		aoo.setCodice("091");
		aoo.setDescrizione("Servizio politiche per il lavoro");
		aoo.setAooPadre(aooPadre);
		aoo.setTipoAoo(new TipoAoo(2L, "SERVIZIO", "SERVIZIO"));
		
		Profilo profilo10 = new Profilo();
		profilo10.setAoo(aoo);
		profilo10.setUtente(utente1);

		QualificaProfessionale qualificaProfessionale1 = new QualificaProfessionale();
//		qualificaProfessionale1.setAoo(aoo);
		qualificaProfessionale1.setDenominazione("Responsabile");

		SottoscrittoreSedutaGiunta e1 = new SottoscrittoreSedutaGiunta();
		e1.setOrdineFirma(new Integer(2));
		e1.setId(9998L);
		e1.setProfilo(profilo1);
		e1.setQualificaProfessionale(qualificaProfessionale1);

		sedutaGiunta.getSottoscrittoriresoconto().add(e);
		sedutaGiunta.getSottoscrittoriresoconto().add(e1);
		
		return sedutaGiunta;
	}
	
	public static OrdineGiorno getOrdineGiornoTest(long tipoOdg, SedutaGiunta sedutaGiunta) {
		//1 = base ordinario, //2 = base straordinario, //3 = suppletivo, //4 = fuorisacco
		TipoOdg _tipoOdg = new TipoOdg();
		_tipoOdg.setId(tipoOdg);
		OrdineGiorno odg = new OrdineGiorno();
		odg.setId(tipoOdg);
		odg.setTipoOdg(_tipoOdg);
		odg.setSedutaGiunta(sedutaGiunta);
		//odg.setNumeroOdg(String.valueOf((int)(Math.random()*99)+1));
		odg.setAttos(getAttiOdg());
		return odg;
	}
	
	private static AttiOdg getAttoOdg(TipoAtto tipoAtto, int ordine, int sezione, int parte, String relatore, String descAooPadre, String descAoo) {
		AttiOdg elem = new AttiOdg();
		Aoo aoo = new Aoo();
		descAoo = descAoo == null || "".equalsIgnoreCase(descAoo) ? "AOO TEST Custom" : descAoo;
		aoo.setDescrizione(descAoo);
		if(descAooPadre!=null) {
			Aoo aooPadre = new Aoo();
			aooPadre.setDescrizione(descAooPadre);
			aoo.setAooPadre(aooPadre);
		}
		Atto atto = new Atto();
		atto.setTipoAtto(tipoAtto);
		atto.setAoo(aoo);
//		atto.setDenominazioneRelatore(relatore);
		atto.setCodiceCifra("AVV/"+tipoAtto.getCodice()+"/2016/0084"+ordine);
		atto.setOggetto("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		if("AVVOCATURA REGIONALE".equalsIgnoreCase(descAoo)) {
			ArgomentoOdg arg = new ArgomentoOdg();
			arg.setDescrizione(ordine == 14 ? "Rettifiche" : "Nomine");
			atto.setArgomentoOdg(arg);
			atto.setUsoEsclusivo("avvocatura");
		}
		String numeroAdozione = String.valueOf((int)(Math.random()*1000)+1);
		atto.setNumeroAdozione(numeroAdozione);
		atto.setDataNumerazione(LocalDate.now());
		elem.setAtto(atto);
		elem.setOrdineGiorno(new OrdineGiorno());
		elem.setOrdineOdg(ordine);
		elem.setSezione(sezione);
		elem.setParte(parte);
		
		int esito = (int)(Math.random()*3)+1;
		if(esito == 1) 
			elem.setEsito("approvati");
		if(esito == 2) 
			elem.setEsito("rinviati");
		if(esito == 3) 
			elem.setEsito("ritirati");
		return elem;		
	}
	
	private static AttiOdg getAttoOdg(TipoAtto tipoAtto, int ordine, int sezione, int parte, String relatore, String descAooPadre, String descAoo, int numAdozione) {
		AttiOdg elem = new AttiOdg();
		Aoo aoo = new Aoo();
		descAoo = descAoo == null || "".equalsIgnoreCase(descAoo) ? "AOO TEST Custom" : descAoo;
		aoo.setDescrizione(descAoo);
		if(descAooPadre!=null) {
			Aoo aooPadre = new Aoo();
			aooPadre.setDescrizione(descAooPadre);
			aoo.setAooPadre(aooPadre);
		}
		Atto atto = new Atto();
		atto.setTipoAtto(tipoAtto);
		atto.setAoo(aoo);
//		atto.setDenominazioneRelatore(relatore);
		atto.setCodiceCifra("AVV/"+tipoAtto.getCodice()+"/2016/0084"+ordine);
		atto.setOggetto("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		if("AVVOCATURA REGIONALE".equalsIgnoreCase(descAoo)) {
			ArgomentoOdg arg = new ArgomentoOdg();
			arg.setDescrizione(ordine == 14 ? "Rettifiche" : "Nomine");
			atto.setArgomentoOdg(arg);
			atto.setUsoEsclusivo("avvocatura");
		}
		String numeroAdozione = String.valueOf(numAdozione);
		atto.setNumeroAdozione(numeroAdozione);
		atto.setDataNumerazione(LocalDate.now());
		elem.setAtto(atto);
		elem.setOrdineGiorno(new OrdineGiorno());
		elem.setOrdineOdg(ordine);
		elem.setSezione(sezione);
		elem.setParte(parte);
		
		int esito = (int)(Math.random()*3)+1;
		if(esito == 1) 
			elem.setEsito("approvati");
		if(esito == 2) 
			elem.setEsito("rinviati");
		if(esito == 3) 
			elem.setEsito("ritirati");
		return elem;		
	}
	
	private static List<AttiOdg> getAttiOdg() {
		
		
		List<AttiOdg> list = new ArrayList<AttiOdg>();
		int ordine = 0;
		
		AttiOdg attiOdgQT = new AttiOdg();
		Atto attoQT = new Atto();
		attoQT.setOggetto("QT");
		attoQT.setCodiceCifra("QT12345");
		attoQT.setId(10l);
		attoQT.setNumeroAdozione("00001");
		attoQT.setDataNumerazione(LocalDate.now());
		attoQT.setDataAdozione(new LocalDate());
		TipoAtto tipoAttoQT = new TipoAtto();
		tipoAttoQT.setCodice("QT");
		attoQT.setTipoAtto(tipoAttoQT);
		attiOdgQT.setAtto(attoQT);
		attiOdgQT.setOrdineOdg(ordine++);
		list.add(attiOdgQT);


		AttiOdg attiOdgVERB = new AttiOdg();
		Atto attoVERB = new Atto();
		attoVERB.setOggetto("VERB");
		attoVERB.setCodiceCifra("VERB12345");
		attoVERB.setId(10l);
		attoVERB.setNumeroAdozione("00001");
		attoQT.setDataNumerazione(LocalDate.now());
		attoVERB.setDataAdozione(new LocalDate());
		TipoAtto tipoAttoVERB = new TipoAtto();
		tipoAttoVERB.setCodice("VERB");
		attoVERB.setTipoAtto(tipoAttoVERB);
		attiOdgVERB.setAtto(attoVERB);
		attiOdgVERB.setOrdineOdg(ordine++);
		list.add(attiOdgVERB);


		AttiOdg attiOdgDG = new AttiOdg();
		Atto attoDG = new Atto();
		attoDG.setOggetto("DG");
		attoDG.setCodiceCifra("DG12345");
		attoDG.setId(10l);
		attoDG.setNumeroAdozione("00001");
		attoQT.setDataNumerazione(LocalDate.now());
		attoDG.setDataAdozione(new LocalDate());
		TipoAtto tipoAttoDG = new TipoAtto();
		tipoAttoDG.setCodice("DG");
		attoDG.setTipoAtto(tipoAttoDG);
		attiOdgDG.setAtto(attoDG);
		attiOdgDG.setOrdineOdg(ordine++);
		list.add(attiOdgDG);


		AttiOdg attiOdgCOM = new AttiOdg();
		Atto attoCOM = new Atto();
		attoCOM.setOggetto("COM");
		attoCOM.setCodiceCifra("COM12345");
		attoCOM.setId(10l);
		attoCOM.setNumeroAdozione("00001");
		attoQT.setDataNumerazione(LocalDate.now());
		attoCOM.setDataAdozione(new LocalDate());
		TipoAtto tipoAttoCOM = new TipoAtto();
		tipoAttoCOM.setCodice("COM");
		attoCOM.setTipoAtto(tipoAttoCOM);
		attiOdgCOM.setAtto(attoCOM);
		attiOdgCOM.setOrdineOdg(ordine++);
		list.add(attiOdgCOM);


		AttiOdg attiOdgINT = new AttiOdg();
		Atto attoINT = new Atto();
		attoINT.setOggetto("INT");
		attoINT.setCodiceCifra("INT12345");
		attoINT.setId(10l);
		attoINT.setNumeroAdozione("00001");
		attoQT.setDataNumerazione(LocalDate.now());
		attoINT.setDataAdozione(new LocalDate());
		TipoAtto tipoAttoINT = new TipoAtto();
		tipoAttoINT.setCodice("INT");
		attoINT.setTipoAtto(tipoAttoINT);
		attiOdgINT.setAtto(attoINT);
		attiOdgINT.setOrdineOdg(ordine++);
		list.add(attiOdgINT);


		AttiOdg attiOdgODG = new AttiOdg();
		Atto attoODG = new Atto();
		attoODG.setOggetto("ODG");
		attoODG.setCodiceCifra("ODG12345");
		attoODG.setId(10l);
		attoODG.setNumeroAdozione("00001");
		attoODG.setDataAdozione(new LocalDate());
		attoQT.setDataNumerazione(LocalDate.now());
		TipoAtto tipoAttoODG = new TipoAtto();
		tipoAttoODG.setCodice("ODG");
		attoODG.setTipoAtto(tipoAttoODG);
		attiOdgODG.setAtto(attoODG);
		attiOdgODG.setOrdineOdg(ordine++);
		list.add(attiOdgODG);


		AttiOdg attiOdgMZ = new AttiOdg();
		Atto attoMZ = new Atto();
		attoMZ.setOggetto("MZ");
		attoMZ.setCodiceCifra("MZ12345");
		attoMZ.setId(10l);
		attoMZ.setNumeroAdozione("00001");
		attoQT.setDataNumerazione(LocalDate.now());
		attoMZ.setDataAdozione(new LocalDate());
		TipoAtto tipoAttoMZ = new TipoAtto();
		tipoAttoMZ.setCodice("MZ");
		attoMZ.setTipoAtto(tipoAttoMZ);
		attiOdgMZ.setAtto(attoMZ);
		attiOdgMZ.setOrdineOdg(ordine++);
		list.add(attiOdgMZ);


		AttiOdg attiOdgRIS = new AttiOdg();
		Atto attoRIS = new Atto();
		attoRIS.setOggetto("RIS");
		attoRIS.setCodiceCifra("RIS12345");
		attoRIS.setId(10l);
		attoRIS.setNumeroAdozione("00001");
		attoQT.setDataNumerazione(LocalDate.now());
		attoRIS.setDataAdozione(new LocalDate());
		TipoAtto tipoAttoRIS = new TipoAtto();
		tipoAttoRIS.setCodice("RIS");
		attoRIS.setTipoAtto(tipoAttoRIS);
		attiOdgRIS.setAtto(attoRIS);
		attiOdgRIS.setOrdineOdg(ordine++);
		list.add(attiOdgRIS);
		
		TipoAtto del = new TipoAtto(1L,"DEL_A","delibera");
		TipoAtto sl = new TipoAtto(2L,"SDL_A","schema dl");
		TipoAtto dl = new TipoAtto(3L,"DDL_A","dl");
		
		AttiOdg elem11sl1 = getAttoOdg(sl, ordine++, 1, 1, "presidente", null, "AOO TEST1");
		AttiOdg elem11sl2 = getAttoOdg(sl, ordine++, 1, 1, "presidente", null, "AOO TEST2");
		AttiOdg elem11sl3 = getAttoOdg(sl, ordine++, 1, 1, "vicepresidente", null, null);;
		AttiOdg elem11sl4 = getAttoOdg(sl, ordine++, 1, 1, "vicepresidente", null, null);
		AttiOdg elem11sl5 = getAttoOdg(sl, ordine++, 1, 1, "assessore", "AOO1 PADRE", "AOO1 FIGLIO");
		AttiOdg elem11sl6 = getAttoOdg(sl, ordine++, 1, 1, "assessore", "AOO1 PADRE", "AOO1 FIGLIO");
                     
		AttiOdg elem11dl1 = getAttoOdg(dl, ordine++, 1, 1, "presidente", null, "AVVOCATURA REGIONALE");
		AttiOdg elem11dl2 = getAttoOdg(dl, ordine++, 1, 1, "presidente", null, null);
		AttiOdg elem11dl3 = getAttoOdg(dl, ordine++, 1, 1, "vicepresidente", null, null);
		AttiOdg elem11dl4 = getAttoOdg(dl, ordine++, 1, 1, "vicepresidente", null, null);
		AttiOdg elem11dl5 = getAttoOdg(dl, ordine++, 1, 1, "assessore", null, "AOO2");
		AttiOdg elem11dl6 = getAttoOdg(dl, ordine++, 1, 1, "assessore", null, "AOO2");
                     
		AttiOdg elem11del1 = getAttoOdg(del, ordine++, 1, 1, "presidente", null, "AVVOCATURA REGIONALE");
		AttiOdg elem11del2 = getAttoOdg(del, ordine++, 1, 1, "presidente", null, "AVVOCATURA REGIONALE");
		AttiOdg elem11del22 = getAttoOdg(del, ordine++, 1, 1, "presidente", null, "AVVOCATURA REGIONALE");
		AttiOdg elem11del3 = getAttoOdg(del, ordine++, 1, 1, "vicepresidente", null, null);
		AttiOdg elem11del4 = getAttoOdg(del, ordine++, 1, 1, "vicepresidente", null, null);
		AttiOdg elem11del5 = getAttoOdg(del, ordine++, 1, 1, "assessore", null, "AOO3");
		AttiOdg elem11del6 = getAttoOdg(del, ordine++, 1, 1, "assessore", null, "AOO4");
                     
		AttiOdg elem12sl1 = getAttoOdg(sl, ordine++, 1, 2, "presidente", null, null);
		AttiOdg elem12sl2 = getAttoOdg(sl, ordine++, 1, 2, "presidente", null, null);
		AttiOdg elem12sl3 = getAttoOdg(sl, ordine++, 1, 2, "vicepresidente", null, null);
		AttiOdg elem12sl4 = getAttoOdg(sl, ordine++, 1, 2, "vicepresidente", null, null);
		AttiOdg elem12sl5 = getAttoOdg(sl, ordine++, 1, 2, "assessore",  "AOO5 PADRE", "AOO5 FIGLIO");
		AttiOdg elem12sl6 = getAttoOdg(sl, ordine++, 1, 2, "assessore", "AOO5 PADRE", "AOO6 FIGLIO");
		                                                
		AttiOdg elem12dl1 = getAttoOdg(dl, ordine++, 1, 2, "presidente", null, null);
		AttiOdg elem12dl2 = getAttoOdg(dl, ordine++, 1, 2, "presidente", null, null);
		AttiOdg elem12dl3 = getAttoOdg(dl, ordine++, 1, 2, "vicepresidente", null, null);
		AttiOdg elem12dl4 = getAttoOdg(dl, ordine++, 1, 2, "vicepresidente", null, null);
		AttiOdg elem12dl5 = getAttoOdg(dl, ordine++, 1, 2, "assessore", "", "");
		AttiOdg elem12dl6 = getAttoOdg(dl, ordine++, 1, 2, "assessore", "", "");
		             
		AttiOdg elem12del1 = getAttoOdg(del, ordine++, 1, 2, "presidente", null, null);
		AttiOdg elem12del2 = getAttoOdg(del, ordine++, 1, 2, "presidente", null, null);
		AttiOdg elem12del3 = getAttoOdg(del, ordine++, 1, 2, "vicepresidente", null, null);
		AttiOdg elem12del4 = getAttoOdg(del, ordine++, 1, 2, "vicepresidente", null, null);
		AttiOdg elem12del5 = getAttoOdg(del, ordine++, 1, 2, "assessore", "", "");;
		AttiOdg elem12del6 = getAttoOdg(del, ordine++, 1, 2, "assessore", "", "");

		AttiOdg elem21sl1 = getAttoOdg(sl, ordine++, 2, 1, "presidente", null, null);
		AttiOdg elem21sl2 = getAttoOdg(sl, ordine++, 2, 1, "presidente", null, null);
		AttiOdg elem21sl3 = getAttoOdg(sl, ordine++, 2, 1, "vicepresidente", null, null);
		AttiOdg elem21sl4 = getAttoOdg(sl, ordine++, 2, 1, "vicepresidente", null, null);
		AttiOdg elem21sl5 = getAttoOdg(sl, ordine++, 2, 1, "assessore", "", "");
		AttiOdg elem21sl6 = getAttoOdg(sl, ordine++, 2, 1, "assessore", "", "");
		
		AttiOdg elem21dl1 = getAttoOdg(dl, ordine++, 2, 1, "presidente", null, null);
		AttiOdg elem21dl2 = getAttoOdg(dl, ordine++, 2, 1, "presidente", null, null);
		AttiOdg elem21dl3 = getAttoOdg(dl, ordine++, 2, 1, "vicepresidente", null, null);
		AttiOdg elem21dl4 = getAttoOdg(dl, ordine++, 2, 1, "vicepresidente", null, null);
		AttiOdg elem21dl5 = getAttoOdg(dl, ordine++, 2, 1, "assessore", "", "");
		AttiOdg elem21dl6 = getAttoOdg(dl, ordine++, 2, 1, "assessore", "", "");
		
		AttiOdg elem21del1 = getAttoOdg(del, ordine++, 2, 1, "presidente", null, null);;
		AttiOdg elem21del2 = getAttoOdg(del, ordine++, 2, 1, "presidente", null, null);
		AttiOdg elem21del3 = getAttoOdg(del, ordine++, 2, 1, "vicepresidente", null, null);
		AttiOdg elem21del4 = getAttoOdg(del, ordine++, 2, 1, "vicepresidente", null, null);
		AttiOdg elem21del5 = getAttoOdg(del, ordine++, 2, 1, "assessore", "", "");
		AttiOdg elem21del6 = getAttoOdg(del, ordine++, 2, 1, "assessore", "", "");
		
		AttiOdg elem22sl1 = getAttoOdg(sl, ordine++, 2, 2, "presidente", null, null);
		AttiOdg elem22sl2 = getAttoOdg(sl, ordine++, 2, 2, "presidente", null, null);
		AttiOdg elem22sl3 = getAttoOdg(sl, ordine++, 2, 2, "vicepresidente", null, null);
		AttiOdg elem22sl4 = getAttoOdg(sl, ordine++, 2, 2, "vicepresidente", null, null);
		AttiOdg elem22sl5 = getAttoOdg(sl, ordine++, 2, 2, "assessore", "", "");
		AttiOdg elem22sl6 = getAttoOdg(sl, ordine++, 2, 2, "assessore", "", "");
		
		AttiOdg elem22dl1 = getAttoOdg(dl, ordine++, 2, 2, "presidente", null, null);
		AttiOdg elem22dl2 = getAttoOdg(dl, ordine++, 2, 2, "presidente", null, null);
		AttiOdg elem22dl3 = getAttoOdg(dl, ordine++, 2, 2, "vicepresidente", null, null);
		AttiOdg elem22dl4 = getAttoOdg(dl, ordine++, 2, 2, "vicepresidente", null, null);
		AttiOdg elem22dl5 = getAttoOdg(dl, ordine++, 2, 2, "assessore", "", "");
		AttiOdg elem22dl6 = getAttoOdg(dl, ordine++, 2, 2, "assessore", "", "");
		
		AttiOdg elem22del1 = getAttoOdg(del, ordine++, 2, 2, "presidente", null, null);
		AttiOdg elem22del2 = getAttoOdg(del, ordine++, 2, 2, "presidente", null, null);
		AttiOdg elem22del3 = getAttoOdg(del, ordine++, 2, 2, "vicepresidente", null, null);
		AttiOdg elem22del4 = getAttoOdg(del, ordine++, 2, 2, "vicepresidente", null, null);
		AttiOdg elem22del5 = getAttoOdg(del, ordine++, 2, 2, "assessore", "", "");
		AttiOdg elem22del6 = getAttoOdg(del, ordine++, 2, 2, "assessore", "", "");

		
		list.add(elem11sl1);
		list.add(elem11sl2);
		list.add(elem11sl3);
		list.add(elem11sl4);
		list.add(elem11sl5);
		list.add(elem11sl6);

		list.add(elem11dl1);
		list.add(elem11dl2);
		list.add(elem11dl3);
		list.add(elem11dl4);
		list.add(elem11dl5);
		list.add(elem11dl6);

		list.add(elem11del1);
		list.add(elem11del2);
		list.add(elem11del22);
		list.add(elem11del3);
		list.add(elem11del4);
		list.add(elem11del5);
		list.add(elem11del6);

		list.add(elem12sl1);
		list.add(elem12sl2);
		list.add(elem12sl3);
		list.add(elem12sl4);
		list.add(elem12sl5);
		list.add(elem12sl6);
		              
		list.add(elem12dl1);
		list.add(elem12dl2);
		list.add(elem12dl3);
		list.add(elem12dl4);
		list.add(elem12dl5);
		list.add(elem12dl6);
		              
		list.add(elem12del1);
		list.add(elem12del2);
		list.add(elem12del3);
		list.add(elem12del4);
		list.add(elem12del5);
		list.add(elem12del6);
		
		list.add(elem21sl1);
		list.add(elem21sl2);
		list.add(elem21sl3);
		list.add(elem21sl4);
		list.add(elem21sl5);
		list.add(elem21sl6);
		
		list.add(elem21dl1);
		list.add(elem21dl2);
		list.add(elem21dl3);
		list.add(elem21dl4);
		list.add(elem21dl5);
		list.add(elem21dl6);
		
		list.add(elem21del1);
		list.add(elem21del2);
		list.add(elem21del3);
		list.add(elem21del4);
		list.add(elem21del5);
		list.add(elem21del6);

		list.add(elem22sl1);
		list.add(elem22sl2);
		list.add(elem22sl3);
		list.add(elem22sl4);
		list.add(elem22sl5);
		list.add(elem22sl6);
		
		list.add(elem22dl1);
		list.add(elem22dl2);
		list.add(elem22dl3);
		list.add(elem22dl4);
		list.add(elem22dl5);
		list.add(elem22dl6);
		
		list.add(elem22del1);
		list.add(elem22del2);
		list.add(elem22del3);
		list.add(elem22del4);
		list.add(elem22del5);
		list.add(elem22del6);
		
		return list;
	}

	public static RelataDiPubblicazioneDto getDatiRelataPubblicazioneTest() {
		RelataDiPubblicazioneDto out = new RelataDiPubblicazioneDto();
		out.setCodiceCifra("999/999/0000000999");
		out.setDataAdozione(new LocalDate());
		LocalDate inizioPubblicazioneAlbo = new LocalDate(2017, 1, 1);
		out.setInizioPubblicazioneAlbo(inizioPubblicazioneAlbo );
		LocalDate finePubblicazioneAlbo = new LocalDate(2017, 1, 16);
		out.setFinePubblicazioneAlbo(finePubblicazioneAlbo);
		out.setNominativoResponsabilePubblicazione("Antonio Rossi");
		out.setOggetto("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur rhoncus nisi sit amet dui molestie suscipit. Curabitur sed ligula mi. Duis consectetur risus in mollis vehicula. Integer sed congue neque. Curabitur tincidunt malesuada aliquet. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur cursus nec urna eget sollicitudin. Vivamus non nibh pulvinar, facilisis sem quis, fringilla enim. Nullam vitae turpis nec ante facilisis vestibulum sit amet id odio. Nullam vitae ante in arcu condimentum pretium in suscipit orci. Ut eget sodales lorem, vel tincidunt nisi. In vel fermentum tortor." );
		out.setNumeroAdozione("654");
		Aoo aooPadre = new Aoo();
		aooPadre.setCodice("AOO");
		aooPadre.setDescrizione("AREA POLITICHE PER LO SVILUPPO ECONOMICO, IL LAVORO E L'INNOVAZIONE");
		aooPadre.setIdentitavisiva("#db7012");
		aooPadre.setTipoAoo(new TipoAoo(1L, "AREA", "AREA"));
		Aoo aoo = new Aoo();
		aoo.setCodice("091");
		aoo.setDescrizione("Servizio politiche per il lavoro");
		aoo.setAooPadre(aooPadre);
		aoo.setTipoAoo(new TipoAoo(2L, "SERVIZIO", "SERVIZIO"));
		out.setAooProponente(aoo);
		return out;
	}
	
}
