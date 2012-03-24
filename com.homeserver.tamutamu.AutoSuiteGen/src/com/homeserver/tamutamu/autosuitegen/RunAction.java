package com.homeserver.tamutamu.autosuitegen;


import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

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

	public void createAllTestsuite(IJavaProject project) {

		try {

			IProgressMonitor pm = new NullProgressMonitor();

			IType[] types = new TestSearcher().findAll(project,
					new NullProgressMonitor());

			IFolder destFolder = project.getProject()
					.getFolder("src/test/java");

			StringBuilder sb = new StringBuilder("\n@RunWith(Suite.class)\n@SuiteClasses( {\n");
			boolean isFirst = true;
			// for (IJavaElement el : project.getPackageFragmentRoot(destFolder)
			// .getChildren()) {
			//
			// if (el.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
			// IPackageFragment pkf = (IPackageFragment) el;
			//
			// for (IJavaElement el2 : pkf.getChildren()) {
			//
			// if (el2.getElementType() == IJavaElement.COMPILATION_UNIT) {
			//
			// if (!isFirst) {
			// sb.append(", ");
			// } else {
			// isFirst = false;
			// }
			//
			// ICompilationUnit icu = (ICompilationUnit) el2;
			// sb.append(
			// icu.findPrimaryType()
			// .getFullyQualifiedName()).append(
			// ".class\n");
			// }
			// }
			// }
			// }
			for (IJavaElement el : types) {
				el.getParent();
				if (el.getParent().getElementType() == IJavaElement.COMPILATION_UNIT) {

					ICompilationUnit icu = (ICompilationUnit) el.getParent();

					String fullName = icu.findPrimaryType().getFullyQualifiedName();

					if(fullName.equals("suite.AllTests")){
						continue;
					}else{
						sb.append(fullName).append(".class\n, ");
					}

				}
			}
			sb.delete(sb.length()-2, sb.length());
			sb.append(" })\n");

			String body = "public class AllTests{\n}";
			sb.append(body);

			IType alltest = project.findType("suite.AllTests");
			ICompilationUnit cu = null;

			if (alltest != null) {
				// alltest.getCompilationUnit().getCorrespondingResource().delete(true,
				// pm);
				cu = alltest.getCompilationUnit();
				cu.discardWorkingCopy();
				cu.delete(true, pm);

			}

			IPackageFragment pack = project.getPackageFragmentRoot(destFolder)
					.createPackageFragment("suite", false, null);

			cu = pack.createCompilationUnit("AllTests.java", sb.toString(),
					false, null);

			cu.createPackageDeclaration("suite", pm);
			cu.createImport("org.junit.runners.Suite.SuiteClasses", null, pm);
			cu.createImport("org.junit.runner.RunWith", null, pm);
			cu.createImport("org.junit.runners.Suite", null, pm);

			cu.save(pm, true);

			// IType itype = cu.getType("suite.AllTests");
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
