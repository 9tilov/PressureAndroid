package com.mobsoftmaster.bloodpressurediary2;

import com.mobsoftmaster.bloodpressurediary2.R;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

	boolean is_adding;
	final String LOG_TAG = "myLogs";

	String name_field, email_field;

	Dialog dialog;

	public EditText name, email;

	/**
	 * Create a new instance of MyDialogFragment, providing "num" as an
	 * argument.
	 */
	static EditNameDialog newInstance(boolean is_adding, String name,
			String email) {
		EditNameDialog dialog = new EditNameDialog();
		Bundle args = new Bundle();
		args.putBoolean("isAdding", is_adding);
		args.putString("name", name);
		args.putString("email", email);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		is_adding = getArguments().getBoolean("isAdding");
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

		btnEmailFromAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				is_adding = false;
				dialog.dismiss();
				getDialog().dismiss();
				EditNameDialogListener activity = (EditNameDialogListener) getActivity();
				activity.onFinishEditDialog(false, name.getText().toString(),
						email.getText().toString());
			}
		});
		btnEmailTyping.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public interface EditNameDialogListener {
		void onFinishEditDialog(boolean is_adding, String inputText_name,
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
		return view;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			// Return input text to activity
			EditNameDialogListener activity = (EditNameDialogListener) getActivity();
			if (is_adding)
				activity.onFinishEditDialog(true, name.getText().toString(),
						email.getText().toString());
			else
				activity.onFinishEditDialog(false, name.getText().toString(),
						email.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}
}
