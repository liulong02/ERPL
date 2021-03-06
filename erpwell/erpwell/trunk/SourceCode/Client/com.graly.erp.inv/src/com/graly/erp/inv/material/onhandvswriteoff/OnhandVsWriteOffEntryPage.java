package com.graly.erp.inv.material.onhandvswriteoff;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class OnhandVsWriteOffEntryPage extends SectionEntryPage {

	public OnhandVsWriteOffEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public OnhandVsWriteOffEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new OnhandVsWriteOffSection(new EntityTableManager(adTable));
	}

}
