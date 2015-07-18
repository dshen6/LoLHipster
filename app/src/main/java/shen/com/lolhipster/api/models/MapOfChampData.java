package shen.com.lolhipster.api.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cfalc on 7/5/15.
 */

public class MapOfChampData {

	Map<Integer, String> champIdNameMap = new HashMap<>();
	Map<String, Integer> champNameIdMap = new HashMap<>();

	public Map<Integer, String> getChampIdNameMap() {
		return champIdNameMap;
	}

	public void setChampIdNameMap(Map<Integer, String> champIdNameMap) {
		this.champIdNameMap = champIdNameMap;
	}

	public Map<String, Integer> getChampNameIdMap() {
		return champNameIdMap;
	}

	public void setChampNameIdMap(Map<String, Integer> champNameIdMap) {
		this.champNameIdMap = champNameIdMap;
	}

}
