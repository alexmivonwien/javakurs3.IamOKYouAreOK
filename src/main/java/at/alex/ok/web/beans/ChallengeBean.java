package at.alex.ok.web.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import at.alex.ok.model.Challenge;
import at.alex.ok.model.User;
import at.alex.ok.model.enums.Category;
import at.alex.ok.model.enums.Level;
import at.alex.ok.services.ChallengeService;
import at.alex.ok.services.UserService;
import at.alex.ok.web.utils.NavigationUtils;


/**
 * Unless you're using JSF 2.2 (which is still not out yet at this moment) or
 * MyFaces CODI (which I'd have expected that you would explicitly mention that)
 * the @ViewScoped doesn't work in CDI. This also pretty much matches your
 * problem symptoms.
 * 
 * http://stackoverflow.com/questions/14812238/jsf-view-scoped-bean-
 * reconstructed-multiple-times
 * 
 * 
 * As per JSF 2.2 and higher, @ManagedBean is deprecated. Use @Named together with @javax.faces.view.ViewScoped,
 * @see https://stackoverflow.com/a/4347707/1925356
 * @see @javax.faces.view.ViewScoped documentation:  
 * 
 * When this annotation, along with javax.inject.Named is found on a class, the runtime must place
 * the bean in a CDI scope such that it remains active as long as javax.faces.application.NavigationHandler.handleNavigation 
 * does not cause a navigation to a view with a viewId that is different than theview Id of the current view. Any injections and
 * notifications required by CDI and the Java EE platform must occur as usual at the expected time.
 * 
 * 
 */

@Named ("challengeBean")
@javax.faces.view.ViewScoped
// javax.faces.bean.ViewScoped is deprecated as per JSF 2.2
// javax.faces.bean.ManagedBean is deprecated as per JSF 2.2
public class ChallengeBean implements Serializable{

	@Inject
	private ChallengeService challengeService;

	@Inject
	private UserService userService;

	private boolean editMode;

	private Challenge existingChallenge;

	private String name;
	private String description;
	private Level level;
	private Category category;

	@PostConstruct
	private void init() {

		Flash flash = FacesContext.getCurrentInstance().getExternalContext()
				.getFlash();

		this.existingChallenge = (Challenge) flash
				.get(AllChallengesBean.SELECTED_CHALLENGE_PARAM);
		
		if (existingChallenge!=null){
			this.name = this.existingChallenge.getName();
			this.description = this.existingChallenge.getDescription();
			this.level = this.existingChallenge.getLevel();
			this.category = this.existingChallenge.getCategory();
		}

	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public Challenge getExistingChallenge() {
		return existingChallenge;
	}

	public void setExistingChallenge(Challenge existingChallenge) {
		this.existingChallenge = existingChallenge;
	}

	
	public ChallengeService getChallengeService() {
		return challengeService;
	}

	public void setChallengeService(ChallengeService challengeService) {
		this.challengeService = challengeService;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SelectItem> getLevelValues() {
		List<SelectItem> result = new ArrayList<SelectItem>();

		for (int i = 0; i < Level.values().length; i++) {
			result.add(new SelectItem(Level.values()[i], Level.values()[i]
					.getName()));
		}
		return result;
	}

	public List<SelectItem> getCategoryValues() {
		List<SelectItem> result = new ArrayList<SelectItem>();

		for (int i = 0; i < Category.values().length; i++) {
			result.add(new SelectItem(Category.values()[i],
					Category.values()[i].toString()));
		}
		return result;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String createChallenge() {

		Challenge challenge = new Challenge();

		challenge.setName(name);
		challenge.setDescription(getDescription());
		challenge.setLevel(level == null ? Level.Beginner : level);
		challenge.setCategory(category == null ? Category.Human : category);
		challenge.setCreatedOn(new Date(new java.util.Date().getTime()));

		String remoteUser = FacesContext.getCurrentInstance()
				.getExternalContext().getRemoteUser();
		User createdByuser = userService.findByUsernameOrEmail(remoteUser,
				remoteUser);
		challenge.setCreatedBy(createdByuser);

		List<Challenge> challenges = new ArrayList<Challenge>();
		challenges.add(challenge);

		this.challengeService.createChallenge(challenges);
		return NavigationUtils.getDevicePrefix() + "/pages/challenges?faces-redirect=true";
	}

}
