package at.alex.ok.web.model;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import at.alex.ok.model.Assignment;
import at.alex.ok.model.Challenge;
import at.alex.ok.services.ChallengeService;

public class LazyAssignmentDataModel extends LazyDataModel<Assignment> {

	private static final long serialVersionUID = 1L;

	private ChallengeService challengeService;

	private Challenge filterByChallenge;

	private String filterByUserId;

	public LazyAssignmentDataModel(Challenge filterByChallenge,
			String filterByUserId) {
		this.filterByChallenge = filterByChallenge;
		this.filterByUserId = filterByUserId;
		this.challengeService = ServiceLocator.getInstance()
				.getChallengeService();
	}

	@Override
	public Assignment getRowData(String rowKey) {
		return challengeService.getChallengeable(Integer.valueOf(rowKey),
				Assignment.class);
	}

	@Override
	public Object getRowKey(Assignment assignment) {
		return assignment.getId();
	}

	@Override
	public List<Assignment> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {

		if (filterByChallenge != null) {
			filters.put(ChallengeService.FILTER_ASSIGNMENTS_BY_CHALLENGE_ID,
					filterByChallenge.getId());
		}

		if (filterByUserId != null) {
			filters.put(ChallengeService.FILTER_ASSIGNMENTS_BY_USER_ID,
					filterByUserId);
		}

		List result = this.challengeService.load(first, pageSize, sortField,
				sortOrder, filters, Assignment.class);
		
		int resultSize = (int)this.challengeService.totalCount(filters, Assignment.class);
    	
		// rowCount
		this.setRowCount(resultSize);

		return result;
	}
}
