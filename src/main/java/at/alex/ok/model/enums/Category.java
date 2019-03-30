package at.alex.ok.model.enums;

/**
 * motivation, see:
 * 
 * http://stackoverflow.com/questions/16140282/jpa-enumerated-types-mapping-best-approach
 * 
 */
public enum Category {

	Environmental(1, "Environmental"),
	Human (2, "Human"),
	Charity(3, "Charity"),
	Pets (4, "Pets");
	
	
	private final int code;
	
    private final String name;
    
    private Category(int code, String name){
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
