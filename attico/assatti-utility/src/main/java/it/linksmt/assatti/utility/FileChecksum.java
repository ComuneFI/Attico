package it.linksmt.assatti.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.LocalDate;

public final class FileChecksum {
	
	private FileChecksum() {
		
	}

	public static String calcolaImpronta(byte[] bytes) throws IOException {
		String impronta = null;
		try {
			InputStream fis = new ByteArrayInputStream(bytes);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = null;
			byte buf[] = new byte[1024];
			int letti;
	
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			while ((letti = fis.read(buf)) > 0) {
				baos.write(buf, 0, letti);
				baos.flush();
				buffer = baos.toByteArray();
				baos.reset();
				md.update(buffer);
			}
			fis.close();
			byte digest[] = md.digest();
	//		String b64digest = Base64.encodeBase64String(digest);
	//		impronta = new String(b64digest);
			impronta = Hex.encodeHexString(digest);
		}catch(Exception e) {
			throw new IOException(e);
		}
		return impronta;
    }
	
	private static String removeZeroToLeft(String num) {
		String numWithoutZeros = null;
		if(num!=null) {
			numWithoutZeros = "";
			for(int i = 0; i < num.length(); i++) {
				if(num.charAt(i) != '0') {
					numWithoutZeros = num.substring(i);
					break;
				}
			}
		}
		return numWithoutZeros;
	}
	
	public static String calcolaImprontaBozza(String codiceCifra, String codiceTipoAttoJE) {
		String sha256 = "";
		if(codiceCifra!=null && !codiceCifra.isEmpty()) {
			try {
				String stringaCod = "B/" + codiceTipoAttoJE + "/" + codiceCifra.split("/")[1] + "/" + FileChecksum.removeZeroToLeft(codiceCifra.split("/")[2]);
				sha256 = FileChecksum.calcolaImpronta(stringaCod.getBytes());
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return sha256;
	}
	
	public static String calcolaImprontaAtto(String numeroAdozione, LocalDate dataAdozione, String codiceTipoAttoJE) {
		String sha256 = "";
		if(numeroAdozione!=null && !numeroAdozione.trim().isEmpty() && dataAdozione!=null) {
			try {
				String stringaCod = "A/" + codiceTipoAttoJE + "/" + dataAdozione.getYear() + "/" + FileChecksum.removeZeroToLeft(numeroAdozione);
				sha256 = FileChecksum.calcolaImpronta(stringaCod.getBytes());
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return sha256;
	}
	
	public static String calcolaImprontaProposta(String codiceCifra, LocalDate dataCreazione, String oggetto, String descrizioneAoo, String testo,  String allegatiParteIntegrante, String datiContabili) {
		String sha256 = "";
		
		if(codiceCifra==null) {
			codiceCifra = "";
		}
		String dataCreazioneStr = dataCreazione==null?"":dataCreazione.toString();
		if(descrizioneAoo==null) {
			descrizioneAoo = "";
		}
		if(testo==null) {
			testo = "";
		}
		if(oggetto==null) {
			oggetto = "";
		}
		if(allegatiParteIntegrante==null) {
			allegatiParteIntegrante = "";
		}
		if(datiContabili==null) {
			datiContabili = "";
		}
		
		String stringaCod = codiceCifra+dataCreazioneStr+oggetto+descrizioneAoo+testo+allegatiParteIntegrante+datiContabili;
		try {
			sha256 = FileChecksum.calcolaImpronta(stringaCod.getBytes());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return sha256;
	}
	
}
