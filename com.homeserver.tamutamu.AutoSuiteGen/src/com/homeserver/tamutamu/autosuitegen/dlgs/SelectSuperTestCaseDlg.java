package com.homeserver.tamutamu.autosuitegen.dlgs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.homeserver.tamutamu.autosuitegen.composit.SelectSuperTestCaseComposite;

public class SelectSuperTestCaseDlg extends Dialog {

	private String title = "";
	private SelectSuperTestCaseComposite comp;

	private String superTestCaseClassName = "";

	public String getSuperTestCaseClassName() {
		return superTestCaseClassName;
	}

	public SelectSuperTestCaseDlg(Shell parent, String title) {
		super(parent);
		this.title = title;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	protected Point getInitialSize() {
		return new org.eclipse.swt.graphics.Point(390, 150);
	}

	protected Control createDialogArea(Composite parent) {
		super.createDialogArea(parent);
		comp = new SelectSuperTestCaseComposite(parent, 0);
		return comp;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			superTestCaseClassName = comp.getSuperTestClassName();
		}
		super.buttonPressed(buttonId);
	}

}
