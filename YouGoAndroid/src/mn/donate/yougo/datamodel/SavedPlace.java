package mn.donate.yougo.datamodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable
public class SavedPlace {
	@DatabaseField(generatedId=true)
	public int id;
	@DatabaseField
	public int place_id;
	@DatabaseField
	public String name;
	@DatabaseField
	public int rating;
	@DatabaseField
	public int rated_people;
	@DatabaseField
	public String desc;
	@DatabaseField
	public double lat;
	@DatabaseField
	public double lng;
	@DatabaseField
	public String images;
	@DatabaseField
	public String menu_images;
	@DatabaseField
	public String contact;
	@DatabaseField
	public int type_id;
	@DatabaseField
	public String phone;
	@DatabaseField
	public String work_hour;
	@DatabaseField
	public String tag1;
	@DatabaseField
	public String tag2;
	@DatabaseField
	public String tag3;
	@DatabaseField
	public double distance;
	@DatabaseField
	public int credit_cards;
	@DatabaseField
	public int wifi;
}
