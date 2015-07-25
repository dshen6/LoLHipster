package util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by cfalc on 7/25/15.
 */
public class ResizeAnimation extends Animation {
	private static final String TAG = "Resize_ANIM";
	View view;
	int startHeight;
	int diff;

	public ResizeAnimation(View v, int targetHeight) {
		view = v;
		startHeight = v.getLayoutParams().height;
		diff = targetHeight - startHeight;
	}

	@Override protected void applyTransformation(float interpolatedTime, Transformation t) {
		view.getLayoutParams().height = startHeight + (int) (diff * interpolatedTime);
		view.requestLayout();
	}

	@Override public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
	}

	@Override public boolean willChangeBounds() {
		return true;
	}
}
