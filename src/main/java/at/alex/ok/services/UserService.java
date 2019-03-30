package at.alex.ok.services;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import at.alex.ok.model.Role;
import at.alex.ok.model.User;
import at.alex.ok.model.enums.RoleType;

@Local
public interface UserService {
	
	/**
	 * returns a list of all users
	 * 
	 * @TODO introduce paging
	 * 
	 * @return
	 */
	
	public List <User> getAllUsers();
	
	/**
	 *  creates a new User and registers it with the default list of Roles (currently only the "general" role)
	 *  
	 * @param username
	 * @param email
	 * @param password
	 * @throws UserAlreadyExistsException
	 */
	public User createAndRegisterUser(String username, String email, String password) throws UserAlreadyExistsException;
	
	public void registerUser(User user, Set<RoleType> roles) throws UserAlreadyExistsException;
	
	public Set<Role> findRoles(Set<RoleType> roles);
	
	public User findByUsernameOrEmail (String username, String email);
	
	public User getUserById (String userId);

}
