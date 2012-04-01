package com.homeserver.tamutamu.autosuitegen;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.homeserver.tamutamu.autosuitegen.util.ProjectPropertyUtil;

public class AutoSuiteGenPropertyPage extends PropertyPage implements
		IWorkbenchPreferencePage {

	private Text testClassName;
	private List superTestCaseList = null;
	private Button buttonAdd = null;
	private Button buttonRemove = null;

	public AutoSuiteGenPropertyPage() {
		setMessage("Suite自動生成の設定です。");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {

		// メインレイアウト
		Composite mainComp = new Composite(parent, SWT.NONE);
		GridLayout main_gl = new GridLayout(1, true);
		main_gl.verticalSpacing = 20;
		mainComp.setLayout(main_gl);

		// 1. テストクラス名パターン
		Group gp1 = new Group(mainComp, SWT.NONE);
		gp1.setLayout(new GridLayout(1, true));
		gp1.setText("１．テストクラス名パターン");
		gp1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		testClassName = new Text(gp1, SWT.SINGLE | SWT.BORDER);
		testClassName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		testClassName.setToolTipText("テストクラス名のパターンを正規表現で指定してください。");

		// 2. 親テストクラスによる絞込み
		Group gp2 = new Group(mainComp, SWT.NONE);
		gp2.setLayout(new GridLayout(3, true));
		gp2.setText("２．親テストクラスによる絞込み");
		GridData gp2_gd = new GridData(GridData.FILL_HORIZONTAL);
		gp2_gd.horizontalSpan = 3;
		gp2.setLayoutData(gp2_gd);

		GridData superTestCaseList_gd = new GridData(GridData.FILL_HORIZONTAL);
		superTestCaseList_gd.horizontalSpan = 2;
		superTestCaseList_gd.heightHint = 80;
		superTestCaseList = new List(gp2, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);
		// superTestCaseList.setSize(2000, 200);
		superTestCaseList.setLayoutData(superTestCaseList_gd);

		this.doLoad();

		Composite buttonComp = new Composite(gp2, SWT.NONE);
		buttonComp.setLayout(new GridLayout(1, false));
		buttonComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		buttonAdd = new Button(buttonComp, SWT.NONE);
		buttonAdd.setText("追加");
		buttonAdd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonAdd
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// final SelectSuperTestCaseDlg dlg = new
						// SelectSuperTestCaseDlg(
						// new Shell(), "親テストクラスの選択");
						try {
							SelectionDialog dialog;

							dialog = JavaUI
									.createTypeDialog(
											new Shell(),
											Activator.getDefault()
													.getWorkbench()
													.getActiveWorkbenchWindow(),
											SearchEngine.createWorkspaceScope(),
											IJavaElementSearchConstants.CONSIDER_CLASSES,
											false);
							dialog.setTitle("絞り込み対象の親クラスを選択");
							if (dialog.open() == Dialog.OK) {
								String superTestCaseClassName = ((IType) dialog
										.getResult()[0])
										.getFullyQualifiedName();

								superTestCaseList.setData(
										superTestCaseClassName,
										superTestCaseClassName);
								superTestCaseList.add(superTestCaseClassName);
								superTestCaseList.select(superTestCaseList
										.getItemCount() - 1);
							}

						} catch (JavaModelException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

		buttonRemove = new Button(buttonComp, SWT.NONE);
		buttonRemove.setText("削除");
		buttonRemove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonRemove
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						int idx = superTestCaseList.getSelectionIndex();
						if (idx < 0)
							return;
						superTestCaseList.remove(idx);
						if (superTestCaseList.getItemCount() > 0) {
							if (idx == 0)
								idx += 1;
							superTestCaseList.select(idx - 1);

						}
					}
				});

		return mainComp;
	}

	@Override
	public boolean performOk() {
		this.doStore();
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
	}

	public void doLoad() {

		IProject project =( (IJavaProject) getElement()).getProject();

		String testCaseNamePTN = ProjectPropertyUtil.getTestCaseNamePTN(project);
		if(testCaseNamePTN == null) testCaseNamePTN = "";
		testClassName.setText(testCaseNamePTN);

		String[] parentTestCase = ProjectPropertyUtil.getParentTestCaseList(project);

		for (int i = 0; i < parentTestCase.length; i++) {
			superTestCaseList.setData(parentTestCase[i], parentTestCase[i]);
			superTestCaseList.add(parentTestCase[i]);
		}

		if (superTestCaseList.getItemCount() > 0) {
			superTestCaseList.select(0);
		}
	}

	public void doStore() {

		IProject project =( (IJavaProject) getElement()).getProject();
		try {
			project.setPersistentProperty(
					new QualifiedName(Activator.PLUGIN_ID,
							ProjectPropertyUtil.TEST_CLASSNAME_PTN_KEY),
					testClassName.getText());

			String parentTestCase = "";
			for (int i = 0; i < superTestCaseList.getItemCount(); i++) {
				parentTestCase += (superTestCaseList.getItem(i) + ";");
			}

			project.setPersistentProperty(new QualifiedName(
					Activator.PLUGIN_ID,
					ProjectPropertyUtil.SUPER_TESTCLASS_LIST_KEY), parentTestCase);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
