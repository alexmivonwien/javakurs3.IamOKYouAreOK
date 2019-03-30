package at.alex.ok.model.enums;


/**
 * motivation, see:
 * 
 * http://stackoverflow.com/questions/16140282/jpa-enumerated-types-mapping-best-approach
 * 
 */
public enum Status {
	
	New(1, "New"),
	Assigned(2, "Assigned"),
	Started(3, "Started"),
	Completed(4, "Completed");
	
	private final int code;
	
    private final String name;
    
    private Status(int code, String name){
        this.code = code;
        this.name = name;
    }

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

}
