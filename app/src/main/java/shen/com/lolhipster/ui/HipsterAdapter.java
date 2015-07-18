package shen.com.lolhipster.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import shen.com.lolhipster.R;
import shen.com.lolhipster.api.ChampIdMapManager;
import shen.com.lolhipster.api.models.ChampionRoleScore;

/**
 * Created by cfalc on 7/6/15.
 */
public class HipsterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int VIEW_TYPE_HEADER = 0;
	private static final int VIEW_TYPE_CHAMPION = 1;

	private List<ChampionRoleScore> championRoleScores;
	private String averageScore;
	private String message;
	public HipsterAdapter(List<ChampionRoleScore> scores) {
		this.championRoleScores = scores;
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
			String champName = ChampIdMapManager.getInstance().NameForId(championRoleScore.champId);
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

	public void setData(List<ChampionRoleScore> data) {
		this.championRoleScores = data;
	}

	public void setHeader(String score, String message) {
		this.averageScore = score;
		this.message = message;
	}

	public static class ChampViewHolder extends RecyclerView.ViewHolder {
		public TextView champName;
		public TextView champRole;
		public TextView hipsterScore;

		public ChampViewHolder(View view) {
			super(view);
			champName = (TextView) view.findViewById(R.id.champName);
			champRole = (TextView) view.findViewById(R.id.role);
			hipsterScore = (TextView) view.findViewById(R.id.score);
		}
	}

	public static class HeaderHolder extends RecyclerView.ViewHolder {
		public TextView averageScore;
		public TextView message;
		public HeaderHolder(View view) {
			super(view);
			averageScore = (TextView) view.findViewById(R.id.averageScore);
			message = (TextView) view.findViewById(R.id.message);
		}
	}
}
