package mn.donate.user;

import java.util.List;

import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.SavedPlace;
import mn.donate.yougo.text.Bold;
import mn.donate.yougo.text.Light;
import mn.donate.yougo.utils.MySingleton;
import mn.donate.yougo.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class SavedPlaceAdapter extends ArrayAdapter<SavedPlace> {
	Context mContext;
	private ImageLoader mImageLoader;

	// private int lastPosition = -1;

	public SavedPlaceAdapter(Context context, List<SavedPlace> objects) {
		super(context, 0, 0, objects);
		this.mContext = context;
		// TODO Auto-generated constructor stub

		mImageLoader = MySingleton.getInstance(context).getImageLoader();
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		SavedPlace place = getItem(position);
		Holder hol = null;
		if (v == null) {
			v = ((Activity) mContext).getLayoutInflater().inflate(
					R.layout.place_item, parent, false);
			hol = new Holder();
			hol.image = (NetworkImageView) v
					.findViewById(R.id.place_item_image);
			hol.name = (Bold) v.findViewById(R.id.place_item_name);
			hol.rate = (ImageView) v.findViewById(R.id.place_item_rate);
			hol.distance = (Light) v.findViewById(R.id.place_item_distance);
			hol.tag = (Bold) v.findViewById(R.id.place_item_tag);
			hol.hot = (ImageView) v.findViewById(R.id.place_item_hot);
			v.setTag(hol);
		} else
			hol = (Holder) v.getTag();
		hol.name.setText(place.name);
		if (position < 4)
			hol.hot.setVisibility(View.VISIBLE);
		else
			hol.hot.setVisibility(View.GONE);
		if (place.rating > 2)
			hol.rate.setImageResource(R.drawable.rate_three);
		if (place.rating > 1 && place.rating < 3)
			hol.rate.setImageResource(R.drawable.rate_two);
		String tag1 = !place.tag1.equals("null") ? "#" + place.tag1 : "";
		String tag2 = !place.tag2.equals("null") ? "#" + place.tag2 : "";
		String tag3 = !place.tag3.equals("null") ? "#" + place.tag3 : "";
		hol.tag.setText(tag1 + tag2 + tag3);
		hol.distance.setText(Utils
				.numberToFormat((int) (place.distance * 1000)) + "m");
		if (place.images.length() > 0)
			hol.image
					.setImageUrl(
							mContext.getResources().getString(
									R.string.mainIpImage)
									+ place.place_id
									+ "/"
									+ place.images.split(",")[0], mImageLoader);

		// Animation animation = AnimationUtils.loadAnimation(getContext(),
		// (position > lastPosition) ? R.anim.up_from_bottom
		// : R.anim.down_from_top);
		// v.startAnimation(animation);
		// lastPosition = position;
		return v;
	}

	class Holder {
		NetworkImageView image;
		Bold name;
		Light distance;
		Bold tag;

		ImageView hot;
		ImageView rate;
	}

}
