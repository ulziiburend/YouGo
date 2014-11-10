package mn.donate.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mn.doanate.yougo.adapter.PlaceAdapter;
import mn.doanate.yougo.adapter.UserAcAdapter;
import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.DatabaseHelper;
import mn.donate.yougo.datamodel.Place;
import mn.donate.yougo.datamodel.UserActivity;
import mn.donate.yougo.place.PlaceFrag.PagerAdapter;
import mn.donate.yougo.place.PlaceFrag.SampleListFragment;
import mn.donate.yougo.text.Bold;
import mn.donate.yougo.text.Light;
import mn.donate.yougo.utils.CircleImageView;
import mn.donate.yougo.utils.CustomRequest;
import mn.donate.yougo.utils.LruBitmapCache;
import mn.donate.yougo.utils.Utils;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class UserAc extends ActionBarActivity {
	private SharedPreferences preferences;
	ViewPager pager;
	Bold name;
	Light followers;
	Light following;
	CircleImageView user_img;
	private ImageLoader mImageLoader;

	private RequestQueue mRequestQueue;
	private ActionBar bar;
	private String userFeed[] = new String[2];
	public static String UserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		userFeed[0] = getString(R.string.myActivity);
		userFeed[1] = getString(R.string.mySaved);
		bar = getSupportActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setHomeButtonEnabled(true);
		// views
		name = (Bold) findViewById(R.id.user_name);
		followers = (Light) findViewById(R.id.user_followers);
		following = (Light) findViewById(R.id.user_following);
		user_img = (CircleImageView) findViewById(R.id.user_img);
		pager = (ViewPager) findViewById(R.id.user_pager);
		preferences = getSharedPreferences("user", MODE_PRIVATE);
		mRequestQueue = Volley.newRequestQueue(this);

		mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
		UserId = preferences.getString("my_id", "0");
		name.setText(preferences.getString("username", ""));
		followers.setText(preferences.getString("followers", ""));
		following.setText(preferences.getString("following", ""));
		user_img.setImageUrl(preferences.getString("pro_img", ""), mImageLoader);

		pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), userFeed));
	}

	public class PagerAdapter extends FragmentStatePagerAdapter {
		String[] title;

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public PagerAdapter(FragmentManager fm, String[] title) {
			super(fm);
			this.title = title;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return title[position];
		}

		@Override
		public int getCount() {
			return title.length;
		}

		@Override
		public Fragment getItem(int position) {

			return UserTabFrag.newInstance(position);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.user, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		// if (id == R.id.action_search_ad) {
		// mPagerAdapter.filterCar();
		// }
		if (id == R.id.action_logout) {

		}
		if (id == android.R.id.home)
			onBackPressed();
		return true;
	}

	public static class UserTabFrag extends Fragment implements
			OnScrollListener {

		private int index = 0;
		private ListView mListView;
		private ArrayList<UserActivity> mListItems;
		private View load_footer;
		private RequestQueue mRequestQueue;
		private UserAcAdapter adapter;
		private boolean isFinish = false;
		private DatabaseHelper helper;
		private boolean flag_loading = false;
		private View v;
		int mNum;
		String searchQuery;

		public static Fragment newInstance(int num) {

			UserTabFrag f = new UserTabFrag();
			Bundle b = new Bundle();
			b.putInt("num", num + 1);
			f.setArguments(b);
			return f;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// mPosition = getArguments().getInt(ARG_POSITION);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;

			mListItems = new ArrayList<UserActivity>();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			v = inflater.inflate(R.layout.fragment_list, container, false);
			mListView = (ListView) v.findViewById(R.id.listView);
			load_footer = inflater.inflate(R.layout.list_load_footer,
					mListView, false);
			mListView.addFooterView(load_footer);
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			helper = new DatabaseHelper(getActivity());

			mRequestQueue = Volley.newRequestQueue(getActivity());
			adapter = new UserAcAdapter(getActivity(), mListItems);
			mListView.setAdapter(adapter);

			if (Utils.isNetworkAvailable(getActivity())) {
				getActivityFeed(index, UserAc.UserId);
			} else
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.noNet),
						Toast.LENGTH_SHORT).show();
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					// Bundle b = new Bundle();
					// b.putInt("ad_id", mListItems.get(position - 1).id);

					// Intent adIntent = new Intent(getActivity(),
					// AdDetail.class);
					// adIntent.putExtras(b);
					// startActivity(adIntent);
				}
			});
		}

		private void getActivityFeed(int sIndex, String user_id) {
			mListView.addFooterView(load_footer);
			CustomRequest adReq = new CustomRequest(Method.GET,
					this.getString(R.string.mainIp) + "activity/user_id="
							+ user_id + "&index=" + sIndex, null,
					new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							Log.e("responce", response + "");
							try {
								if (response != null
										&& response.getInt("response") == 1) {

									int num_rows = response
											.getInt("data_count");
									Log.i("row", num_rows + "");
									if (num_rows < 10) {
										isFinish = true;
									}
									index = index + 10;
									JSONArray data = response
											.getJSONArray("data");
									makeAc(data);

								}
								flag_loading = false;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							mListView.removeFooterView(load_footer);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("error", error.getMessage() + "");
							mListView.removeFooterView(load_footer);
						}

					}) {

			};
			mRequestQueue.add(adReq);
		}

		protected void makeAc(JSONArray data) throws JSONException,
				SQLException {
			// TODO Auto-generated method stub
			if (data.length() > 0) {
				helper.deleteUserAC();
				for (int i = 0; i < data.length(); i++) {
					UserActivity ac = new UserActivity();
					JSONObject obj = data.getJSONObject(i);
					ac.place_id = obj.getInt("place_id");
					ac.place_name = obj.getString("place_name");
					ac.place_img = obj.getString("place_img");
					ac.user_id = obj.getInt("user_id");
					ac.title = obj.getString("title");
					ac.rating = obj.getInt("rating");

					ac.image = obj.getString("image_url");
					ac.created_date = obj.getString("created_date");

					helper.getUserAcDao().create(ac);
					mListItems.add(ac);

				}
			} else {
				isFinish = true;
			}

			adapter.notifyDataSetChanged();
			mListView.setOnScrollListener(this);
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			if (firstVisibleItem + visibleItemCount == totalItemCount
					&& totalItemCount != 0) {
				if (flag_loading == false && isFinish == false) {
					flag_loading = true;
					getActivityFeed(index, UserAc.UserId);
				}
			}
		}

	}

}
