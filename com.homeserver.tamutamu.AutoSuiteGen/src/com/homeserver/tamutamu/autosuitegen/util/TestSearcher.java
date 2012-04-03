package com.homeserver.tamutamu.autosuitegen.util;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TestSearcher {

	private final static String RUN_WITH = "org.junit.runner.RunWith";

	/**
	 * 指定のテストクラスがテスト実行対象条件に合致しているか判定します。
	 * 
	 * @param project
	 * @param type
	 * @return true:対象 / false：非対象
	 */
	public static boolean matchingTestClass(IJavaProject project, IType type) {
		try {
			ITypeHierarchy ith = type
					.newSupertypeHierarchy(new NullProgressMonitor());

			final String testCaseNamePTN = ProjectPropertyUtil
					.getTestCaseNamePTN(project.getProject());

			Pattern pattern = Pattern.compile(testCaseNamePTN);
			if (!(pattern.matcher(type.getFullyQualifiedName()).matches()))
				return false;

			// 親テストクラスによる絞込み
			boolean parentTestCaseFlg = false;
			final String[] parentTestCaseList = ProjectPropertyUtil
					.getParentTestCaseList(project.getProject(),
							ProjectPropertyUtil.SUPER_TESTCLASS_LIST_KEY);

			for (String parentTestCase : parentTestCaseList) {
				parentTestCaseFlg = true;
				if (ith.contains(project.findType(parentTestCase))) {
					return true;
				}
			}

			// @RunWithのテストランナーによる絞込み
			boolean testRunnerFlg = false;
			final String[] testRunnerList = ProjectPropertyUtil
					.getParentTestCaseList(project.getProject(),
							ProjectPropertyUtil.TESTRUNNER_LIST_KEY);
			ASTParser parser = ASTParser.newParser(AST.JLS4);
			parser.setSource(type.getCompilationUnit());
			parser.setResolveBindings(true);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			ITypeBinding typeBinding = getTypeBinding(type, cu);
			for (IAnnotationBinding iab : typeBinding.getAnnotations()) {
				
				if (iab.getAnnotationType().getQualifiedName().equals(RUN_WITH)) {
					String testRunner = ((ITypeBinding)iab.getDeclaredMemberValuePairs()[0].getValue()).getQualifiedName();
					
					for (String testRunnerOfList : testRunnerList) {
						testRunnerFlg = true;
						if (testRunner.equals(testRunnerOfList)) {
							return true;
						}
					}
				}
			}
			
			// テストクラス名パターンには合致し、かつ親クラスとテストランナーの設定が空の場合
			// →　テスト対象とする！
			if(!(parentTestCaseFlg|testRunnerFlg)){
				return true;
			}


		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static ITypeBinding getTypeBinding(IType type, CompilationUnit cu) {
		TypeVisitor vis = new TypeVisitor(type);
		cu.accept(vis);
		return vis.getTypeBinding();
	}

	private static class TypeVisitor extends ASTVisitor {

		private IType mType;

		private ITypeBinding fTypeBinding;

		public TypeVisitor(IType method) {
			this.mType = method;
		}

		public boolean visit(TypeDeclaration node) {

			if (node == null || node.resolveBinding() == null)
				return false;

			ITypeBinding binding = node.resolveBinding();
			if (binding.getJavaElement().equals(mType)) {
				fTypeBinding = binding;
			}
			return false;
		}

		public ITypeBinding getTypeBinding() {
			return fTypeBinding;
		}
	}
}