package de.kevcodez.servlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.brickred.socialauth.*;
import org.brickred.socialauth.util.SocialAuthUtil;

import at.alex.ok.model.User;
import at.alex.ok.model.enums.RoleType;
import at.alex.ok.services.UserService;
import at.alex.ok.web.beans.LoginBean;
import at.alex.ok.web.utils.NavigationUtils;
import de.kevcodez.system.Consts;
import de.kevcodez.viewbeans.Auth;

/**
 * Dieses Servlet kommt nach dem Redirect bei einem Login in ein soziales
 * Netzwerk zum Einsatz<br>
 * <br>
 * Zunächst wird im {@link LoginBean} ein neuer {@link SocialAuthManager}
 * erstellt und in die Session gepackt. <br>
 * Sobald nach dem Login wieder zurückgeleitet wird, wird die
 * {@link SocialLoginServlet#doGet(HttpServletRequest, HttpServletResponse)
 * doGet-Methode} ausgeführt. Hier können wir nun auf das Session-Attribut
 * zugreifen und die Profildetails abfragen.
 * 
 * @author kevcodez.de
 *
 */
//@WebServlet(name="SocialServlet", displayName="SocialServlet", urlPatterns = {"/socialLogin"})
public class SocialLoginServlet extends HttpServlet {
	
	private static final long serialVersionUID = -8141169124278826240L;
	
	/** URL des Servlets **/
	public static final String SERVLET_URL = Consts.BASE_URL + "/socialLogin";
	
	@Inject
	private Auth auth;

	@Inject
	private UserService userService;
	/**
	 * Bei einem GET-Request wird aus der {@link HttpSession} der
	 * {@link SocialAuthManager authManager} entnommen. Dieser fragt dann die
	 * Daten des Nutzers ab.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		// AuthManager auth Session holen
		SocialAuthManager socialAuthManager = (SocialAuthManager) session
				.getAttribute("authManager");
		if (socialAuthManager != null) {
			Map<String, String> paramsMap = SocialAuthUtil
					.getRequestParametersMap(req);
			AuthProvider provider;
			try {
				provider = socialAuthManager.connect(paramsMap);
				Profile socialProfile = provider.getUserProfile();
				auth.setSocialProfile(socialProfile);
				// AuthManager aus Session löschen
				session.setAttribute("authManager", null);
				String eMail = socialProfile.getEmail();
				String userName = socialProfile.getFullName();
				
				User user = userService.findByUsernameOrEmail(userName, eMail);
				
				if (user==null){ 
					
					// 1.) the user does not exist in the database yet, so create it
					try {
						user = userService.createAndRegisterUser(userName, eMail, User.DEFAULT_FB_PASSWORD);
					} catch (Exception e) {
						e.printStackTrace();
						user = null;
					}
					
				}
				if (user!=null){
					// 2.) Log the user in:
					//req.login(user.getUsername(), user.getPassword());
					LoginBean.loginUserAndSetUserIdSessionAttribute(req, user.getUsername(), User.DEFAULT_FB_PASSWORD, user);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Wieder auf die Startseite navigieren
		resp.sendRedirect(Consts.BASE_URL + NavigationUtils.getOriginalURL(req, false)  + "?" + NavigationUtils.getOriginalQueryString(req));

	}
}
