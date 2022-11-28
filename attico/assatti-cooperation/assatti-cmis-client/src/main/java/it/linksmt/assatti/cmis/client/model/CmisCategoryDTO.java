package it.linksmt.assatti.cmis.client.model;

import java.io.Serializable;

/**
 * Categoria di alfresco.
 * 
 * @author marco ingrosso
 */
public class CmisCategoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nodeRef;
	private String parentId;
	private String title;
	private boolean isContainer;
	private String name;
	private String description;
	private String type;
	private String displayPath;
	private String parentType;
	private String parentName;
	private boolean selectable;
	private CmisCategoryDTO[] items;	// children

	public CmisCategoryDTO() {
		// empty contructor
	}

	public CmisCategoryDTO(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isContainer() {
		return isContainer;
	}

	public void setContainer(boolean isContainer) {
		this.isContainer = isContainer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDisplayPath() {
		return displayPath;
	}

	public void setDisplayPath(String displayPath) {
		this.displayPath = displayPath;
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public boolean getSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public CmisCategoryDTO[] getItems() {
		return items;
	}

	public void setItems(CmisCategoryDTO[] items) {
		this.items = items;
	}

	public class CategoryMain {
		private CategoryData data;

		public CategoryData getData() {
			return data;
		}

		public void setData(CategoryData data) {
			this.data = data;
		}
	}

	public class CategoryData {
		private CategoryParent parent;
		private CmisCategoryDTO[] items;

		public CategoryParent getParent() {
			return parent;
		}
		public void setParent(CategoryParent parent) {
			this.parent = parent;
		}
		public CmisCategoryDTO[] getItems() {
			return items;
		}
		public void setItems(CmisCategoryDTO[] items) {
			this.items = items;
		}
	}
	
	public class CategoryParent {
		private String nodeRef;
		private String title;
		private boolean isContainer;
		private String name;
		
		public String getNodeRef() {
			return nodeRef;
		}
		public void setNodeRef(String nodeRef) {
			this.nodeRef = nodeRef;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public boolean isContainer() {
			return isContainer;
		}
		public void setContainer(boolean isContainer) {
			this.isContainer = isContainer;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}
