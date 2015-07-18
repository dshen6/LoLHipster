package shen.com.lolhipster.api.models;

/**
 * Created by cfalc on 7/5/15.
 */
public class ChampionAndRole {
	public int champId;
	public String role;

	public ChampionAndRole(int champId, String role) {
		this.champId = champId;
		this.role = role;
	}

	@Override public boolean equals(Object o) {
		return o instanceof ChampionAndRole && champId == ((ChampionAndRole) o).champId && role.equals(
				((ChampionAndRole) o).role);
	}

	@Override public String toString() {
		return "ChampId: " + champId + ", role: " + role;
	}

	@Override public int hashCode() {
		return champId + role.hashCode();
	}
}