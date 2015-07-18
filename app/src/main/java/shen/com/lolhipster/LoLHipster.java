package shen.com.lolhipster;

import android.app.Application;
import android.content.Context;

/**
 * Created by cfalc on 7/5/15.
 */
public class LoLHipster extends Application {

	public static final String SHARED_PREFERENCES = "shen.com.lolhipster.shared_pref";

	public static Context appContext;

	@Override public void onCreate() {
		super.onCreate();
		appContext = getApplicationContext();
	}
}
