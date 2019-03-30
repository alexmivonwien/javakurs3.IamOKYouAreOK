package de.kevcodez.viewbeans;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import de.kevcodez.servlet.SocialLoginServlet;
import de.kevcodez.system.Consts;
import de.kevcodez.system.SocialAuthProvider;

/**
 * <p>
 * Login-Bean mit Context-Dependency-Injection und einem {@link RequestScoped
 * Request-Scope}
 * </p>
 * <p>
 * Dieses Bean dient zum Login in ein soziales Netzwerk, in diesem Falle
 * Facebook. Der Nutzer wird auf Facebook weitergeleitet und akzeptiert dort den
 * API-Zugriff. Der {@link SocialAuthManager} wird als Session-Attribut gesetzt.
 * </p>
 * <p>
 * Die Authentifizierung und Auswertung der Nutzerdaten handhabt dann das
 * {@link SocialLoginServlet}.
 * </p>
 * 
 * @author kevcodez.de
 *
 */
@Named ("socialLoginBean")
@RequestScoped
public class SocialLoginBean implements Serializable {
	private static final long serialVersionUID = 3359936012215461852L;

	/**
	 * Leitet auf Facebook weiter, wo der Nutzer den API-Zugriff erlauben muss
	 */
	public void doFacebookLogin() {
		initializeSocialLoginAndRedirect(SocialAuthProvider.FACEBOOK);
	}

	/**
	 * Invalidiert die Session und leitet nochmal auf die Startseite. Alle
	 * Session-Beans werden zurückgesetzt.
	 * 
	 * @return Startseite
	 */
	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext()
				.invalidateSession();
		return "/default.xhtml?faces-redirect=true";
	}

	/**
	 * Intialisiert den {@link SocialAuthManager} mit der entsprechenden
	 * {@link SocialAuthConfig} aus der {@link Consts Konstanten-Klasse}. <br>
	 * <br>
	 * Nach erfolgereicher Initialisierung wird der Nutzer auf die entsprechende
	 * Seite für den Login weitergeleitet. Der Login fällt weg, wenn der Nutzer
	 * mit dem Browser eingeloggt ist und bereits API-Zugriff erlaubt.
	 * 
	 * @param provider
	 *            Der {@link SocialAuthProvider} mit dem der Nutzer sich
	 *            verbinden soll
	 */
	private static void initializeSocialLoginAndRedirect(
			SocialAuthProvider provider) {
		try {
			// Neuen SocialAuthManager erstellen und die Config laden
			SocialAuthManager manager = new SocialAuthManager();
			manager.setSocialAuthConfig(Consts.SOCIAL_AUTH_CONFIG);
			// Gibt anhand der Return-URL und der Provider-ID einen Link zum
			// Redirecten wieder
			String url = manager.getAuthenticationUrl(provider.toString(),
					SocialLoginServlet.SERVLET_URL);
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			// AuthManager in der Session speichern
			((HttpServletRequest) context.getRequest()).getSession()
					.setAttribute("authManager", manager);
			// Redirect durchführen
			context.redirect(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}