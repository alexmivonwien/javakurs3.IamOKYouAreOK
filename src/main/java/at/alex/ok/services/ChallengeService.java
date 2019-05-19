package at.alex.ok.services;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import org.primefaces.model.SortOrder;

import at.alex.ok.model.Assignment;
import at.alex.ok.model.Challenge;
import at.alex.ok.model.Challengeable;
import at.alex.ok.model.Result;
import at.alex.ok.model.User;

@Local
public interface ChallengeService {
	
	public static final String FILTER_ASSIGNMENTS_BY_CHALLENGE_ID = "c.challenge.id";
	
	public static final String FILTER_ASSIGNMENTS_BY_USER_ID = "c.assignedTo.id";
	
    public <T> List< ? extends Challengeable> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters, Class <? extends Challengeable> clazz);
	
    /** obtains the total number of entries corresponding to the filters criteria **/
    public long totalCount(Map<String,Object> filters, Class <? extends Challengeable> clazz);

    
	public void createChallenge (List <Challenge> challenges);
	
	/**
	 * 
	 * This method creates a new assignment or updates an existing one.
	 * The update involves a state machine approach, as only some 
	 * status changes are possible. 
	 * If the assignment status changes to completed, a new Result entity is returned
	 * Otherwise, a null is returned
	 * 
	 * @param assignment
	 * @return
	 */
	public Result createOrUpdateAssignment (Assignment assignment);
	
	
	/**
	 * This method finds an assignment by a user and a challenge, both of which may not be null
	 * 
	 * @param user
	 * @param challenge
	 * @return
	 */
	public Assignment findAssignmentBy(User user, Challenge challenge);
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T getChallengeable(Integer id, Class <T> clazz);
	
	
	/**
	 * This method loads an assignment with all of its assignment history
	 * 
	 * @param id
	 * @return
	 */
	public Assignment getAssignementFetchHistory(Integer id);
	
	
}
