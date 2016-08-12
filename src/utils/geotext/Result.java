package utils.geotext;

public class Result {
	private String status;
	private String query_string;
	private String nlp_status;
    private String source;
	
    public Result()
	{
		super();
	}
	public Result(String status, String query_string, String nlp_status, String source, Location location)
	{
		super();			
		this.status = status;
		this.query_string = query_string;
		this.nlp_status = nlp_status;
		this.location = location;
		this.source = source;
	}
	
	public String toString() {
		String ct =  "{status:" + this.status + ", query_string:" + this.query_string
		+ ", nlp_status:" + this.nlp_status + ", location:" + this.location 
		+ ", source:" + this.source + "}";
		
		return ct;
	}
	
	public void setStatus( String status ) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}
	
	public void setQuery_string( String query_string ) {
		this.query_string = query_string;
	}

	public String getQuery_string() {
		return this.query_string;
	}
	
	public void setNlp_status( String nlp_status ) {
		this.nlp_status = nlp_status;
	}

	public String getNlp_status() {
		return this.nlp_status;
	}
	public void setSource( String source ) {
		this.source = source;
	}

	public String getSource() {
		return this.source;
	}
	
	
	public void setLocation( Location location ) {
		this.location = location;
	}

	public Location getLocation() {
		return this.location;
	}
	
	public Location location;
} 
