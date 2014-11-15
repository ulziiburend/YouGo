package mn.donate.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.UserActivity;
import mn.donate.yougo.text.Bold;
import mn.donate.yougo.text.Light;
import mn.donate.yougo.utils.CircleImageView;
import mn.donate.yougo.utils.CustomRequest;
import mn.donate.yougo.utils.MySingleton;
import mn.donate.yougo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

public class FriendAc extends ActionBarActivity implements OnRefreshListener,
		OnScrollListener, OnClickListener {
	Bold name;
	Light followers;
	int index = 0;
	Light following;
	CircleImageView user_img;
	private ImageLoader mImageLoader;
	Button follow;
	private ActionBar bar;
	public static String UserId;
	private ListView mListView;
	private List<UserActivity> mListItems;
	private View load_footer;
	private UserAcAdapter adapter;
	private boolean isFinish = false;
	private boolean flag_loading = false;
	int mNum;
	TextView nodata;
	SwipeRefreshLayout swipeLayout;

	Bundle b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.friend_profile);

		b = getIntent().getExtras();
		bar = getSupportActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		// views
		name = (Bold) findViewById(R.id.friend_name);
		followers = (Light) findViewById(R.id.friend_followers);
		follow = (Button) findViewById(R.id.friend_follow_but);
		follow.setOnClickListener(this);
		following = (Light) findViewById(R.id.friend_following);
		user_img = (CircleImageView) findViewById(R.id.friend_img);
		mListView = (ListView) findViewById(R.id.friend_listView);

		mImageLoader = MySingleton.getInstance(this).getImageLoader();
		nodata = (TextView) findViewById(R.id.friend_list_nodata);
		load_footer = getLayoutInflater().inflate(R.layout.list_load_footer,
				mListView, false);
		mListView.addFooterView(load_footer);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.friend_swipe_container);
		swipeLayout.setOnRefreshListener(this);

		mListItems = new ArrayList<UserActivity>();

		adapter = new UserAcAdapter(this, mListItems);
		mListView.setAdapter(adapter);
		UserId = b.getInt("user_id") + "";
		if (Utils.isNetworkAvailable(this)) {
			getUserInfo(b.getInt("user_id"), b.getInt("my_id"));
			getActivityFeed(index, FriendAc.UserId, false);
		} else
			Toast.makeText(this, this.getString(R.string.noNet),
					Toast.LENGTH_SHORT).show();
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// Bundle b = new Bundle();
				// b.putInt("ad_id", mListItems.get(position - 1).id);

				// Intent adIntent = new Intent(this,
				// AdDetail.class);
				// adIntent.putExtras(b);
				// startActivity(adIntent);
			}
		});
	}

	private void setUserInfo(JSONObject user) throws JSONException {
		name.setText(user.getString("name"));
		followers.setText(user.getInt("followers") + "");
		following.setText(user.getInt("following") + "");
		user_img.setImageUrl(user.getString("profile_img"), mImageLoader);
		if (user.getInt("you_follow") == 1) {
			follow.setText(getString(R.string.following));
			follow.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.follow_but));
		} else {
			follow.setText(getString(R.string.follow));
			follow.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.unfollow_but));
		}
	}

	private void getUserInfo(int user_id, int my_id) {
		CustomRequest adReq = new CustomRequest(Method.GET,
				this.getString(R.string.mainIp) + "user/user_id=" + user_id
						+ "&my_id=" + my_id, null, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e("responce", response + "");
						try {
							if (response != null
									&& response.getInt("response") == 1) {

								setUserInfo(response.getJSONObject("data"));

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
					}

				}) {

		};
		MySingleton.getInstance(this).addToRequestQueue(adReq);
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

	private void getActivityFeed(int sIndex, String user_id,
			final boolean refresh) {
		mListView.addFooterView(load_footer);
		CustomRequest adReq = new CustomRequest(Method.GET,
				this.getString(R.string.mainIp) + "activity/user_id=" + user_id
						+ "&index=" + sIndex, null, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e("responce sdara", response + "");
						try {
							if (response != null
									&& response.getInt("response") == 1) {

								int num_rows = response.getInt("data_count");
								Log.i("row", num_rows + "");
								if (num_rows < 10) {
									isFinish = true;
								}
								if (num_rows == 0 && adapter.getCount() == 0)
									nodata.setVisibility(View.VISIBLE);
								else {
									nodata.setVisibility(View.GONE);
								}

								index = index + 10;
								JSONArray data = response.getJSONArray("data");
								makeAc(data, refresh);

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
						if (refresh)
							swipeLayout.setRefreshing(false);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
						mListView.removeFooterView(load_footer);
						if (refresh)
							swipeLayout.setRefreshing(false);
					}

				}) {

		};
		MySingleton.getInstance(this).addToRequestQueue(adReq);
	}

	protected void makeAc(JSONArray data, boolean refresh)
			throws JSONException, SQLException {
		// TODO Auto-generated method stub
		if (data.length() > 0) {
			if (refresh)
				mListItems.clear();
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
				getActivityFeed(index, FriendAc.UserId, false);
			}
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		index = 0;
		getActivityFeed(index, UserId, true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == follow) {
			sendFollowReq();
		}
	}

	private boolean sendFollowReq() {
		return false;
	}
}
