package com.mobsoftmaster.bloodpressurediary2;

import com.mobsoftmaster.bloodpressurediary2.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.Locale;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class ScreenSlidePageFragment extends Fragment {
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	final String LOG_TAG = "myLogs";

	Bitmap bitmap;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}

	public class ScreenResolution {
		int width;
		int height;
	}

	@SuppressLint("NewApi")
	private ScreenResolution GetResolution() {
		ScreenResolution sr = new ScreenResolution();
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Point size = new Point();
			display.getSize(size);
			sr.width = size.x;
			sr.height = size.y;
		} else {
			sr.width = display.getWidth();
			sr.height = display.getHeight();
		}
		return sr;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_slide_page, container, false);

		final ImageView screen = (ImageView) rootView
				.findViewById(R.id.imageViewScreenProfile);

		ScreenResolution sr = GetResolution();
		final int width = sr.width;
		final int height = sr.height;

		String land = "";
		String lang = "";
		if (locale_define.equals("ru"))
			lang = "ru";
		else
			lang = "en";
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			land = "land_";
		int resID = getResources().getIdentifier(
				viewName[mPageNumber] + land + lang, "raw",
				getActivity().getPackageName());
		if (bitmap != null)
			bitmap.recycle();
		bitmap = decodeSampledBitmapFromResource(getResources(), resID,
				width / 4, height / 4);
		screen.setImageBitmap(bitmap);
		if (mPageNumber == 8) {
			SharedPreference.SavePreferences(getActivity(),
					SharedPreference.s_tutorial, false);
			getActivity().onBackPressed();
		}
		return rootView;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
