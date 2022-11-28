package it.linksmt.assatti.datalayer.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.DocumentoPdf;

/**
 * Spring Data JPA repository for the DocumentoInformatico entity.
 */
public interface DocumentoPdfRepository extends JpaRepository<DocumentoPdf,Long>, QueryDslPredicateExecutor<DocumentoPdf> {
	
	@Query(value="select codice_cifra from atto where id = (select atto_scheda_anagrafico_contabile from documentopdf where id = ?1)", nativeQuery=true)
	String getCodiceCifraBySchedaAnagraficaContabile(Long documentoPdfId);
	
	@Modifying
	@Query(value="update documentopdf set numero_protocollo = ?2, data_protocollo = ?3 where id = ?1", nativeQuery=true)
	void setNumeroDataProtocollo(Long idDocumento, String numeroProtocollo, String dataProtocollo);
	
	@Modifying
	@Query(value="update documentopdf set pareremodificato = true where parere_id = ?1", nativeQuery=true)
	void setParereModificato(Long parereId);
	
	@Modifying
	@Query(value="insert into riversamento_sac (sac_id, documentopadre_id, da_riversare_rup, riversato_rup) values (?1, ?2, ?3, false)", nativeQuery=true)
	void insertSchedaAnagraficoContabileDaRiversare(Long sacId, BigInteger docPadreId, boolean pubblicazioneRup);
	
	@Query(value="select sac_id, documentopadre_id from riversamento_sac where da_riversare_rup is true and riversato_rup is false", nativeQuery=true)
	List<Object[]> getAllSchedeAnagraficheContabiliDaRiversareRup();
	
	@Modifying
	@Query(value="update riversamento_sac set riversato_rup = true where sac_id = ?1 and documentopadre_id = ?2", nativeQuery=true)
	void setSchedaAnagraficaContabileRiversataRup(Long sacId, Long docPadreId);
	
	@Modifying
	@Query(value="delete from riversamento_sac where sac_id = ?1", nativeQuery=true)
	void deleteSchedaAnagraficoContabileDaRiversare(Long idDocumentoPdf);
	
	@Query(value="select id from documentopdf where firmatodatutti is true and atto_adozione_id = (select atto_scheda_anagrafico_contabile from documentopdf where id = ?1)", nativeQuery=true)
	List<BigInteger> findDocumentiPrincipaliOfSchedaAnagraficaContabile(Long idDocSchedaAnagraficoContabile);
	
	
	
	@Query(value="select atto_scheda_anagrafico_contabile from documentopdf where id = ?1 and atto_scheda_anagrafico_contabile is not null and firmatodatutti is true", nativeQuery=true)
	Long checkIfDocumentoIsSchedaAnagraficoContabile(Long documentoPdfId);
	
	@Query(value="select parere_id from documentopdf where id = ?1 and parere_id is not null and firmatodatutti is true", nativeQuery=true)
	Long checkIfDocumentoIsParere(Long documentoPdfId);
	
	@Query(value=" select pdf.id, file.nome_file, pdf.created_date, pdf.aoo_id, pdf.tipodocumentoserie_id"
    			+" from"
    			+" (select p.id, p.file_id, p.created_date, p.aoo_id, p.tipodocumentoserie_id"
                +" from documentopdf p"
                +" left join documentoserie s on p.id = s.documentopdf_id"
                +" where s.documentopdf_id is null"
                +" and p.firmatodatutti is true and p.id not in (select distinct sac_id from riversamento_sac)) pdf, file"
                +" where pdf.file_id = file.id"
                +" and pdf.tipodocumentoserie_id not in (select id from tipodocumentoserie where codice in ('DELIBERAZIONI_GR', 'DL_APPROVATI'))", nativeQuery = true)
	List<Object[]> findByFirmatoAndNotRiversatoInSerie();

	@Query(value=" select pdf.id, file.nome_file, pdf.created_date, pdf.aoo_id, pdf.tipodocumentoserie_id, pdf.atto_adozione_id"
			+" from"
			+" (select p.id, p.file_id, p.created_date, p.aoo_id, p.tipodocumentoserie_id, p.atto_adozione_id, p.atto_adozione_omissis_id"
			+" from documentopdf p"
			+" left join documentoserie s on p.id = s.documentopdf_id"
			+" where s.documentopdf_id is null"
			+" and p.firmatodatutti is true) pdf, file, atto_da_repertoriare"
			+" where pdf.file_id = file.id"
			+" and pdf.tipodocumentoserie_id in (select id from tipodocumentoserie where codice in ('DELIBERAZIONI_GR', 'DL_APPROVATI')) "
			+" and (pdf.atto_adozione_id = atto_da_repertoriare.atto_id or pdf.atto_adozione_omissis_id = atto_da_repertoriare.atto_id)"
			+" and atto_da_repertoriare.repertoriato is true", nativeQuery = true)
	List<Object[]> findByFirmatoAndNotRiversatoInSerieAttiGiunta();

	@Query(value = "select id from documentopdf where atto_id =?1 and firmato is true limit 1", nativeQuery = true)
	BigInteger findByAttoAndFirmatoTrue(BigInteger attoId);

	@Query(value = "select id from documentopdf where atto_adozione_omissis_id =?1 and firmato is true order by created_date desc limit 1", nativeQuery = true)
	Long findByAttoAdozioneOmissisAndFirmatoTrue(Long attoId);
	
	@Query(value = "select id from documentopdf where atto_adozione_id =?1 and firmato is true order by created_date desc limit 1", nativeQuery = true)
	Long findByAttoAdozioneAndFirmatoTrue(Long attoId);
	
	@Query(value = "select id from documentopdf where parere_id =?1 and firmato is true limit 1", nativeQuery = true)
	BigInteger findByParereAndFirmatoTrue(BigInteger parereId);
	
	@Query(value = "select id from documentopdf where resoconto_id =?1 and firmato is true limit 1", nativeQuery = true)
	BigInteger findByResocontoAndFirmatoTrue(BigInteger resocontoId);
	
	@Query(value = "select id from documentopdf where lettera_id =?1 and firmato is true limit 1", nativeQuery = true)
	BigInteger findByLetteraAndFirmatoTrue(BigInteger letteraId);

	@Query(value = "select id from documentopdf where verbale_id =?1 and firmato is true limit 1", nativeQuery = true)
	BigInteger findByVerbaleAndFirmatoTrue(BigInteger verbaleId);

	@Query(value = " select pdf.id, file.nome_file, pdf.created_date, pdf.aoo_id, pdf.tipodocumentoserie_id"
				  +" from"
				  +" (select distinct p.id, p.file_id, p.created_date, p.aoo_id, p.tipodocumentoserie_id"
				  +" from DocumentoPdf p "
				  +" where p.firmato = 1 and "
				  +" (p.atto_id =?1 or p.atto_adozione_id =?1 or p.atto_adozione_omissis_id =?1 "
				  +"  or p.atto_scheda_anagrafico_contabile =?1)"
				  +" union "
				  +" select distinct p.id, p.file_id, p.created_date, p.aoo_id, p.tipodocumentoserie_id "
				  +" from DocumentoPdf p INNER JOIN Parere parere on p.parere_id = parere.id  " 
				  +" where p.firmato = 1 and parere.atto_id =?1 "
				  + ") pdf, file" 
				  +" where pdf.file_id = file.id", nativeQuery = true)
	List<Object[]> relatedToAttoAndFirmatoTrue(Long attoId);
	
	@Query(value = " select pdf.id, file.nome_file, pdf.created_date, pdf.aoo_id, pdf.tipodocumentoserie_id"
			  +" from"
			  +" (select distinct p.id, p.file_id, p.created_date, p.aoo_id, p.tipodocumentoserie_id"
			  +" from DocumentoPdf p left join documentoserie s on p.id = s.documentopdf_id"
			  +" where s.documentopdf_id is null and p.resoconto_id is not null)  pdf, file"
			  +" where pdf.file_id = file.id", nativeQuery = true)
	List<Object[]> relatedToResoconto();

	@Query(value = "select id, documentopdf_id "
			+ "from documentoserie "
			+ "where documentopdf_id is not null and documentoinformatico_id is null and da_pubblicare is true", nativeQuery = true)
	List<Object[]> findAllDaRiversareInAlbo();

	@Query(value = "select file_id from documentopdf where id = ?1", nativeQuery = true)
	public Long getFileIdByDocumentoPdfId(Long documentoPdfId);
	
	@Query(value = "select pdf from DocumentoPdf pdf where pdf.attoAdozioneId = ?1")
	public List<DocumentoPdf> findByAttoAdozioneId(Long attoId);
	
	@Query(value = "select pdf from DocumentoPdf pdf where pdf.attoSchedaAnagraficoContabileId = ?1 and firmato is true ")
	public DocumentoPdf findSchedaContabileByAtto(Long attoId);
}
