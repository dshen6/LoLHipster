package shen.com.lolhipster.api;

import dagger.Component;
import javax.inject.Singleton;
import shen.com.lolhipster.ui.ApplicationModule;

/**
 * Created by cfalc on 7/19/15.
 */
@Singleton @Component(modules = ApplicationModule.class) public interface HipsterApiComponent {
	HipsterApi hipsterApi();

	ChampIdMapManager champIdMapManager();

	PopularityMap popularityMap();
}
