package mn.donate.yougo.place;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.doanate.yougo.adapter.PlaceAdapter;
import mn.donate.yougo.MainActivity;
import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.DatabaseHelper;
import mn.donate.yougo.datamodel.Place;
import mn.donate.yougo.datamodel.PlaceType;
import mn.donate.yougo.utils.CustomRequest;
import mn.donate.yougo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class PlaceFrag extends Fragment implements
		SearchView.OnQueryTextListener {
	View v;
	private ViewPager mViewPager;
	private RequestQueue mRequestQueue;
	private DatabaseHelper helper;
	private ProgressDialog progress;
	private List<PlaceType> catData;
	private PagerAdapter mPagerAdapter;
	private SearchView mSearchView;
	private PlaceFind placeSearch;
	private MenuItem searchItem;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		// LocationManager lm = (LocationManager)
		// getActivity().getSystemService(
		// Context.LOCATION_SERVICE);
		// Location location = lm
		// .getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// // double longitude = location.getLongitude();
		// // double latitude = location.getLatitude();
		// Log.e("location:",longitude+"-"+latitude);
		helper = new DatabaseHelper(getActivity());
		mRequestQueue = Volley.newRequestQueue(getActivity());
		progress = ProgressDialog.show(getActivity(), "",
				getString(R.string.loading));

		if (Utils.isNetworkAvailable(getActivity())) {
			getPlaceCat();
		} else {
			progress.dismiss();
			Toast.makeText(getActivity(), getString(R.string.noNet),
					Toast.LENGTH_SHORT).show();
			try {
				catData = helper.getTypeDao().queryForAll();
				setAdapters();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void setAdapters() {
		progress.dismiss();
		mPagerAdapter = new PagerAdapter(getActivity()
				.getSupportFragmentManager());

		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOffscreenPageLimit(8);

	}

	public class PagerAdapter extends FragmentStatePagerAdapter {
		String queryStr;

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public PagerAdapter(FragmentManager fm, String query) {
			super(fm);
			this.queryStr = query;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return catData.get(position).name;
		}

		@Override
		public int getCount() {
			return catData.size();
		}

		@Override
		public Fragment getItem(int position) {

			return SampleListFragment.newInstance(position,
					catData.get(position).id, queryStr);

		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				"section_number"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.place_main, container, false);

		mViewPager = (ViewPager) v.findViewById(R.id.place_pager);
		// actionBar = ((ActionBarActivity)
		// getActivity()).getSupportActionBar();
		// actionBar.setBackgroundDrawable(null);
		return v;
	}

	private void getPlaceCat() {

		CustomRequest logRequest = new CustomRequest(Method.GET,
				this.getString(R.string.mainIp) + "placeCat", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.i("type", response + "");
						try {
							if (response != null
									&& response.getInt("response") == 1) {
								JSONArray data = response.getJSONArray("data");

								makePlaceCat(data);

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
						Toast.makeText(getActivity(),
								getString(R.string.noData), Toast.LENGTH_SHORT)
								.show();
						
						progress.dismiss();
					}

				}) {

		};
		mRequestQueue.add(logRequest);
	}

	private void makePlaceCat(JSONArray data) throws JSONException,
			SQLException {
		// TODO Auto-generated method stub
		if (data.length() > 0) {
			helper.deleteType();
			for (int i = 0; i < data.length(); i++) {
				PlaceType cat = new PlaceType();
				JSONObject obj = data.getJSONObject(i);
				cat.id = obj.getInt("id");
				cat.name = obj.getString("name");
				helper.getTypeDao().create(cat);
			}
		}
		catData = helper.getTypeDao().queryForAll();
		setAdapters();
	}

	public static class SampleListFragment extends Fragment implements
			OnScrollListener {

		private int index = 0;
		private ListView mListView;
		private ArrayList<Place> mListItems;
		private int cat_id;
		private View load_footer;
		private RequestQueue mRequestQueue;
		private PlaceAdapter adapter;
		private boolean isFinish = false;
		private DatabaseHelper helper;
		private boolean flag_loading = false;
		private View v;
		int mNum;
		String searchQuery;

		public static Fragment newInstance(int num, int catId, String query) {

			SampleListFragment f = new SampleListFragment();
			Bundle b = new Bundle();
			b.putInt("num", num + 1);
			b.putString("query", query);
			b.putInt("cat", catId);
			f.setArguments(b);
			return f;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// mPosition = getArguments().getInt(ARG_POSITION);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;
			cat_id = getArguments().getInt("cat");
			searchQuery = getArguments().getString("query");

			mListItems = new ArrayList<Place>();
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
			adapter = new PlaceAdapter(getActivity(), mListItems);
			mListView.setAdapter(adapter);

			if (Utils.isNetworkAvailable(getActivity())) {
				getPlace(index);
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

		private void getPlace(final int sIndex) {
			mListView.addFooterView(load_footer);
			CustomRequest adReq = new CustomRequest(Method.POST,
					this.getString(R.string.mainIp) + "place", null,
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
									makeAd(data);

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

				@Override
				protected Map<String, String> getParams() {
					Map<String, String> params = new HashMap<String, String>();
					params.put("sindex", sIndex + "");
					params.put("type_id", cat_id + "");
					if(searchQuery!=null )
					params.put("query", searchQuery );
					return params;
				}

			};
			mRequestQueue.add(adReq);
		}

		protected void makeAd(JSONArray data) throws JSONException,
				SQLException {
			// TODO Auto-generated method stub
			if (data.length() > 0) {
				for (int i = 0; i < data.length(); i++) {
					Place place = new Place();
					JSONObject obj = data.getJSONObject(i);
					place.place_id = obj.getInt("id");
					place.name = obj.getString("name");
					place.rating = obj.getInt("rating");
					place.rated_people = obj.getInt("rated_people");
					place.desc = obj.getString("descripton");
					place.images = obj.getString("images_url");
					place.lat = obj.getDouble("lat");
					place.lng = obj.getDouble("lng");
					place.menu_images = obj.optString("menu_images");
					place.credit_cards = obj.getInt("credit_cards");
					place.wifi = obj.getInt("wifi");
					place.phone = obj.optString("phone");
					
					place.work_hour = obj.optString("working hours");
					place.tag1 = obj.getString("tag1_name");
					place.tag2 = obj.getString("tag2_name");
					place.tag3 = obj.getString("tag3_name");
					place.distance = obj.getDouble("distance");
					place.price=obj.getInt("price");
					helper.getPlaceDao().create(place);
					mListItems.add(place);

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
					getPlace(index);
				}
			}
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!MainActivity.mNavigationDrawerFragment.isDrawerOpen()) {
			inflater.inflate(R.menu.main, menu);
			searchItem = menu.findItem(R.id.action_search);
			mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
			mSearchView.setOnQueryTextListener(this);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			placeSearch = new PlaceFind();
			getActivity().getSupportFragmentManager().beginTransaction()
					.add(R.id.container, placeSearch).addToBackStack("search")
					.commit();
			mSearchView.setIconified(false);
			break;

		}

		return true;

	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		Log.i("hahah", arg0);
		getActivity().getSupportFragmentManager().popBackStack();
		MenuItemCompat.collapseActionView(searchItem);
		
			mPagerAdapter = new PagerAdapter(getActivity()
					.getSupportFragmentManager(), arg0);
			mViewPager.setAdapter(mPagerAdapter);
	
		return false;
	}

}
