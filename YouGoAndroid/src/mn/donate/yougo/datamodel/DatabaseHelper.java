package mn.donate.yougo.datamodel;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static String databaseName = "mcar.ormlite";
	private static int databaseVersion = 1;
	Dao<PlaceType, Integer> typeDao = null;
	Dao<Place, Integer> placeDao = null;

	public DatabaseHelper(Context context) {
		super(context, databaseName, null, databaseVersion);
		// TODO Auto-generated constructor stub
	}

	// creating tables
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		// TODO Auto-generated method stub
		try {
			TableUtils.createTableIfNotExists(connectionSource, Place.class);
			TableUtils
					.createTableIfNotExists(connectionSource, PlaceType.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// updgrade tables
	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

		try {
			TableUtils.createTable(connectionSource, Place.class);
			TableUtils.createTable(connectionSource, PlaceType.class);
			onCreate(arg0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Dao<PlaceType, Integer> getTypeDao() throws SQLException {
		if (typeDao == null)
			typeDao = getDao(PlaceType.class);
		return typeDao;
	}

	public Dao<Place, Integer> getPlaceDao() throws SQLException {
		if (placeDao == null)
			placeDao = getDao(Place.class);
		return placeDao;
	}

	public void deleteType() {
		try {
			TableUtils.clearTable(connectionSource, PlaceType.class);
			TableUtils.clearTable(connectionSource, Place.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		typeDao = null;

		super.close();
	}

}
