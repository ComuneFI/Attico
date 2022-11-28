package it.linksmt.assatti.utility;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;


public final class DateUtil {
	
	private DateUtil() {}
	
	public static XMLGregorianCalendar dateTimeToXMLGregorianCalendar(DateTime dateTime) throws Exception {
	    XMLGregorianCalendar xmlGregorianCalendar = null;
	    try {
	      DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();
	      xmlGregorianCalendar = dataTypeFactory.newXMLGregorianCalendar(dateTime.toGregorianCalendar());
	    }
	    catch (DatatypeConfigurationException e) {
	    	e.printStackTrace();
			throw new Exception(e);
	    }
	    return xmlGregorianCalendar;
	  }
	
	private static String[] giorni = {"uno","due","tre","quattro","cinque","sei","sette","otto","nove","dieci",
			"undici","dodici","tredici","quattordici","quindici","sedici","diciassette","diciotto","diciannove","venti",
			"ventuno","ventidue","ventitre","ventiquattro","venticinque","ventisei","ventisette","ventotto","ventinove","trenta","trentuno"};
	
	private static String[] mesi = {"gennaio","febbraio","marzo","aprile","maggio","giugno","luglio","agosto","settembre","ottobre","novembre","dicembre"};
	
	private static String[] duemila = {"venti","ventuno","ventidue","ventitre","ventiquattro","venticinque","ventisei","ventisette","ventotto","ventinove","trenta","trentuno","trentadue"};
	
	public static String conversioneDataInFormatoTestuale (DateTime dateTime, String formato) throws Exception {
		if(dateTime!=null && formato!=null) {
			String out = formato;
			
			int meseInt = dateTime.getMonthOfYear();
			int giornoInt = dateTime.getDayOfMonth();
			int annoInt = dateTime.getYear();
			
			String anno = "duemila"+duemila[annoInt-2020];
			String mese = mesi[meseInt-1];
			String giorno = giorni[giornoInt-1];
			
			
			out = out.replace("#anno#", anno).replace("#mese#", mese).replace("#giorno#", giorno);
			
			return out;
		}return "";
	}

}
