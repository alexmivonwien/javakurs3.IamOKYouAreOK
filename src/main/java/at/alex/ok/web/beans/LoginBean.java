package at.alex.ok.web.beans;

import java.io.IOException;


import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import at.alex.ok.model.User;
import at.alex.ok.services.UserService;
import at.alex.ok.web.utils.NavigationUtils;

@ManagedBean(name="loginBean")
@ViewScoped
public class LoginBean {
	
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
				
				String redirectURL = getOriginalURL()  + "?faces-redirect=true&" + NavigationUtils.getOriginalQueryString(request);
				
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
