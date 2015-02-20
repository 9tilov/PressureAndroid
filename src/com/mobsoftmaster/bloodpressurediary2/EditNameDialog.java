package com.mobsoftmaster.bloodpressurediary2;

import com.mobsoftmaster.bloodpressurediary2.R;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EditNameDialog extends DialogFragment implements
		OnEditorActionListener {

	boolean isProfileAdition, isGetEmailFromAccount;
	final static String LOG_TAG = "myLogs";

	int id;
	String name_field, email_field;

	Dialog dialog;

	public EditText name, email;

	/**
	 * Create a new instance of MyDialogFragment, providing "num" as an
	 * argument.
	 */
	static EditNameDialog newInstance(int id, boolean isProfileAdition,
			boolean isGetEmailFromAccount, String name, String email) {
		EditNameDialog dialog = new EditNameDialog();
		Log.d(LOG_TAG, "EditNameDialog");
		Log.d(LOG_TAG, "isProfileAdition = " + isProfileAdition);
		Bundle args = new Bundle();
		args.putInt("ids", id);
		args.putBoolean("isAdding", isProfileAdition);
		args.putBoolean("isGetEmailFromAccount", isGetEmailFromAccount);
		args.putString("name", name);
		args.putString("email", email);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "onCreate");
		id = getArguments().getInt("ids");
		isProfileAdition = getArguments().getBoolean("isAdding");
		isGetEmailFromAccount = getArguments().getBoolean(
				"isGetEmailFromAccount");
		Log.d(LOG_TAG, "isProfileAdition = " + isProfileAdition);
		name_field = getArguments().getString("name");
		email_field = getArguments().getString("email");
		dialog = new Dialog(super.getActivity());
		dialog.setContentView(R.layout.dialog_chose_email);
		dialog.setTitle(R.string.email_print);
	}

	public void dialogNotif() {
		Button btnEmailFromAccount = (Button) dialog.getWindow().findViewById(
				R.id.btnEmailFromAccount);
		Button btnEmailTyping = (Button) dialog.getWindow().findViewById(
				R.id.btnEmailType);
		Log.d(LOG_TAG, "dialogNotif");
		Log.d(LOG_TAG, "isProfileAdition = " + isProfileAdition);
		btnEmailFromAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				getDialog().dismiss();
				EditNameDialogListener activity = (EditNameDialogListener) getActivity();
				activity.onFinishEditDialog(id, isProfileAdition,
						isGetEmailFromAccount, name.getText().toString(), email
								.getText().toString());
			}
		});
		btnEmailTyping.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isProfileAdition = true;
				isGetEmailFromAccount = false;
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public interface EditNameDialogListener {
		void onFinishEditDialog(int id, boolean isProfileAdition,
				boolean isGetEmailFromAccount, String inputText_name,
				String inputText_email);
	}

	public EditNameDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_add_name, container);
		name = (EditText) view.findViewById(R.id.addNewName);
		email = (EditText) view.findViewById(R.id.addMail);
		getDialog().setTitle(R.string.profile_data);
		// Show soft keyboard automatically
		name.requestFocus();
		name.setText(name_field);
		email.setText(email_field);
		getDialog().getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		email.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if ((hasFocus) && (email.getText().toString().equals(""))) {
					dialogNotif();
				}
			}
		});
		name.setOnEditorActionListener(this);
		email.setOnEditorActionListener(this);
		Log.d(LOG_TAG, "onCreateView");
		Log.d(LOG_TAG, "isProfileAdition = " + isProfileAdition);
		return view;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			// Return input text to activity
			EditNameDialogListener activity = (EditNameDialogListener) getActivity();
			Log.d(LOG_TAG, "onEditorAction");
			Log.d(LOG_TAG, "isProfileAdition = " + isProfileAdition);
			activity.onFinishEditDialog(id, isProfileAdition,
					isGetEmailFromAccount, name.getText().toString(), email
							.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}
}
