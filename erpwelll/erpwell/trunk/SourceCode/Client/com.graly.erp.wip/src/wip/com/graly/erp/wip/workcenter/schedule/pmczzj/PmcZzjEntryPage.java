package com.graly.erp.wip.workcenter.schedule.pmczzj;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class PmcZzjEntryPage extends SectionEntryPage {

	public PmcZzjEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public PmcZzjEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new PmcZzjMainSection(new EntityTableManager(adTable));
		masterSection.setWhereClause("");
	}

}
