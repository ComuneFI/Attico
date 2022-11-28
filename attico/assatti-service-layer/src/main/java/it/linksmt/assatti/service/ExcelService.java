package it.linksmt.assatti.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.TempFile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.InseribiliOdgExcelEnum;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.utility.StringUtil;



@Service
public class ExcelService {
	
	@Inject
	private UtilityService utilityService;
	
	@Inject OrdineGiornoService ordineGiornoService;
	
	public File createExcelOdg(final List<Atto> atti) throws Exception{
		File tmpFile = TempFile.createTempFile("Odg_", new Date().getTime() + ".xls");
		FileOutputStream fout = new FileOutputStream(tmpFile);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		
		Row row = sheet.createRow(0);
		
		Font headerFont= wb.createFont();
		headerFont.setColor(IndexedColors.BLACK.getIndex());
		headerFont.setBold(true);
		headerFont.setItalic(true);
	    CellStyle evidenziatoStyle = wb.createCellStyle();
		evidenziatoStyle.setFont(headerFont);
		
		int i = 0;
		for(InseribiliOdgExcelEnum en : InseribiliOdgExcelEnum.sortedEnum()){
			Cell cell = row.createCell(i);
			cell.setCellValue(en.getDescrizione());
			cell.setCellStyle(evidenziatoStyle);
			i++;
		}
		int rNum = 1;
		for(Atto atto : atti){
			row = sheet.createRow(rNum);
			int c = 0;
			for(InseribiliOdgExcelEnum en : InseribiliOdgExcelEnum.sortedEnum()){
				if(en.equals(InseribiliOdgExcelEnum.CODICE_ATTO)) {
					row.createCell(c).setCellValue(atto.getCodiceCifra() != null ? atto.getCodiceCifra() : "");
				}else if(en.equals(InseribiliOdgExcelEnum.DATA)) {
					row.createCell(c).setCellValue(atto.getDataCreazione()!=null ? atto.getDataCreazione().toString("dd/MM/yyyy") : "");
				}else if(en.equals(InseribiliOdgExcelEnum.MOTIVO_SOSPENSIONE)) {
					row.createCell(c).setCellValue(atto.getStato()!=null && atto.getStato().equalsIgnoreCase(SedutaGiuntaConstants.statiAtto.propostaSospesa.toString()) && atto.getNote()!=null ? atto.getNote() : "");
				}else if(en.equals(InseribiliOdgExcelEnum.OGGETTO)) {
					row.createCell(c).setCellValue(atto.getOggetto()!=null ? atto.getOggetto() : "");
				}else if(en.equals(InseribiliOdgExcelEnum.SOSPESO)) {
					row.createCell(c).setCellValue(atto.getStato() != null && atto.getStato().equalsIgnoreCase(SedutaGiuntaConstants.statiAtto.propostaSospesa.toString()) ? "Si" : "");
				}else if(en.equals(InseribiliOdgExcelEnum.TIPO_ATTO)) {
					row.createCell(c).setCellValue(atto.getTipoAtto()!=null ? atto.getTipoAtto().getDescrizione() : "");
				}else if(en.equals(InseribiliOdgExcelEnum.TIPO_ITER)) {
					row.createCell(c).setCellValue(atto.getTipoIter()!=null ? atto.getTipoIter().getDescrizione() : "");
				}else if(en.equals(InseribiliOdgExcelEnum.ASSESSORE_PROPONENTE)) {
					row.createCell(c).setCellValue(atto.getAssessoreProponente()!=null ? atto.getAssessoreProponente() : "");
				}
				c++;
			}
			rNum++;
		}
		wb.write(outputStream);
		outputStream.writeTo(fout);
		outputStream.close();
		fout.close();
    	wb.close();
    	return tmpFile;
	}
	
	public File createExcel(final List<Atto> atti, final Set<String> colNamesSorted) throws Exception{
		Properties prop = new Properties();
		ClassPathResource res = new ClassPathResource("xls.properties");
		prop.load(res.getInputStream());
		
		File tmpFile = TempFile.createTempFile("atti", new Date().getTime() + ".xls");
		FileOutputStream fout = new FileOutputStream(tmpFile);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		List<String> cols = new ArrayList<String>();
		for(String c : colNamesSorted){
			if(c.contains("-") && c.split("-").length > 1){
				cols.add(c.split("-")[1]);
			}
		}
		List<Map<String, String>> listMapAtti = utilityService.listMapAtti(atti, cols);
		Row row = sheet.createRow(0);
		
		Font headerFont= wb.createFont();
		headerFont.setColor(IndexedColors.BLACK.getIndex());
		headerFont.setBold(true);
		headerFont.setItalic(true);
	    CellStyle evidenziatoStyle = wb.createCellStyle();
		evidenziatoStyle.setFont(headerFont);
		
		int i = 0;
		for(String col : cols){
			Cell cell = row.createCell(i);
			cell.setCellValue(prop.getProperty(col));
			cell.setCellStyle(evidenziatoStyle);
			i++;
		}
		int rNum = 1;
		for(Map<String, String> mapAtto : listMapAtti){
			row = sheet.createRow(rNum);
			int c = 0;
			for(String col : cols){
				row.createCell(c).setCellValue(mapAtto.get(col) != null ? mapAtto.get(col) : "");
				c++;
			}
			rNum++;
		}
		wb.write(outputStream);
		outputStream.writeTo(fout);
		outputStream.close();
		fout.close();
    	wb.close();
    	return tmpFile;
	}
	
	public File createReportSeduta(final SedutaGiunta seduta) throws Exception{
		File tmpFile = TempFile.createTempFile("seduta", new Date().getTime() + ".xls");
		String odlOdg = SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				seduta.getOrgano())?"Ordine del Giorno":"Ordine dei Lavori";
		FileOutputStream fout = new FileOutputStream(tmpFile);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		
		
		
		
		Font headerFont= wb.createFont();
		headerFont.setColor(IndexedColors.BLACK.getIndex());
		headerFont.setBold(true);
		headerFont.setItalic(true);
	    CellStyle evidenziatoStyle = wb.createCellStyle();
		evidenziatoStyle.setFont(headerFont);
		
		Row row = sheet.createRow(0);
		
		Cell cell = row.createCell(0);
		String tipoSeduta = "";
		if(seduta.getTipoSeduta() == 1)
			tipoSeduta = "Ordinaria";
		else
			tipoSeduta = "Straordinaria";
		cell.setCellValue("Report Seduta " + tipoSeduta + " numero "+seduta.getNumero());
		cell.setCellStyle(evidenziatoStyle);
		int rNum = 2;
		
		
		
		
		Set<OrdineGiorno> allOdg = seduta.getOdgs();
		if (allOdg != null) {
			
			
			row = sheet.createRow(rNum);
			for (OrdineGiorno odg : allOdg) {
				
				Row rigaIntestazioneOdg = sheet.createRow(rNum);
				cell  = rigaIntestazioneOdg.createCell(0);
				
				String tipoOdg = "Base";
				
				if (odg.getTipoOdg().getId() == 3) {
					tipoOdg = "Suppletivo";
				}else if (odg.getTipoOdg().getId() == 4) {
					tipoOdg = "Fuori Sacco";
				}
				
				cell.setCellValue(odlOdg + " " +tipoOdg);
				
				rNum++;
				
				Row intestazioneColonneDati = sheet.createRow(rNum);
				cell = intestazioneColonneDati.createCell(0);
				cell.setCellValue("Codice Atto");
				cell = intestazioneColonneDati.createCell(1);
				cell.setCellValue("Tipo Atto");
				cell = intestazioneColonneDati.createCell(2);
				cell.setCellValue("Oggetto");
				if(SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
						seduta.getOrgano())) {
					cell = intestazioneColonneDati.createCell(3);
					cell.setCellValue("Assessore Proponente");
				}
				rNum++;
				
				List<AttiOdg> atti = odg.getAttos();
				if (atti != null) {
					for (AttiOdg attoOdg : atti) {
						Row dati = sheet.createRow(rNum);
						cell = dati.createCell(0);
						cell.setCellValue(attoOdg.getAtto().getCodiceCifra());
						cell = dati.createCell(1);
						cell.setCellValue(attoOdg.getAtto().getTipoAtto()!=null&&!StringUtil.isNull(attoOdg.getAtto().getTipoAtto().getDescrizione())?attoOdg.getAtto().getTipoAtto().getDescrizione():"");
						cell = dati.createCell(2);
						cell.setCellValue(attoOdg.getAtto().getOggetto());
						if(SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
								seduta.getOrgano())) {
							cell = dati.createCell(3);
							cell.setCellValue(!StringUtil.isNull(attoOdg.getAtto().getAssessoreProponente())?attoOdg.getAtto().getAssessoreProponente():"");
						}
						rNum++;
					}
				}rNum++;	// End attiOdg
				
			}
		}
		
		
		wb.write(outputStream);
		outputStream.writeTo(fout);
		outputStream.close();
		fout.close();
    	wb.close();
    	return tmpFile;
	}
}
