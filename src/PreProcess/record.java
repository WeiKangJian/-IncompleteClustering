package PreProcess;

import java.io.Serializable;

public  class record implements Serializable{
	public String id;
	public String [] themes;
	public record(String id,String [] strs){
		this.id =id;
		this.themes = strs;
	}
}