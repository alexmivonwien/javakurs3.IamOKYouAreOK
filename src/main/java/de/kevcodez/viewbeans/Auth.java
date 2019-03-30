package de.kevcodez.viewbeans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.brickred.socialauth.Profile;

/**
 * Session-Bean für den Nutzer um alle relevanten Nutzerdaten für die komplette
 * Session zu speichern
 * 
 * @author Kevin
 *
 */
@Named(value = "auth")
@SessionScoped
public class Auth implements Serializable {
	
	private static final long serialVersionUID = 1523479642013931903L;

	private Profile socialProfile = null;

	public boolean isConnected() {
		return socialProfile != null;
	}

	public Profile getSocialProfile() {
		return socialProfile;
	}

	public void setSocialProfile(Profile socialProfile) {
		this.socialProfile = socialProfile;
	}
}