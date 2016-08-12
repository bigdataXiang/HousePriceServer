package utils.geotext;


public class Region {
	//province，city，county，town
	private String province;
	private String city;
	private String county;
	private String town;
	
	public Region()
	{
		super();
	}
	public Region(String province, String city, String county, String town)
	{
		super();			
		this.province = province;
		this.city=city;
		this.county = county;
		this.town = town;
	}
	
	public String toString() {
		//"region":{"province":"江苏省","city":"南通市","county":"如皋市","town":"下原镇"}
		String ct =  "{";
		if (province != null)
			ct += "province:" + this.province;
		if (city != null)
		{
			if (!ct.endsWith("{"))
				ct += ", city:" + this.city;
			else
				ct += "city:" + this.city;
		}
		
		if (county != null)
		{
			if (!ct.endsWith("{"))
				ct += ", county:" + this.county;
			else
				ct += "county:" + this.county;
		}
		
		if (town != null)
		{
			if (!ct.endsWith("{"))
				ct += ", town:" + this.town;
			else
				ct += "town:" + this.town;
		}
		
		ct += "}";
		
		return ct;
	}
	
	public void setProvince( String province ) {
		this.province = province;
	}

	public String getProvince() {
		return this.province;
	}
	public void setCity( String city ) {
		this.city = city;
	}

	public String getCity() {
		return this.city;
	}
	
	public void setCounty( String county ) {
		this.county = county;
	}

	public String getCounty() {
		return this.county;
	}
	
	public void setTown( String town ) {
		this.town = town;
	}

	public String getTown() {
		return this.town;
	}
}
