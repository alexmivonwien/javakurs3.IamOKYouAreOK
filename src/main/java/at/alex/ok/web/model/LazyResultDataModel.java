package at.alex.ok.web.model;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import at.alex.ok.model.Assignment;
import at.alex.ok.model.Challenge;
import at.alex.ok.model.Result;
import at.alex.ok.services.ChallengeService;

public class LazyResultDataModel extends LazyDataModel<Result> {

	private static final long serialVersionUID = 1L;

	private ChallengeService challengeService;

	private Challenge filterByChallenge;

	private String filterByUserId;

	public LazyResultDataModel(Challenge filterByChallenge,
			String filterByUserId) {
		this.filterByChallenge = filterByChallenge;
		this.filterByUserId = filterByUserId;
		this.challengeService = ServiceLocator.getInstance()
				.getChallengeService();
	}

	@Override
	public Result getRowData(String rowKey) {
		return challengeService.getChallengeable(Integer.valueOf(rowKey),
				Result.class);
	}

	@Override
	public Object getRowKey(Result result) {
		return result.getId();
	}

	@Override
	public List<Result> load(int first, int pageSize, String sortField,
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
				sortOrder, filters, Result.class);

		// rowCount
		this.setRowCount(result.size());

		return result;
	}
}
