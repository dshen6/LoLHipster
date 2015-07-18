package shen.com.lolhipster.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import dto.Static.Champion;
import dto.Static.ChampionList;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import main.java.riotapi.RiotApiException;
import shen.com.lolhipster.LoLHipster;
import shen.com.lolhipster.api.models.MapOfChampData;

/**
 * Created by cfalc on 7/5/15.
 */
public class ChampIdMapManager {

	private static final String SHARED_PREF_VERSION_KEY = "shen.com.lolhipster.champIdMapVersion";
	private static final String SHARED_PREF_DATA_KEY = "shen.com.lolhipster.champIdMapData";
	private static final int SHARED_PREF_VERSION_VALUE = 1;
	private static final int SHARED_PREF_FIRST_VERSION = 1;

	private MapOfChampData champNameIdMap = new MapOfChampData();

	private static ChampIdMapManager instance;

	public static ChampIdMapManager getInstance() {
		if (instance == null) {
			instance = new ChampIdMapManager();
		}
		return instance;
	}

	public ChampIdMapManager() {
		SharedPreferences prefs =
				LoLHipster.appContext.getSharedPreferences(LoLHipster.SHARED_PREFERENCES, Context.MODE_PRIVATE);

		int localVersion = prefs.getInt(SHARED_PREF_VERSION_KEY, SHARED_PREF_FIRST_VERSION);

		boolean queryServer = localVersion != SHARED_PREF_VERSION_VALUE;
		try {
			String json = prefs.getString(SHARED_PREF_DATA_KEY, null);
			if (json == null || !queryServer) {
				json = getAllChampIdsFromServer();
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
		} catch (RiotApiException | IOException e) {
			e.printStackTrace();
		}

		prefs.edit().putInt(SHARED_PREF_VERSION_KEY, SHARED_PREF_VERSION_VALUE).apply();
	}

	public int IdForName(String name) {
		return champNameIdMap.getChampNameIdMap().get(name);
	}

	public @Nullable String NameForId(int id) {
		return champNameIdMap.getChampIdNameMap().get(id);
	}

	private String getAllChampIdsFromServer() throws RiotApiException, IOException {
		ChampionList championList = HipsterApi.getInstance().api.getDataChampionList();
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

		SharedPreferences prefs =
				LoLHipster.appContext.getSharedPreferences(LoLHipster.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		prefs.edit().putString(SHARED_PREF_DATA_KEY, json).commit();
		return json;
	}
}
