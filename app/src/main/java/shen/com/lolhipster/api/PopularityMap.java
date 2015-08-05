package shen.com.lolhipster.api;

import android.content.Context;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import shen.com.lolhipster.api.models.ChampionAndRole;

/**
 * Created by cfalc on 7/5/15.
 */
@Singleton public class PopularityMap implements ChampIdMapManager.ChampIdsUpdatedDelegate {

	private static final String CSV_FILE = "AllChampPopularity.csv";
	private static final String TAG = PopularityMap.class.getSimpleName();

	private static final int MAX_HIPSTER_LEVEL = 100;
	// equivalent to 3 warby parkers, 30 plaid shirts, or 300 flat-whites
	private HashMap<ChampionAndRole, Float> champRoles = new HashMap<>();

	private final Context appContext;
	private final ChampIdMapManager champIdMapManager;

	@Inject public PopularityMap(Context context, ChampIdMapManager champIdMapManager) {
		this.appContext = context;
		this.champIdMapManager = champIdMapManager;
		champIdMapManager.setDelegate(this);
		readCSV();
	}

	private void readCSV() {
		try {
			InputStream stream = appContext.getAssets().open(CSV_FILE);
			CSVReader reader = new CSVReader(new InputStreamReader(stream));
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine.length != 3) {
					continue;
				}
				String name = nextLine[0];
				String role = nextLine[1];
				String stringPercent = nextLine[2];
				stringPercent = stringPercent.substring(0, stringPercent.length() - 1);
				float playPercent = Float.valueOf(stringPercent);
				Integer champId = champIdMapManager.IdForName(name);
				if (champId != null) {
					ChampionAndRole championAndRole = new ChampionAndRole(champId, role);
					champRoles.put(championAndRole, playPercent);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override public void onChampIdsChange() {
		readCSV();
	}

	public int popularityScoreForChampionAndRole(ChampionAndRole championAndRole) {
		if (championAndRole == null) {
			return 0;
		}
		int score;
		Float playPercent = champRoles.get(championAndRole);
		if (playPercent == null) {
			score = MAX_HIPSTER_LEVEL;
		} else {
			score = popularityScoreForPlayPercent(playPercent);
		}
		return score;
	}

	private int popularityScoreForPlayPercent(float x) {
		return (int) (0.0434 * (x * x) - 3.8208 * x + 93.8513);
	}
}
