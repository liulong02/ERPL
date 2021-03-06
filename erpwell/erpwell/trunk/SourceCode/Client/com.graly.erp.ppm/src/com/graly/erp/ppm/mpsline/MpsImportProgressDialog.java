package com.graly.erp.ppm.mpsline;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class MpsImportProgressDialog extends ProgressMonitorDialog {
	private String title = "";

	public MpsImportProgressDialog(Shell parent) {
		super(parent);
	}
	
	public MpsImportProgressDialog(Shell parent, String title) {
		super(parent);
		this.title = title;
	}
	
	// 使取消按钮可以中英文显示
	protected void createCancelButton(Composite parent) {
//		cancel = createButton(parent, IDialogConstants.CANCEL_ID,
//				Message.getString("common.cancel"), false);
//		if (arrowCursor == null) {
//			arrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
//		}
//		cancel.setCursor(arrowCursor);
//		setOperationCancelButtonEnabled(enableCancelButton);
	}
	
	// 使对话框标题可以中英文显示
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}

}
