package it.linksmt.assatti.datalayer.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Repertorio;

/**
 * Spring Data JPA repository for the Repertorio entity.
 */
public interface RepertorioRepository extends JpaRepository<Repertorio,Long>, QueryDslPredicateExecutor<Repertorio> {
	@Query(value="select numero_repertorio + 1 from repertorio where anno = ?1 and aoo_id IS NULL and tipoatto_id = ?2", nativeQuery=true)
	BigInteger findProssimoRepertorio(Integer anno, Long tipoAttoId);
	
	@Modifying
	@Query(value="update repertorio set numero_repertorio = ?1 where anno = ?2 and aoo_id IS NULL and tipoatto_id = ?3", nativeQuery=true)
	void incrementaRepertorio(Integer numero, Integer anno, Long tipoAttoId);
	
	@Query(value="select a.id, CAST(a.numero_adozione AS UNSIGNED) as numero from atto a inner join atto_da_repertoriare adr on a.id = adr.atto_id where adr.errore = false and adr.repertoriato = false and a.numero_adozione is not null order by numero asc", nativeQuery=true)
	List<Object[]> getMappaAttoIdNumeroProgressivoDaRepertoriareAttiAdottati();
	
	@Query(value="select a.id from atto a inner join atto_da_repertoriare adr on a.id = adr.atto_id where adr.errore = false and adr.repertoriato = false and a.numero_adozione is null", nativeQuery=true)
	List<BigInteger> getAttiDaRepertoriareNonAdottati();
	
	@Modifying
	@Query(value="update atto_da_repertoriare set errore = true where atto_id = ?1", nativeQuery=true)
	void setRepertoriazioneError(Long attoId);
	
	@Modifying
	@Query(value="insert into atto_da_repertoriare(atto_id, errore, repertoriato) values(?1, false, false)", nativeQuery=true)
	void richiediRepertoriazioneSchedulata(Long attoId);
	
	@Modifying
	@Query(value="update atto_da_repertoriare set repertoriato = true where atto_id = ?1", nativeQuery=true)
	void attoRepertoriato(Long attoId);
}
