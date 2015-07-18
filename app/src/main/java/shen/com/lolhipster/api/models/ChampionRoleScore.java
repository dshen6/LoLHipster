package shen.com.lolhipster.api.models;

/**
 * Created by cfalc on 7/6/15.
 */
public class ChampionRoleScore {
	public int champId;
	public String role;
	public int score;

	public ChampionRoleScore(ChampionAndRole championAndRole, int score) {
		this.champId = championAndRole.champId;
		this.role = championAndRole.role;
		this.score = score;
	}
}
