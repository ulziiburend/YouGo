package mn.donate.yougo.datamodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class UserFeed {
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public int place_id;
	@DatabaseField
	public int user_id;
	@DatabaseField
	public int rating;
	@DatabaseField
	public String username;
	@DatabaseField
	public String user_img;
	@DatabaseField
	public String title;
	@DatabaseField
	public String place_name;
	@DatabaseField
	public String image;
	@DatabaseField
	public String created_date;
	@DatabaseField
	public String place_img;

}
