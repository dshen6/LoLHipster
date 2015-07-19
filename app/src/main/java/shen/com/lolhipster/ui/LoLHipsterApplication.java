package shen.com.lolhipster.ui;

import android.app.Application;

/**
 * Created by cfalc on 7/5/15.
 */
public class LoLHipsterApplication extends Application {

	private ApplicationComponent component;

	@Override
	public void onCreate() {
		super.onCreate();

		this.component = DaggerApplicationComponent.builder()
				.applicationModule(new ApplicationModule(this))
				.build();
	}

	public ApplicationComponent getComponent() {
		return component;
	}
}
