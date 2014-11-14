package mn.donate.yougo.feed;

import java.util.List;

import mn.donate.user.FriendAc;
import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.UserFeed;
import mn.donate.yougo.text.Bold;
import mn.donate.yougo.text.Light;
import mn.donate.yougo.text.Regular;
import mn.donate.yougo.utils.CircleImageView;
import mn.donate.yougo.utils.MySingleton;
import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class UserFeedAdapter extends ArrayAdapter<UserFeed> {
	Context mContext;
	private ImageLoader mImageLoader;

	// private int lastPosition = -1;
	String id;

	public UserFeedAdapter(Context context, List<UserFeed> mListItems,
			String myID) {
		super(context, 0, 0, mListItems);
		this.mContext = context;
		this.id = myID;
		// TODO Auto-generated constructor stub

		mImageLoader = MySingleton.getInstance(context).getImageLoader();
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		final UserFeed item = getItem(position);
		Holder hol = null;
		if (v == null) {
			v = ((Activity) mContext).getLayoutInflater().inflate(
					R.layout.user_feed_item, parent, false);
			hol = new Holder();
			hol.user = (LinearLayout) v.findViewById(R.id.user_feed_user);
			hol.user_img = (CircleImageView) v
					.findViewById(R.id.user_feed_profile);
			hol.com_image = (NetworkImageView) v
					.findViewById(R.id.user_feed_com_image);
			hol.place_image = (NetworkImageView) v
					.findViewById(R.id.user_feed_place_img);
			hol.place_name = (Bold) v.findViewById(R.id.user_feed_place_name);
			hol.username = (Regular) v.findViewById(R.id.user_feed_username);
			hol.title = (Regular) v.findViewById(R.id.user_feed_title);
			hol.date = (Light) v.findViewById(R.id.user_feed_time);
			hol.rate = (ImageView) v.findViewById(R.id.user_feed_rating);
			v.setTag(hol);
		} else
			hol = (Holder) v.getTag();
		String imageIp = mContext.getResources()
				.getString(R.string.mainIpImage);
		hol.place_image.setImageUrl(imageIp + item.place_id + "/"
				+ item.place_img, mImageLoader);
		hol.place_name.setText(item.place_name);
		hol.user_img.setImageUrl(item.user_img, mImageLoader);
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

		hol.com_image.setImageUrl(imageIp + item.place_id + "/" + item.user_id
				+ "/" + item.image, mImageLoader);
		hol.username.setText(item.username + "");

		hol.user.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				b.putInt("user_id", item.user_id);
				b.putInt("my_id", Integer.parseInt(id));
				Intent userDet = new Intent(mContext, FriendAc.class);
				userDet.putExtras(b);
				mContext.startActivity(userDet);
			}
		});

		return v;
	}

	class Holder {
		LinearLayout user;
		NetworkImageView com_image;
		NetworkImageView place_image;
		CircleImageView user_img;
		ImageView rate;
		Regular username;
		Bold place_name;
		Regular title;
		Light date;
	}

}