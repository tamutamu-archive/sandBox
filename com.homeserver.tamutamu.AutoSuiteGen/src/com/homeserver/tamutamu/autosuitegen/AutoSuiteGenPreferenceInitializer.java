package com.homeserver.tamutamu.autosuitegen;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class AutoSuiteGenPreferenceInitializer extends AbstractPreferenceInitializer {

    public AutoSuiteGenPreferenceInitializer() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(Activator.TEST_CLASSNAME_PTN_KEY, "");

    }

}
