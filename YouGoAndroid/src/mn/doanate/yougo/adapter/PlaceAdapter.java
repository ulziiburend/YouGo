/*
 * Copyright (C) 2014 Lucas Rocha
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

package mn.doanate.yougo.adapter;

import java.util.List;

import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.Place;
import mn.donate.yougo.text.Bold;
import mn.donate.yougo.text.Light;
import mn.donate.yougo.utils.LruBitmapCache;
import mn.donate.yougo.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class PlaceAdapter extends ArrayAdapter<Place> {
	Context mContext;
	private ImageLoader mImageLoader;
	private RequestQueue mRequestQueue;
//	private int lastPosition = -1;

	public PlaceAdapter(Context context, List<Place> objects) {
		super(context, 0, 0, objects);
		this.mContext = context;
		// TODO Auto-generated constructor stub
		mRequestQueue = Volley.newRequestQueue(mContext);

		mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		Place place = getItem(position);
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
			hol.hot=(ImageView)v.findViewById(R.id.place_item_hot);
			v.setTag(hol);
		} else
			hol = (Holder) v.getTag();
		hol.name.setText(place.name);
		if(position<4)
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
