package shen.com.lolhipster.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by cfalc on 7/19/15.
 */
@Module public final class ApplicationModule {
	private final Application application;

	ApplicationModule(Application application) {
		this.application = application;
	}

	@Provides Context applicationContext() {
		return application;
	}

	@Provides
	@Singleton SharedPreferences providePreferenceManager() {
		return PreferenceManager.getDefaultSharedPreferences(application);
	}

}