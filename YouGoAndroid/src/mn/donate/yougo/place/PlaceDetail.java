package mn.donate.yougo.place;

import java.sql.SQLException;

import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.DatabaseHelper;
import mn.donate.yougo.datamodel.Place;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;

public class PlaceDetail extends ActionBarActivity implements
		OnSliderClickListener {
	private ActionBar bar;
	private DatabaseHelper helper;
	Place item;
	SliderLayout sliderLayout;
	private ShareActionProvider mActionProvider;

	// NetworkImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		FadingActionBarHelper acHelper = new FadingActionBarHelper()
				.actionBarBackground(R.color.mainColor)
				.headerLayout(R.layout.place_det_img)
				.contentLayout(R.layout.place_det);
		setContentView(acHelper.createView(this));

		acHelper.initActionBar(this);
		bar = getSupportActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setIcon(android.R.color.transparent);
		// image = (NetworkImageView) findViewById(R.id.place_det_img);
		sliderLayout = (SliderLayout) findViewById(R.id.place_det_slider);

		helper = new DatabaseHelper(this);
		try {
			item = helper.getPlaceDao().queryForId(
					getIntent().getExtras().getInt("place_id", 1));
			// image.setImageUrl(getString(R.string.mainIpImage) + item.place_id
			// + "/" + item.images, MySingleton.getInstance(this)
			// .getImageLoader());
			initImage();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		bar = getSupportActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setHomeButtonEnabled(true);
	}

	private void initImage() {
		String images[] = item.images.split(",");

		for (String url : images) {
			if (url.length() > 1) {
				TextSliderView textSliderView = new TextSliderView(this);
				textSliderView
						.description(item.tag1)
						.image(getString(R.string.mainIpImage) + item.place_id
								+ "/" + url)
						.setScaleType(BaseSliderView.ScaleType.CenterCrop)
						.setOnSliderClickListener(this);

				textSliderView.getBundle().putString("extra", url);

				sliderLayout.addSlider(textSliderView);
			} else {
				TextSliderView textSliderView = new TextSliderView(this);
				textSliderView
						// .description(Utils.numberToFormat(item.price) + " ₮")
						.image(R.drawable.place)
						.setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
						.setOnSliderClickListener(this);

				textSliderView.getBundle().putString("extra",
						R.drawable.place + "");

				sliderLayout.addSlider(textSliderView);
				break;
			}
		}

		sliderLayout.setPresetTransformer(SliderLayout.Transformer.Tablet);
		sliderLayout
				.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
		sliderLayout.setCustomAnimation(new DescriptionAnimation());
		sliderLayout.setDuration(10000);
	}

	@Override
	public void onSliderClick(BaseSliderView slider) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		// if (id == R.id.action_search_ad) {
		// mPagerAdapter.filterCar();
		// }
		if (mActionProvider != null) {
			mActionProvider.setShareIntent(new Intent());
		}
		if (id == android.R.id.home)
			onBackPressed();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detail, menu);
		MenuItem shareItem = menu.findItem(R.id.action_share);

		// Need to use MenuItemCompat to retrieve the Action Provider
		mActionProvider = (ShareActionProvider) MenuItemCompat
				.getActionProvider(shareItem);
		return true;
	}

}
