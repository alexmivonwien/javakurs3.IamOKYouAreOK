package at.alex.ok.web.beans;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.alex.ok.model.User;
import at.alex.ok.services.UserService;

@Named("userbean")
@RequestScoped
public class UserBean {
	
	@Inject 
	private UserService userService;
	
	private String value = "This editor is provided by PrimeFaces";
	
	public List <User> getAllUsers(){
		
		return userService.getAllUsers();
		
	};
	
	

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
