package shen.com.lolhipster.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import java.util.List;
import main.java.riotapi.RiotApiException;
import shen.com.lolhipster.R;
import shen.com.lolhipster.api.DaggerHipsterApiComponent;
import shen.com.lolhipster.api.HipsterApi;
import shen.com.lolhipster.api.HipsterApiComponent;
import shen.com.lolhipster.api.models.ChampionRoleScore;
import util.DividerItemDecoration;
import util.ErrorDialogFragment;
import util.ResizeAnimation;
import util.ResizeAwareRelativeLayout;

public class MainActivity extends AppCompatActivity {

	@Bind(R.id.progressSearch) ProgressBar progressSearch;
	@Bind(R.id.summonerName) EditText nameInput;
	@Bind(R.id.recyclerView) RecyclerView recyclerView;
	@Bind(R.id.space) View space;
	@Bind(R.id.legal) View legal;
	@Bind(R.id.poweredBy) View poweredBy;

	private HipsterApi hipsterApi;
	private HipsterAdapter adapter;
	private boolean animated;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		HipsterApiComponent hipsterApiComponent =
				DaggerHipsterApiComponent.builder().applicationModule(new ApplicationModule(getApplication())).build();

		hipsterApi = hipsterApiComponent.hipsterApi();

		adapter = new HipsterAdapter(hipsterApiComponent.champIdMapManager(), getResources());
		recyclerView.addItemDecoration(
				new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_list_item), false, false));
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);

		final ResizeAwareRelativeLayout container = (ResizeAwareRelativeLayout) findViewById(R.id.container);
		container.setOnResizeListener(new ResizeAwareListener());
	}

	private void searchSummoner(final String name) {
		progressSearch.setVisibility(View.VISIBLE);
		new AsyncTask<String, List<ChampionRoleScore>, List<ChampionRoleScore>>() {
			@Override protected List<ChampionRoleScore> doInBackground(String... params) {
				if (params.length < 1) {
					return null;
				}
				String summonerName = params[0];
				try {
					return hipsterApi.getHipsterScoresForSummoner(summonerName);
				} catch (RiotApiException e) {
					ErrorDialogFragment.newInstance(e.getMessage()).show(getFragmentManager(), ErrorDialogFragment.FRAG_TAG);
				}
				return null;
			}

			@Override protected void onPostExecute(List<ChampionRoleScore> championRoleScoreList) {
				if (championRoleScoreList != null) {
					adapter.setData(championRoleScoreList, name);
					adapter.notifyDataSetChanged();
				}
				progressSearch.setVisibility(View.GONE);
			}
		}.execute(name);
	}

	private final class ResizeAwareListener extends ResizeAwareRelativeLayout.RelativeLayoutOnResizeListener {
		@Override public void onResize(int x, int y, int xOld, int yOld) {
			if (y == 0 || yOld == 0) { // ignore if we're just starting up
				return;
			}

			final int yDelta = yOld - y; // if positive, means keyboard grew, negative means keyboard shrank
			if (yDelta > 0) {
				if (!animated) {
					animated = true;
					RESIZE_TO_ZERO.apply(space, 0);
				}
			}
			legal.post(new Runnable() {
				@Override public void run() {
					legal.setVisibility(yDelta > 0 ? View.GONE : View.VISIBLE);
				}
			});
			poweredBy.post(new Runnable() {
				@Override public void run() {
					poweredBy.setVisibility(yDelta > 0 ? View.GONE : View.VISIBLE);
				}
			});
		}
	}

	@OnClick(R.id.poweredBy) void routeToChampionGG() {
		String url = "http://champion.gg";
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	@OnClick(R.id.legal) void displayLegal() {
		ErrorDialogFragment.newInstance(getString(R.string.legal_jargon))
				.show(getFragmentManager(), ErrorDialogFragment.FRAG_TAG);
	}

	@OnEditorAction(R.id.summonerName) boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		boolean handled = false;
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			handled = true;
			if (TextUtils.isEmpty(nameInput.getText())) {
				nameInput.setError(getString(R.string.error_empty_input));
			} else {
				nameInput.setError(null);
				searchSummoner(nameInput.getText().toString().trim());
			}
		}
		return handled;
	}

	private static final ButterKnife.Action<View> RESIZE_TO_ZERO = new ButterKnife.Action<View>() {
		@Override public void apply(View view, int index) {
			Animation scale = new ResizeAnimation(view, 0);
			scale.setInterpolator(new DecelerateInterpolator());
			scale.setDuration(400);
			scale.setFillAfter(true);
			view.startAnimation(scale);
		}
	};
}
