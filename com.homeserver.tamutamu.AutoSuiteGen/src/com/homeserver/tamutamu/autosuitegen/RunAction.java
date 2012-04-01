package com.homeserver.tamutamu.autosuitegen;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.homeserver.tamutamu.autosuitegen.util.TestSearcher;

public class RunAction implements IObjectActionDelegate {

	ISelection selection;

	public RunAction() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void run(IAction action) {
		IStructuredSelection structured = (IStructuredSelection) selection;
		IProject type = (IProject) structured.getFirstElement();

		IJavaProject javaProject = JavaCore.create(type);

		createAllTestsuite(javaProject);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	public void createAllTestsuite(final IJavaProject project) {

		try {

			final IFolder destFolder = project.getProject().getFolder(
					"src/test/java");

			final StringBuilder suiteClasses_stmt = new StringBuilder();

			final int pkgCnt = project.getPackageFragmentRoot(destFolder)
					.getChildren().length;

			ProgressMonitorDialog dialog = new ProgressMonitorDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell());

			try {
				dialog.run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {

						try {

							monitor.beginTask("テストスイートの作成中です。。", pkgCnt);
							for (IJavaElement el : project
									.getPackageFragmentRoot(destFolder)
									.getChildren()) {

								if (monitor.isCanceled()) {
									throw new InterruptedException(
											"Cancel has been requested.");
								} else {
									monitor.subTask("");
									if (el.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {

										existSuiteTestRemove(project,
												(IPackageFragment) el);

										ICompilationUnit cu = createSuiteClassFromPkg(
												project, (IPackageFragment) el);
										if (cu == null) {
											monitor.worked(1);
											continue;
										}

										suiteClasses_stmt.append(cu.getParent()
												.getElementName()
												+ ".AllTests.class\n, ");

									}
									monitor.worked(1);
								}
							}
							monitor.done();
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (suiteClasses_stmt.length() == 0)
				return;

			suiteClasses_stmt.delete(suiteClasses_stmt.length() - 2,
					suiteClasses_stmt.length());
			suiteClasses_stmt.append(" })\n");

			this.existSuiteTestRemove(project,
					project.getPackageFragmentRoot(destFolder)
							.getPackageFragment(""));
			this.createSuiteClass(project.getPackageFragmentRoot(destFolder)
					.createPackageFragment("allsuite", true, null), suiteClasses_stmt);

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private ICompilationUnit createSuiteClassFromPkg(IJavaProject project,
			IPackageFragment pkf) throws JavaModelException {

		StringBuilder suiteClasses_stmt = new StringBuilder();

		// 　アノテーション
		if (pkf.getChildren() == null || pkf.getChildren().length == 0)
			return null;
		for (IJavaElement testClass : pkf.getChildren()) {

			if (testClass.getElementType() == IJavaElement.COMPILATION_UNIT) {
				ICompilationUnit icu = (ICompilationUnit) testClass;

				if (icu.getElementName().equals("AllTests.java"))
					continue;

				if (!TestSearcher.matchingTestClass(project,
						icu.getAllTypes()[0]))
					continue;

				suiteClasses_stmt.append(icu.getAllTypes()[0].getElementName())
						.append(".class\n, ");
			}
		}

		if (suiteClasses_stmt.length() == 0)
			return null;

		suiteClasses_stmt.delete(suiteClasses_stmt.length() - 2,
				suiteClasses_stmt.length());
		suiteClasses_stmt.append(" })\n");

		return this.createSuiteClass(pkf, suiteClasses_stmt);
	}

	private void existSuiteTestRemove(IJavaProject project, IPackageFragment pkf)
			throws JavaModelException {
		IType suite = project.findType(pkf.getElementName() + ".AllTests");
		ICompilationUnit cu = null;

		if (suite != null) {
			cu = suite.getCompilationUnit();
			cu.delete(true, new NullProgressMonitor());
			cu.discardWorkingCopy();
		}
	}

	private ICompilationUnit createSuiteClass(IPackageFragment pkf,
			StringBuilder suiteClasses_stmt) throws JavaModelException {

		IProgressMonitor pm = new NullProgressMonitor();

		StringBuilder suite_stmt = new StringBuilder(
				"\n@RunWith(Suite.class)\n@SuiteClasses( {\n");

		suite_stmt.append(suiteClasses_stmt);

		// クラス宣言
		suite_stmt.append("public class AllTests{\n}");

		// 本体生成
		ICompilationUnit cu = pkf.createCompilationUnit("AllTests.java",
				suite_stmt.toString(), false, pm);

		cu = cu.getWorkingCopy(pm);

		if (!pkf.getElementName().equals("")) {
			cu.createPackageDeclaration(pkf.getElementName(), pm);
		}

		cu.createImport("org.junit.runners.Suite.SuiteClasses", null, pm);
		cu.createImport("org.junit.runner.RunWith", null, pm);
		cu.createImport("org.junit.runners.Suite", null, pm);
		cu.commitWorkingCopy(true, pm);

		cu.discardWorkingCopy();

		return cu;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
