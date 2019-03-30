package at.alex.ok.web.model;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.jboss.logging.Logger;

import at.alex.ok.services.ChallengeService;
import at.alex.ok.services.UserService;



public class ServiceLocator {

	private Logger logger = Logger.getLogger(ServiceLocator.class);

	public static final String USER_SERVICE = "User Service";
	
	public static final String CHALLENGE_SERVICE = "Challenge Service";
	
	public static final String MYSQL_DB_SERVICE = "My SQL Data Source Service";

	private  Map<String, Object> serviceHolder = new HashMap <String, Object>();
	
	private static ServiceLocator instance;
	
	static {
		instance = new ServiceLocator();
	}
	
	private ServiceLocator(){
		try {
			Context ctx = new InitialContext();
			UserService userService = (UserService) ctx
					.lookup("java:app/IamOKYouAreOK/UserServiceImpl!at.alex.ok.services.UserService");
			
			ChallengeService challengeService = (ChallengeService) ctx
					.lookup("java:app/IamOKYouAreOK/ChallengeServiceImpl!at.alex.ok.services.ChallengeService");
		
			
			DataSource dataSource = (DataSource)ctx.lookup("java:/MySqlDS_IamOK");
			
			serviceHolder.put(USER_SERVICE, userService);
			serviceHolder.put(CHALLENGE_SERVICE, challengeService);
			
			serviceHolder.put(MYSQL_DB_SERVICE, dataSource);
		}
		catch (NamingException ne) {
			logger.error(ne.getMessage(), ne);
		}
	}
	
	public static ServiceLocator getInstance(){
		return instance;
	}

	public UserService getUserService() {
		return (UserService)serviceHolder.get(USER_SERVICE);
	}
	
	public DataSource getDBService() {
		return (DataSource)serviceHolder.get(MYSQL_DB_SERVICE);
	}

	public ChallengeService getChallengeService() {
		return (ChallengeService)serviceHolder.get(CHALLENGE_SERVICE);
	}
}
