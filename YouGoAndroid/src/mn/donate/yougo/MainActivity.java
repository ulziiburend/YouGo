package mn.donate.yougo;

import java.util.Locale;

import mn.donate.yougo.feed.FeedFrag;
import mn.donate.yougo.place.PlaceFrag;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	public static NavigationDrawerFragment mNavigationDrawerFragment;
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Locale locale = new Locale("mn");
		Locale.setDefault(locale);
		Configuration config = getBaseContext().getResources()
				.getConfiguration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.main_title));
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		// update the main content by replacing fragments

		switch (position) {
		case 1:

			fragmentManager.beginTransaction()
					.replace(R.id.container, PlaceFrag.newInstance(1)).commit();
			break;
		case 2:

			fragmentManager.beginTransaction()
					.replace(R.id.container, FeedFrag.newInstance(2)).commit();
			break;
		default:
			break;
		}
		//suuld nemsen

	}

	public void onSectionAttached(int number) {
		switch (number) {
//		case 1:
//			mTitle = getString(R.string.title_section1);
//			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		case 5:
			mTitle = getString(R.string.title_section5);
			break;

		}
		Log.i("title:", mTitle+"");

	
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
         
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);

	}

}
