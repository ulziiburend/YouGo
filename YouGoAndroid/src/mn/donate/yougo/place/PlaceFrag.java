package mn.donate.yougo.place;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.donate.yougo.MainActivity;
import mn.donate.yougo.R;
import mn.donate.yougo.datamodel.DatabaseHelper;
import mn.donate.yougo.datamodel.Place;
import mn.donate.yougo.datamodel.PlaceType;
import mn.donate.yougo.utils.CustomRequest;
import mn.donate.yougo.utils.MySingleton;
import mn.donate.yougo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

public class PlaceFrag extends Fragment implements
		SearchView.OnQueryTextListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
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
	private static final String ARG_SECTION_NUMBER = "section_number";
	LocationClient mLocationClient;
	private LocationManager lm;
	private static String lat;
	private static String lng;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		LocationCheck();
		helper = new DatabaseHelper(getActivity());

		progress = ProgressDialog.show(getActivity(), "",
				getString(R.string.loading));

		if (Utils.isNetworkAvailable(getActivity())) {
			mRequestQueue = MySingleton.getInstance(getActivity())
					.getRequestQueue();
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
				ARG_SECTION_NUMBER));
	}

	public static PlaceFrag newInstance(int section) {

		PlaceFrag f = new PlaceFrag();
		Bundle b = new Bundle();
		b.putInt(ARG_SECTION_NUMBER, section);
		f.setArguments(b);

		return f;
	}

	public PlaceFrag() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.place_main, container, false);
		mLocationClient = new LocationClient(getActivity(), this, this);
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
			OnScrollListener, OnRefreshListener {

		private int index = 0;
		private ListView mListView;
		private List<Place> mListItems;
		private int cat_id;
		private View load_footer;
		private RequestQueue mRequestQueueFRag;
		private PlaceAdapter adapter;
		private boolean isFinish = false;
		private DatabaseHelper helper;
		private boolean flag_loading = false;
		private View v;

		int mNum;
		String searchQuery;
		SwipeRefreshLayout swipeLayout;
		TextView nodata;

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

		}

		@SuppressWarnings("deprecation")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			v = inflater.inflate(R.layout.fragment_list, container, false);
			mListView = (ListView) v.findViewById(R.id.listView);
			load_footer = inflater.inflate(R.layout.list_load_footer,
					mListView, false);
			nodata = (TextView) v.findViewById(R.id.list_nodata);
			mListView.addFooterView(load_footer);
			swipeLayout = (SwipeRefreshLayout) v
					.findViewById(R.id.swipe_container);
			swipeLayout.setOnRefreshListener(this);
			swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
					android.R.color.holo_green_light,
					android.R.color.holo_orange_light,
					android.R.color.holo_red_light);
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			helper = new DatabaseHelper(getActivity());

			try {
				mListItems = helper.getPlaceDao().queryForAll();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapter = new PlaceAdapter(getActivity(), mListItems);

			mListView.setAdapter(adapter);
			if (Utils.isNetworkAvailable(getActivity())) {
				mRequestQueueFRag = MySingleton.getInstance(getActivity())
						.getRequestQueue();
				getPlace(index, false);
			} else {
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.noNet),
						Toast.LENGTH_SHORT).show();
			}
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Bundle b = new Bundle();
					b.putInt("place_id", mListItems.get(position).id);
					Intent detIntent = new Intent(getActivity(),
							PlaceDetail.class);
					detIntent.putExtras(b);
					startActivity(detIntent);
				}
			});
		}

		private void getPlace(final int sIndex, final boolean refresh) {

			mListView.addFooterView(load_footer);
			CustomRequest adReq = new CustomRequest(Method.POST,
					this.getString(R.string.mainIp) + "place", null,
					new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							try {
								if (response != null
										&& response.getInt("response") == 1) {

									int num_rows = response
											.getInt("data_count");
									Log.i("row", num_rows + "");
									if (num_rows < 10) {
										isFinish = true;
									}
									if (num_rows == 0
											&& adapter.getCount() == 0)
										nodata.setVisibility(View.VISIBLE);
									else {
										nodata.setVisibility(View.GONE);
									}
									index = index + 10;
									JSONArray data = response
											.getJSONArray("data");
									makeAd(data, refresh);

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

				@Override
				protected Map<String, String> getParams() {
					Map<String, String> params = new HashMap<String, String>();
					if (lat != null && lng != null) {
						params.put("lat", lat);
						params.put("lng", lng);
					}
					params.put("sindex", sIndex + "");
					params.put("type_id", cat_id + "");
					if (searchQuery != null)
						params.put("query", searchQuery);
					return params;
				}

			};

			mRequestQueueFRag.add(adReq);
		}

		protected void makeAd(JSONArray data, boolean refresh)
				throws JSONException, SQLException {
			// TODO Auto-generated method stub
			if (data.length() > 0) {
				if (refresh)
					mListItems.clear();
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
					place.price = obj.getInt("price");
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
					getPlace(index, false);
				}
			}
		}

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			index = 0;
			getPlace(index, true);
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

			mSearchView.setIconified(false);
			break;
		case R.id.action_map:
			placeSearch = new PlaceFind();
			getActivity().getSupportFragmentManager().beginTransaction()
					.add(R.id.container, placeSearch).addToBackStack("search")
					.commit();
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
		getActivity().getSupportFragmentManager().popBackStack();
		MenuItemCompat.collapseActionView(searchItem);

		mPagerAdapter = new PagerAdapter(getActivity()
				.getSupportFragmentManager(), arg0);
		mViewPager.setAdapter(mPagerAdapter);

		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		if (arg0.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				arg0.startResolutionForResult(getActivity(), 0);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		setupLoc();
		Log.i("location", "connected");

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Log.i("location", "disconnected");

	}

	@Override
	public void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	@Override
	public void onStop() {
		// Disconnect the client.
		mLocationClient.disconnect();
		super.onStop();
	}

	public void setupLoc() {
		// Get the current location's latitude & longitude
		Location currentLocation = mLocationClient.getLastLocation();

//		lat = Double.toString(currentLocation.getLatitude());
//		lng = Double.toString(currentLocation.getLongitude());
//		Log.v("long", lng);
//		Log.v("lat", lat);

	}

	private void LocationCheck() {
		lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		boolean gps_enabled = false, network_enabled = false;
		if (lm == null)
			lm = (LocationManager) getActivity().getSystemService(
					Context.LOCATION_SERVICE);
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		if (!gps_enabled && !network_enabled) {
			Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage(getActivity().getString(R.string.onGPS));
			dialog.setPositiveButton(getActivity().getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							Intent myIntent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(myIntent);
						}
					});
			dialog.setNegativeButton(getActivity().getString(R.string.no),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							// TODO Auto-generated method stub

						}
					});
			dialog.show();

		}
	}
}
