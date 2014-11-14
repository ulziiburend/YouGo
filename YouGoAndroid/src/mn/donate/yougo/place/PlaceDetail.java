package mn.donate.yougo.place;

import java.sql.SQLException;

import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.DatabaseHelper;
import mn.donate.yougo.datamodel.Place;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

public class PlaceDetail extends ActionBarActivity implements
		OnSliderClickListener {
	private ActionBar bar;
	private DatabaseHelper helper;
	Place item;
	SliderLayout sliderLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_det);
		sliderLayout = (SliderLayout) findViewById(R.id.place_det_slider);

		helper = new DatabaseHelper(this);
		try {
			item = helper.getPlaceDao().queryForId(
					getIntent().getExtras().getInt("place_id", 1));
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
}
