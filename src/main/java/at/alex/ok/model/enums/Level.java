package at.alex.ok.model.enums;


/**
 * motivation, see:
 * 
 * http://stackoverflow.com/questions/16140282/jpa-enumerated-types-mapping-best-approach
 *
 */

public enum Level {

	Beginner(1, "Beginner"), Good(2, "Good"), OK(3, "OK");
	
	private final int code;
	
    private final String name;
    
    private Level(int code, String name){
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
