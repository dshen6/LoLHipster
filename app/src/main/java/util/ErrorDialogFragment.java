package util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by cfalc on 7/17/15.
 */
public class ErrorDialogFragment extends DialogFragment {

	public static final String FRAG_TAG = "com.ekoapp.eko.ERROR_DIALOG";

	public static ErrorDialogFragment newInstance(String errorMessage) {
		ErrorDialogFragment frag = new ErrorDialogFragment();
		Bundle args = new Bundle();
		args.putString("error", errorMessage);
		frag.setArguments(args);
		return frag;
	}

	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
		String errorMessage = getArguments().getString("error");

		return new AlertDialog.Builder(getActivity()).setMessage(errorMessage)
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				})
				.create();
	}
}
