package shen.com.lolhipster.ui;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import main.java.riotapi.RiotApiException;
import shen.com.lolhipster.R;
import shen.com.lolhipster.api.HipsterApi;
import shen.com.lolhipster.api.models.ChampionRoleScore;

public class MainActivity extends AppCompatActivity {

	private HipsterAdapter adapter;
	private ProgressBar progressSearch;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final EditText summonerNameInput = (EditText) findViewById(R.id.summonerName);
		progressSearch = (ProgressBar) findViewById(R.id.progressSearch);
		summonerNameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					handled = true;
					searchSummoner(summonerNameInput.getText().toString().trim());
				}
				return handled;
			}
		});

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		adapter = new HipsterAdapter(null);
		Drawable divider = getResources().getDrawable(R.drawable.divider_list_item);
		recyclerView.addItemDecoration(new DividerItemDecoration(divider, false, false));
		recyclerView.setAdapter(adapter);
	}

	private void searchSummoner(final String name) {
		progressSearch.setVisibility(View.VISIBLE);
		new AsyncTask<String, Void, Void>() {
			@Override protected Void doInBackground(String... params) {
				if (params.length < 1) {
					return null;
				}
				try {
					List<ChampionRoleScore> championRoleScoreList =
							HipsterApi.getInstance().getHipsterScoresForSummoner(params[0]);
					adapter.setData(championRoleScoreList);
					displayMessageForSummoner(championRoleScoreList, name);
				} catch (RiotApiException e) {
					ErrorDialogFragment.newInstance(getString(R.string.error_querying))
							.show(getFragmentManager(), ErrorDialogFragment.FRAG_TAG);
				} finally {
					runOnUiThread(new Runnable() {
						@Override public void run() {
							adapter.notifyDataSetChanged();
							progressSearch.setVisibility(View.GONE);
						}
					});
				}
				return null;
			}
		}.execute(name);
	}

	private void displayMessageForSummoner(List<ChampionRoleScore> scores, String name) {
		int average = (int) calculateAverage(scores);
		String message = getString(Utils.StringForAverage(average));
		String score = String.format(getString(R.string.score_out_of_hundred), average);
		message = String.format(message, name);
		adapter.setHeader(score, message);
	}

	private float calculateAverage(List<ChampionRoleScore> scores) {
		if (scores.size() == 0) {
			return 0;
		}
		float sum = 0;
		for (ChampionRoleScore score : scores) {
			sum += score.score;
		}
		return sum / scores.size();
	}
}
