package shen.com.lolhipster.api;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import dto.Static.Champion;
import dto.Static.ChampionList;
import java.io.IOException;
import java.util.HashMap;
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

	private final RiotApiWrapper riotApiWrapper;
	private final SharedPreferences preferences;

	@Inject public ChampIdMapManager(RiotApiWrapper riotApiWrapper, SharedPreferences sharedPreferences) {
		this.riotApiWrapper = riotApiWrapper;
		this.preferences = sharedPreferences;

		int localVersion = preferences.getInt(SHARED_PREF_VERSION_KEY, SHARED_PREF_FIRST_VERSION);

		boolean queryServer = localVersion != SHARED_PREF_VERSION_VALUE;
		try {
			String json = preferences.getString(SHARED_PREF_DATA_KEY, null);
			if (json == null || !queryServer) {
				getAllChampIdsFromServer();
			}
			if (json != null) {
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<MapOfChampData> jsonAdapter = moshi.adapter(MapOfChampData.class);
				champNameIdMap = jsonAdapter.fromJson(json);
			} else {
				champNameIdMap = new MapOfChampData();
				champNameIdMap.setChampIdNameMap(new HashMap<Integer, String>());
				champNameIdMap.setChampNameIdMap(new HashMap<String, Integer>());
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

	private void getAllChampIdsFromServer() {
		new AsyncTask<Void, String, String>() {
			@Override protected String doInBackground(Void... params) {
				ChampionList championList = null;
				try {
					championList = riotApiWrapper.getDataChampionList();
					Map<String, Champion> data = championList.getData();
					for (Object key : data.keySet()) {
						Champion champion = data.get(key);
						String name = champion.getName();
						int id = champion.getId();
						champNameIdMap.getChampNameIdMap().put(name, id);
						champNameIdMap.getChampIdNameMap().put(id, name);
					}
					Moshi moshi = new Moshi.Builder().build();
					JsonAdapter<MapOfChampData> jsonAdapter = moshi.adapter(MapOfChampData.class);
					String json = jsonAdapter.toJson(champNameIdMap);
					preferences.edit().putString(SHARED_PREF_DATA_KEY, json).commit();
					return json;
				} catch (RiotApiException | IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override protected void onPostExecute(String json) {
				super.onPostExecute(json);
				if (json != null) {
					Moshi moshi = new Moshi.Builder().build();
					JsonAdapter<MapOfChampData> jsonAdapter = moshi.adapter(MapOfChampData.class);
					try {
						champNameIdMap = jsonAdapter.fromJson(json);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.execute();
	}
}
