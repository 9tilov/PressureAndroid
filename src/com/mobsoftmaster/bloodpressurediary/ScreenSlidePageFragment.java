/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobsoftmaster.bloodpressurediary;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.Locale;
import android.content.res.Configuration;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy
 * title indicating the page number, along with some dummy text.
 * 
 * <p>
 * This class is used by the {@link CardFlipActivity} and
 * {@link ScreenSlideActivity} samples.
 * </p>
 */
public class ScreenSlidePageFragment extends Fragment {
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	final String LOG_TAG = "myLogs";

	OrientationEventListener myOrientationEventListener;

	final String locale_define = Locale.getDefault().getLanguage();

	final String[] viewName = new String[] { "ic_screen_title_",
			"ic_screen_profile_", "ic_screen_profile_add_", "ic_screen_stat_",
			"ic_screen_stat_add_", "ic_screen_graph_", "ic_screen_settings_",
			"ic_title_end_", "dummy" };

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int mPageNumber;
	SharedPreference sharedPref;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the
	 * given page number.
	 */
	public static ScreenSlidePageFragment create(int pageNumber) {
		ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public ScreenSlidePageFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_slide_page, container, false);

		sharedPref = new SharedPreference(getActivity());

		final ImageView screen = (ImageView) rootView
				.findViewById(R.id.imageViewScreenProfile);

		myOrientationEventListener = new OrientationEventListener(getActivity()) {

			@Override
			public void onOrientationChanged(int arg0) {
				int resID;
				if (locale_define.equals("ru")) {
					if (arg0 == 270)
						resID = getResources().getIdentifier(
								viewName[mPageNumber] + "land_ru", "raw",
								getActivity().getPackageName());
					else
						resID = getResources().getIdentifier(
								viewName[mPageNumber] + "ru", "raw",
								getActivity().getPackageName());
				} else {
					if (arg0 == 270) {
						resID = getResources().getIdentifier(
								viewName[mPageNumber] + "land_en", "raw",
								getActivity().getPackageName());
					} else {
						resID = getResources().getIdentifier(
								viewName[mPageNumber] + "en", "raw",
								getActivity().getPackageName());

					}
				}
				screen.setImageResource(resID);
			}
		};

		if (myOrientationEventListener.canDetectOrientation()) {
			int resID;
			if (locale_define.equals("ru")) {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					resID = getResources().getIdentifier(
							viewName[mPageNumber] + "land_ru", "raw",
							getActivity().getPackageName());
				} else {
					resID = getResources().getIdentifier(
							viewName[mPageNumber] + "ru", "raw",
							getActivity().getPackageName());
				}
			} else {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					resID = getResources().getIdentifier(
							viewName[mPageNumber] + "land_en", "raw",
							getActivity().getPackageName());
				} else {
					resID = getResources().getIdentifier(
							viewName[mPageNumber] + "en", "raw",
							getActivity().getPackageName());
				}
				screen.setImageResource(resID);
				myOrientationEventListener.enable();
			}

			if (mPageNumber == 8) {
				// sharedPref.SavePreferences(sharedPref.s_tutorial, false);
				getActivity().onBackPressed();
			}
		}
		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		myOrientationEventListener.disable();
	}

}
