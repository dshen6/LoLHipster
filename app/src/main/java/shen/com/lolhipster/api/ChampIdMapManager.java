package shen.com.lolhipster.api;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import dto.Static.Champion;
import dto.Static.ChampionList;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import main.java.riotapi.RiotApiException;
import shen.com.lolhipster.api.models.MapOfChampData;

/**
 * Created by cfalc on 7/5/15.
 */
@Singleton public class ChampIdMapManager {

	private static final String SHARED_PREF_VERSION_KEY = "shen.com.lolhipster.champIdMapVersion";
	private static final String SHARED_PREF_DATA_KEY = "shen.com.lolhipster.champIdMapData";
	private static final int SHARED_PREF_VERSION_VALUE = 1;
	private static final int SHARED_PREF_FIRST_VERSION = 1;

	private MapOfChampData champNameIdMap = new MapOfChampData();
	private ChampIdsUpdatedDelegate delegate;

	private final RiotApiWrapper riotApiWrapper;
	private final SharedPreferences preferences;

	@Inject public ChampIdMapManager(RiotApiWrapper riotApiWrapper, SharedPreferences sharedPreferences) {
		this.riotApiWrapper = riotApiWrapper;
		this.preferences = sharedPreferences;

		int localVersion = preferences.getInt(SHARED_PREF_VERSION_KEY, SHARED_PREF_FIRST_VERSION);
		String json = preferences.getString(SHARED_PREF_DATA_KEY, null);

		boolean queryServer = localVersion != SHARED_PREF_VERSION_VALUE || json == null;
		if (queryServer) {
			getAllChampIdsFromServer();
		}
		try {
			if (json != null) {
				populateFromJson(json);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		preferences.edit().putInt(SHARED_PREF_VERSION_KEY, SHARED_PREF_VERSION_VALUE).apply();
	}

	public Integer IdForName(String name) {
		return champNameIdMap.getChampNameIdMap().get(name);
	}

	public @Nullable String NameForId(int id) {
		return champNameIdMap.getChampIdNameMap().get(id);
	}

	private void populateFromJson(String json) throws IOException {
		Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<MapOfChampData> jsonAdapter = moshi.adapter(MapOfChampData.class);
		champNameIdMap = jsonAdapter.fromJson(json);
	}

	private void writeToJson() throws IOException {
		Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<MapOfChampData> jsonAdapter = moshi.adapter(MapOfChampData.class);
		String json = jsonAdapter.toJson(champNameIdMap);
		preferences.edit().putString(SHARED_PREF_DATA_KEY, json).commit();
	}

	private void getAllChampIdsFromServer() {
		new AsyncTask<Void, Void, Void>() {
			@Override protected Void doInBackground(Void... params) {
				try {
					ChampionList championList = riotApiWrapper.getDataChampionList();
					Map<String, Champion> data = championList.getData();
					for (Object key : data.keySet()) {
						Champion champion = data.get(key);
						String name = champion.getName();
						int id = champion.getId();
						champNameIdMap.getChampNameIdMap().put(name, id);
						champNameIdMap.getChampIdNameMap().put(id, name);
					}
					writeToJson();
					if (delegate != null) {
						delegate.onChampIdsChange();
					}
				} catch (RiotApiException | IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	public void setDelegate(ChampIdsUpdatedDelegate delegate) {
		this.delegate = delegate;
	}

	public interface ChampIdsUpdatedDelegate {
		void onChampIdsChange();
	}
}
