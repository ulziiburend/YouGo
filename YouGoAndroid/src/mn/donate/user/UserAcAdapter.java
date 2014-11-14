package mn.donate.user;

import java.util.List;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.UserActivity;
import mn.donate.yougo.text.Light;
import mn.donate.yougo.text.Regular;
import mn.donate.yougo.utils.CircleImageView;
import mn.donate.yougo.utils.MySingleton;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class UserAcAdapter extends ArrayAdapter<UserActivity> {
	Context mContext;
	private ImageLoader mImageLoader;

	// private int lastPosition = -1;

	public UserAcAdapter(Context context, List<UserActivity> objects) {
		super(context, 0, 0, objects);
		this.mContext = context;
		// TODO Auto-generated constructor stub

		mImageLoader =MySingleton.getInstance(context).getImageLoader();
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		UserActivity item = getItem(position);
		Holder hol = null;
		if (v == null) {
			v = ((Activity) mContext).getLayoutInflater().inflate(
					R.layout.user_ac_item, parent, false);
			hol = new Holder();
			hol.place_img = (CircleImageView) v
					.findViewById(R.id.user_ac_place_img);
			hol.image = (NetworkImageView) v.findViewById(R.id.user_ac_image);
			hol.place_name = (Regular) v.findViewById(R.id.user_ac_placeName);
			hol.title = (Regular) v.findViewById(R.id.user_ac_title);
			hol.date = (Light) v.findViewById(R.id.user_ac_time);
			hol.rate = (ImageView) v.findViewById(R.id.user_ac_rating);
			v.setTag(hol);
		} else
			hol = (Holder) v.getTag();
		String imageIp = mContext.getResources()
				.getString(R.string.mainIpImage);
		hol.place_img.setImageUrl(imageIp + item.place_id + "/"
				+ item.place_img, mImageLoader);
		hol.place_name.setText(item.place_name);
		if (item.rating > 2)
			hol.rate.setImageResource(R.drawable.rate_three);
		if (item.rating > 1 && item.rating < 3)
			hol.rate.setImageResource(R.drawable.rate_two);
		hol.title.setText(item.title);
		String dates[] = item.created_date.split("-");
		DateTime time = new DateTime(Integer.parseInt(dates[0]),
				Integer.parseInt(dates[1]), Integer.parseInt(dates[2]),
				Integer.parseInt(dates[3]), Integer.parseInt(dates[4]),
				Integer.parseInt(dates[5]));

		hol.date.setText(DateUtils.getRelativeTimeSpanString(mContext, time)
				+ "");
		hol.image.setImageUrl(imageIp

		+ item.place_id + "/" + item.user_id + "/" + item.image, mImageLoader);

		return v;
	}

	class Holder {
		NetworkImageView image;
		CircleImageView place_img;
		ImageView rate;
		Regular place_name;
		Regular title;
		Light date;
	}

}