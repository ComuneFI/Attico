package it.linksmt.assatti.service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.util.TempFile;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.linksmt.assatti.datalayer.domain.Atto;

@Service
public class XmlBuilderService {
	public File creaXml(List<Atto> atti) throws Exception{
		String baseUrl = System.getProperty("baseUrl");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.newDocument();
		
		Element root = dom.createElement("atti");
		dom.appendChild(root);
		
		for(Atto atto : atti){
			Element attoElement = this.buildElementFromAtto(atto, dom, baseUrl);
			root.appendChild(attoElement);
		}
		
		File tmpFile = TempFile.createTempFile("atti", new Date().getTime() + ".xml");
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(dom);
		StreamResult result = new StreamResult(tmpFile);
		transformer.transform(source, result);
		
		return tmpFile;
	}
	
	public File creaXml(Atto atto) throws Exception{
		String baseUrl = System.getProperty("baseUrl");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.newDocument();
		
		Element root = dom.createElement("atti");
		dom.appendChild(root);
		
		Element attoElement = this.buildElementFromAtto(atto, dom, baseUrl);
		root.appendChild(attoElement);
		
		File tmpFile = TempFile.createTempFile("atti", new Date().getTime() + ".xml");
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(dom);
		StreamResult result = new StreamResult(tmpFile);
		transformer.transform(source, result);
		
		return tmpFile;
	}
	
	private Element buildElementFromAtto(Atto atto, Document dom, String baseUrl){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Element elAtto = dom.createElement("atto");
		Element codiceCifra = dom.createElement("codiceCifra");
		codiceCifra.appendChild(dom.createTextNode(atto.getCodiceCifra()));
		elAtto.appendChild(codiceCifra);
		
		Element dataCreazione = dom.createElement("dataCreazione");
		dataCreazione.appendChild(dom.createTextNode(atto.getDataCreazione() != null ? df.format(atto.getDataCreazione().toDate()) : ""));
		elAtto.appendChild(dataCreazione);		

		Element oggetto = dom.createElement("oggetto");
		oggetto.appendChild(dom.createTextNode(atto.getOggetto() !=null ? atto.getOggetto() : ""));
		elAtto.appendChild(oggetto);
		
		Element aoo = dom.createElement("aoo");
		aoo.appendChild(dom.createTextNode(atto.getAoo() != null ? atto.getAoo().getCodice() + "-" + atto.getAoo().getDescrizione() : ""));
		elAtto.appendChild(aoo);

		Element tipoIter = dom.createElement("tipoIter");
		tipoIter.appendChild(dom.createTextNode(atto.getTipoIter() != null ? atto.getTipoIter().getDescrizione() : ""));
		elAtto.appendChild(tipoIter);

		Element stato = dom.createElement("stato");
		stato.appendChild(dom.createTextNode(atto.getStato() != null ? atto.getStato() : ""));
		elAtto.appendChild(stato);

		Element numeroAdozione = dom.createElement("numeroAdozione");
		numeroAdozione.appendChild(dom.createTextNode(atto.getNumeroAdozione() != null ? atto.getNumeroAdozione() : ""));
		elAtto.appendChild(numeroAdozione);

		Element dataAdozione = dom.createElement("dataAdozione");
		dataAdozione.appendChild(dom.createTextNode(atto.getDataAdozione() != null ? df.format(atto.getDataAdozione().toDate()) : ""));
		elAtto.appendChild(dataAdozione);

		Element dataInizioPubblicazione = dom.createElement("dataInizioPubblicazione");
		dataInizioPubblicazione.appendChild(dom.createTextNode(atto.getInizioPubblicazioneAlbo() != null ? df.format(atto.getInizioPubblicazioneAlbo().toDate()) : ""));
		elAtto.appendChild(dataInizioPubblicazione);

		Element dataFinePubblicazione = dom.createElement("dataFinePubblicazione");
		dataFinePubblicazione.appendChild(dom.createTextNode(atto.getFinePubblicazioneAlbo() != null ? df.format(atto.getFinePubblicazioneAlbo().toDate()) : ""));
		elAtto.appendChild(dataFinePubblicazione);
		if(atto.getRiservato()!=null && !atto.getRiservato()){
			Element urlAttoPubblicato = dom.createElement("urlAttoPubblicato");
	
			String idDocumento = null;
			if(atto.getDocumentiPdfAdozioneOmissis()!=null && atto.getDocumentiPdfAdozioneOmissis().size() > 0){
				idDocumento = atto.getDocumentiPdfAdozioneOmissis().get(0).getId() + "";
			}else if(atto.getDocumentiPdfAdozione()!=null && atto.getDocumentiPdfAdozione().size() > 0){
				idDocumento = atto.getDocumentiPdfAdozione().get(0).getId() + "";
			}
			if(idDocumento!=null){
				urlAttoPubblicato.appendChild(dom.createTextNode(baseUrl + "/api/attos/open/" + atto.getId() + "/documento/" + idDocumento));
				elAtto.appendChild(urlAttoPubblicato);
			}
		}
		return elAtto;
	}
}
