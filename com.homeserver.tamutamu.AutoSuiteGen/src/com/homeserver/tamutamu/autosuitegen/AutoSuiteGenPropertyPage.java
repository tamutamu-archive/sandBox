package com.homeserver.tamutamu.autosuitegen;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public class AutoSuiteGenPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	Button autoSuiteGen;

	public AutoSuiteGenPropertyPage() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	private IProject getProject() {
		return (IProject) getElement();
	}

	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		Control composite = addControl(parent);
		try {
			boolean hasNature = getProject().hasNature(
					AutoSuiteGenNature.NATURE_ID);
			autoSuiteGen.setSelection(hasNature);
		} catch (CoreException e) {
			// TODO エラーダイアログ
		}
		return composite;
	}

	private Control addControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);
		Font font = parent.getFont();
		Label label = new Label(composite, SWT.NONE);
		label.setText("自動テストでは、ビルドごとにテストが実行されます。");
		label.setFont(font);
		autoSuiteGen = new Button(composite, SWT.CHECK);
		autoSuiteGen.setText("Auto-test");
		autoSuiteGen.setFont(font);
		return composite;
	}


	public boolean performOk() {
		try {
			Activator plugin = Activator.getDefault();
			if (autoSuiteGen.getSelection()) {
				plugin.addAutoBuildNature(getProject());
			} else {
				plugin.removeAutoBuildNature(getProject());
			}
		} catch (CoreException e) {
		    ErrorDialog.openError(getShell(), "Error",
		      "自動テストを設定できません。 ", e.getStatus());
		}
		return true;
	}

}
