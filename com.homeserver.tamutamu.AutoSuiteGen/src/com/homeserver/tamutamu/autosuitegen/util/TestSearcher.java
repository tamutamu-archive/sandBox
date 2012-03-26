package com.homeserver.tamutamu.autosuitegen.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.search.matching.SuperTypeReferencePattern;

public class TestSearcher {
	public IType[] findAll(IJavaProject project, IProgressMonitor pm)
			throws JavaModelException {

		// IType[] candidates1 = getAllTestCaseSubclasses(project, pm);
		// IType[] candidates2 = getTestCaseClasses(project);
		//
		// IType[] allCandidates = new IType[candidates1.length
		// + candidates2.length];
		// System.arraycopy(candidates1, 0, allCandidates, 0,
		// candidates1.length);
		// System.arraycopy(candidates2, 0, allCandidates, candidates1.length,
		// candidates2.length);

		IType[] allCandidates = getTestCaseClasses(project);

		return collectTestsInProject(allCandidates, project);
	}

	//
	// private IType[] getAllTestCaseSubclasses(IJavaProject project,
	// IProgressMonitor pm) throws JavaModelException {
	// IType testCase = project.findType("junit.framework.TestCase");
	// if (testCase == null)
	// return new IType[0];
	// ITypeHierarchy hierarchy = testCase.newTypeHierarchy(project, pm);
	// return hierarchy.getAllSubtypes(testCase);
	// }

	private IType[] collectTestsInProject(IType[] candidates,
			IJavaProject project) {
		List result = new ArrayList();
		for (int i = 0; i < candidates.length; i++) {
			try {
				if (isTestInProject(candidates[i], project))
					result.add(candidates[i]);
			} catch (JavaModelException e) {
				// 何もしない
			}
		}
		return (IType[]) result.toArray(new IType[result.size()]);
	}

	private boolean isTestInProject(IType type, IJavaProject project)
			throws JavaModelException {
		IResource resource = type.getUnderlyingResource();
		if (resource == null)
			return false;
		if (!resource.getProject().equals(project.getProject()))
			return false;
		return !Flags.isAbstract(type.getFlags());
	}

	private IType[] getTestCaseClasses(final IJavaProject project) {

		final ArrayList<IType> list = new ArrayList<IType>();

		// スコープ
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(
				new IJavaElement[] { project }, false);

		SearchPattern parentTestCase_ptn = SearchPattern.createPattern(
				PreferenceUtil.getTestCaseNamePTN(),
				IJavaSearchConstants.CLASS, IJavaSearchConstants.DECLARATIONS,
				SearchPattern.R_PATTERN_MATCH);

/*		// 検索パターン登録
		SearchPattern pattern_TestCase = null;
		SearchPattern tmp_pattern_TestCase;
		boolean initFlg = true;

		for (String parentTestCase : PreferenceUtil.getParentTestCaseList()) {

			tmp_pattern_TestCase = SearchPattern.createPattern(parentTestCase,
					IJavaSearchConstants.CLASS,
					IJavaSearchConstants.SUPERTYPE_TYPE_REFERENCE,
					SearchPattern.R_EXACT_MATCH);

			if (initFlg) {
				pattern_TestCase = tmp_pattern_TestCase;
				initFlg = false;
				continue;
			}

			pattern_TestCase = SearchPattern.createOrPattern(pattern_TestCase,
					tmp_pattern_TestCase);

		}*/

/*		SearchPattern pattern_RunWith = SearchPattern.createPattern(
				"org.junit.runner.RunWith",
				IJavaSearchConstants.ANNOTATION_TYPE,
				IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE,
				SearchPattern.R_EXACT_MATCH);*/

/*		// マッチング処理
		SearchRequestor requestor_TestCase = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				System.out.println(match.getElement());

				if (!(match.getElement() instanceof IType))
					return;

				IType type = (IType) match.getElement();
				list.add(type);
			}
		};*/

		SearchRequestor requestor_ParentTestCaseName = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				System.out.println(match.getElement());

				if (!(match.getElement() instanceof IType))
					return;

				IType type = (IType) match.getElement();
				System.out.println("■" + type.getElementName());
				try {
					ITypeHierarchy ith = type.newSupertypeHierarchy(new NullProgressMonitor());
					if(ith.contains(project.findType("junit.framework.TestCase"))){
						System.out.println("!!!!! HIT !!!!!");
					}
				} catch (JavaModelException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				list.add(type);
			}
		};

/*		SearchRequestor requestor_RunWith = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				System.out.println(match.getElement());
				IType type = (IType) match.getElement();
				list.add(type);
			}
		};*/

		try {
			SearchEngine searchEngine = new SearchEngine();

			searchEngine.search(parentTestCase_ptn,
					new SearchParticipant[] { SearchEngine
							.getDefaultSearchParticipant() }, scope,
					requestor_ParentTestCaseName, null);

/*			searchEngine.search(pattern_RunWith,
					new SearchParticipant[] { SearchEngine
							.getDefaultSearchParticipant() }, scope,
					requestor_RunWith, null);

			searchEngine.search(pattern_TestCase,
					new SearchParticipant[] { SearchEngine
							.getDefaultSearchParticipant() }, scope,
					requestor_TestCase, null);*/

		} catch (CoreException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return list.toArray(new IType[list.size()]);

	}

}