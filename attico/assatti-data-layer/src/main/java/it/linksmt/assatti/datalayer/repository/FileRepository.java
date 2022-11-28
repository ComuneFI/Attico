package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.File;

/**
 * Spring Data JPA repository for the File entity.
 */
public interface FileRepository extends JpaRepository<File,Long> {
	@Modifying
    @Transactional
    @Query("delete from File f where f.id = ?1")
	void deleteById(Long id);
	
	@Query(value="select contenuto from file where id = ?1", nativeQuery=true)
	byte[] getContenutoOfFileById(Long id);
	
	File findByCmisObjectId(String cmisObjectId);
}
