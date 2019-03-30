package at.alex.ok.web.utils;

import java.io.IOException;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.alex.ok.web.beans.LoginBean;

public class NavigationUtils {
	
	
	
	public static String getOriginalURL(HttpServletRequest req, boolean trimExtension) {

		HttpSession session = req.getSession(false);
		String originalURL = (String)session.getAttribute(LoginBean.ORIGINAL_URL);
		
		if (trimExtension && originalURL!=null && (originalURL.endsWith(".jsf") || originalURL.endsWith(".faces") || originalURL.endsWith(".xhtml"))) {
			originalURL  = originalURL.substring(0, originalURL.lastIndexOf("."));
		}
		
		return originalURL == null? StringUtils.EMPTY : originalURL;
	}
	
	public static String getOriginalQueryString(HttpServletRequest req) {
		HttpSession session = (HttpSession)req.getSession(false);
		return (String)session.getAttribute(LoginBean.ORIGINAL_URL_QUERY_STRING);
	}
	

	/**
	 * This method composes an absolute URL to which a next view will be
	 * redirected
	 * 
	 * @param path
	 *            - the relative path to the next view, i.e.:
	 *            /pages/assihnments.jsf or /default.jsf
	 * @param request
	 *            - the HttpRequest
	 * @return - the absolute URL to redirect to, i.e.:
	 *         http://localhost:8080/IamOKYouAreOK/pages/assignment.jsf
	 */
	public static String getNextView(String path, HttpServletRequest request) {
		
		String applicationName = request.getContextPath();
		String originalURL = request.getRequestURL().toString();
		String redirectURL = originalURL
				.substring(0, originalURL.indexOf(applicationName)
						+ applicationName.length());

		path = getDevicePrefix() + path;
		
		return redirectURL + path;
	}
	
	public static String getDevicePrefix(){
		
		return isMobileDevice() ? "/m" : "/d";
	}

	public static void addMessage(String summary, Severity severity) {
		FacesMessage message = new FacesMessage(severity, summary, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public static void redirect(String url) {
		ExternalContext extCtx = FacesContext.getCurrentInstance()
				.getExternalContext();
		try {
			extCtx.redirect(NavigationUtils.getNextView(url,
					(HttpServletRequest) extCtx.getRequest()));
		} catch (IOException e) {
			Logger logger = LogManager.getLogger();
			logger.debug(e);
		}
	}
	
	/**
	 * This method puts the object param into the flas cope Map under the provided key
	 * and redirects the request to the relative URL provided in the url param.
	 * 
	 * @param key
	 * @param param
	 * @param url
	 */
	
	public static boolean isMobileDevice(){

		ExternalContext extCtx = FacesContext.getCurrentInstance()
				.getExternalContext();
		
		HttpServletRequest req = (HttpServletRequest) extCtx.getRequest();
		HttpSession session = req.getSession(false);
		
		if (session == null) return false;
		Boolean isMobileDevice  = (Boolean)session.getAttribute(UserAgentInfo.SESSION_ATTRIBUTE_IS_MOBILE_DEVICE);
		
		if (isMobileDevice == null){
			
	        String userAgent = req.getHeader("user-agent");
	        String accept = req.getHeader("Accept");
	        
	        if (userAgent != null && accept != null) {
	            UserAgentInfo agent = new UserAgentInfo(userAgent, accept);
	            isMobileDevice = agent.isMobileDevice();
	            req.getSession(false).setAttribute(UserAgentInfo.SESSION_ATTRIBUTE_IS_MOBILE_DEVICE, isMobileDevice);
	        }
		}
		
		return ( isMobileDevice == null ? false : isMobileDevice );
	}
	
	public static void flashAndRedirect(String key, Object param, String url) {

		ExternalContext extCtx = FacesContext.getCurrentInstance()
				.getExternalContext();

		extCtx.getFlash().put(key,param);
		
		NavigationUtils.redirect(url);
	}
	
	/**
	 * This method puts the object param into the flas cope Map under the provided key
	 * and redirects the request to the relative URL provided in the url param appending 
	 * all the request parameters after the requested URL
	 * 
	 * @param key
	 * @param param
	 * @param url
	 * @param requestParameters
	 */
	
	public static void flashAndRedirect(String key, Object param, String url, Properties requestParameters) {

		ExternalContext extCtx = FacesContext.getCurrentInstance()
				.getExternalContext();

		extCtx.getFlash().put(key,param);
		
		StringBuilder params = new StringBuilder("?");
		if (requestParameters != null)
		for (Object property: requestParameters.keySet()){
			params.append(property + "=" + requestParameters.get((String)property) + ";");
		}
		
		params.delete(params.length()-1, params.length());
		url+= params.toString();
		NavigationUtils.redirect(url.toString());
	}
}
