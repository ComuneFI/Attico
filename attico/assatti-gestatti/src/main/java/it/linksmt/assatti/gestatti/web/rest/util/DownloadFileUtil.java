package it.linksmt.assatti.gestatti.web.rest.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.multipart.MultipartFile;

import it.linksmt.assatti.datalayer.domain.StoricoDocumento;

/**
 * Utility class to manage file download.
 *
 */
public class DownloadFileUtil {
//	public static ResponseEntity<FileSystemResource> responseStream(File result)
//			throws IOException {
//		
//		java.io.File fileTemp = java.io.File.createTempFile("downloadcifra" ,".tmp");
//		FileUtils.writeByteArrayToFile(fileTemp, result.getContenuto());
//		fileTemp.deleteOnExit();
//		FileSystemResource body = new FileSystemResource(fileTemp);
//		
//	 	BodyBuilder response = ResponseEntity.ok()
//	 			.header("Content-Disposition", "attachment;filename=\""+ result.getNomeFile() + "\"" );
//	 	
//	 	if(result.getSize()!=null)
//	 		response = response.contentLength(result.getSize());
//	 	
//	 	return response.contentType(MediaType.parseMediaType(result.getContentType())).body( body );
//	 	
//	 	return responseStream(result.getContenuto(), result.getNomeFile(), result.getSize(), result.getContentType());
//	}
	
	public static ResponseEntity<FileSystemResource> responseStream(byte[] content, String nomeFile, Long size, String contentType)
			throws IOException {
		
		java.io.File fileTemp = java.io.File.createTempFile("downloadcifra" ,".tmp");
		FileUtils.writeByteArrayToFile(fileTemp, content);
		fileTemp.deleteOnExit();
		FileSystemResource body = new FileSystemResource(fileTemp);
		
	 	BodyBuilder response = ResponseEntity.ok()
	 			.header("Content-Disposition", "attachment;filename=\""+ URLEncoder.encode(nomeFile.replaceAll(" ", "_"), StandardCharsets.UTF_8.toString()) + "\"" );
	 	
	 	if(size!=null)
	 		response = response.contentLength(size);
	 	
	 	return response.contentType(MediaType.parseMediaType(contentType)).body( body );
	}
	
	public static ResponseEntity<FileSystemResource> responseStream(java.io.File file, String fileName)
			throws IOException {
		
		FileSystemResource body = new FileSystemResource(file);
		
	 	BodyBuilder response = ResponseEntity.ok()
	 			.header("Content-Disposition", "attachment;filename=\""+ fileName + "\"" );
	 	
	 	response = response.contentLength(file.length());
	 	
	 	return response.contentType(MediaType.parseMediaType(Files.probeContentType(file.toPath()))).body( body );
	}
	
	public static ResponseEntity<FileSystemResource> responseStream(StoricoDocumento file)
			throws IOException {
		
		
		java.io.File fileTemp = java.io.File.createTempFile("downloadcifra" ,".tmp");
		FileUtils.writeByteArrayToFile(fileTemp, file.getFileContent());
		fileTemp.deleteOnExit();
		FileSystemResource body = new FileSystemResource(fileTemp);
		
	 	BodyBuilder response = ResponseEntity.ok()
	 			.header("Content-Disposition", "attachment;filename=\""+ file.getNome() + "\"" );
	 	
	 	if(file.getFileContent() != null)
	 		response = response.contentLength(file.getFileContent().length);
	 	
	 	return response.contentType(MediaType.parseMediaType(getContentTypeByFileName(file.getNome()))).body( body );
	}
	
	private static String getContentTypeByFileName(String nomeFile){
		
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

		// only by file name
		String mimeType = mimeTypesMap.getContentType(nomeFile);
		
		return mimeType;
	}
	
	public static File convertMultipartFileToFile(MultipartFile multipartFile) {
    	File convFile = new File(multipartFile.getOriginalFilename());
        try {
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile); 
	        fos.write(multipartFile.getBytes());
	        fos.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
        return convFile;
	}
}
