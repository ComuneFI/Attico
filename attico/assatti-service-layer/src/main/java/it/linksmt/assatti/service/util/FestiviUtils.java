package it.linksmt.assatti.service.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

public class FestiviUtils {

	/*
	 
	public static void main(String [] args) throws ParseException{
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//		Date dataFine = FestiviUtils.aggiungiEnneGiorniLavorativi(df.parse("13-04-2017"), 10);
		Date dataFine = FestiviUtils.aggiungiEnneGiorniLavorativi(df.parse("29-04-2017"), 10);
		System.out.println(df.format(dataFine));
	}
	*/
	
	public static LocalDate aggiungiGiorniConUltimoNonLavorativo(Date dataPartenza, int numeroGiorni) throws ParseException{
		Calendar cal = new GregorianCalendar();
		cal.setTime(dataPartenza);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, (numeroGiorni));
		
		List<Date> festivi = FestiviUtils.getFestiviProssimiNAnni((numeroGiorni/365)+2);
		while(festivi.contains(cal.getTime())){
			cal.add(Calendar.DATE, 1);
		}
		TimeZone tz = cal.getTimeZone();
		DateTimeZone jodaTz = DateTimeZone.forID(tz.getID());
		DateTime dateTime = new DateTime(cal.getTimeInMillis(), jodaTz);
		return dateTime.toLocalDate();
	}
	
	public static LocalDate aggiungiEnneGiorniLavorativi(Date dataPartenza, int numeroGiorni) throws ParseException{
		Calendar cal = new GregorianCalendar();
		cal.setTime(dataPartenza);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		int giorniAggiunti = 0;
		List<Date> festivi = FestiviUtils.getFestiviProssimiNAnni((numeroGiorni/365)+2);
//		System.out.println("Festivi\n" + festivi);
		while(giorniAggiunti != numeroGiorni){
			cal.add(Calendar.DATE, 1);
			if(!festivi.contains(cal.getTime())){
				giorniAggiunti++;
			}
		}
		TimeZone tz = cal.getTimeZone();
		DateTimeZone jodaTz = DateTimeZone.forID(tz.getID());
		DateTime dateTime = new DateTime(cal.getTimeInMillis(), jodaTz);
		return dateTime.toLocalDate();
	}

	private static List<Date> getFestiviProssimiNAnni(int n) throws ParseException{
		List<Date> festivi = new ArrayList<Date>();
		Calendar c = new GregorianCalendar();
		final int questanno = c.get(Calendar.YEAR);
		for(int anno = questanno; anno < questanno + n + 1; anno++){
			festivi.addAll(FestiviUtils.getFestiviByAnno(anno));
		}
		return festivi;
	}
	
	private static List<Date> getFestiviByAnno(int anno) throws ParseException{
		List<Date> festivi = new ArrayList<Date>();
		
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		List<Date> domeniche = new ArrayList<Date>();
		Calendar giorno = new GregorianCalendar();
		giorno.setTime(df.parse("01-01-" + anno + " 00:00:00"));
		Calendar trentunoDicembre = new GregorianCalendar();
		trentunoDicembre.setTime(df.parse("31-12-" + anno + " 00:00:00"));
		List<Date> festiviFissi = FestiviUtils.getFestiviFissiByAnno(anno);
		while(!giorno.getTime().after(trentunoDicembre.getTime())){
			if(giorno.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				domeniche.add(new Date(giorno.getTimeInMillis()));
			}
			giorno.add(Calendar.DATE, 1);
		}
		festivi.addAll(domeniche);
		festivi.addAll(festiviFissi);
		festivi.add(FestiviUtils.getPasquettaByAnno(anno));
		return festivi;
	}
	
	private static List<Date> getFestiviFissiByAnno(int anno) throws ParseException{
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		List<Date> festivi = new ArrayList<Date>();
		List<String> festeStr = new ArrayList<String>();
		festeStr.add("01-01-"+anno + " 00:00:00"); //Capodanno
		festeStr.add("06-01-"+anno + " 00:00:00"); //Epifania
		festeStr.add("25-04-"+anno + " 00:00:00"); //Liberazione dal nazifascismo
		festeStr.add("01-05-"+anno + " 00:00:00"); //Festa del lavoro
		festeStr.add("02-06-"+anno + " 00:00:00"); //Festa della Repubblica
		festeStr.add("15-08-"+anno + " 00:00:00"); //Assunzione di Maria
		festeStr.add("01-11-"+anno + " 00:00:00"); //Ognissanti
		festeStr.add("08-12-"+anno + " 00:00:00"); //Immacolata Concezione
		festeStr.add("25-12-"+anno + " 00:00:00"); //Natale
		festeStr.add("26-12-"+anno + " 00:00:00"); //Santo Stefano
		festeStr.add("08-05-"+anno + " 00:00:00"); //San Nicola patrono di Bari
		for(String str : festeStr){
			festivi.add(df.parse(str));
		}
		return festivi;
	}
	
	private static Date getPasquettaByAnno(int anno) throws ParseException{
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        int a = anno % 19,
            b = anno / 100,
            c = anno % 100,
            d = b / 4,
            e = b % 4,
            g = (8 * b + 13) / 25,
            h = (19 * a + b - d - g + 15) % 30,
            j = c / 4,
            k = c % 4,
            m = (a + 11 * h) / 319,
            r = (2 * e + 2 * j - k - h + m + 32) % 7,
            mese = (h - m + r + 90) / 25,
            giorno = (h - m + r + mese + 19) % 32;

        String strGiorno = giorno < 10 ? "0" + giorno : giorno + "";
        String strMese = mese < 10 ? "0" + mese : mese + "";
        Date pasqua = df.parse(strGiorno + "-" + strMese + "-" + anno + " 00:00:00");
        Calendar pasquettaCal = new GregorianCalendar();
        pasquettaCal.setTime(pasqua);
        pasquettaCal.add(Calendar.DATE, 1);
        
        return pasquettaCal.getTime();
	}
}
