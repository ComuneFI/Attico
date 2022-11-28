package it.linksmt.assatti.service.util;
import java.io.File;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {

	public static boolean contieneZip(File fileZip) throws Exception{
		boolean contieneZip = false;
	    ZipFile zip = new ZipFile(fileZip);
	    Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

	    while (zipFileEntries.hasMoreElements())
	    {
	        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	        String currentEntry = entry.getName();

	        if (!entry.isDirectory() && currentEntry.endsWith(".zip"))
	        {
	        	contieneZip = true;
	        	break;
	        }

	    }
	    return contieneZip;
	}
	
	public static void main(String[] args) throws Exception {
		String zipFile = "C:\\Users\\MAZZOTTAD\\Desktop\\tracking.zip";
		File file = new File(zipFile);

		boolean contieneZip = ZipUtil.contieneZip(file);
		
		System.out.println("Cotiene zip: " + contieneZip);
	}

}
