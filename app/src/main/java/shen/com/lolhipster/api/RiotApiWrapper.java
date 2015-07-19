package shen.com.lolhipster.api;

import dto.MatchHistory.MatchSummary;
import dto.MatchHistory.Participant;
import dto.Static.ChampionList;
import dto.Summoner.Summoner;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;
import shen.com.lolhipster.BuildConfig;
import shen.com.lolhipster.api.models.ApiChampionLane;
import shen.com.lolhipster.api.models.ApiChampionRole;
import shen.com.lolhipster.api.models.ChampionAndRole;
import shen.com.lolhipster.api.models.Role;

/**
 * Created by cfalc on 7/19/15.
 */

@Singleton public class RiotApiWrapper {

	private static final String MATCH_MODE_CLASSIC = "CLASSIC";
	private static final String MATCH_TYPE_MATCHED_GAME = "MATCHED_GAME";
	private static final String MATCH_QUEUE_TYPE_NORMAL_5x5_BLIND = "NORMAL_5x5_BLIND";
	private static final String MATCH_QUEUE_TYPE_NORMAL_5x5_DRAFT = "NORMAL_5x5_DRAFT";
	private static final String MATCH_QUEUE_TYPE_RANKED_SOLO_5x5 = "RANKED_SOLO_5x5";
	private static final String MATCH_QUEUE_TYPE_RANKED_PREMADE_5x5 = "RANKED_PREMADE_5x5";
	private static final String MATCH_QUEUE_TYPE_RANKED_TEAM_5x5 = "RANKED_TEAM_5x5";
	private static final String MATCH_QUEUE_TYPE_GROUP_FINDER_5x5 = "GROUP_FINDER_5x5";
	private static final String MATCH_QUEUE_TYPE_COUNTER_PICK = "COUNTER_PICK";

	private RiotApi api;

	@Inject public RiotApiWrapper() {
		this.api = new RiotApi(BuildConfig.ApiKey);
	}

	public long getSummonerIdForName(String name) throws RiotApiException {
		Summoner summoner = api.getSummonerByName(name);
		if (summoner != null) {
			return summoner.getId();
		}
		return 0;
	}

	public ChampionList getDataChampionList() throws RiotApiException {
		return api.getDataChampionList();
	}

	public List<ChampionAndRole> getChampionRolesFromRecentMatches(long summonerId) throws RiotApiException {
		List<MatchSummary> matches = api.getMatchHistory(summonerId, 0).getMatches();
		if (matches == null) {
			throw new RiotApiException(RiotApiException.DATA_NOT_FOUND);
		}

		List<ChampionAndRole> champIds = new ArrayList<>(matches.size());
		for (MatchSummary match : matches) {
			if (!isMatchedSummonerRiftGame(match)) {
				continue;
			}
			List<Participant> participants = match.getParticipants();
			if (participants.size() != 1) {
				continue;
			}
			Participant participant = participants.get(0);
			int champId = participant.getChampionId();
			String apiRole = participant.getTimeline().getRole();
			String apiLane = participant.getTimeline().getLane();
			String role = roleForApiRoleAndLane(apiRole, apiLane);
			ChampionAndRole championAndRole = new ChampionAndRole(champId, role);
			champIds.add(championAndRole);
		}
		return champIds;
	}

	private boolean isMatchedSummonerRiftGame(MatchSummary match) {
		return match.getMatchMode().equals(MATCH_MODE_CLASSIC) && match.getMatchType().equals(MATCH_TYPE_MATCHED_GAME) && (
				match.getQueueType().equals(MATCH_QUEUE_TYPE_NORMAL_5x5_BLIND)
						|| match.getQueueType().equals(MATCH_QUEUE_TYPE_RANKED_SOLO_5x5)
						|| match.getQueueType().equals(MATCH_QUEUE_TYPE_RANKED_PREMADE_5x5)
						|| match.getQueueType().equals(MATCH_QUEUE_TYPE_NORMAL_5x5_DRAFT)
						|| match.getQueueType().equals(MATCH_QUEUE_TYPE_RANKED_TEAM_5x5)
						|| match.getQueueType().equals(MATCH_QUEUE_TYPE_GROUP_FINDER_5x5)
						|| match.getQueueType().equals(MATCH_QUEUE_TYPE_COUNTER_PICK));
	}

	private String roleForApiRoleAndLane(String apiRole, String apiLane) {
		String role;
		if (apiRole.equals(ApiChampionRole.SOLO)) {
			switch (apiLane) {
				case ApiChampionLane.MID:
				case ApiChampionLane.MIDDLE:
					role = Role.Middle;
					break;
				case ApiChampionLane.TOP:
					role = Role.Top;
					break;
				default:
					role = Role.Top;
					break;
			}
		} else if (apiRole.equals(ApiChampionRole.DUO_CARRY)) {
			role = Role.ADC;
		} else if (apiRole.equals(ApiChampionRole.DUO_SUPPORT)) {
			role = Role.Support;
		} else if (apiRole.equals(ApiChampionRole.NONE) && apiLane.equals(ApiChampionLane.JUNGLE)) {
			role = Role.Jungle;
		} else if (apiRole.equals(ApiChampionRole.DUO)) {
			role = Role.DuoLane;
		} else {
			role = Role.DuoLane;
		}
		return role;
	}
}
