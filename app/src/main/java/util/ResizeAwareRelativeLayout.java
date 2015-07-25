package util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by cfalc on 7/25/15.
 */
public class ResizeAwareRelativeLayout extends RelativeLayout {

	public RelativeLayoutOnResizeListener resizeListener;

	public ResizeAwareRelativeLayout(Context context) {
		super(context);
	}

	public ResizeAwareRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResizeAwareRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override protected void onSizeChanged(int x, int y, int xOld, int yOld) {
		super.onSizeChanged(x, y, xOld, yOld);
		// export the onSizeChanged event to other listeners
		if (resizeListener != null) {
			resizeListener.onResize(x, y, xOld, yOld);
		}
	}

	public void setOnResizeListener(RelativeLayoutOnResizeListener listener) {
		resizeListener = listener;
	}

	public static class RelativeLayoutOnResizeListener {
		public void onResize(int x, int y, int xOld, int yOld) {
		}
	}
}
