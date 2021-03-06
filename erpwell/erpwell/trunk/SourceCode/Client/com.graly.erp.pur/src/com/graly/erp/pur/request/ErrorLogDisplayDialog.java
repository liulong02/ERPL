package com.graly.erp.pur.request;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.pur.model.PurErrLog;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Message;

public class ErrorLogDisplayDialog extends InClosableTitleAreaDialog {	
	IManagedForm form;
	List<PurErrLog> errlogs;
	ErrorLogTableManager tableManager;
	StructuredViewer viewer;
	
	
	public ErrorLogDisplayDialog(List<PurErrLog> errlogs, Shell parentShell) {
		super(parentShell);
		this.errlogs = errlogs;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		String dialogTitle = String.format(Message.getString("ppm.error_log_detial"));
		setTitle(dialogTitle);
		Composite composite = (Composite) super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());		
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		tableManager = new ErrorLogTableManager();
		viewer = tableManager.createViewer(body, toolkit);
		viewer.setInput(errlogs);
		tableManager.updateView(viewer);
		return composite;
	}

	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(800), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(350), shellSize.y));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), true);
	}
	
	class ErrorLogTableManager extends TableListManager {
		public String ErrMessage = "errMessage";
		public String ErrDate = "errDate";

		public String Cloumn_ErrMessage = Message.getString("ppm.error_message");
		public String Cloumn_ErrDate = Message.getString("ppm.error_date");
		
		public ErrorLogTableManager() {
			super(null);
		}
		
	    @Override
	    protected String[] getColumns() {
	    	return new String[]{ErrMessage, ErrDate};
	    }
	    
	    @Override
	    protected String[] getColumnsHeader() {
	    	return new String[]{Cloumn_ErrMessage, Cloumn_ErrDate};
	    }
	    
	    protected Integer[] getColumnSize() {
	    	return new Integer[]{70, 15};
	    }
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

}
