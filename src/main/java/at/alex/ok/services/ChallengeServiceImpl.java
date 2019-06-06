package at.alex.ok.services;

import java.sql.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.primefaces.model.SortOrder;

import at.alex.ok.model.Assignment;
import at.alex.ok.model.AssignmentHistory;
import at.alex.ok.model.Challenge;
import at.alex.ok.model.Challengeable;
import at.alex.ok.model.Result;
import at.alex.ok.model.User;
import at.alex.ok.model.enums.Status;

@Stateless(mappedName = "ChallengeService")
@Local(ChallengeService.class)
public class ChallengeServiceImpl implements ChallengeService {

	@PersistenceContext
	private EntityManager em;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createChallenge(List<Challenge> challenges) {

		for (Challenge challenge: challenges){
			this.em.persist(challenge);
		}
	}
	
	@Override
	public <T> List< ? extends Challengeable> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters, Class <? extends Challengeable> clazz){

		final String sqlQuery = " from " + clazz.getSimpleName() + " c  " + composeWhereClause(filters) + composeOrderClause(sortField, sortOrder);

		Query query = this.em.createQuery(sqlQuery);
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		
		List<Challengeable> challengeList =  query.getResultList();
		return challengeList;
	};
	
	@Override
	public long totalCount(Map<String,Object> filters, Class <? extends Challengeable> clazz){

		final String jpaQuery = " select count(*) from " + clazz.getSimpleName() + " c  " + composeWhereClause(filters);
		Query query = this.em.createQuery(jpaQuery);
		
		List<Long> challengeList =  query.getResultList();
		return challengeList.size() > 0 ? challengeList.get(0) : 0;
	};
	
	
	private String composeWhereClause (Map<String,Object> filters){
		
		if (filters == null || filters.size() == 0){
			return "";
		}

		StringBuilder whereClause = new StringBuilder(" where ");
		final String AND = " and ";
		for (String key : filters.keySet()){
			Object value = filters.get(key);
			if (key.endsWith("descriptionShort")){
				key = "description";
			}
			if (key.endsWith("name")  || key.endsWith("category") || key.endsWith("level") || key.endsWith("status") || key.endsWith("description")){
				// challenge name, or username, category, or level, so like needed:
				whereClause.append(key + " like '" + value + "%'" + AND);
			}else{
				// something else, like Id, so equals needed:
				whereClause.append(key + " = '" + value + "'" + AND);
			}
		};
		
		int startIndex = whereClause.lastIndexOf(AND);
		whereClause.delete(startIndex, startIndex+AND.length());

		return whereClause.toString();
	}
	
	private String composeOrderClause (String sortField, SortOrder sortOrder){
		
		if (sortField!=null && sortField.trim().length() > 0){
			return " order by c." + sortField + " " + (sortOrder.equals(SortOrder.ASCENDING)? "ASC" : "DESC");
		}
		return "";
	}
	
	
	//
	public <T> T getChallengeable(Integer id, Class <T> clazz){
		return  this.em.find(clazz, id);
	};
    
		
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED) 
	public Result createOrUpdateAssignment(Assignment modifiedAssignment) {
		
		Assignment existingAssignment = null;
		Result result = null;
		if (modifiedAssignment.getId() > 0){
			// this is an existing, ev. modified assignment:
			existingAssignment = this.getChallengeable(modifiedAssignment.getId(), Assignment.class);
			existingAssignment.setStatus(modifiedAssignment.getStatus());
			existingAssignment.setDateDue(modifiedAssignment.getDateDue());
			existingAssignment.setAssignedTo(modifiedAssignment.getAssignedTo());
			existingAssignment.setLastFilePath(modifiedAssignment.getLastFilePath());
			
		}

		//Assignment existingAssignment = this.findAssignmentBy(assignment.getAssignedTo(), assignment.getChallenge());
		Assignment preparedAssignment = addAssignmentHistoryEntryFor( existingAssignment != null ? existingAssignment : modifiedAssignment);
		
		
		if (existingAssignment!=null){
			
			//we shall check here 
			//if the status change is possible
			
			Status fromStatus = existingAssignment.getStatus();
			Status toStatus = modifiedAssignment.getStatus();
			
			// check if it is possible
			// fromStatus ===> toStatus
			
			//....yes, it is:
			
			// 2.) update the parent assignment entry:
			// http://stackoverflow.com/questions/1069992/jpa-entitymanager-why-use-persist-over-merge
			this.em.merge(preparedAssignment);
			
			if (Status.Completed.equals(toStatus)){
				//if toStatus == Completed, create a Result:
				result = new Result();
				result.setAssignmentId(preparedAssignment.getId());
				result.setChallenge(preparedAssignment.getChallenge());
				result.setDateDue(preparedAssignment.getDateDue());
				result.setDateStarted(getDateStarted(preparedAssignment)); // here is date started to see the progress
				result.setDateCompleted(new Date (new java.util.Date().getTime())); //now
				result.setFilePath(preparedAssignment.getLastFilePath());
				result.setOfUser(preparedAssignment.getAssignedTo());
				this.em.persist(result);
			}
		}
		
		this.em.persist(preparedAssignment);
		return result;
	}
	
	/**
	 * In a list of assignment history entities ordered chronologically, the date started is defined 
	 * as the first date, in which the status is set to "started", after which no other status changes back
	 * to "assigned" occurred
	 * 
	 * @param assignment
	 * @return
	 */
	private Date getDateStarted (Assignment assignment){
		
		List<AssignmentHistory> assignmentHistory = assignment.getAssignmentHistory();
		Collections.sort(assignmentHistory, new Comparator<AssignmentHistory>(){
			@Override
			public int compare(AssignmentHistory o1, AssignmentHistory o2) {
				return o1.getId() - o2.getId();
			}
		});
		
		Date dateStared = null;
		
		for (int i = assignmentHistory.size(); i >=0; i-- ) {
			if (assignmentHistory.get(i).getStatus().equals(Status.Started)){
				Date currentDateStared =  assignmentHistory.get(i).getChangeDate();
				
				if (i > 0 && assignmentHistory.get(i-1).getStatus().equals(Status.Assigned)){
					dateStared = currentDateStared;
					break;
				}
			}
		}
		
		return dateStared;
	}
	
	
	/**
	 * this method creates a new AssignmentHistory for each assignment to be saved.
	 * It contains the latest status of the assignment, as well as the date (now) on which this status was set
	 * 
	 * @param assignment
	 * @return
	 */
	private Assignment  addAssignmentHistoryEntryFor (Assignment  assignment ){
		AssignmentHistory history = new AssignmentHistory();
		
		// 1.) create an assignment history entry:
		history.setAssignment(assignment);
		history.setChangeDate(new Date(new java.util.Date().getTime()));
		history.setStatus(assignment.getStatus());
		history.setDueDate(assignment.getDateDue());
		history.setFilePath(assignment.getLastFilePath());
		
		// 2.) update the parent assignment entry:
		// http://stackoverflow.com/questions/1069992/jpa-entitymanager-why-use-persist-over-merge
		assignment.getAssignmentHistory().add(history);
		return assignment;
	}
	
	@Override
	public Assignment findAssignmentBy(User user, Challenge challenge){
		
		if (user ==null || challenge == null)
			return null;
		
		Query query = this.em.createQuery("from Assignment a where a.assignedTo.username =:UNAME and a.challenge.id=:CHALLID");
		
		query.setParameter("UNAME", user.getUsername());
		query.setParameter("CHALLID", challenge.getId());
		
		List <Assignment> assignmentList = query.getResultList();
		
		return ( assignmentList.size() == 0? null : assignmentList.get(0));
	}
	
	
	/**
	 * This method loads an assignement with all of its assignment history
	 * 
	 * @param id
	 * @return
	 */
	public Assignment getAssignementFetchHistory(Integer assignementId){
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Assignment> q = cb.createQuery(Assignment.class);
		Root<Assignment> o = q.from(Assignment.class);
		o.fetch("assignmentHistory", JoinType.INNER);
		q.select(o);
		q.where(cb.equal(o.get("id"), assignementId));

		Assignment assignment = (Assignment)this.em.createQuery(q).getSingleResult();
		
		return assignment;
		
	}


}
