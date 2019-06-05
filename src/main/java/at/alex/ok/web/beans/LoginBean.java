package at.alex.ok.web.beans;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import at.alex.ok.model.User;
import at.alex.ok.services.UserService;
import at.alex.ok.web.utils.NavigationUtils;

/**
 * Unless you're using JSF 2.2 (which is still not out yet at this moment) or
 * MyFaces CODI (which I'd have expected that you would explicitly mention that)
 * the @ViewScoped doesn't work in CDI. This also pretty much matches your
 * problem symptoms.
 * 
 * http://stackoverflow.com/questions/14812238/jsf-view-scoped-bean-
 * reconstructed-multiple-times
 * 
 * 
 * As per JSF 2.2 and higher, @ManagedBean is deprecated. Use @Named together with @javax.faces.view.ViewScoped,
 * @see https://stackoverflow.com/a/4347707/1925356
 * @see @javax.faces.view.ViewScoped documentation:  
 * 
 * When this annotation, along with javax.inject.Named is found on a class, the runtime must place
 * the bean in a CDI scope such that it remains active as long as javax.faces.application.NavigationHandler.handleNavigation 
 * does not cause a navigation to a view with a viewId that is different than theview Id of the current view. Any injections and
 * notifications required by CDI and the Java EE platform must occur as usual at the expected time.
 * 
 * 
 */

@Named ("loginBean")
@javax.faces.view.ViewScoped
// javax.faces.bean.ViewScoped is deprecated as per JSF 2.2
// javax.faces.bean.ManagedBean is deprecated as per JSF 2.2
public class LoginBean implements Serializable{
	
	public static final String SESSION_ATTRIBUTE_USERID = "userId";
	
	public static final String ORIGINAL_URL = "original.url";

	public static final String ORIGINAL_URL_QUERY_STRING = "original.url_query.string";

	@Inject
	private UserService userService;

	private String login;

	private String password;
	
	public LoginBean(){}
	
	@PostConstruct
	private void init() {

		ExternalContext extCtx = FacesContext.getCurrentInstance()

		.getExternalContext();
		
		String origURLString = (String) extCtx.getRequestMap().get(
	            RequestDispatcher.FORWARD_REQUEST_URI);

		HttpSession session = (HttpSession)extCtx.getSession(false);
		
		if (session == null){
			session = (HttpSession)extCtx.getSession(true);
		}
		
		
		if (!StringUtils.isEmpty(origURLString) && session.getAttribute(ORIGINAL_URL) == null){
			
			String applicationName = ((HttpServletRequest)extCtx.getRequest()).getContextPath();
			origURLString = origURLString.substring(applicationName.length(), origURLString.length());
			
			String forwQueryString = (String) extCtx.getRequestMap().get(
		            RequestDispatcher.FORWARD_QUERY_STRING);
			
			if (!StringUtils.isEmpty(forwQueryString)) {
				session.setAttribute(ORIGINAL_URL_QUERY_STRING, forwQueryString);
			}
			
			session.setAttribute(ORIGINAL_URL, origURLString.toString());
		}
		
	}
	public String login() {

		FacesContext context = FacesContext.getCurrentInstance();
		
		
		if (login == null || login.length() <= 2) {

			context.addMessage(null, new FacesMessage(
					"Your login must be at least two characters long"));

			return null;
		}else if (login.startsWith(":")){
			context.addMessage(null, new FacesMessage(
					"Your login cannot start with :"));

			return null;
		}

		User user = userService.findByUsernameOrEmail(this.getLogin(),
				this.getLogin());

		if (user != null) {
			ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();
			HttpServletRequest request = (HttpServletRequest)extCtx.getRequest();

			try {
//				request.login(this.getLogin(), this.getPassword());
//				request.getSession().setAttribute(SESSION_ATTRIBUTE_USERID, new Integer(user.getId()));
				loginUserAndSetUserIdSessionAttribute(request, this.getLogin(), this.getPassword(), user);
				
			} catch (ServletException e) {
				try {
					request.logout();
				} catch (ServletException e1) {
					e1.printStackTrace();
				}
				context.addMessage(
						null,
						new FacesMessage("Login failed."
								+ e.getLocalizedMessage()));
				return null;
			}

			if (StringUtils.isEmpty(getOriginalURL())) {
				
				return "default";
				
			} else {
				
				String originalURL = getOriginalURL();
				int indexOfsq = -1;
				String requestParameters = StringUtils.EMPTY;
				if ((indexOfsq = originalURL.indexOf(";")) != -1) { //originalURL contains request parameters, i.e. browser session via URL rewriting
					requestParameters = originalURL.substring(indexOfsq, originalURL.length());							
					originalURL = originalURL.substring(0, indexOfsq);					
				}
				
				String redirectURL = originalURL  + "?faces-redirect=true&" + NavigationUtils.getOriginalQueryString(request) + requestParameters;
				
				return redirectURL;
			}

		} else {
			context.addMessage(null, new FacesMessage(" No user found for "
					+ this.getLogin()
					+ ". Please provide an existing username or email"));

			return null;
		}
	}

	public String logoff() {

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context
				.getExternalContext().getRequest();
		try {
			request.logout();
			request.getSession().invalidate();
			context.getExternalContext().redirect(NavigationUtils.getNextView("/default.jsf", request));

		} catch (ServletException se ){
			context.addMessage(null, new FacesMessage("Logout failed."));
		}
		catch (IOException e) {
			context.addMessage(null, new FacesMessage("Logout failed."));
		}

		return "default.xhtml";
	}
	

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	
	public static void loginUserAndSetUserIdSessionAttribute(HttpServletRequest request, String username, String password, User user) throws ServletException {
		request.login(username, password);
		request.getSession().setAttribute(SESSION_ATTRIBUTE_USERID, new Integer(user.getId()));
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getOriginalURL() {
		ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest)extCtx.getRequest();

		return NavigationUtils.getOriginalURL(request, true);
	}
	
}
