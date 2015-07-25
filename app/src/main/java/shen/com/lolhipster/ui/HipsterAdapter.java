package shen.com.lolhipster.ui;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;
import shen.com.lolhipster.R;
import shen.com.lolhipster.api.ChampIdMapManager;
import shen.com.lolhipster.api.models.ChampionRoleScore;
import util.Utils;

/**
 * Created by cfalc on 7/6/15.
 */
public class HipsterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int VIEW_TYPE_HEADER = 0;
	private static final int VIEW_TYPE_CHAMPION = 1;

	private final ChampIdMapManager champIdMapManager;

	private List<ChampionRoleScore> championRoleScores;
	private String averageScore;
	private String message;
	private Resources res;

	public HipsterAdapter(ChampIdMapManager champIdMapManager, Resources res) {
		this.champIdMapManager = champIdMapManager;
		this.res = res;
	}

	@Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view;
		if (i == 0) {
			view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_header, null);
			return new HeaderHolder(view);
		} else {
			view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_champ_hipster, null);
			return new ChampViewHolder(view);
		}
	}

	@Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof ChampViewHolder) {
			ChampViewHolder champViewHolder = (ChampViewHolder) holder;
			ChampionRoleScore championRoleScore = championRoleScores.get(getItemCount() - position - 1);
			String champName = champIdMapManager.NameForId(championRoleScore.champId);
			champViewHolder.champName.setText(champName);
			champViewHolder.champRole.setText(championRoleScore.role);
			champViewHolder.hipsterScore.setText(championRoleScore.score + "");
		} else if (holder instanceof HeaderHolder) {
			HeaderHolder headerHolder = (HeaderHolder) holder;
			headerHolder.averageScore.setText(averageScore);
			headerHolder.message.setText(message);
		}
	}

	@Override public int getItemCount() {
		if (championRoleScores == null) {
			return 0;
		}
		return championRoleScores.size() + 1;
	}

	@Override public int getItemViewType(int position) {
		if (position == 0) {
			return VIEW_TYPE_HEADER;
		}
		return VIEW_TYPE_CHAMPION;
	}

	public void setData(List<ChampionRoleScore> data, String name) {
		this.championRoleScores = data;
		displayMessageForSummoner(data, name);
	}

	private void displayMessageForSummoner(List<ChampionRoleScore> scores, String name) {
		int average = (int) Utils.calculateAverage(scores);
		String message = res.getString(Utils.StringForAverage(average));
		String score = String.format(res.getString(R.string.score_out_of_hundred), average);
		message = String.format(message, name);
		setHeader(score, message);
	}

	private void setHeader(String score, String message) {
		this.averageScore = score;
		this.message = message;
	}

	public static class ChampViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.champName) TextView champName;
		@Bind(R.id.role) TextView champRole;
		@Bind(R.id.score) TextView hipsterScore;

		public ChampViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}

	public static class HeaderHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.averageScore) TextView averageScore;
		@Bind(R.id.message) TextView message;

		public HeaderHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
