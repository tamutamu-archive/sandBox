package com.homeserver.tamutamu.autosuitegen.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import com.homeserver.tamutamu.autosuitegen.Activator;

public class ProjectPropertyUtil {

	// テストクラス名
	public static final String TEST_CLASSNAME_PTN_KEY = "TEST_CLASSNAME_PTN_KEY";

	// 絞込み対象の親テストクラス
	public static final String SUPER_TESTCLASS_LIST_KEY = "SUPER_TESTCLASS_LIST_KEY";

	public static String getTestCaseNamePTN(IProject project) {
		try {
			return project.getPersistentProperty(new QualifiedName(
					Activator.PLUGIN_ID, TEST_CLASSNAME_PTN_KEY));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static String[] getParentTestCaseList(IProject project) {

		try {

			String tmp = project.getPersistentProperty(new QualifiedName(
					Activator.PLUGIN_ID, SUPER_TESTCLASS_LIST_KEY));

			if (tmp != null) {
				return parseString(tmp);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new String[]{};
	}

	private static String[] parseString(String parseString) {
		StringTokenizer st = new StringTokenizer(parseString, ";");
		ArrayList<String> list = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

}
