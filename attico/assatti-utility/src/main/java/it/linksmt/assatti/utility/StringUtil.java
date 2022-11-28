package it.linksmt.assatti.utility;

import java.net.URLConnection;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class StringUtil {
	// NON rimuovere: gestione della classe come se fosse un Singleton
	private StringUtil() { }

	public static final String EXTERNAL_ENTITY_NAME_SEPERATOR = ":";

	public static final String CODICE_PATTERN = "00000";
	
	public static final String USER_MESSAGE_NEW_LINE = "\u003Cbr\u003E";
	
	public static final SimpleDateFormat MYSQL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static final SimpleDateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final SimpleDateFormat IT_DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	public static final SimpleDateFormat IT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static final int LENGTH_CODICE_FISCALE = 16;
	public static final int LENGTH_PARTITA_IVA = 11;
	public static final int LENGTH_CAP = 5;
	public static final int LENGTH_CODICE_BIC_SWIFT = 11;


	public static final int LENGTH_AUTO_COMPLETE = 3;

	public static final String[] BOOLEAN_TRUE_VAL = {"true", "yes", "si"};

	public static Pattern NUMERIC_PATTERN = Pattern.compile("[0-9]+");

	//pattern source: <a href="http://regxlib.com/REDetails.aspx?regexp_id=26" target="_blank" rel="nofollow">http://regxlib.com/REDetails.aspx?regexp_id=26</a>
	public static final Pattern EMAIL_PATTERN = Pattern.compile(
		      "([_A-Za-z0-9-]+)(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})");

	public static final Pattern IBAN_PATTERN = Pattern.compile("[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}");

	public static final Pattern CODICE_FISCALE = Pattern.compile("[a-zA-Z]{6}\\d\\d[a-zA-Z]\\d\\d[a-zA-Z]\\d\\d\\d[a-zA-Z]");

	public static boolean validateMinLength(final String aString, final int minLength){
		if((isNull(aString) && minLength!=0)  || (!isNull(aString) && aString.trim().length() > minLength)){
			return false;
		}
		return true;
	}

	public static boolean validateFixLength(final String aString, final int length){
		if((isNull(aString) && length!=0) || (!isNull(aString) && aString.trim().length() != length)){
			return false;
		}
		return true;
	}

	public static boolean isNull(final String aString) {

		if((aString == null) || (aString.trim().length() == 0)) {
			return true;
		}

		return false;
	}

	public static String trimStr(final String original) {

		String retVal = "";
		if (original != null) {
			retVal = original.trim();
		}

		return retVal;
	}

	public static String cleanStr(final String source) {

		String retVal = trimStr(source);

		// ASCII
		retVal = Normalizer.normalize(retVal, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		retVal = pattern.matcher(retVal).replaceAll("");

		// AlphaAndDigits
		retVal = retVal.replaceAll("[^a-zA-Z0-9]+", "");

		return retVal;
	}
	private static Date toDefaultDate(final String inputDf) throws ParseException {
		return DEFAULT_DATE_FORMAT.parse(StringUtil.trimStr(inputDf));
	}

	public static Date toDefaultDateTime(final String inputDf) throws ParseException {
		return DEFAULT_DATE_TIME_FORMAT.parse(StringUtil.trimStr(inputDf));
	}

	public static boolean toBooleanVal(final String inputVal) {
		if (StringUtil.isNull(inputVal)) {
			return false;
		}

		String checkVal = StringUtil.trimStr(inputVal).toLowerCase();
		for (String string : BOOLEAN_TRUE_VAL) {
			if (checkVal.equals(string)) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Validate hex with regular expression
	 *
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public static boolean validateEmail(final String hex) {
		Pattern pattern = EMAIL_PATTERN;
		Matcher matcher = pattern.matcher(hex);
		return matcher.matches();
	}

	public static boolean validateIban(final String hex) {
		Pattern pattern = IBAN_PATTERN;
		Matcher matcher = pattern.matcher(hex);
		return matcher.matches();
	}

	public static boolean validateCodiceFiscale(final String hex) {
		Pattern pattern = CODICE_FISCALE;
		Matcher matcher = pattern.matcher(hex);
		return matcher.matches();
	}

	public static boolean validateOnlyNumber(final String hex) {
		Pattern pattern = NUMERIC_PATTERN;
		Matcher matcher = pattern.matcher(hex);
		return matcher.matches();
	}


	/**
	 * Validazione Date
	 *
	 * @param sData
	 * @return
	 * @throws Exception
	 */
	public static Date parseDate(final String sData) throws Exception {
		DateFormat formatter;
		Date data;
		formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			data = formatter.parse(sData);
			return data;
		}
		catch (Exception er) {
			er.printStackTrace();
			throw new Exception(er);
		}
	}


	public static String getFileExtension(final String filename) {
		if (StringUtil.isNull(filename)) {
			return "";
		}

		int idx = StringUtil.trimStr(filename).lastIndexOf('.');
		if (idx > -1) {
			return StringUtil.trimStr(filename).substring(idx);
		}
		return "";
	}

	public static String guessContentType(final String fileName) {

		String fileNameTrim = StringUtil.trimStr(fileName.toLowerCase());
		String contentType = URLConnection.guessContentTypeFromName(fileNameTrim);

		if (StringUtil.isNull(contentType)) {

			// Patch per tipi di dati OpenXml
			if (fileNameTrim.endsWith(".docx")) {
				contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			}
			else if (fileNameTrim.endsWith(".dotx")) {
				contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.template";
			}
			else if (fileNameTrim.endsWith(".pptx")) {
				contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
			}
			else if (fileNameTrim.endsWith(".ppsx")) {
				contentType = "application/vnd.openxmlformats-officedocument.presentationml.slideshow";
			}
			else if (fileNameTrim.endsWith(".potx")) {
				contentType = "application/vnd.openxmlformats-officedocument.presentationml.template";
			}
			else if (fileNameTrim.endsWith(".xlsx")) {
				contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			}
			else if (fileNameTrim.endsWith(".xltx")) {
				contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.template";
			}
			else {
				contentType = "application/octet-stream";
			}
		}

		return contentType;
	}

	public static boolean validateNotNullAndMinLenght(final String value, final int lenght){
		if(!isNull(value) && validateMinLength(value, lenght)){
			return true;
		}
		return false;
	}
	
	public static String cleanEnvVar(final String original) {

		if (original == null) {
			return "";
		}

		return original.replace("\n", "").replace("\r", "").replace("\"", "").trim();
	}
	
	public static String riempiASinistra (String stringa, String carattere, int lunghezza) {
		
		if(stringa == null) {
			stringa = "";
		}
		
		while (stringa.length()<lunghezza) {
			stringa = carattere+stringa;
		}
		
		return stringa;
	}
}
