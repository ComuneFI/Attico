package it.linksmt.assatti.service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Assessorato;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreAtto;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreSedutaGiunta;
import it.linksmt.assatti.datalayer.domain.Ufficio;
import it.linksmt.assatti.datalayer.domain.dto.RigaFuoriSaccoDto;
import it.linksmt.assatti.datalayer.repository.AttoRepository;

@Service
public class CartaStampataService {

	@Inject
	private AssessoratoService assessoratoService;
	@Inject
	private AttoRepository attoRepository;
	@Inject
	private SottoScrittoreAttoService sottoScrittoreAttoService;
	
	private static Logger logger = LoggerFactory.getLogger(CartaStampataService.class);

	public static final String FONT = "/fonts/FreeSans.ttf";
	public static final String FONT_PALSCRI = "/fonts/PALSCRI.TTF";
	public static final String PRESIDENTE_STR = "Presidente";
	public static final String VICE_PRESIDENTE_STR = "VicePresidente";

	public static BaseColor hex2Rgb(final String colorStr) {
		try {
			if (colorStr != null && colorStr.startsWith("#") && colorStr.length() == 7) {
				Color color = Color.decode(colorStr);
				return new BaseColor(color.getRed(), color.getGreen(), color.getBlue());
			}
		}
		catch (RuntimeException e) {
			logger.error("Errore nel calcolo del colore", e);
		}

		return BaseColor.LIGHT_GRAY;
	}

	/**
	 * Main method.
	 *
	 * @param args no arguments needed
	 * @throws DocumentException
	 * @throws IOException
	 */
	public File addFiligrana(final InputStream input, final InputStream inputBanner, final InputStream inputLogo, final Aoo aoo, final Ufficio ufficio, final String urlSitoRegione, boolean nascondiSitoESpostaDicituraArea, boolean soloFrontespizio, boolean soloLogoCentrale)
			throws IOException, DocumentException {

		// float x = document.getPageSize().getWidth() - document.leftMargin() -
		// document.rightMargin();
		// float y = document.getPageSize().getHeight() - document.topMargin() -
		// document.bottomMargin();

		List<String> aoosIntestazione = new ArrayList<String>();
		
		if(aoo!=null && aoo.getAooPadre()!=null && aoo.getAooPadre().getDescrizione()!=null){
			aoosIntestazione.add(aoo.getAooPadre().getDescrizione());
		}
		
		if(aoo != null && aoo.getDescrizione()!=null){
			aoosIntestazione.add(aoo.getDescrizione());
		}
		
		if(ufficio!=null && ufficio.getDescrizione()!=null){
			aoosIntestazione.add(ufficio.getDescrizione());
		}
		
		File result = File.createTempFile("withfiligrana_", ".pdf");
		PdfReader pdfReader = new PdfReader(input);

		Document document = nascondiSitoESpostaDicituraArea?new Document(PageSize.A4): new Document(PageSize.A4,30,30,20,0);
		
		document.open();

		Image imageBanner = null;
		try {
			imageBanner = Image.getInstance(IOUtils.toByteArray(inputBanner));
			imageBanner.scaleToFit(70, 240);
			imageBanner.setTransparency(new int[] { 0xF0, 0xFF });
		}
		catch (Exception e) {
			// ok
		}

		Image imageLogo  = Image.getInstance(IOUtils.toByteArray(inputLogo));
		if(soloLogoCentrale){
			imageLogo.scaleToFit(150, 100);
		}else{
			
			imageLogo.scaleToFit(175,92);
		}
		

		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(result));

		BaseColor color = hex2Rgb("#000000");
		logger.debug("Colore" + "#000000");

		Rectangle rectLineOrizzontaleTop = null;

		Rectangle rectLineVerticalTop = null;

 		Font font = FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
		BaseFont bf = font.getBaseFont();

		for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
			int pagina = soloFrontespizio?1:i;
			// PdfContentByte content = pdfStamper.getUnderContent(i);

			if(i==1 || !soloFrontespizio){
				PdfContentByte content = pdfStamper.getOverContent(pagina);
				Rectangle rectanglePage = pdfReader.getPageSize(pagina);
				
				if(soloLogoCentrale){
					float x1 = rectanglePage.getLeft() + rectanglePage.getWidth() / 2 - 65;
					float y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight();
					imageLogo.setAbsolutePosition(x1, y1);
					imageLogo.setTransparency(new int[] { 0xF0, 0xFF });
					
					content.saveState();
					content.addImage(imageLogo);
					
				}else
				{
					float x1 = rectanglePage.getLeft() + document.leftMargin();
					float y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight();
					float y1Logo = rectanglePage.getTop() - 30 - imageLogo.getScaledHeight();
					imageLogo.setAbsolutePosition(nascondiSitoESpostaDicituraArea?x1+3:x1, y1Logo);
					imageLogo.setTransparency(new int[] { 0xF0, 0xFF });
		
					try {
						float x0 = rectanglePage.getWidth() - imageBanner.getScaledWidth() - 18;
						float y0 = rectanglePage.getHeight() - imageBanner.getScaledHeight() - 18;
						imageBanner.setAbsolutePosition(x0, y0);
					}
					catch (Exception e) {
						// ok
					}
		
					rectLineOrizzontaleTop = new Rectangle(x1, y1 - 10, rectanglePage.getWidth() - document.leftMargin() - document.rightMargin() - 20, y1 - 8);
					rectLineOrizzontaleTop.setBackgroundColor(color);
					rectLineOrizzontaleTop.setBorderColor(color);
		
					rectLineVerticalTop = new Rectangle(x1 + 165, y1 - 8, x1 + 167, rectanglePage.getHeight() - document.topMargin());
					rectLineVerticalTop.setBackgroundColor(color);
					rectLineVerticalTop.setBorderColor(color);

					content.saveState();
		
					content.addImage(imageLogo);
					

		
					x1+=20;
					
					
					if(nascondiSitoESpostaDicituraArea){
						if(aoosIntestazione!=null && aoosIntestazione.size()>0){
							int ydif = -8;
							for (int j = 0; j < aoosIntestazione.size(); j++) {
								
								String wrap = WordUtils.wrap(aoosIntestazione.get(j), 50);
								String[] wraps = wrap.split("\n");
								for (String wr : wraps) {
									content.beginText();
									content.setFontAndSize(bf, 11f);
									content.setColorFill(color);
									content.setTextMatrix(x1 + 60, y1 + ydif);
									content.showText(wr);
									content.endText();
									ydif = ydif - 15;
								}
								ydif = ydif - 8;
							}
						}
					}else{
						if(aoosIntestazione!=null && aoosIntestazione.size()>0){
							int ydif = 45;
							if(aoosIntestazione.size()==3){
								ydif+=23;
							}else if(aoosIntestazione.size()==2){
								ydif+=8;
							}
							for (int j = 0; j < aoosIntestazione.size(); j++) {
								String wrap = WordUtils.wrap(aoosIntestazione.get(j), 50);
								String[] wraps = wrap.split("\n");
								for (String wr : wraps) {
									content.beginText();
									content.setFontAndSize(bf, 11f);
									content.setColorFill(color);
									content.setTextMatrix(x1 + 175, y1 + ydif);
									content.showText(wr);
									content.endText();
									ydif = ydif - 15;
								}
								ydif = ydif - 8;
							}
						}
					}
					if(!nascondiSitoESpostaDicituraArea){
						//piè di pagina: linea - sito
						Rectangle rectLineOrizzontaleBottom = new Rectangle(x1, document.bottomMargin() +26, rectanglePage.getWidth() - document.leftMargin() - document.rightMargin()+5, document.bottomMargin()+25);
						rectLineOrizzontaleBottom.setBackgroundColor(color);
						rectLineOrizzontaleBottom.setBorderColor(color);
			
						content.rectangle(rectLineOrizzontaleBottom);
						content.stroke();
						
						ColumnText.showTextAligned(content, Element.ALIGN_LEFT, new Phrase(urlSitoRegione),
								x1, 15, 0);
					}
				}
				content.restoreState();
				
			}
		}

		pdfStamper.close();
		document.close();
		pdfReader.close();
		return result;
	}

	public File addFiligranaDeliberaConsiglio(final InputStream input, final InputStream inputBanner, final InputStream inputLogo, final Aoo aoo, final Ufficio ufficio, final String urlSitoRegione, boolean nascondiSitoESpostaDicituraArea, boolean soloFrontespizio, boolean soloLogoCentrale)
			throws IOException, DocumentException {

		// float x = document.getPageSize().getWidth() - document.leftMargin() -
		// document.rightMargin();
		// float y = document.getPageSize().getHeight() - document.topMargin() -
		// document.bottomMargin();

		List<String> aoosIntestazione = new ArrayList<String>();
		
		if(aoo!=null && aoo.getAooPadre()!=null && aoo.getAooPadre().getDescrizione()!=null){
			aoosIntestazione.add(aoo.getAooPadre().getDescrizione());
		}
		
		if(aoo != null && aoo.getDescrizione()!=null){
			aoosIntestazione.add(aoo.getDescrizione());
		}
		
		if(ufficio!=null && ufficio.getDescrizione()!=null){
			aoosIntestazione.add(ufficio.getDescrizione());
		}
		
		File result = File.createTempFile("withfiligrana_", ".pdf");
		PdfReader pdfReader = new PdfReader(input);

		Document document = nascondiSitoESpostaDicituraArea?new Document(PageSize.A4): new Document(PageSize.A4,30,30,5,0);
		
		document.open();

		Image imageBanner = null;
		try {
			imageBanner = Image.getInstance(IOUtils.toByteArray(inputBanner));
			imageBanner.scaleToFit(70, 240);
			imageBanner.setTransparency(new int[] { 0xF0, 0xFF });
		}
		catch (Exception e) {
			// ok
		}

		Image imageLogo  = Image.getInstance(IOUtils.toByteArray(inputLogo));
		if(soloLogoCentrale){
			imageLogo.scaleToFit(125, 83);
		}else{
			
			imageLogo.scaleToFit(175,92);
		}
		

		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(result));

		BaseColor color = hex2Rgb("#000000");
		logger.debug("Colore" + "#000000");

		Rectangle rectLineOrizzontaleTop = null;

		Rectangle rectLineVerticalTop = null;

 		Font font = FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
		BaseFont bf = font.getBaseFont();

		for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
			int pagina = soloFrontespizio?1:i;
			// PdfContentByte content = pdfStamper.getUnderContent(i);

			if(i==1 || !soloFrontespizio){
				PdfContentByte content = pdfStamper.getOverContent(pagina);
				Rectangle rectanglePage = pdfReader.getPageSize(pagina);
				
				if(soloLogoCentrale){
					float x1 = rectanglePage.getLeft() + rectanglePage.getWidth() / 2 - 65;
					float y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight();
					imageLogo.setAbsolutePosition(x1, y1);
					imageLogo.setTransparency(new int[] { 0xF0, 0xFF });
					
					content.saveState();
					content.addImage(imageLogo);
					
				}else
				{
					float x1 = rectanglePage.getLeft() + document.leftMargin();
					float y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight();
					float y1Logo = rectanglePage.getTop() - 30 - imageLogo.getScaledHeight();
					imageLogo.setAbsolutePosition(nascondiSitoESpostaDicituraArea?x1+3:x1, y1Logo);
					imageLogo.setTransparency(new int[] { 0xF0, 0xFF });
		
					try {
						float x0 = rectanglePage.getWidth() - imageBanner.getScaledWidth() - 18;
						float y0 = rectanglePage.getHeight() - imageBanner.getScaledHeight() - 18;
						imageBanner.setAbsolutePosition(x0, y0);
					}
					catch (Exception e) {
						// ok
					}
		
					rectLineOrizzontaleTop = new Rectangle(x1, y1 - 10, rectanglePage.getWidth() - document.leftMargin() - document.rightMargin() - 20, y1 - 8);
					rectLineOrizzontaleTop.setBackgroundColor(color);
					rectLineOrizzontaleTop.setBorderColor(color);
		
					rectLineVerticalTop = new Rectangle(x1 + 165, y1 - 8, x1 + 167, rectanglePage.getHeight() - document.topMargin());
					rectLineVerticalTop.setBackgroundColor(color);
					rectLineVerticalTop.setBorderColor(color);

					content.saveState();
		
					content.addImage(imageLogo);
					

		
					x1+=20;
					
					
					if(nascondiSitoESpostaDicituraArea){
						if(aoosIntestazione!=null && aoosIntestazione.size()>0){
							int ydif = -8;
							for (int j = 0; j < aoosIntestazione.size(); j++) {
								
								String wrap = WordUtils.wrap(aoosIntestazione.get(j), 50);
								String[] wraps = wrap.split("\n");
								for (String wr : wraps) {
									content.beginText();
									content.setFontAndSize(bf, 11f);
									content.setColorFill(color);
									content.setTextMatrix(x1 + 60, y1 + ydif);
									content.showText(wr);
									content.endText();
									ydif = ydif - 15;
								}
								ydif = ydif - 8;
							}
						}
					}else{
						if(aoosIntestazione!=null && aoosIntestazione.size()>0){
							int ydif = 45;
							if(aoosIntestazione.size()==3){
								ydif+=23;
							}else if(aoosIntestazione.size()==2){
								ydif+=8;
							}
							for (int j = 0; j < aoosIntestazione.size(); j++) {
								String wrap = WordUtils.wrap(aoosIntestazione.get(j), 50);
								String[] wraps = wrap.split("\n");
								for (String wr : wraps) {
									content.beginText();
									content.setFontAndSize(bf, 11f);
									content.setColorFill(color);
									content.setTextMatrix(x1 + 175, y1 + ydif);
									content.showText(wr);
									content.endText();
									ydif = ydif - 15;
								}
								ydif = ydif - 8;
							}
						}
					}
					if(!nascondiSitoESpostaDicituraArea){
						//piè di pagina: linea - sito
						Rectangle rectLineOrizzontaleBottom = new Rectangle(x1, document.bottomMargin() +26, rectanglePage.getWidth() - document.leftMargin() - document.rightMargin()+5, document.bottomMargin()+25);
						rectLineOrizzontaleBottom.setBackgroundColor(color);
						rectLineOrizzontaleBottom.setBorderColor(color);
			
						content.rectangle(rectLineOrizzontaleBottom);
						content.stroke();
						
						ColumnText.showTextAligned(content, Element.ALIGN_LEFT, new Phrase(urlSitoRegione),
								x1, 15, 0);
					}
				}
				content.restoreState();
				
			}
		}

		pdfStamper.close();
		document.close();
		pdfReader.close();
		return result;
	}
	
	
	
	public File addFiligranaFrontespizio(final InputStream input, final InputStream inputLogo, final String denominazioneRegione, final Aoo aoo, final Ufficio ufficio)
			throws IOException, DocumentException {

		File result = File.createTempFile("withfiligrana_", ".pdf");
		PdfReader pdfReader = new PdfReader(input);

		Document document = new Document(PageSize.A4);
		document.open();

		Image imageLogo = Image.getInstance(IOUtils.toByteArray(inputLogo));
		imageLogo.scaleToFit(180, 120);

		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(result));

		BaseColor color = hex2Rgb("#000000");
		Font font = FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
		BaseFont bf = font.getBaseFont();

		if (pdfReader.getNumberOfPages() >= 1) {
			int i = 1;

			PdfContentByte content = pdfStamper.getOverContent(i);
			Rectangle rectanglePage = pdfReader.getPageSize(i);
			float offsetHeight = 100;

			float x1 = rectanglePage.getLeft() + document.leftMargin() + 20;
			float y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight() - offsetHeight;
			imageLogo.setAbsolutePosition(x1, y1);
			imageLogo.setTransparency(new int[] { 0xF0, 0xFF });

			content.saveState();

			content.addImage(imageLogo);

			content.beginText();
			content.setFontAndSize(bf, 22f);
			content.setColorFill(hex2Rgb("#000000"));
			content.setTextMatrix(x1 + 85, y1 + 50);
			content.showText("REGIONE");
			content.endText();

			content.beginText();
			content.setFontAndSize(bf, 22f);
			content.setColorFill(hex2Rgb("#000000"));
			content.setTextMatrix(x1 + 85, y1 + 30);
			content.showText(denominazioneRegione);
			content.endText();

			List<String> aoosIntestazione = new ArrayList<String>();
			
			if(aoo!=null && aoo.getAooPadre()!=null && aoo.getAooPadre().getDescrizione()!=null){
				aoosIntestazione.add(aoo.getAooPadre().getDescrizione());
			}
			
			if(aoo != null && aoo.getDescrizione()!=null){
				aoosIntestazione.add(aoo.getDescrizione());
			}
			
			if(ufficio!=null && ufficio.getDescrizione()!=null){
				aoosIntestazione.add(ufficio.getDescrizione());
			}
			if(aoosIntestazione!=null && aoosIntestazione.size()>0){
				int ydif = 0;
				for (int j = 0; j < aoosIntestazione.size(); j++) {
					String wrap = WordUtils.wrap(aoosIntestazione.get(j), 40);
					String[] wraps = wrap.split("\n");
					for (String wr : wraps) {
						content.beginText();
						content.setFontAndSize(bf, 12f);
						content.setColorFill(color);
						content.setTextMatrix(x1 + 85, y1 + ydif);
						content.showText(wr);
						content.endText();
						ydif = ydif - 15;
					}
				}
			}
			
//			Rectangle rectLineOrizzontaleTop = new Rectangle(x1 + 75, 500, 500, 498);
//			rectLineOrizzontaleTop.setBackgroundColor(color);
//			rectLineOrizzontaleTop.setBorderColor(color);
//
//			Rectangle rectLineOrizzontaleBottom = new Rectangle(x1 + 75, 400, 500, 398);
//			rectLineOrizzontaleBottom.setBackgroundColor(color);
//			rectLineOrizzontaleBottom.setBorderColor(color);
//
//			content.rectangle(rectLineOrizzontaleTop);
//			content.stroke();
//			content.rectangle(rectLineOrizzontaleBottom);
//			content.stroke();

			content.restoreState();
		}

		pdfStamper.close();
		document.close();
		pdfReader.close();
		return result;
	}

	public File addFiligranaLogoCentrale(final InputStream input, final InputStream inputLogo) throws IOException, DocumentException {

		File result = File.createTempFile("withfiligranalogocentrale_", ".pdf");
		PdfReader pdfReader = new PdfReader(input);

		Document document = new Document(PageSize.A4);
		document.open();

		Image imageLogo = Image.getInstance(IOUtils.toByteArray(inputLogo));
		imageLogo.scaleToFit(180, 120);
		FileOutputStream fos = new FileOutputStream(result);
		PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
		if (pdfReader.getNumberOfPages() >= 1) {
			PdfContentByte content = pdfStamper.getOverContent(1);
			Rectangle rectanglePage = pdfReader.getPageSize(1);

			float x1 = rectanglePage.getLeft() + rectanglePage.getWidth() / 2 - 50;
			float y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight();
			imageLogo.setAbsolutePosition(x1, y1);
			imageLogo.setTransparency(new int[] { 0xF0, 0xFF });

			content.saveState();
			content.addImage(imageLogo);
			content.restoreState();
		}

		pdfStamper.close();
		document.close();
		pdfReader.close();
		fos.close();
		return result;
	}
	
	public File addFiligranaDelibera(final InputStream input, final InputStream inputLogo) throws IOException, DocumentException {

		File result = File.createTempFile("withfiligranalogocentrale_", ".pdf");
		PdfReader pdfReader = new PdfReader(input);

		Document document = new Document(PageSize.A4);
		document.open();

		Image imageLogo = Image.getInstance(IOUtils.toByteArray(inputLogo));
		imageLogo.scaleToFit(180, 120);

		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(result));
		if (pdfReader.getNumberOfPages() >= 1) {
			PdfContentByte content = pdfStamper.getOverContent(1);
			Rectangle rectanglePage = pdfReader.getPageSize(1);

			float x1 = rectanglePage.getLeft() + rectanglePage.getWidth() / 2 - 50;
			float y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight();
			imageLogo.setAbsolutePosition(x1, y1);
			imageLogo.setTransparency(new int[] { 0xF0, 0xFF });

			content.saveState();
			content.addImage(imageLogo);
			content.restoreState();
		}

		pdfStamper.close();
		document.close();
		pdfReader.close();
		return result;
	}

	public File addFiligranaFrontespizioLogoCentrale(final InputStream input, final InputStream inputLogo, final String denominazioneRegione, final String descrizioneArea, final String descrizioneServizio)
			throws IOException, DocumentException {

		File result = File.createTempFile("withfiligrana_", ".pdf");
		PdfReader pdfReader = new PdfReader(input);

		Document document = new Document(PageSize.A4);
		document.open();

		Image imageLogo = Image.getInstance(IOUtils.toByteArray(inputLogo));
		imageLogo.scaleToFit(180, 120);

		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(result));

		BaseColor color = hex2Rgb("#000000");
		logger.debug("Colore" + "#000000");

		Font font = FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
		BaseFont bf = font.getBaseFont();

		if (pdfReader.getNumberOfPages() >= 1) {
			int i = 1;

			PdfContentByte content = pdfStamper.getOverContent(i);
			Rectangle rectanglePage = pdfReader.getPageSize(i);

			float x1 = rectanglePage.getLeft() + document.leftMargin() + 20;
			float y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight();
			imageLogo.setAbsolutePosition(x1, y1);
			imageLogo.setTransparency(new int[] { 0xF0, 0xFF });

			content.saveState();

			content.addImage(imageLogo);

			content.beginText();
			content.setFontAndSize(bf, 22f);
			content.setColorFill(hex2Rgb("#000000"));
			content.setTextMatrix(x1 + 85, y1 + 50);
			content.showText("REGIONE");
			content.endText();

			content.beginText();
			content.setFontAndSize(bf, 22f);
			content.setColorFill(hex2Rgb("#000000"));
			content.setTextMatrix(x1 + 85, y1 + 30);
			content.showText(denominazioneRegione);
			content.endText();

			String wrap = WordUtils.wrap(descrizioneArea, 40);
			String[] wraps = wrap.split("\n");

			int ydif = 0;

			for (String wr : wraps) {
				content.beginText();
				content.setFontAndSize(bf, 12f);
				content.setColorFill(color);
				content.setTextMatrix(x1 + 85, y1 + ydif);
				content.showText(wr);
				content.endText();
				ydif = ydif - 15;
			}
			
			wrap = WordUtils.wrap(descrizioneServizio, 40);
			wraps = wrap.split("\n");
			for (String wr : wraps) {
				content.beginText();
				content.setFontAndSize(bf, 12f);
				content.setColorFill(hex2Rgb("#000000"));
				content.setTextMatrix(x1 + 85, y1 + ydif);
				content.showText(wr);
				content.endText();
				ydif = ydif - 12;
			}

			Rectangle rectLineOrizzontaleTop = new Rectangle(x1 + 75, 500, 500, 498);
			rectLineOrizzontaleTop.setBackgroundColor(color);
			rectLineOrizzontaleTop.setBorderColor(color);

			Rectangle rectLineOrizzontaleBottom = new Rectangle(x1 + 75, 400, 500, 398);
			rectLineOrizzontaleBottom.setBackgroundColor(color);
			rectLineOrizzontaleBottom.setBorderColor(color);

			content.rectangle(rectLineOrizzontaleTop);
			content.stroke();
			content.rectangle(rectLineOrizzontaleBottom);
			content.stroke();

			content.restoreState();
		}

		if (pdfReader.getNumberOfPages() >= 2) {
			int i = 2;

			PdfContentByte content = pdfStamper.getOverContent(i);
			Rectangle rectanglePage = pdfReader.getPageSize(i);

			float x1 = rectanglePage.getLeft() + rectanglePage.getWidth() / 2 - 50;
			float y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight();
			imageLogo.setAbsolutePosition(x1, y1);
			imageLogo.setTransparency(new int[] { 0xF0, 0xFF });

			content.saveState();

			content.addImage(imageLogo);

			content.restoreState();
		}

		pdfStamper.close();
		document.close();
		pdfReader.close();
		return result;
	}

	public File addFiligranaOrdinegiorno(boolean base, 
			final InputStream input, final InputStream inputLogo, final InputStream inputLogoOdg, 
			final InputStream inputLogoIntestazione, final String organo) throws IOException, DocumentException {

		File result = File.createTempFile("withfiligrana_", ".pdf");
		PdfReader pdfReader = new PdfReader(input);
		Document document = new Document(PageSize.A4);
		document.open();

		Image imageLogo = Image.getInstance(IOUtils.toByteArray(inputLogo));
		imageLogo.scaleToFit(180, 120);
		Image imageLogoOdg = Image.getInstance(IOUtils.toByteArray(inputLogoOdg));
		imageLogoOdg.scalePercent(80);
//		Image imageLogoIntestazione = Image.getInstance(IOUtils.toByteArray(inputLogoIntestazione));
//		imageLogoIntestazione.scaleToFit(120, 180);
		
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(result));

		for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
			PdfContentByte content = pdfStamper.getOverContent(i);
			Rectangle rectanglePage = pdfReader.getPageSize(i);

			float x1 = rectanglePage.getLeft() + rectanglePage.getWidth() / 2 - 50;
			float y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight();
			imageLogo.setAbsolutePosition(x1, y1);
			imageLogo.setTransparency(new int[] { 0xF0, 0xFF });

			x1 = rectanglePage.getLeft() + rectanglePage.getWidth() / 2 - imageLogoOdg.getScaledWidth() / 2;
			y1 = y1 - imageLogo.getScaledHeight() - imageLogoOdg.getScaledHeight() + 50;
			imageLogoOdg.setAbsolutePosition(x1, y1);
			imageLogoOdg.setTransparency(new int[] { 0xF0, 0xFF });

			content.saveState();
			if (i == 1){
				content.addImage(imageLogo);
				content.saveState();
			}
				
			if(i == 2){
				x1 = rectanglePage.getLeft() + document.leftMargin() + 20;
				y1 = rectanglePage.getTop() - +document.topMargin() - imageLogo.getScaledHeight();
//				imageLogoIntestazione.setAbsolutePosition(x1, y1);
//				imageLogoIntestazione.setTransparency(new int[] { 0xF0, 0xFF });

				Font font = FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
				BaseFont bf = font.getBaseFont();
				content.saveState();

				
//				content.addImage(imageLogoIntestazione);


				int ydif = 25;
				content.beginText();
				content.setFontAndSize(bf, 10f);
				content.setTextMatrix(x1 + 175, y1 + ydif);
				content.showText(organo);
				content.endText();
				
				content.saveState();
				
				Rectangle rectLineOrizzontaleTop = new Rectangle(x1, y1 - 10, rectanglePage.getWidth() - document.leftMargin() - document.rightMargin(), y1 - 9);
				rectLineOrizzontaleTop.setBackgroundColor(hex2Rgb("#000000"));
				rectLineOrizzontaleTop.setBorderColor(hex2Rgb("#000000"));
				
				content.rectangle(rectLineOrizzontaleTop);
				content.stroke();
			}
			if (base && i == 1){
				content.addImage(imageLogoOdg);
				content.saveState();
			}
			content.restoreState();
		}

		pdfStamper.close();
		document.close();
		pdfReader.close();
		return result;
	}
	
	//SDL, DDL, DEL+COM
	private List<AttiOdg> ordinaListOdg(List<AttiOdg> listAttiOdg){
		List<AttiOdg> sortedList = new ArrayList<AttiOdg>();
		List<AttiOdg> sdl = new ArrayList<AttiOdg>();
		List<AttiOdg> ddl = new ArrayList<AttiOdg>();
		List<AttiOdg> delCom = new ArrayList<AttiOdg>();
		if(listAttiOdg!=null){
			for(AttiOdg aodg : listAttiOdg){
				if(aodg.getAtto().getTipoAtto().getCodice().equals("SDL")){
					sdl.add(aodg);
				}else if(aodg.getAtto().getTipoAtto().getCodice().equals("DDL")){
					ddl.add(aodg);
				}else if(aodg.getAtto().getTipoAtto().getCodice().equals("COM") || aodg.getAtto().getTipoAtto().getCodice().equals("DEL")){
					delCom.add(aodg);
				}
			}
		}
		
		/*
		 * 2018-06-26
		 * Il campo denominazioneRelatore è stato eliminato dalla tabella atto in quanto ritenuto non opportuno:
		 * probabilmente converrebbe usare un campo calcolato a runtime.
		 * 
		 * TODO occorre capire se serve continuare a usare quasto metodo ordinaListOdg e come implementare l'ordinamento tramite
		 * comparator, non avendo più a disposizione il campo denominazioneRelatore
		 */
//		Comparator<AttiOdg> attiOdgComparator = new Comparator<AttiOdg>(){
//            public int compare(AttiOdg ao1, AttiOdg ao2){
//            	if(ao1.getAtto().getDenominazioneRelatore().equalsIgnoreCase(ao2.getAtto().getDenominazioneRelatore())){
//            		if(ao1.getAtto().getAoo().getId().equals(ao2.getAtto().getAoo().getId())){
//            			return ao1.getAtto().getCodiceCifra().compareTo(ao2.getAtto().getCodiceCifra());
//            		}else{
//            			return ao1.getAtto().getAoo().getDescrizione().compareTo(ao2.getAtto().getAoo().getDescrizione());
//            		}
//            	}else if(ao1.getAtto().getDenominazioneRelatore().equalsIgnoreCase(PRESIDENTE_STR)){
//            		return -1;
//            	}else if(ao1.getAtto().getDenominazioneRelatore().equalsIgnoreCase(VICE_PRESIDENTE_STR)){
//            		if(ao2.getAtto().getDenominazioneRelatore().equalsIgnoreCase(PRESIDENTE_STR)){
//            			return 1;
//            		}else{
//            			return -1;
//            		}
//            	}else{
//            		return 1;
//            	}
//          }};
//		
//		Collections.sort(sdl, attiOdgComparator);
//		Collections.sort(ddl, attiOdgComparator);
//		Collections.sort(delCom, attiOdgComparator);
		
		sortedList.addAll(sdl);
		sortedList.addAll(ddl);
		sortedList.addAll(delCom);
		return sortedList;
	}
	
	//SDL, DDL, DEL+COM
	private List<AttiOdg> ordinaListOdgFuoriSacco(List<AttiOdg> listAttiOdg){
		List<AttiOdg> sortedList = new ArrayList<AttiOdg>();
		List<AttiOdg> sdl = new ArrayList<AttiOdg>();
		List<AttiOdg> ddl = new ArrayList<AttiOdg>();
		List<AttiOdg> delCom = new ArrayList<AttiOdg>();
		if(listAttiOdg!=null){
			for(AttiOdg aodg : listAttiOdg){
				if(aodg.getAtto().getTipoAtto().getCodice().equals("SDL")){
					sdl.add(aodg);
				}else if(aodg.getAtto().getTipoAtto().getCodice().equals("DDL")){
					ddl.add(aodg);
				}else if(aodg.getAtto().getTipoAtto().getCodice().equals("COM") || aodg.getAtto().getTipoAtto().getCodice().equals("DEL")){
					delCom.add(aodg);
				}
			}
		}
		
		/*
		 * 2018-06-26
		 * Il campo denominazioneRelatore è stato eliminato dalla tabella atto in quanto ritenuto non opportuno:
		 * probabilmente converrebbe usare un campo calcolato a runtime.
		 * 
		 * TODO occorre capire se serve continuare a usare quasto metodo ordinaListOdgFuoriSacco e come implementare l'ordinamento tramite
		 * comparator, non avendo più a disposizione il campo denominazioneRelatore
		 */
//		Comparator<AttiOdg> attiOdgComparator = new Comparator<AttiOdg>(){
//            public int compare(AttiOdg ao1, AttiOdg ao2){
//            	if(ao1.getAtto().getDenominazioneRelatore().equalsIgnoreCase(ao2.getAtto().getDenominazioneRelatore())){
//            		if(ao1.getAtto().getAoo().getId().equals(ao2.getAtto().getAoo().getId())){
//            			return ao1.getAtto().getCodiceCifra().compareTo(ao2.getAtto().getCodiceCifra());
//            		}else{
//            			return ao1.getAtto().getAoo().getDescrizione().compareTo(ao2.getAtto().getAoo().getDescrizione());
//            		}
//            	}else if(ao1.getAtto().getDenominazioneRelatore().equalsIgnoreCase(PRESIDENTE_STR)){
//            		return -1;
//            	}else if(ao1.getAtto().getDenominazioneRelatore().equalsIgnoreCase(VICE_PRESIDENTE_STR)){
//            		if(ao2.getAtto().getDenominazioneRelatore().equalsIgnoreCase(PRESIDENTE_STR)){
//            			return 1;
//            		}else{
//            			return -1;
//            		}
//            	}else{
//            		return 1;
//            	}
//          }};
//          
//          Comparator<AttiOdg> attiOdgComparatorFinal = new Comparator<AttiOdg>(){
//	            public int compare(AttiOdg ao1, AttiOdg ao2){
//	            	if(ao1.getAtto().getDenominazioneRelatore().equalsIgnoreCase(ao2.getAtto().getDenominazioneRelatore())){
//	            		if(ao1.getAtto().getAoo().getId().equals(ao2.getAtto().getAoo().getId())){
//	            			
//	            			if(ao1.getAtto().getTipoAtto().getId().longValue() == ao2.getAtto().getTipoAtto().getId().longValue()){
//	            				return ao1.getAtto().getCodiceCifra().compareTo(ao2.getAtto().getCodiceCifra());
//	            			}else{
//	            				
//	            				if(ao1.getAtto().getTipoAtto().getCodice().equalsIgnoreCase("SDL") || ao1.getAtto().getTipoAtto().getCodice().equalsIgnoreCase("SDL_A")){
//	            					return -1;
//	            				}else if(ao1.getAtto().getTipoAtto().getCodice().equalsIgnoreCase("DDL") || ao1.getAtto().getTipoAtto().getCodice().equalsIgnoreCase("DDL_A")){
//	            					if(ao2.getAtto().getTipoAtto().getCodice().equalsIgnoreCase("SDL") || ao2.getAtto().getTipoAtto().getCodice().equalsIgnoreCase("SDL_A")){
//	        	            			return 1;
//	        	            		}else{
//	        	            			return -1;
//	        	            		}
//	            				}else{
//	            					return 1;
//	            				}
//	            			}
//	            		}else{
//	            			return ao1.getAtto().getAoo().getDescrizione().compareTo(ao2.getAtto().getAoo().getDescrizione());
//	            		}
//	            	}else if(ao1.getAtto().getDenominazioneRelatore().equalsIgnoreCase(PRESIDENTE_STR)){
//	            		return -1;
//	            	}else if(ao1.getAtto().getDenominazioneRelatore().equalsIgnoreCase(VICE_PRESIDENTE_STR)){
//	            		if(ao2.getAtto().getDenominazioneRelatore().equalsIgnoreCase(PRESIDENTE_STR)){
//	            			return 1;
//	            		}else{
//	            			return -1;
//	            		}
//	            	}else{
//	            		return 1;
//	            	}
//	          }};
//		
//		Collections.sort(sdl, attiOdgComparator);
//		Collections.sort(ddl, attiOdgComparator);
//		Collections.sort(delCom, attiOdgComparator);
		
		sortedList.addAll(sdl);
		sortedList.addAll(ddl);
		sortedList.addAll(delCom);
		
//		Collections.sort(sortedList, attiOdgComparatorFinal);
		
		return sortedList;
	}

	public OdgPdfObject addListaOdg(boolean giacenza, List<AttiOdg> listAttiOdg, int tipoOdg, int startList) throws IOException, DocumentException {
		File fileOdg = File.createTempFile("withODG_", ".pdf");
		Document document = new Document(PageSize.A4);
		
		//effettuo ordinamento come da specifiche
//		listAttiOdg = this.ordinaListOdg(listAttiOdg);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileOdg));

		int current_index = startList;
		com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);

		document.open();
		float marginBottom = 60;
		float marginTop = 60;
		float marginLeft = 70;
		float marginRight = 70;
		document.setMargins(marginLeft, marginRight, marginTop, marginBottom);
		OdgPdfObject odg = new OdgPdfObject();
		writer.setPageEvent(odg);
		document.top(100);
		document.add(Chunk.NEWLINE);

		// base aggiunge gli argomenti preliminari
//		if (!giacenza && (tipoOdg == 1 || tipoOdg == 2)) {
//			Chunk argsPreliminari = new Chunk("ARGOMENTI PRELIMINARI", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
//			argsPreliminari.setUnderline(0.1f, -2f);
//			document.add(new Paragraph(argsPreliminari));
//			document.add(Chunk.NEWLINE);
//
//			list.add(new ListItem(new Chunk("Approvazione verbale seduta precedente.", FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL))));
//			document.add(list);
//			document.add(Chunk.NEWLINE);
//			list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
//			list.setFirst(2);
//			list.add(new ListItem(new Chunk("Comunicazioni del Presidente della Giunta.", FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL))));
//			document.add(list);
//			document.add(Chunk.NEWLINE);
//			current_index = 3;
//		}

		int current_sezione = 0;
		int current_parte = 0;
		long current_tipoAtto = 0L;
		String current_relatore = "";
		String current_assessore = "";
		String current_aoo_proponente = "";
		String current_avvocatura = "";
		boolean delCom = true;
		for (Iterator<AttiOdg> iterator = listAttiOdg.iterator(); iterator.hasNext();) {
			AttiOdg attiOdg = iterator.next();

			if (attiOdg.getSezione() > current_sezione) {
				current_sezione = attiOdg.getSezione();
				// reset
				current_parte = 0;
				current_tipoAtto = 0L;
				current_relatore = "";
				current_assessore = "";
				current_aoo_proponente = "";
				current_avvocatura = "";

				if (tipoOdg != 4) {
					Chunk sezione = null;
					if (current_sezione == 1)
						sezione = new Chunk("SEZIONE PRIMA - ARGOMENTI RINVIATI O NON TRATTATI IN SEDUTE PRECEDENTI", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
					if (current_sezione == 2)
						sezione = new Chunk("SEZIONE SECONDA - ARGOMENTI DI PRIMA ISCRIZIONE", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));

					sezione.setUnderline(0.1f, -2f);
					Paragraph p_sezione = new Paragraph(sezione);
					p_sezione.setAlignment(Element.ALIGN_CENTER);
					document.add(p_sezione);
					document.add(Chunk.NEWLINE);
				}
			}

			if (attiOdg.getParte() > current_parte) {
				current_parte = attiOdg.getParte();
				// reset
				current_tipoAtto = 0L;
				current_relatore = "";
				current_assessore = "";
				current_aoo_proponente = "";
				current_avvocatura = "";

				if (tipoOdg != 4) {
					Chunk parte = null;
					if (current_parte == 1)
						parte = new Chunk("PARTE PRIMA", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
					if (current_parte == 2)
						parte = new Chunk("PARTE SECONDA - ATTIVITA' DI CONTROLLO DELLA GIUNTA", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));

					Paragraph p_parte = new Paragraph(parte);
					p_parte.setAlignment(Element.ALIGN_CENTER);
					document.add(p_parte);
					document.add(Chunk.NEWLINE);
				}
			}
			
			if (attiOdg.getAtto().getTipoAtto().getId() != current_tipoAtto && delCom) {
				current_tipoAtto = attiOdg.getAtto().getTipoAtto().getId();
				// reset
				current_relatore = "";
				current_assessore = "";
				current_aoo_proponente = "";
				current_avvocatura = "";

				String codeTipoAtto = attiOdg.getAtto().getTipoAtto().getCodice();

				if (tipoOdg != 4) {
//					Chunk tipoAtto = null;
//					if (codeTipoAtto.equalsIgnoreCase("DEL") || codeTipoAtto.equalsIgnoreCase("DEL_A") || codeTipoAtto.equalsIgnoreCase("COM") || codeTipoAtto.equalsIgnoreCase("COM_A")){
//						tipoAtto = new Chunk("DELIBERE", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
//						delCom = false;
//					}
//					if (codeTipoAtto.equalsIgnoreCase("SDL") || codeTipoAtto.equalsIgnoreCase("SDL_A"))
//						tipoAtto = new Chunk("SCHEMI DI DISEGNI DI LEGGE", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
//					if (codeTipoAtto.equalsIgnoreCase("DDL") || codeTipoAtto.equalsIgnoreCase("DDL_A"))
//						tipoAtto = new Chunk("DISEGNI DI LEGGE", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));

//					tipoAtto.setUnderline(0.1f, -2f);
//					Paragraph p_tipoAtto = new Paragraph(tipoAtto);
//					p_tipoAtto.setAlignment(Element.ALIGN_CENTER);
//					document.add(p_tipoAtto);
//					document.add(Chunk.NEWLINE);
				}
			}

			/*
			 * FIXME occorre capire se è corretto usare denomiazioneRelatore per determinare la voce
			 * da stampare sul template: forse sarebbe meglio usare un campo che specifichi la tipologia di relatore
			 * 
			 * NB: questo codice era ripetuto più volte in questa classe, eventualmente occorre rifattorizzare!!!!
			 */
//			if (!attiOdg.getAtto().getDenominazioneRelatore().equalsIgnoreCase(current_relatore)) {
//				current_relatore = attiOdg.getAtto().getDenominazioneRelatore();
//				current_aoo_proponente = "";
//				current_avvocatura = "";
//
//				Chunk relatore = null;
//				if (current_relatore.equalsIgnoreCase(PRESIDENTE_STR)) {
//					relatore = new Chunk("Su Relazione del Presidente", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
//					odg.addPresidente();
//				}
//				if (current_relatore.equalsIgnoreCase(VICE_PRESIDENTE_STR)) {
//					relatore = new Chunk("Su Relazione del Vice Presidente", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
//					odg.addVicePresidente();
//				}
//
//				if (relatore != null) {
//					Paragraph p_relatore = new Paragraph(relatore);
//					p_relatore.setAlignment(Element.ALIGN_CENTER);
//					document.add(p_relatore);
//					document.add(Chunk.NEWLINE);
//				}
//			}

			if (current_relatore.equalsIgnoreCase("Assessore")) {
				String aooDescr = "";
				Atto atto = attoRepository.findOne(attiOdg.getAtto().getId());
				Iterable<SottoscrittoreAtto> sottoscrittoriAtto = sottoScrittoreAttoService.getSottoscrittoriByAttoId(atto.getId());
				
				if(sottoscrittoriAtto != null ){				
					
					for(SottoscrittoreAtto sott : sottoscrittoriAtto){
						if(
								sott.getProfilo().getAoo() != null && 
								atto.getAoo() != null && 
								//atto.getAoo().getId() == sott.getProfilo().getAoo().getId() && 
								sott.getQualificaProfessionale()!=null && 
								sott.getQualificaProfessionale().getDenominazione()!=null && 
								sott.getQualificaProfessionale().getDenominazione().toLowerCase().contains("assessore")){
							List<Assessorato> assessorati = assessoratoService.findByProfiloResponsabileId(sott.getProfilo().getId());
							if(assessorati.size() > 0){
								aooDescr = assessorati.get(0).getQualifica();
							}
							
							break;
						}
					}
						
				} 
				else if (attiOdg.getAtto().getAoo() != null) {
					Aoo current_aoo = attiOdg.getAtto().getAoo().getAooPadre() != null ? attiOdg.getAtto().getAoo().getAooPadre() : attiOdg.getAtto().getAoo();
					aooDescr = current_aoo.getDescrizione();
				}
				else if (attiOdg.getAtto().getDescrizioneArea() != null) {
					aooDescr = attiOdg.getAtto().getDescrizioneArea();
				}
				if (!current_assessore.equalsIgnoreCase(aooDescr)) {
					current_assessore = aooDescr;
					current_aoo_proponente = "";
					current_avvocatura = "";

					Chunk assessore = new Chunk("Su Relazione dell' " + current_assessore, FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
					odg.addAssessore(current_assessore);
					Paragraph p_assessore = new Paragraph(assessore);
					p_assessore.setAlignment(Element.ALIGN_CENTER);
					document.add(p_assessore);
					document.add(Chunk.NEWLINE);
				}
			}
			String aooDescr = "";

			if (attiOdg.getAtto().getAoo() != null)
				aooDescr = attiOdg.getAtto().getAoo().getDescrizione();
			else if (attiOdg.getAtto().getDescrizioneArea() != null)
				aooDescr = attiOdg.getAtto().getDescrizioneArea();

			if (!aooDescr.equalsIgnoreCase(current_aoo_proponente)) {
				current_aoo_proponente = aooDescr;
				current_avvocatura = "";

				Chunk aoo_proponente = new Chunk(current_aoo_proponente, FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
				aoo_proponente.setGenericTag(current_relatore + current_assessore + "=" + current_aoo_proponente);
				Paragraph p_aoo_proponente = new Paragraph(aoo_proponente);
				p_aoo_proponente.setAlignment(Element.ALIGN_LEFT);
				document.add(p_aoo_proponente);
				document.add(Chunk.NEWLINE);
			}

			if ("avvocatura".equalsIgnoreCase(attiOdg.getAtto().getUsoEsclusivo())) {

				if (!attiOdg.getAtto().getArgomentoOdg().getDescrizione().equalsIgnoreCase(current_avvocatura)) {
					current_avvocatura = attiOdg.getAtto().getArgomentoOdg().getDescrizione();
					Chunk avvocatura = new Chunk(current_avvocatura, FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
					Paragraph p_avvocatura = new Paragraph(avvocatura);
					p_avvocatura.setAlignment(Element.ALIGN_CENTER);
					document.add(p_avvocatura);
					document.add(Chunk.NEWLINE);
				}
			}

			if (tipoOdg != 4){
				list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
				list.setFirst(current_index++);
				ListItem listItem = new ListItem(new Chunk(attiOdg.getAtto().getCodiceCifra() + " - " + attiOdg.getAtto().getOggetto(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL)));
				listItem.setFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL));
				listItem.setAlignment(Element.ALIGN_JUSTIFIED);
				list.add(listItem);
				document.add(list);
				document.add(Chunk.NEWLINE);
			}else
			{
				Chunk chunk = new Chunk(String.valueOf(current_index) + " - " +attiOdg.getAtto().getCodiceCifra() + " - " + attiOdg.getAtto().getOggetto(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL));
				Paragraph p = new Paragraph(chunk);
				p.setAlignment(Element.ALIGN_JUSTIFIED);
				document.add(p);
				document.add(Chunk.NEWLINE);
				current_index++;
			}
		}

		document.close();
		odg.setBody(fileOdg);
		return odg;
	}
	
	
	public OdgPdfObject addListaOdgFuoriSacco(boolean giacenza, 
			List<AttiOdg> listAttiOdg, int tipoOdg, int startList,final InputStream inputLogo, 
			String dataSeduta, boolean sedutaOrdinaria, final String organo) throws IOException, DocumentException {
		
		File fileOdg = File.createTempFile("withODG_", ".pdf");
		Document document = new Document(PageSize.A4);
		
		//effettuo ordinamento come da specifiche
		listAttiOdg = this.ordinaListOdgFuoriSacco(listAttiOdg);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileOdg));

		int current_index = startList;
		com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);

		document.open();
		float marginBottom = 60;
		float marginTop = 60;
		float marginLeft = 70;
		float marginRight = 70;
		document.setMargins(marginLeft, marginRight, marginTop, marginBottom);
		OdgPdfObject odg = new OdgPdfObject();
		writer.setPageEvent(odg);
		document.top(100);
		document.add(Chunk.NEWLINE);
		
		Image imageLogo = Image.getInstance(IOUtils.toByteArray(inputLogo));
		imageLogo.scaleToFit(180, 120);
		imageLogo.setAlignment(Element.ALIGN_CENTER);

		
		//\<p th:style="${seduta.tipoSeduta != 2 ? 'color: #C8500A; text-align: center;': 'color: #6699ff; text-align: center;'}">
		//String color = sedutaOrdinaria?"#C8500A":"#6699ff";
		
		document.add(imageLogo);
		//font-family: Palace Script MT; font-size: 48pt;
		Chunk titoloEnte = new Chunk("Comune di Firenze");
		Font font = FontFactory.getFont(FONT_PALSCRI, BaseFont.CP1252,false, 48);
		if(sedutaOrdinaria){
			font.setColor(102,153,255);
		}else{
			font.setColor(200,80,10);
		}
		titoloEnte.setFont(font);
		Paragraph pEnte = new Paragraph(titoloEnte);
		pEnte.setAlignment(Element.ALIGN_CENTER);
		
		document.add(pEnte);
		
		
		//font-family: Palace Script MT; font-size: 28pt;
		Chunk segretariato = new Chunk(organo);
		Font font2 = FontFactory.getFont(FONT_PALSCRI, BaseFont.CP1252,false, 28);
		if(sedutaOrdinaria){
			font2.setColor(102,153,255);
		}else{
			font2.setColor(200,80,10);
		}
		segretariato.setFont(font2);
		Paragraph pSegretariato = new Paragraph(segretariato);
		pSegretariato.setAlignment(Element.ALIGN_CENTER);
		
		document.add(pSegretariato);
		document.add(Chunk.NEWLINE);
		//font-family: Times New Roman; font-size: 10pt; color: #000; text-align: center; text-decoration: underline;
		Chunk seduta = new Chunk("SEDUTA DI GIUNTA DEL "+dataSeduta);
		seduta.setFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.NORMAL));
		seduta.setUnderline(0.1f, -2f);
		Paragraph pSeduta = new Paragraph(seduta);
		pSeduta.setAlignment(Element.ALIGN_CENTER);
		
		document.add(pSeduta);
		document.add(Chunk.NEWLINE);
		//font-family: Times New Roman; font-size: 10pt; font-weight: bold; color: #000; text-align: center;
		Chunk fuoriSacco = new Chunk("\"FUORI SACCO\"");
		fuoriSacco.setFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
		Paragraph pFuoriSacco = new Paragraph(fuoriSacco);
		pFuoriSacco.setAlignment(Element.ALIGN_CENTER);
		
		
		document.add(pFuoriSacco);
		document.add(Chunk.NEWLINE);
		

		// base aggiunge gli argomenti preliminari
		if (!giacenza && (tipoOdg == 1 || tipoOdg == 2)) {
			Chunk argsPreliminari = new Chunk("ARGOMENTI PRELIMINARI", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
			argsPreliminari.setUnderline(0.1f, -2f);
			document.add(new Paragraph(argsPreliminari));
			document.add(Chunk.NEWLINE);

			list.add(new ListItem(new Chunk("Approvazione verbale seduta precedente.", FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL))));
			document.add(list);
			document.add(Chunk.NEWLINE);
			list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
			list.setFirst(2);
			list.add(new ListItem(new Chunk("Comunicazioni del Presidente della Giunta.", FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL))));
			document.add(list);
			document.add(Chunk.NEWLINE);
			current_index = 3;
		}

		int current_sezione = 0;
		int current_parte = 0;
		long current_tipoAtto = 0L;
		String current_relatore = "";
		String current_assessore = "";
		String current_aoo_proponente = "";
		String current_avvocatura = "";
		String ultimoRelatore ="";
//		String ultimaAoo ="";
		boolean delCom = true;
		for (Iterator<AttiOdg> iterator = listAttiOdg.iterator(); iterator.hasNext();) {
			AttiOdg attiOdg = iterator.next();

			if (attiOdg.getSezione() > current_sezione) {
				current_sezione = attiOdg.getSezione();
				// reset
				current_parte = 0;
				current_tipoAtto = 0L;
				current_relatore = "";
				current_assessore = "";
				current_aoo_proponente = "";
				current_avvocatura = "";

				if (tipoOdg != 4) {
					Chunk sezione = null;
					if (current_sezione == 1)
						sezione = new Chunk("SEZIONE PRIMA - ARGOMENTI RINVIATI O NON TRATTATI IN SEDUTE PRECEDENTI", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
					if (current_sezione == 2)
						sezione = new Chunk("SEZIONE SECONDA - ARGOMENTI DI PRIMA ISCRIZIONE", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));

					sezione.setUnderline(0.1f, -2f);
					Paragraph p_sezione = new Paragraph(sezione);
					p_sezione.setAlignment(Element.ALIGN_CENTER);
					document.add(p_sezione);
					document.add(Chunk.NEWLINE);
				}
			}

			if (attiOdg.getParte() > current_parte) {
				current_parte = attiOdg.getParte();
				// reset
				current_tipoAtto = 0L;
				current_relatore = "";
				current_assessore = "";
				current_aoo_proponente = "";
				current_avvocatura = "";

				if (tipoOdg != 4) {
					Chunk parte = null;
					if (current_parte == 1)
						parte = new Chunk("PARTE PRIMA", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
					if (current_parte == 2)
						parte = new Chunk("PARTE SECONDA - ATTIVITA' DI CONTROLLO DELLA GIUNTA", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));

					Paragraph p_parte = new Paragraph(parte);
					p_parte.setAlignment(Element.ALIGN_CENTER);
					document.add(p_parte);
					document.add(Chunk.NEWLINE);
				}
			}
			
			if (attiOdg.getAtto().getTipoAtto().getId() != current_tipoAtto && delCom) {
				current_tipoAtto = attiOdg.getAtto().getTipoAtto().getId();
				// reset
				current_relatore = "";
				current_assessore = "";
				//current_aoo_proponente = "";
				current_avvocatura = "";

				String codeTipoAtto = attiOdg.getAtto().getTipoAtto().getCodice();

				if (tipoOdg != 4) {
					Chunk tipoAtto = null;
					if (codeTipoAtto.equalsIgnoreCase("DEL") || codeTipoAtto.equalsIgnoreCase("DEL_A") || codeTipoAtto.equalsIgnoreCase("COM") || codeTipoAtto.equalsIgnoreCase("COM_A")){
						tipoAtto = new Chunk("DELIBERE", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
						delCom = false;
					}
					if (codeTipoAtto.equalsIgnoreCase("SDL") || codeTipoAtto.equalsIgnoreCase("SDL_A"))
						tipoAtto = new Chunk("SCHEMI DI DISEGNI DI LEGGE", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
					if (codeTipoAtto.equalsIgnoreCase("DDL") || codeTipoAtto.equalsIgnoreCase("DDL_A"))
						tipoAtto = new Chunk("DISEGNI DI LEGGE", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));

					tipoAtto.setUnderline(0.1f, -2f);
					Paragraph p_tipoAtto = new Paragraph(tipoAtto);
					p_tipoAtto.setAlignment(Element.ALIGN_CENTER);
					document.add(p_tipoAtto);
					document.add(Chunk.NEWLINE);
				}
			}

			/*
			 * FIXME occorre capire se è corretto usare denomiazioneRelatore per determinare la voce
			 * da stampare sul template: forse sarebbe meglio usare un campo che specifichi la tipologia di relatore
			 * 
			 * NB: questo codice era ripetuto più volte in questa classe, eventualmente occorre rifattorizzare!!!!
			 */
//			if (!attiOdg.getAtto().getDenominazioneRelatore().equalsIgnoreCase(current_relatore)) {
//				current_relatore = attiOdg.getAtto().getDenominazioneRelatore();
//				//current_aoo_proponente = "";
//				current_avvocatura = "";
//				
//
//				Chunk relatore = null;
//				if (current_relatore.equalsIgnoreCase(PRESIDENTE_STR)) {
//					if(!current_relatore.equalsIgnoreCase(ultimoRelatore)){
//						relatore = new Chunk("Su Relazione del Presidente", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
//						odg.addPresidente();
//						ultimoRelatore=PRESIDENTE_STR;
//						current_aoo_proponente = "";
//					}
//				}
//				if (current_relatore.equalsIgnoreCase(VICE_PRESIDENTE_STR)) {
//					if(!current_relatore.equalsIgnoreCase(ultimoRelatore)){
//						relatore = new Chunk("Su Relazione del Vice Presidente", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
//						odg.addVicePresidente();
//						ultimoRelatore=VICE_PRESIDENTE_STR;
//						current_aoo_proponente = "";
//					}
//				}
//
//				if (relatore != null) {
//					Paragraph p_relatore = new Paragraph(relatore);
//					p_relatore.setAlignment(Element.ALIGN_CENTER);
//					document.add(p_relatore);
//					document.add(Chunk.NEWLINE);
//				}
//			}

			if (current_relatore.equalsIgnoreCase("Assessore")) {
				String aooDescr = "";
				Atto atto = attoRepository.findOne(attiOdg.getAtto().getId());
				Iterable<SottoscrittoreAtto> sottoscrittoriAtto = sottoScrittoreAttoService.getSottoscrittoriByAttoId(atto.getId());
				
				if(sottoscrittoriAtto != null ){				
					
					for(SottoscrittoreAtto sott : sottoscrittoriAtto){
					if(
								sott.getProfilo().getAoo() != null && 
								atto.getAoo() != null && 
							//	atto.getAoo().getId() == sott.getProfilo().getAoo().getId() && 
								sott.getQualificaProfessionale()!=null && 
								sott.getQualificaProfessionale().getDenominazione()!=null && 
								sott.getQualificaProfessionale().getDenominazione().toLowerCase().contains("assessore")){
							List<Assessorato> assessorati = assessoratoService.findByProfiloResponsabileId(sott.getProfilo().getId());
							if(assessorati.size() > 0){
								aooDescr = assessorati.get(0).getQualifica();
							}
							
							break;
						}
					}
						
				} else	if (attiOdg.getAtto().getAoo() != null) {
					Aoo current_aoo = attiOdg.getAtto().getAoo().getAooPadre() != null ? attiOdg.getAtto().getAoo().getAooPadre() : attiOdg.getAtto().getAoo();
					aooDescr = current_aoo.getDescrizione();
				}
				else if (attiOdg.getAtto().getDescrizioneArea() != null) {
					aooDescr = attiOdg.getAtto().getDescrizioneArea();
				}
				if (!current_assessore.equalsIgnoreCase(aooDescr)) {
					current_assessore = aooDescr;
					current_aoo_proponente = "";
					current_avvocatura = "";
					if(!current_assessore.equalsIgnoreCase(ultimoRelatore)){
						Chunk assessore = new Chunk("Su Relazione dell' " + current_assessore, FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
						ultimoRelatore  = current_assessore;
						odg.addAssessore(current_assessore);
						Paragraph p_assessore = new Paragraph(assessore);
						p_assessore.setAlignment(Element.ALIGN_CENTER);
						document.add(p_assessore);
						document.add(Chunk.NEWLINE);
						current_aoo_proponente = "";
					}
				}
			}
			String aooDescr = "";

			if (attiOdg.getAtto().getAoo() != null)
				aooDescr = attiOdg.getAtto().getAoo().getDescrizione();
			else if (attiOdg.getAtto().getDescrizioneArea() != null)
				aooDescr = attiOdg.getAtto().getDescrizioneArea();

			if (!aooDescr.equalsIgnoreCase(current_aoo_proponente)) {
				current_aoo_proponente = aooDescr;
				current_avvocatura = "";

				Chunk aoo_proponente = new Chunk(current_aoo_proponente, FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
				aoo_proponente.setGenericTag(current_relatore + current_assessore + "=" + current_aoo_proponente);
				Paragraph p_aoo_proponente = new Paragraph(aoo_proponente);
				p_aoo_proponente.setAlignment(Element.ALIGN_LEFT);
				document.add(p_aoo_proponente);
				document.add(Chunk.NEWLINE);
			}

			if ("avvocatura".equalsIgnoreCase(attiOdg.getAtto().getUsoEsclusivo())) {

				if (!attiOdg.getAtto().getArgomentoOdg().getDescrizione().equalsIgnoreCase(current_avvocatura)) {
					current_avvocatura = attiOdg.getAtto().getArgomentoOdg().getDescrizione();
					Chunk avvocatura = new Chunk(current_avvocatura, FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
					Paragraph p_avvocatura = new Paragraph(avvocatura);
					p_avvocatura.setAlignment(Element.ALIGN_CENTER);
					document.add(p_avvocatura);
					document.add(Chunk.NEWLINE);
				}
			}

			if (tipoOdg != 4){
				list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
				list.setFirst(current_index++);
				ListItem listItem = new ListItem(new Chunk(attiOdg.getAtto().getCodiceCifra() + " - " + attiOdg.getAtto().getOggetto(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL)));
				listItem.setFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL));
				listItem.setAlignment(Element.ALIGN_JUSTIFIED);
				list.add(listItem);
				document.add(list);
				document.add(Chunk.NEWLINE);
			}else
			{
				Chunk chunk = new Chunk(String.valueOf(current_index) + " - " +attiOdg.getAtto().getCodiceCifra() + " - " + attiOdg.getAtto().getOggetto(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL));
				Paragraph p = new Paragraph(chunk);
				p.setAlignment(Element.ALIGN_JUSTIFIED);
				document.add(p);
				document.add(Chunk.NEWLINE);
				current_index++;
			}
		}

		document.close();
		odg.setBody(fileOdg);
		return odg;
	}
	
	public List<RigaFuoriSaccoDto> getListRigaFuoriSacco(boolean giacenza, List<AttiOdg> listAttiOdg, int tipoOdg, int startList) throws IOException, DocumentException {
		File fileOdg = File.createTempFile("withODG_", ".pdf");
		Document document = new Document(PageSize.A4);
		List<RigaFuoriSaccoDto> out = new ArrayList<RigaFuoriSaccoDto>();
		//effettuo ordinamento come da specifiche
		listAttiOdg = this.ordinaListOdg(listAttiOdg);

		int current_index = startList;
		com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);

		int current_sezione = 0;
		int current_parte = 0;
		long current_tipoAtto = 0L;
		String current_relatore = "";
		String current_assessore = "";
		String current_aoo_proponente = "";
		String current_avvocatura = "";
		boolean delCom = true;
		for (Iterator<AttiOdg> iterator = listAttiOdg.iterator(); iterator.hasNext();) {
			AttiOdg attiOdg = iterator.next();

			if (attiOdg.getSezione() > current_sezione) {
				current_sezione = attiOdg.getSezione();
				// reset
				current_parte = 0;
				current_tipoAtto = 0L;
				current_relatore = "";
				current_assessore = "";
				current_aoo_proponente = "";
				current_avvocatura = "";
			}

			if (attiOdg.getParte() > current_parte) {
				current_parte = attiOdg.getParte();
				// reset
				current_tipoAtto = 0L;
				current_relatore = "";
				current_assessore = "";
				current_aoo_proponente = "";
				current_avvocatura = "";
			}
			
			if (attiOdg.getAtto().getTipoAtto().getId() != current_tipoAtto && delCom) {
				current_tipoAtto = attiOdg.getAtto().getTipoAtto().getId();
				// reset
				current_relatore = "";
				current_assessore = "";
				current_aoo_proponente = "";
				current_avvocatura = "";
			}

			/*
			 * FIXME occorre capire se è corretto usare denomiazioneRelatore per determinare la voce
			 * da stampare sul template: forse sarebbe meglio usare un campo che specifichi la tipologia di relatore
			 * 
			 * NB: questo codice era ripetuto più volte in questa classe, eventualmente occorre rifattorizzare!!!!
			 */
//			if (!attiOdg.getAtto().getDenominazioneRelatore().equalsIgnoreCase(current_relatore)) {
//				current_relatore = attiOdg.getAtto().getDenominazioneRelatore();
//				current_aoo_proponente = "";
//				current_avvocatura = "";
//
//				String relatoreStringa = "";
//				if (current_relatore.equalsIgnoreCase(PRESIDENTE_STR)) {
//					relatoreStringa = "Su Relazione del Presidente";
//				}
//				if (current_relatore.equalsIgnoreCase(VICE_PRESIDENTE_STR)) {
//					relatoreStringa = "Su Relazione del Vice Presidente";
//				}
//
//				if (!relatoreStringa.equals("")) {
//					RigaFuoriSaccoDto riga = new RigaFuoriSaccoDto();
//					riga.setTesto(relatoreStringa);
//					riga.setTipo(1);
//					out.add(riga);
//				}
//			}

			if (current_relatore.equalsIgnoreCase("Assessore")) {
				String aooDescr = "";
				Atto atto = attoRepository.findOne(attiOdg.getAtto().getId());
				Iterable<SottoscrittoreAtto> sottoscrittoriAtto = sottoScrittoreAttoService.getSottoscrittoriByAttoId(atto.getId());
				
				if(sottoscrittoriAtto != null ){				
					
					for(SottoscrittoreAtto sott : sottoscrittoriAtto){
						if(
								sott.getProfilo().getAoo() != null && 
								atto.getAoo() != null && 
								//atto.getAoo().getId() == sott.getProfilo().getAoo().getId() && 
								sott.getQualificaProfessionale()!=null && 
								sott.getQualificaProfessionale().getDenominazione()!=null && 
								sott.getQualificaProfessionale().getDenominazione().toLowerCase().contains("assessore")){
							List<Assessorato> assessorati = assessoratoService.findByProfiloResponsabileId(sott.getProfilo().getId());
							if(assessorati.size() > 0){
								aooDescr = assessorati.get(0).getQualifica();
							}
							
							break;
						}
					}
						
				} else if (attiOdg.getAtto().getAoo() != null) {
					Aoo current_aoo = attiOdg.getAtto().getAoo().getAooPadre() != null ? attiOdg.getAtto().getAoo().getAooPadre() : attiOdg.getAtto().getAoo();
					aooDescr = current_aoo.getDescrizione();
				}
				else if (attiOdg.getAtto().getDescrizioneArea() != null) {
					aooDescr = attiOdg.getAtto().getDescrizioneArea();
				}
				if (!current_assessore.equalsIgnoreCase(aooDescr)) {
					current_assessore = aooDescr;
					current_aoo_proponente = "";
					current_avvocatura = "";

					String assessoreStringa ="Su Relazione dell' " + current_assessore;
					RigaFuoriSaccoDto riga = new RigaFuoriSaccoDto();
					riga.setTesto(assessoreStringa);
					riga.setTipo(1);
					out.add(riga);
				}
			}
			String aooDescr = "";

			if (attiOdg.getAtto().getAoo() != null)
				aooDescr = attiOdg.getAtto().getAoo().getDescrizione();
			else if (attiOdg.getAtto().getDescrizioneArea() != null)
				aooDescr = attiOdg.getAtto().getDescrizioneArea();

			if (!aooDescr.equalsIgnoreCase(current_aoo_proponente)) {
				current_aoo_proponente = aooDescr;
				current_avvocatura = "";

				String aoo_proponenteString = current_aoo_proponente;
				RigaFuoriSaccoDto riga = new RigaFuoriSaccoDto();
				riga.setTesto(aoo_proponenteString);
				riga.setTipo(2);
				out.add(riga);
			}

			if ("avvocatura".equalsIgnoreCase(attiOdg.getAtto().getUsoEsclusivo())) {

				if (!attiOdg.getAtto().getArgomentoOdg().getDescrizione().equalsIgnoreCase(current_avvocatura)) {
					current_avvocatura = attiOdg.getAtto().getArgomentoOdg().getDescrizione();
					String avvocaturaString = current_avvocatura;
					RigaFuoriSaccoDto riga = new RigaFuoriSaccoDto();
					riga.setTesto(avvocaturaString);
					riga.setTipo(1);
					out.add(riga);
				}
			}

			String atto = String.valueOf(current_index) + " - " +attiOdg.getAtto().getCodiceCifra() + " - " + attiOdg.getAtto().getOggetto();
			RigaFuoriSaccoDto riga = new RigaFuoriSaccoDto();
			riga.setTesto(atto);
			riga.setTipo(3);
			out.add(riga);
			current_index++;
		}
		return out;
	}
	
	

	@SuppressWarnings("rawtypes")
	public File addIndiceAnalitico(OdgPdfObject odg) throws IOException, DocumentException {
		File fileToc = File.createTempFile("withTOC_", ".pdf");
		Document document = new Document(PageSize.A4);
//		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileToc));
		document.open();

		Chunk indice = new Chunk("INDICE ANALITICO", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
		indice.setUnderline(0.1f, -2f);
		document.add(new Paragraph(indice));
		document.add(Chunk.NEWLINE);

		List<String> relatori = odg.getTitoli();
		if (relatori.contains("PRESIDENTE")) {
			document.add(printTitolo("PRESIDENTE"));
			List<Struttura> strutture = odg.getTitoli_strutture().get("PRESIDENTE");
			Collections.sort(strutture);
			for (Iterator iterator = strutture.iterator(); iterator.hasNext();) {
				Struttura struttura = (Struttura) iterator.next();
				document.add(printStruttura(struttura.getAoo(), struttura.getPagine()));
			}
			relatori.remove("PRESIDENTE");
		}
		if (relatori.contains("VICEPRESIDENTE")) {
			document.add(printTitolo("VICEPRESIDENTE"));
			List<Struttura> strutture = odg.getTitoli_strutture().get("VICEPRESIDENTE");
			Collections.sort(strutture);
			for (Iterator iterator = strutture.iterator(); iterator.hasNext();) {
				Struttura struttura = (Struttura) iterator.next();
				document.add(printStruttura(struttura.getAoo(), struttura.getPagine()));
			}
			relatori.remove("VICEPRESIDENTE");
		}

		Collections.sort(relatori);
		for (Iterator iterator = relatori.iterator(); iterator.hasNext();) {
			String relatore = (String) iterator.next();
			document.add(printTitolo(relatore));
			List<Struttura> strutture = odg.getTitoli_strutture().get(("ASSESSORE "+relatore).toUpperCase().replaceAll("\\s+", ""));
			Collections.sort(strutture);
			for (Iterator iterator2 = strutture.iterator(); iterator2.hasNext();) {
				Struttura struttura = (Struttura) iterator2.next();
				document.add(printStruttura(struttura.getAoo(), struttura.getPagine()));
			}
		}

		document.close();
		return fileToc;
	}
	
	public File addListaSottoscrittoriResoconto(SedutaGiunta seduta) throws IOException, DocumentException{
		File fileSott = File.createTempFile("withRES_", ".pdf");
		Document document = new Document(PageSize.A4);
//		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileSott));
		document.open();
//		document.top(100);
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
//		table.setSpacingAfter(10f);	
		
		for(SottoscrittoreSedutaGiunta sottoscrittore : seduta.getSottoscrittoriresoconto()){
			PdfPCell cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			
			PdfPCell content = new PdfPCell();
			content.setBorder(Rectangle.NO_BORDER);
			content.setHorizontalAlignment(Element.ALIGN_CENTER);
			
			Phrase contentPhrase = new Phrase();
			contentPhrase.setFont(FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL));
			String qualificaProfessionale = "";
			if(sottoscrittore.getQualificaProfessionale() != null){
				qualificaProfessionale = sottoscrittore.getQualificaProfessionale().getDenominazione();
			}
			table.addCell(cell);
			table.addCell(cell);
			contentPhrase.add(qualificaProfessionale + "\n\n" + sottoscrittore.getProfilo().getUtente().getNome() +  " " + sottoscrittore.getProfilo().getUtente().getCognome() + "\n\n");
			content.setPhrase(contentPhrase);
			table.addCell(content);
			
		}
		document.add(table);
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		
		PdfPTable tablebottom = new PdfPTable(1);
		tablebottom.setWidthPercentage(100);
		
		PdfPCell contentbottom = new PdfPCell();
		contentbottom.setBorder(Rectangle.NO_BORDER);
		contentbottom.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		Phrase contentPhraseB = new Phrase();
		contentPhraseB.setFont(FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL));
		contentPhraseB.add("Documento informatico firmato digitalmente ai sensi del Testo Unico D.P.R. 28 dicembre 2000, n. 445 e del D.Lgs 7 marzo 2005, n. 82 e norme collegate che sostituisce il testo cartaceo e la firma autografa.");
		
		contentbottom.setPhrase(contentPhraseB);
		tablebottom.addCell(contentbottom);
		
		document.add(tablebottom);
		document.close();
		
		return fileSott;
	}

	public OdgPdfObject addListaResocontoOdg(Integer versioneCompleta, List<AttiOdg> listAttiOdg) throws IOException, DocumentException {
		File fileOdg = File.createTempFile("withODG_", ".pdf");
		Document document = new Document(PageSize.A4);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileOdg));

		int current_index = 1;
		com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);

		document.open();
		OdgPdfObject resoconto = new OdgPdfObject();
		writer.setPageEvent(resoconto);
		document.top(100);
		document.add(Chunk.NEWLINE);

		String current_esito = null;
		String current_aoo_proponente = "";

		// ordinamento liste
		Map<String, List<AttiOdg>> map = new HashMap<String, List<AttiOdg>>();
		for (AttiOdg attiOdg : listAttiOdg) {
			String key = attiOdg.getEsito() != null ? attiOdg.getEsito().toUpperCase() : "";
			List<AttiOdg> _list = null;
			if (key.equals("APPROVATO_CON_MODIFICA")) {
				key = "APPROVATO";
			}

			if (key.equals("ADOTTATO_CON_MODIFICA")) {
				key = "ADOTTATO";
			}

			if (map.containsKey(key))
				_list = map.get(key);
			else
				_list = new ArrayList<AttiOdg>();

			_list.add(attiOdg);
			map.put(key, _list);
		}
		// reset and reorder
		listAttiOdg = new ArrayList<AttiOdg>();

		List<AttiOdg> approvati = map.get("APPROVATO");
		if (approvati != null) {
			listAttiOdg.addAll(approvati);
		}

		List<AttiOdg> adottato = map.get("ADOTTATO");
		if (adottato != null) {
			listAttiOdg.addAll(adottato);
		}

		List<AttiOdg> presaDAtto = map.get("PRESA_D_ATTO");
		if (presaDAtto != null) {
			listAttiOdg.addAll(presaDAtto);
		}

		List<AttiOdg> verbalizzato = map.get("VERBALIZZATO");
		if (verbalizzato != null) {
			listAttiOdg.addAll(verbalizzato);
		}
		
		Collections.sort(listAttiOdg);

		List<AttiOdg> rinviati = new ArrayList<AttiOdg>();
		List<AttiOdg> ritirati = new ArrayList<AttiOdg>();
		if (versioneCompleta == 1) {
			List<AttiOdg> rinviatos = map.get("RINVIATO");
			if(rinviatos!=null){
				rinviati.addAll(rinviatos);
			}
			List<AttiOdg> nonTrattati = map.get("NON_TRATTATO");
			if(nonTrattati!=null){
				rinviati.addAll(nonTrattati);
			}
			
			if (rinviati != null) {
				Collections.sort(rinviati);
				listAttiOdg.addAll(rinviati);
			}
			ritirati = map.get("RITIRATO");
			if (ritirati != null) {
				Collections.sort(ritirati);
				listAttiOdg.addAll(ritirati);
			}
		}
		//questi esiti devono essere trattati tutti come "Approvati" - Mappa di codice/nomeleggibile
		Map<String, String> esitiApprovati = new HashMap<String, String>();
		esitiApprovati.put("approvato", "Approvato");
		esitiApprovati.put("adottato", "Approvato");
		esitiApprovati.put("presa_d_atto", "Presa d\'Atto");
		esitiApprovati.put("verbalizzato", "Verbalizzato");
		
		//questi esiti devono essere trattati tutti come "Approvati" - Mappa di codice/nomeleggibile
		Map<String, String> esitiRinviati = new HashMap<String, String>();
		esitiRinviati.put("rinviato", "Rinviato");
		esitiRinviati.put("non_trattato", "Non Trattato");
		
		for (Iterator<AttiOdg> iterator = listAttiOdg.iterator(); iterator.hasNext();) {
			AttiOdg attiOdg = iterator.next();

			if (current_esito==null || 
					 (esitiApprovati.keySet().contains(attiOdg.getEsito().toLowerCase()) && !current_esito.equalsIgnoreCase("approvati")) ||
					 (esitiRinviati.keySet().contains(attiOdg.getEsito().toLowerCase()) && !current_esito.equalsIgnoreCase("rinviati")) ||
					 (attiOdg.getEsito().toLowerCase().equalsIgnoreCase("ritirato") && !current_esito.equalsIgnoreCase("ritirati") ) 
				) {
				
				current_esito = attiOdg.getEsito();
				if(esitiApprovati.keySet().contains(current_esito.toLowerCase())){
					current_esito = "approvati";
				}else if(esitiRinviati.keySet().contains(current_esito.toLowerCase())){
					current_esito = "rinviati";
				}else{
					current_esito = "ritirati";
				}
				current_aoo_proponente = "";

				resoconto.addEsito(current_esito);
				if (versioneCompleta == 1) {
					Chunk esito = new Chunk(current_esito.toUpperCase(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD));
					esito.setGenericTag(current_esito + "=");
					esito.setUnderline(0.1f, -2f);
					Paragraph p_esito = new Paragraph(esito);
					p_esito.setAlignment(Element.ALIGN_CENTER);
					document.add(p_esito);
					document.add(Chunk.NEWLINE);
				}

			}

			if (!attiOdg.getAtto().getAoo().getDescrizione().equalsIgnoreCase(current_aoo_proponente)) {
				current_aoo_proponente = attiOdg.getAtto().getAoo().getDescrizione();

				Chunk aoo_proponente = new Chunk(current_aoo_proponente, FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
				aoo_proponente.setGenericTag(current_esito + "=" + current_aoo_proponente);
				Paragraph p_aoo_proponente = new Paragraph(aoo_proponente);
				p_aoo_proponente.setAlignment(Element.ALIGN_CENTER);
				document.add(p_aoo_proponente);
				document.add(Chunk.NEWLINE);
			}

			list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
			list.setFirst(current_index++);
			String dopoOggetto = "(" + this.calcolaStringaAfterOggetto(attiOdg, esitiApprovati) + ")";
			ListItem listItem = new ListItem(new Chunk(attiOdg.getAtto().getCodiceCifra() + " - " + attiOdg.getAtto().getOggetto() + " " + dopoOggetto, FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL)));
			listItem.setFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL));
			listItem.setAlignment(Element.ALIGN_JUSTIFIED);
			list.add(listItem);
			document.add(list);
			document.add(Chunk.NEWLINE);
		}

		document.close();
		resoconto.setBody(fileOdg);
		return resoconto;
	}
	
	private String calcolaStringaAfterOggetto(AttiOdg aOdg, Map<String, String> esitiApprovati){
		String stringa = "";
		if(aOdg.getEsito()!=null){
			if(aOdg.getEsito().toLowerCase().equals("rinviato")){
				stringa = "Rinviato";
			}else if(aOdg.getEsito().toLowerCase().equals("non_trattato")){
				stringa = "Non Trattato";
			}else if(aOdg.getEsito().toLowerCase().equals("ritirato")){
				stringa = "Ritirato";
			}else if(esitiApprovati.keySet().contains(aOdg.getEsito().toLowerCase())){
				//DEL APPROVATE
				if(aOdg.getAtto().getTipoAtto().getCodice().equalsIgnoreCase("del")){
					String numeroAdozione = aOdg.getAtto().getNumeroAdozione() != null ? aOdg.getAtto().getNumeroAdozione() : " ";
					stringa = "n." + numeroAdozione;
					//se fuorisacco
					if(aOdg.getOrdineGiorno().getTipoOdg().getId().equals(4L)){
						stringa += "/fuori sacco";
					}
				}else if(aOdg.getAtto().getTipoAtto().getCodice().equalsIgnoreCase("sdl") || aOdg.getAtto().getTipoAtto().getCodice().equalsIgnoreCase("ddl")){
					//SDL E DDL APPROVATI
					stringa = esitiApprovati.get(aOdg.getEsito().toLowerCase());
				}else if(aOdg.getAtto().getTipoAtto().getCodice().equalsIgnoreCase("com")){
					//COM VERBALIZZATE
					if(aOdg.getEsito().equalsIgnoreCase("verbalizzato")){
						stringa = "Verbale";
					}else{
						stringa = esitiApprovati.get(aOdg.getEsito().toLowerCase());
					}
				}
			}
		}else{
			logger.error("calcolaStringaAfterOggetto :: esito null :: AttoOdg id " + aOdg.getId());
		}
		return stringa;
	}

	@SuppressWarnings("rawtypes")
	public File addIndiceAnaliticoResoconto(OdgPdfObject odg) throws IOException, DocumentException {
		File fileToc = File.createTempFile("withTOC_", ".pdf");
		Document document = new Document(PageSize.A4);
//		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileToc));
		document.open();

		Chunk indice = new Chunk("INDICE ANALITICO", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD));
		indice.setUnderline(0.1f, -2f);
		document.add(new Paragraph(indice));
		document.add(Chunk.NEWLINE);

		List<String> titoli = odg.getTitoli();
		for (String titolo : titoli) {
			List<Struttura> strutture = odg.getTitoli_strutture().get(titolo);
			for (Iterator iterator = strutture.iterator(); iterator.hasNext();) {
				Struttura struttura = (Struttura) iterator.next();
				document.add(printEsito(struttura.getAoo(), struttura.getPagine()));
			}
		}

		document.close();
		return fileToc;
	}

	private Paragraph printTitolo(String text) {
		return new Paragraph(new Chunk(text, FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.BOLD)));
	}

	@SuppressWarnings("rawtypes")
	private Paragraph printStruttura(String text, SortedSet<Integer> pagine) {

		String numerazione = "";
		for (Iterator iterator = pagine.iterator(); iterator.hasNext();) {
			if (!numerazione.isEmpty())
				numerazione = numerazione + ",";
			Integer pagina = (Integer) iterator.next();
			numerazione = numerazione + String.valueOf(pagina);
		}

		Paragraph p = new Paragraph(new Chunk(text, FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL)));
		p.setIndentationLeft(12);
		p.add(new Chunk(new DottedLineSeparator()));
		p.add(String.valueOf(numerazione));

		return p;
	}

	@SuppressWarnings("rawtypes")
	private Paragraph printEsito(String text, SortedSet<Integer> pagine) {

		text = text.toUpperCase();
		String numerazione = "";
		for (Iterator iterator = pagine.iterator(); iterator.hasNext();) {
			if (!numerazione.isEmpty())
				numerazione = numerazione + ",";
			Integer pagina = (Integer) iterator.next();
			numerazione = numerazione + String.valueOf(pagina);
		}
		Font font = text.equalsIgnoreCase("APPROVATI") || text.equalsIgnoreCase("RINVIATI") || text.equalsIgnoreCase("RITIRATI") ? FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD)
				: FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL);
		Paragraph p = new Paragraph(new Chunk(text, font));
		p.setIndentationLeft(12);
		p.add(new Chunk(new DottedLineSeparator()));
		p.add(String.valueOf(numerazione));

		return p;
	}

	public class OdgPdfObject extends PdfPageEventHelper {

		private List<String> titoli = new ArrayList<String>();

		private Map<String, List<Struttura>> titoli_strutture = new HashMap<String, List<Struttura>>();

		private File body;

		@Override
		public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
			String[] info = text.split("=");
			String titolo = info[0];
			String aoo = info.length == 2 ? info[1] : titolo;
			addStruttura(titolo.toUpperCase().replaceAll("\\s+", ""), new Struttura(aoo), writer.getPageNumber());
		}

		public void addPresidente() {
			if (!titoli.contains("PRESIDENTE"))
				titoli.add("PRESIDENTE");
			if (!titoli_strutture.containsKey("PRESIDENTE"))
				titoli_strutture.put("PRESIDENTE", new ArrayList<Struttura>());
		}

		public void addVicePresidente() {
			if (!titoli.contains("VICEPRESIDENTE"))
				titoli.add("VICEPRESIDENTE");
			if (!titoli_strutture.containsKey("VICEPRESIDENTE"))
				titoli_strutture.put("VICEPRESIDENTE", new ArrayList<Struttura>());
		}

		public void addAssessore(String aoo) {
			if (!titoli.contains("" + aoo.toUpperCase()))
				titoli.add("" + aoo.toUpperCase());
			if (!titoli_strutture.containsKey("ASSESSORE" + aoo.toUpperCase().trim()))
				titoli_strutture.put("ASSESSORE" + aoo.toUpperCase().replaceAll("\\s+", ""), new ArrayList<Struttura>());
		}

		public void addEsito(String esito) {
			esito = esito.toUpperCase();
			if (!titoli.contains(esito))
				titoli.add(esito);
			if (!titoli_strutture.containsKey(esito))
				titoli_strutture.put(esito, new ArrayList<Struttura>());
		}

		public void addStruttura(String relatore, Struttura struttura, int pagina) {
			List<Struttura> strutture = titoli_strutture.get(relatore);

			boolean presente = false;
			int i = 0;
			if (strutture != null) {
				while (i < strutture.size() && !presente) {
					Struttura _struttura = strutture.get(i);
					if (_struttura.getAoo().equalsIgnoreCase(struttura.getAoo())) {
						_struttura.addPagina(pagina);
						strutture.set(i, _struttura);
						presente = true;
					}
					i++;
				}
				if (!presente) {
					struttura.addPagina(pagina);
					strutture.add(struttura);
				}
				titoli_strutture.put(relatore, strutture);
			}
		}

		public File getBody() {
			return body;
		}

		public void setBody(File body) {
			this.body = body;
		}

		public List<String> getTitoli() {
			return titoli;
		}

		public void setTitoli(List<String> titoli) {
			this.titoli = titoli;
		}

		public Map<String, List<Struttura>> getTitoli_strutture() {
			return titoli_strutture;
		}

		public void setTitoli_strutture(Map<String, List<Struttura>> titoli_strutture) {
			this.titoli_strutture = titoli_strutture;
		}

	}

	public class Struttura implements Comparable<Struttura> {

		private String aoo;

		private SortedSet<Integer> pagine = new TreeSet<Integer>();

		public Struttura(String aoo) {
			this.aoo = aoo;
		}

		public String getAoo() {
			return aoo;
		}

		public SortedSet<Integer> getPagine() {
			return pagine;
		}

		public void addPagina(int pagina) {
			this.pagine.add(pagina);
		}

		@Override
		public int compareTo(Struttura o) {
			return this.aoo.compareToIgnoreCase(o.getAoo());
		}

	}

}