package com.homeserver.tamutamu.autosuitegen.composit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Point;

/**
 * HSQLAddDefinitionComposite
 * @author LittleSoft(http://www.littlesoft.jp/) HagiwaraSatoshi
 */
public class SelectSuperTestCaseComposite extends Composite {

	private Label labelDBName = null;
	private Text superTestClassName = null;

	private GridData grd_h = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);

	public SelectSuperTestCaseComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout grl = new GridLayout(1, true);
		this.setLayout(grl);
		this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL));
		initialize();
	}

	private void initialize() {
		labelDBName = new Label(this, SWT.NONE);
		labelDBName.setText("対象の親テストクラスのフルパッケージ＋クラス名");

		superTestClassName = new Text(this, SWT.BORDER);
		grd_h.verticalAlignment = SWT.CENTER;
		superTestClassName.setLayoutData(grd_h);
		this.setSize(new Point(386, 53));
	}

	public String getSuperTestClassName(){
		return superTestClassName.getText();
	}

}