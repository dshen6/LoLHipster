package shen.com.lolhipster.api;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import main.java.riotapi.RiotApiException;
import shen.com.lolhipster.api.models.ChampionAndRole;
import shen.com.lolhipster.api.models.ChampionRoleScore;

/**
 * Created by cfalc on 7/4/15.
 */
@Singleton public class HipsterApi {

	private static final String TAG = HipsterApi.class.getSimpleName();

	private final PopularityMap popularityMap;
	private final RiotApiWrapper riotApiWrapper;

	@Inject public HipsterApi(RiotApiWrapper riotApiWrapper, PopularityMap popularityMap) {
		this.riotApiWrapper = riotApiWrapper;
		this.popularityMap = popularityMap;
	}

	public List<ChampionRoleScore> getHipsterScoresForSummoner(String name) throws RiotApiException {
		long summonerId = riotApiWrapper.getSummonerIdForName(name);
		if (summonerId == 0) {
			throw new RiotApiException(RiotApiException.DATA_NOT_FOUND);
		}
		List<ChampionAndRole> championAndRoles = riotApiWrapper.getChampionRolesFromRecentMatches(summonerId);
		int[] scores = getHipsterScoresForChampionAndRoleList(championAndRoles);
		List<ChampionRoleScore> toRet = new ArrayList<>(championAndRoles.size());
		for (int i = 0; i < championAndRoles.size(); i++) {
			ChampionAndRole championAndRole = championAndRoles.get(i);
			int score = scores[i];
			toRet.add(new ChampionRoleScore(championAndRole, score));
		}
		return toRet;
	}

	private int[] getHipsterScoresForChampionAndRoleList(List<ChampionAndRole> championAndRoleList) {
		int[] scores = new int[championAndRoleList.size()];
		for (int i = 0; i < championAndRoleList.size(); i++) {
			ChampionAndRole championAndRole = championAndRoleList.get(i);
			scores[i] = popularityMap.popularityScoreForChampionAndRole(championAndRole);
		}
		return scores;
	}
}
