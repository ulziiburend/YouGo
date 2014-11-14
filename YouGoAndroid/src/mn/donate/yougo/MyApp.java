package mn.donate.yougo;

import java.util.Locale;

import net.danlew.android.joda.JodaTimeAndroid;
import android.app.Application;
import android.content.res.Configuration;

public class MyApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		JodaTimeAndroid.init(this);
	
	}
}
