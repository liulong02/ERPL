package com.graly.erp.inv.transfer;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.framework.activeentity.model.ADTable;

public class TransferLineLotDialog extends LotDialog {
	private static final String TableName = "INVMovementLineLot";
	private MovementTransfer mt;
	private MovementLine transferLine;
	
	protected boolean isView = false;
	
	public TransferLineLotDialog(Shell shell) {
		super(shell);
	}
	
	public TransferLineLotDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public TransferLineLotDialog(Shell shell, Object parent, Object child, boolean isView) {
		super(shell);
		this.mt = (MovementTransfer)parent;
		this.transferLine = (MovementLine)child;
		this.isView = isView;
	}
	
	public TransferLineLotDialog(Shell shell, Object out, Object movementInLine,
			List<MovementLine> lines, boolean isView) {
		this(shell, out, movementInLine, isView);
		this.lines = lines;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new TransferLineLotSection(this, table, mt, transferLine, lines, isView);
		lotSection.createContents(managedForm, composite);
	}
	
	public String getADTableName() {
		return TableName;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}
	
	protected boolean isSureExit() {
		if(mt != null &&
				MovementTransfer.STATUS_APPROVED.equals(mt.getDocStatus())) {
			return true;
		}
		return super.isSureExit();
	}

}
