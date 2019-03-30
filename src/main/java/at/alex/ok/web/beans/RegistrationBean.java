package at.alex.ok.web.beans;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import at.alex.ok.model.User;
import at.alex.ok.model.enums.RoleType;
import at.alex.ok.services.UserAlreadyExistsException;
import at.alex.ok.services.UserService;

@Named("registrationBean")
@RequestScoped
public class RegistrationBean {

	@Inject
	private UserService userService;

	private String desiredUsername;

	private String email;

	private String desiredPassword;

	public String registerUser() {

		if (desiredUsername == null || desiredUsername.length() == 0) {

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Please choose a username at least two characters long"));

			return null;
		}

		if (desiredPassword == null || desiredPassword.length() == 0) {

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Please choose a password that is at least two characters long"));

			return null;
		}

		try {
			userService.createAndRegisterUser(this.getDesiredUsername(), this.getEmail(), this.getDesiredPassword());
		} catch (Exception e) {
			FacesContext context = FacesContext.getCurrentInstance();
			String message = "";
			if (e instanceof UserAlreadyExistsException){
				message = "The username " + this.getDesiredUsername() + " already exists. Please choose another one!";
				 
			}else{
				message = "An exception occured when creating the user " + this.getDesiredUsername() + ":" + e.getMessage();
			}
			
			context.addMessage(null, new FacesMessage(message));

			return null;
		}

		return "registrationSuccessfull";
	}

	public String getDesiredUsername() {
		return desiredUsername;
	}

	public void setDesiredUsername(String desiredUsername) {
		this.desiredUsername = desiredUsername;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDesiredPassword() {
		return desiredPassword;
	}

	public void setDesiredPassword(String desiredPassword) {
		this.desiredPassword = desiredPassword;
	}
}
