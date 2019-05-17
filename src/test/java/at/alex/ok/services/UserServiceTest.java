package at.alex.ok.services;

import static org.mockito.Mockito.*;

import org.mockito.Mockito;

import at.alex.ok.model.User;



import org.junit.*;
import org.mockito.Mockito;

import at.alex.ok.model.User;
import at.alex.ok.services.UserService;

import static org.junit.Assert.*;

/**
 * 
 * @author Alex-Mi
 *
 *  The classical example for a mock object is a data provider. In
 *  production a real database is used, but for testing a mock object
 *  simulates the database and ensures that the test conditions are
 *  always the same.
 *
 */

public class UserServiceTest {

	@Test
	public void testUserService() {
		 // create mock
		UserService test = Mockito.mock(UserService.class);

		User user1 = new User();
		user1.setUsername("user1");

		// define return value for method getUniqueId()
		when(test.findByUsernameOrEmail("user1", null)).thenReturn(user1);
		

		// use mock in test....
		assertEquals(test.findByUsernameOrEmail("user1", null), user1);
	}

}
