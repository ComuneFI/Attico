package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.linksmt.assatti.datalayer.domain.StatoRichiestaHD;

/**
 * Spring Data JPA repository for the StatoRichiestaHD entity.
 */
public interface StatoRichiestaHDRepository extends JpaRepository<StatoRichiestaHD,Long> {
	StatoRichiestaHD findByDescrizione(String descrizione);
}
