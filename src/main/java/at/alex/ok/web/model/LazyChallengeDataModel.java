package at.alex.ok.web.model;

import java.util.List;
import java.util.Map;


import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import at.alex.ok.model.Challenge;
import at.alex.ok.services.ChallengeService;

/**
 * 
 * @author User
 *
 */

public class LazyChallengeDataModel extends LazyDataModel<Challenge> {
     
	private static final long serialVersionUID = 1L;
	
	private ChallengeService challengeService;
	
	public LazyChallengeDataModel(){
		this.challengeService = ServiceLocator.getInstance().getChallengeService();
	}
	
     
    
    @Override
    public Challenge getRowData(String rowKey) {
        return challengeService.getChallengeable(Integer.valueOf(rowKey), Challenge.class);
    }
 
    @Override
    public Object getRowKey(Challenge challenge) {
        return challenge.getId();
    }
 
    @Override
    public List<Challenge> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
    	
    	List result =  this.challengeService.load(first, pageSize, "id", sortOrder, filters, Challenge.class);
    	
    	//rowCount
        this.setRowCount(result.size());
    	
    	return result;
    }
}