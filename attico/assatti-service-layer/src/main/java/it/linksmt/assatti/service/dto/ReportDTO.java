package it.linksmt.assatti.service.dto;

import java.util.List;

public class ReportDTO {
	
		public final static String FORMATO_PDF ="PDF";
		public final static Boolean OMISSIS_S = true;
		public final static Boolean OMISSIS_N = false;
		
		private Long idAtto ;
		private Long idSeduta;
		private Long idModelloHtml;
		private String formato = FORMATO_PDF;
		private Boolean omissis = OMISSIS_N;
		private String tipoDoc ;
		private Long idProfiloSottoscrittore;
		private Boolean landscapeOrientation;
		private List<DelegaFirma> delegheFirme;
		private Boolean isDoc;
		
		public class DelegaFirma{
			private String delegante;
			private String delegato;
			
			public DelegaFirma(String delegante, String delegato) {
				this.delegante = delegante;
				this.delegato = delegato;
			}
			public String getDelegante() {
				return delegante;
			}
			public String getDelegato() {
				return delegato;
			}
		}
		
		public Long getIdAtto() {
			return idAtto;
		}
		public void setIdAtto(Long idAtto) {
			this.idAtto = idAtto;
		}
		public Long getIdSeduta() {
			return idSeduta;
		}
		public void setIdSeduta(Long idSeduta) {
			this.idSeduta = idSeduta;
		}
		public Long getIdModelloHtml() {
			return idModelloHtml;
		}
		public void setIdModelloHtml(Long idModelloHtml) {
			this.idModelloHtml = idModelloHtml;
		}
		public String getFormato() {
			return formato;
		}
		public void setFormato(String formato) {
			this.formato = formato;
		}
		public Boolean getOmissis() {
			return omissis;
		}
		public void setOmissis(Boolean omissis) {
			this.omissis = omissis;
		}
		public Long getIdProfiloSottoscrittore() {
			return idProfiloSottoscrittore;
		}
		public void setIdProfiloSottoscrittore(Long idProfiloSottoscrittore) {
			this.idProfiloSottoscrittore = idProfiloSottoscrittore;
		}
		@Override
		public String toString() {
			return idAtto!=null?"ReportDTO [idAtto=" + idAtto + ", idModelloHtml="
					+ idModelloHtml + ", formato=" + formato + ", omissis="
					+ omissis + "]":"ReportDTO [ idModelloHtml="
							+ idModelloHtml + ", formato=" + formato + ", omissis="
							+ omissis + "]";
		}
		public String getTipoDoc() {
			return tipoDoc;
		}
		public void setTipoDoc(String tipoDoc) {
			this.tipoDoc = tipoDoc;
		}
		public Boolean getLandscapeOrientation() {
			return landscapeOrientation;
		}
		public void setLandscapeOrientation(Boolean landscapeOrientation) {
			this.landscapeOrientation = landscapeOrientation;
		}
		public List<DelegaFirma> getDelegheFirme() {
			return delegheFirme;
		}
		public void setDelegheFirme(List<DelegaFirma> delegheFirme) {
			this.delegheFirme = delegheFirme;
		}
		public Boolean getIsDoc() {
			return isDoc;
		}
		public void setIsDoc(Boolean isDoc) {
			this.isDoc = isDoc;
		}
		
		
}
