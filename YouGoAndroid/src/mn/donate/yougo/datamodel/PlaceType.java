package mn.donate.yougo.datamodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class PlaceType {
	@DatabaseField
	public int id;
	@DatabaseField
	public String name;

}
