package com.homeserver.tamutamu.autosuitegen;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class AutoSuiteGenPage extends PreferencePage implements IWorkbenchPreferencePage {


    private Text text;
    private List superTestCaseList = null;
    private Button buttonAdd = null;
    private Button buttonRemove = null;

    public AutoSuiteGenPage() {
        super("タイトル");
        setMessage("Suite自動生成の設定です。");
        setPreferenceStore(
          Activator.getDefault().getPreferenceStore());
    }

    public AutoSuiteGenPage(String title) {
        super(title);
        // TODO 自動生成されたコンストラクター・スタブ
    }

    public AutoSuiteGenPage(String title, ImageDescriptor image) {
        super(title, image);
        // TODO 自動生成されたコンストラクター・スタブ
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {

        IPreferenceStore store = getPreferenceStore();

        Composite c = new Composite(parent, SWT.NONE);
        GridLayout grl = new GridLayout(1, true);
        grl.verticalSpacing = 20;
        c.setLayout(grl);

        Group gp1 = new Group(c, SWT.NONE);
        gp1.setLayout(new GridLayout());

        gp1.setText("テストクラス名パターン");
        text = new Text(gp1, SWT.SINGLE | SWT.BORDER);
        text.setToolTipText("テストクラス名のパターンを正規表現で指定してください。");

        GridData grd = new GridData(GridData.FILL_HORIZONTAL);
        grd.verticalAlignment=GridData.FILL;
        grd.horizontalSpan=2;
        text.setLayoutData(grd);
        text.setText(store.getString(Activator.TEST_CLASSNAME_PTN_KEY));
        gp1.setLayoutData(grd);

        Group gp2 = new Group(c, SWT.NONE);
        gp2.setLayout(new GridLayout(3, true));
        gp2.setText("テストクラス判定条件");

        GridData grd2_1 = new GridData(GridData.FILL_HORIZONTAL);
        grd2_1.horizontalSpan=3;
        gp2.setLayoutData(grd2_1);

        Label label = new Label(gp2, SWT.NONE);
        label.setLayoutData(grd2_1);
        label.setText("■継承クラス");

        GridData grd2_2 = new GridData(GridData.FILL_HORIZONTAL);
        grd2_2.horizontalSpan=2;
        grd2_2.heightHint=120;
        superTestCaseList = new List(gp2, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        //superTestCaseList.setSize(2000, 200);
        superTestCaseList.setLayoutData(grd2_2);

        GridData grd2_3 = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);

        Composite c1 = new Composite(gp2, SWT.NONE);
        GridLayout grl1 = new GridLayout(1, false);
        c1.setLayout(grl1);
        c1.setLayoutData(grd2_3);

        buttonAdd = new Button(c1, SWT.NONE);
        buttonAdd.setText("追加");
        buttonAdd.setLayoutData(grd2_3);

        buttonRemove = new Button(c1, SWT.NONE);
        buttonRemove.setText("削除");
        buttonRemove.setLayoutData(grd2_3);

        return c;
    }

    @Override
    public boolean performOk() {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(Activator.TEST_CLASSNAME_PTN_KEY, text.getText());
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        IPreferenceStore store = getPreferenceStore();
        String str = store.getDefaultString(Activator.TEST_CLASSNAME_PTN_KEY);
        text.setText(str);
        super.performDefaults();
    }
}
