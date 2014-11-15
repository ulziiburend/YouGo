package mn.donate.yougo.feed;

import java.sql.SQLException;
import java.util.List;

import mn.donate.user.UserAc;
import mn.donate.yougo.MainActivity;
import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.DatabaseHelper;
import mn.donate.yougo.datamodel.UserFeed;
import mn.donate.yougo.utils.CustomRequest;
import mn.donate.yougo.utils.MySingleton;
import mn.donate.yougo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class FeedFrag extends Fragment implements OnRefreshListener,
		OnScrollListener {
	private ListView mListView;
	private List<UserFeed> mListItems;
	private View v;
	int mNum;
	private DatabaseHelper helper;
	private View load_footer;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private TextView nodata;
	private SwipeRefreshLayout swipeLayout;
	private UserFeedAdapter adapter;
	private int index = 0;
	private boolean isFinish = false;
	private String UserId;
	private boolean flag_loading;
	private SharedPreferences preferences;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mPosition = getArguments().getInt(ARG_POSITION);
		mNum = getArguments() != null ? getArguments().getInt("num") : 1;

	}

	public static FeedFrag newInstance(int section) {

		FeedFrag f = new FeedFrag();
		Bundle b = new Bundle();
		b.putInt(ARG_SECTION_NUMBER, section);
		f.setArguments(b);

		return f;
	}

	public FeedFrag() {
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		preferences = getActivity().getSharedPreferences("user", 0);
		UserId = preferences.getString("my_id", "0");
		helper = new DatabaseHelper(getActivity());
		try {
			mListItems = helper.getUserFeedDao().queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter = new UserFeedAdapter(getActivity(), mListItems, UserId);
		mListView.setAdapter(adapter);

		if (Utils.isNetworkAvailable(getActivity())) {
			helper.deleteFeed();
			getActivityFeed(index, UserId, false);
		} else
			Toast.makeText(getActivity(),
					getActivity().getString(R.string.noNet), Toast.LENGTH_SHORT)
					.show();
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

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.fragment_list, container, false);
		mListView = (ListView) v.findViewById(R.id.listView);
		load_footer = inflater.inflate(R.layout.list_load_footer, mListView,
				false);
		nodata = (TextView) v.findViewById(R.id.list_nodata);
		mListView.addFooterView(load_footer);
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		return v;
	}

	private void getActivityFeed(int sIndex, String user_id,
			final boolean refresh) {
		mListView.addFooterView(load_footer);
		CustomRequest adReq = new CustomRequest(Method.GET,
				this.getString(R.string.mainIp) + "feed/user_id=" + user_id
						+ "&index=" + sIndex, null, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response != null
									&& response.getInt("response") == 1) {

								int num_rows = response.getInt("data_count");
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
		MySingleton.getInstance(getActivity()).addToRequestQueue(adReq);
	}

	protected void makeAc(JSONArray data, boolean refresh)
			throws JSONException, SQLException {
		// TODO Auto-generated method stub
		if (data.length() > 0) {
			if (refresh)
				mListItems.clear();
			for (int i = 0; i < data.length(); i++) {
				UserFeed ac = new UserFeed();
				JSONObject obj = data.getJSONObject(i);
				ac.place_id = obj.getInt("place_id");
				ac.place_name = obj.getString("place_name");
				ac.place_img = obj.getString("place_img");
				ac.user_id = obj.getInt("user_id");
				ac.title = obj.getString("title");
				ac.rating = obj.getInt("rating");
				ac.username = obj.getString("username");
				ac.user_img = obj.getString("profile_img");

				ac.image = obj.getString("image_url");
				ac.created_date = obj.getString("created_date");

				helper.getUserFeedDao().create(ac);
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
				getActivityFeed(index, UserAc.UserId, false);
			}
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		index = 0;
		getActivityFeed(index, UserId, true);
	}

}
