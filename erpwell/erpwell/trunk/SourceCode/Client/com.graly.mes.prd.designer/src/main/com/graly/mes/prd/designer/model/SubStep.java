package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.AbstractNamedElement;

public class SubStep extends AbstractNamedElement {
	
	private String version;
	
	public void setVersion(String newVersion) {
		String oldVersion = version;
		version = newVersion;
		firePropertyChange("version", oldVersion, newVersion);
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getName() {
		if (super.getName() == null) {
			setName("");
		}
		return super.getName();
	}
}
