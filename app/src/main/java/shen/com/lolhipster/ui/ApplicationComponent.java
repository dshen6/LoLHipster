package shen.com.lolhipster.ui;

import android.content.Context;
import android.content.SharedPreferences;
import dagger.Component;
import javax.inject.Singleton;

/**
 * Created by cfalc on 7/19/15.
 */
@Singleton @Component(modules = ApplicationModule.class) interface ApplicationComponent {
	Context context();
	SharedPreferences sharedPreferences();
}
