package com.homeserver.tamutamu.autosuitegen.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.homeserver.tamutamu.autosuitegen.Activator;

public class PreferenceUtil {

	// 絞込み対象の親テストクラス
	public static final String SUPER_TESTCLASS_LIST_KEY = "SUPER_TESTCLASS_LIST_KEY";

	// テストクラス名
	public static final String TEST_CLASSNAME_PTN_KEY = "TEST_CLASSNAME_PTN_KEY";

	public static String getTestCaseNamePTN() {
		return Activator.getDefault().getPreferenceStore()
				.getString(TEST_CLASSNAME_PTN_KEY);
	}

	public static String[] getParentTestCaseList() {

		return parseString(Activator.getDefault().getPreferenceStore()
				.getString(SUPER_TESTCLASS_LIST_KEY));
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
