package it.linksmt.assatti.gestatti.web.rest.util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

public class AllegatoAttoUtil {

	public static boolean isEstensionePermessa(MultipartFile[] multipart) {
		String estensioniAbilitate = WebApplicationProps.getProperty(ConfigPropNames.ATTO_ALLEGATO_ESTENSIONI_ABILITATE);
		
		if (multipart != null && multipart.length > 0) {
			for (MultipartFile multipartFile : multipart) {
				
				String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
				StringTokenizer tokenizer = new StringTokenizer(estensioniAbilitate, " ");
				while (tokenizer.hasMoreElements() ) {
					if(tokenizer.nextToken().equalsIgnoreCase(ext)) {
						return true;
					}
				}
			}
			
		}
		
		return false;
	}
	
	public static boolean isEstensionePermessa(String nomeFile) {
		String estensioniAbilitate = WebApplicationProps.getProperty(ConfigPropNames.ATTO_ALLEGATO_ESTENSIONI_ABILITATE);
		
		if (nomeFile != null) {
			String ext = FilenameUtils.getExtension(nomeFile);
			StringTokenizer tokenizer = new StringTokenizer(estensioniAbilitate, " ");
			while (tokenizer.hasMoreElements() ) {
				if(tokenizer.nextToken().equalsIgnoreCase(ext)) {
					return true;
				}
			}
		}		
		return false;
	}
	
	public static boolean isAFileZipWithZipInside(MultipartFile[] multipart) {
		
		if (multipart != null && multipart.length > 0) {
			for (MultipartFile multipartFile : multipart) {
				
				
				if(FilenameUtils.getExtension(multipartFile.getOriginalFilename()).equals("zip"))
				{
					    
					try {
						File tmp = File.createTempFile("tmp_zip", ".zip");
						tmp.deleteOnExit();
						FileUtils.writeByteArrayToFile(tmp, multipartFile.getBytes());
						ZipFile zip = new ZipFile(tmp);
						Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
						while (zipFileEntries.hasMoreElements()) {
							ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
							if(!entry.isDirectory()) {
								String currentEntry = entry.getName();
						        if (currentEntry.endsWith(".zip")) {
						        	zip.close();
							        return true;
						        }
						        if(!isEstensionePermessa(currentEntry)) {
						        	zip.close();
						        	return true;
						        }
							}
					    }
						zip.close();
					} catch (IllegalStateException | IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
}
