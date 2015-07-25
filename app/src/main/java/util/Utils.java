package util;

import android.support.annotation.StringRes;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import shen.com.lolhipster.R;
import shen.com.lolhipster.api.models.ChampionRoleScore;

/**
 * Created by cfalc on 7/15/15.
 */
public class Utils {

	public static float calculateAverage(List<ChampionRoleScore> scores) {
		if (scores.size() == 0) {
			return 0;
		}
		float sum = 0;
		for (ChampionRoleScore score : scores) {
			sum += score.score;
		}
		return sum / scores.size();
	}

	public static @StringRes int StringForAverage(float average){
		ArrayList<Integer> possibleStrings = new ArrayList<>();
		if (average >= 60) {
			possibleStrings.add(R.string.over_sixty_message_one);
			possibleStrings.add(R.string.over_sixty_message_two);
			possibleStrings.add(R.string.over_sixty_message_three);
			possibleStrings.add(R.string.over_sixty_message_four);
			possibleStrings.add(R.string.over_sixty_message_five);
			possibleStrings.add(R.string.over_sixty_message_six);
			possibleStrings.add(R.string.over_sixty_message_seven);
			if (average >= 70) {
				possibleStrings.add(R.string.over_seventy_message_one);
				possibleStrings.add(R.string.over_seventy_message_two);
				possibleStrings.add(R.string.over_seventy_message_three);
			}
		} else {
			possibleStrings.add(R.string.less_sixty_message_one);
			possibleStrings.add(R.string.less_sixty_message_two);
			possibleStrings.add(R.string.less_sixty_message_three);
			possibleStrings.add(R.string.less_sixty_message_four);
			possibleStrings.add(R.string.less_sixty_message_five);
			possibleStrings.add(R.string.less_sixty_message_six);
			possibleStrings.add(R.string.less_sixty_message_seven);
			possibleStrings.add(R.string.less_sixty_message_eight);
			if (average < 40) {
				possibleStrings.add(R.string.less_forty_message_one);
				possibleStrings.add(R.string.less_forty_message_two);
			}
		}
		int random = new Random().nextInt(possibleStrings.size());
		return possibleStrings.get(random);
	}
}
