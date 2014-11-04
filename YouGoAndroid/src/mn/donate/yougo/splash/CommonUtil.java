package mn.donate.yougo.splash;

import android.content.Context;
import android.content.Intent;

public class CommonUtil {

	public static final String SENDER_ID = "1032871566967";

	static final String DISPLAY_MESSAGE_ACTION = "mn.donate.yougo.DISPLAY_MESSAGE";

	static final String EXTRA_MESSAGE = "message";

	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
