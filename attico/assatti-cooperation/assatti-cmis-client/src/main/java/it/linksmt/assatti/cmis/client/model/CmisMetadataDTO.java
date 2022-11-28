package it.linksmt.assatti.cmis.client.model;

import java.io.Serializable;

/**
 * @author marco ingrosso
 * 
 */
public class CmisMetadataDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String prefix;

	private String name;

	private String value;

	/**
	 * nome del type o dell'aspect cui il metadato appartiene
	 * ex: (rd:risorsadigitale)
	 */
	private String customType;

	/**
	 * tipo di dato conservato nel valore del metadato
	 */
	private String type;

	public CmisMetadataDTO() {
	}

	public CmisMetadataDTO(String prefix, String name, String value) {
		super();
		this.prefix = prefix;
		this.name = name;
		this.value = value;
	}

	public CmisMetadataDTO(String prefix, String name, String value, String customType) {
		super();
		this.prefix = prefix;
		this.name = name;
		this.value = value;
		this.customType = customType;
	}

	/*
	 * Get & Set
	 */

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCustomType() {
		return customType;
	}

	public void setCustomType(String customType) {
		this.customType = customType;
	}


}
