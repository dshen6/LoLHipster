package shen.com.lolhipster.ui;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

	@OnClick(R.id.legal) void displayLegal() {
		ErrorDialogFragment.newInstance(getString(R.string.legal_jargon))
				.show(getFragmentManager(), ErrorDialogFragment.FRAG_TAG);
	}

	@OnEditorAction(R.id.summonerName) boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		boolean handled = false;
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			handled = true;
			searchSummoner(nameInput.getText().toString().trim());
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

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		adapter = new HipsterAdapter(hipsterApiComponent.champIdMapManager(), getResources());
		Drawable divider = getResources().getDrawable(R.drawable.divider_list_item);
		recyclerView.addItemDecoration(new DividerItemDecoration(divider, false, false));
		recyclerView.setAdapter(adapter);

		final ResizeAwareRelativeLayout container = (ResizeAwareRelativeLayout) findViewById(R.id.container);
		container.setOnResizeListener(new ResizeAwareRelativeLayout.RelativeLayoutOnResizeListener() {
			@Override public void onResize(int x, int y, int xOld, int yOld) {
				if (y == 0 || yOld == 0) { // ignore if we're just starting up
					return;
				}

				int yDelta = yOld - y; // if positive, means keyboard grew, negative means keyboard shrank
				if (yDelta > 0) {
					if (!animated) {
						animated = true;
						RESIZE_TO_ZERO.apply(space, 0);
					}
				}
			}
		});
	}

	private void searchSummoner(final String name) {
		progressSearch.setVisibility(View.VISIBLE);
		new AsyncTask<String, Void, Void>() {
			@Override protected Void doInBackground(String... params) {
				if (params.length < 1) {
					return null;
				}
				try {
					List<ChampionRoleScore> championRoleScoreList = hipsterApi.getHipsterScoresForSummoner(params[0]);
					adapter.setData(championRoleScoreList, name);
				} catch (RiotApiException e) {
					ErrorDialogFragment.newInstance(e.getMessage()).show(getFragmentManager(), ErrorDialogFragment.FRAG_TAG);
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
}
