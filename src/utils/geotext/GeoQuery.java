package utils.geotext;

import java.util.List;

public class GeoQuery {
	
	private String status;
	public void setStatus( String status ) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}
	
	
	private Within within;
	
	public void setWithin( Within within ) {
		this.within = within;
	}

	public Within getWithin() {
		return this.within;
	}
	
	private int total;
	public void setTotal( int total ) {
		this.total = total;
	}

	public int getTotal() {
		return this.total;
	}
	
	
	private List<Result> result;
	
	public void setResult(List<Result> result)
	{
		this.result = result;
	}
	
	public List<Result> getResult()
	{
		return this.result;
	}
	
	public GeoQuery()
	{
		super();
	}
	
	public GeoQuery(String status, Within within, int total, List<Result> result)
	{
		super();
		this.status = status;
		this.within = within;
		this.total = total;
		this.result = result;
	}
	
	public String toString() {
		String ct = "{status:" + this.status ;
		if (within != null)
			ct += "," + within;
		
		ct += ",total:" + total;
		
		if (result != null)
		{
			ct += ",result:[";
			ct += result.get(0);
			for (int n = 1; n < result.size(); n ++)
			{
				ct += "," + result.get(n);
			}
			ct += "]";
		}
		ct += "}";
		return ct;
	}
	
}
