package it.linksmt.assatti.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.repository.FileRepository;

/**
 * Service class for managing indirizzo.
 */
@Service
@Transactional
public class FileService {

	private final Logger log = LoggerFactory.getLogger(FileService.class);

	@Inject
	private FileRepository fileRepository;
	
	@Transactional(readOnly=true)
	public File getByFileId(Long fileId){
		return fileRepository.getOne(fileId);
	}
	
	@Transactional(readOnly=true)
	public byte[] getContenutoOfFileById(Long fileId){
		return fileRepository.getContenutoOfFileById(fileId);
	}

	@Transactional(readOnly=true)
	public File findByFileId(Long fileId){
		return fileRepository.findOne(fileId);
	}
}
