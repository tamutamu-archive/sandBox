package com.homeserver.tamutamu.autosuitegen;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
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
	private List runnerList = null;

	public AutoSuiteGenPropertyPage() {
		setMessage("Suite自動生成");
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
		gp1.setText("1. テストクラス名パターン（Junit3/4共通）");
		gp1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		testClassName = new Text(gp1, SWT.SINGLE | SWT.BORDER);
		testClassName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		testClassName.setToolTipText("テストクラス名のパターンを正規表現で指定してください。\nこの条件に合致しない場合、無条件にテスト非対象です。");

		// 2. 親テストクラスによる絞込み
		superTestCaseList = createClassSelectList(mainComp, "2. さらに親クラスで絞り込み（JUnit3用）", "いずれかにマッチすればテスト対象");

		// 3. @RunWithのRunnerによる絞込み
		runnerList = createClassSelectList(mainComp, "3. さらに@RunWithのテストランナーで絞り込み（JUnit4用）", "いずれかにマッチすればテスト対象");

		this.doLoad();

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

	/**
	 * 設定をロードします。
	 */
	private void doLoad() {

		IProject project = (IProject) getElement().getAdapter(IProject.class);

		String testCaseNamePTN = ProjectPropertyUtil
				.getTestCaseNamePTN(project);
		if (testCaseNamePTN == null)
			testCaseNamePTN = "";
		testClassName.setText(testCaseNamePTN);

		String[] parentTestCase = ProjectPropertyUtil.getParentTestCaseList(
				project, ProjectPropertyUtil.SUPER_TESTCLASS_LIST_KEY);

		for (int i = 0; i < parentTestCase.length; i++) {
			superTestCaseList.setData(parentTestCase[i], parentTestCase[i]);
			superTestCaseList.add(parentTestCase[i]);
		}

		if (superTestCaseList.getItemCount() > 0) {
			superTestCaseList.select(0);
		}

		String[] testRunner = ProjectPropertyUtil.getParentTestCaseList(
				project, ProjectPropertyUtil.TESTRUNNER_LIST_KEY);

		for (int i = 0; i < testRunner.length; i++) {
			runnerList.setData(testRunner[i], testRunner[i]);
			runnerList.add(testRunner[i]);
		}

		if (runnerList.getItemCount() > 0) {
			runnerList.select(0);
		}
	}

	/**
	 * 設定を保存します。
	 */
	private void doStore() {

		IProject project = (IProject) getElement().getAdapter(IProject.class);

		try {
			// テストクラス名のパターン
			project.setPersistentProperty(new QualifiedName(
					Activator.PLUGIN_ID,
					ProjectPropertyUtil.TEST_CLASSNAME_PTN_KEY), testClassName
					.getText());

			// 対象親クラス
			String parentTestCase = "";
			for (int i = 0; i < superTestCaseList.getItemCount(); i++) {
				parentTestCase += (superTestCaseList.getItem(i) + ";");
			}

			project.setPersistentProperty(new QualifiedName(
					Activator.PLUGIN_ID,
					ProjectPropertyUtil.SUPER_TESTCLASS_LIST_KEY),
					parentTestCase);

			// 対象@RunWithのテストランナー
			String testRunner = "";
			for (int i = 0; i < runnerList.getItemCount(); i++) {
				testRunner += (runnerList.getItem(i) + ";");
			}

			project.setPersistentProperty(new QualifiedName(
					Activator.PLUGIN_ID,
					ProjectPropertyUtil.TESTRUNNER_LIST_KEY), testRunner);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * クラス選択リストを作成します。
	 * 
	 * @param mainComp
	 * @param title
	 * @return
	 */
	private List createClassSelectList(Composite mainComp, String title, String toolTip) {

		Group gp = new Group(mainComp, SWT.NONE);
		gp.setLayout(new GridLayout(3, true));
		gp.setText(title);
		GridData gp_gd = new GridData(GridData.FILL_HORIZONTAL);
		gp_gd.horizontalSpan = 3;
		gp.setLayoutData(gp_gd);

		GridData caseList_gd = new GridData(GridData.FILL_HORIZONTAL);
		caseList_gd.horizontalSpan = 2;
		caseList_gd.heightHint = 80;
		final List classList = new List(gp, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);
		classList.setLayoutData(caseList_gd);

		Composite buttonComp = new Composite(gp, SWT.NONE);
		buttonComp.setLayout(new GridLayout(1, false));
		buttonComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		Button buttonAdd = new Button(buttonComp, SWT.NONE);
		buttonAdd.setText("追加");
		buttonAdd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonAdd
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
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
								String selectClassName = ((IType) dialog
										.getResult()[0])
										.getFullyQualifiedName();
								
								for(String className : classList.getItems()){
									if(className.equals(selectClassName)) return;
								}

								classList.setData(selectClassName,
										selectClassName);
								classList.add(selectClassName);
								classList.select(classList.getItemCount() - 1);
							}

						} catch (JavaModelException e1) {
							e1.printStackTrace();
						}
					}
				});

		Button buttonRemove = new Button(buttonComp, SWT.NONE);
		buttonRemove.setText("削除");
		buttonRemove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonRemove
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						int idx = classList.getSelectionIndex();
						if (idx < 0)
							return;
						classList.remove(idx);
						if (classList.getItemCount() > 0) {
							if (idx == 0)
								idx += 1;
							classList.select(idx - 1);

						}
					}
				});
		
		classList.setToolTipText(toolTip);
		
		return classList;

	}

}
