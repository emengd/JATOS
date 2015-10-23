package services;

import javax.inject.Singleton;

import models.Breadcrumbs;
import models.Component;
import models.Study;
import models.User;
import models.workers.Worker;
import play.Logger;
import utils.JsonUtils;
import utils.MessagesStrings;

import com.fasterxml.jackson.core.JsonProcessingException;
import common.RequestScopeMessaging;

/**
 * Provides breadcrumbs for different views of JATOS' GUI.
 * 
 * @author Kristian Lange
 */
@Singleton
public class BreadcrumbsService {

	private static final String CLASS_NAME = BreadcrumbsService.class
			.getSimpleName();

	public static final String HOME = "Home";
	public static final String EDIT_PROPERTIES = "Edit Properties";
	public static final String WORKERS = "Workers";
	public static final String MECHANICAL_TURK_HIT_LAYOUT_SOURCE_CODE = "Mechanical Turk HIT Layout Source Code";
	public static final String CHANGE_USERS = "Change Users";
	public static final String NEW_STUDY = "New Study";
	public static final String RESULTS = "Results";
	public static final String NEW_COMPONENT = "New Component";
	public static final String CHANGE_PASSWORD = "Change Password";
	public static final String EDIT_PROFILE = "Edit Profile";
	public static final String NEW_USER = "New User";

	public String generateForHome() {
		return generateForHome(null);
	}

	public String generateForHome(String last) {
		Breadcrumbs breadcrumbs = new Breadcrumbs();
		if (last != null) {
			breadcrumbs.addBreadcrumb(HOME, controllers.routes.Home.home()
					.url());
			breadcrumbs.addBreadcrumb(last, "");
		} else {
			breadcrumbs.addBreadcrumb(HOME, "");
		}
		String breadcrumbsStr = "";
		try {
			breadcrumbsStr = JsonUtils.asJson(breadcrumbs);
		} catch (JsonProcessingException e) {
			Logger.error(CLASS_NAME + ".generateForHome", e);
			RequestScopeMessaging
					.warning(MessagesStrings.PROBLEM_GENERATING_BREADCRUMBS);
		}
		return breadcrumbsStr;
	}

	public String generateForUser(User user) {
		return generateForUser(user, null);
	}

	public String generateForUser(User user, String last) {
		Breadcrumbs breadcrumbs = new Breadcrumbs();
		breadcrumbs.addBreadcrumb(HOME, controllers.routes.Home.home().url());
		if (last != null) {
			breadcrumbs.addBreadcrumb(user.toString(), controllers.routes.Users
					.profile(user.getEmail()).url());
			breadcrumbs.addBreadcrumb(last, "");
		} else {
			breadcrumbs.addBreadcrumb(user.toString(), "");
		}
		String breadcrumbsStr = "";
		try {
			breadcrumbsStr = JsonUtils.asJson(breadcrumbs);
		} catch (JsonProcessingException e) {
			Logger.error(CLASS_NAME + ".generateForUser", e);
			RequestScopeMessaging
					.warning(MessagesStrings.PROBLEM_GENERATING_BREADCRUMBS);
		}
		return breadcrumbsStr;
	}

	public String generateForStudy(Study study) {
		return generateForStudy(study, null);
	}

	public String generateForStudy(Study study, String last) {
		Breadcrumbs breadcrumbs = new Breadcrumbs();
		breadcrumbs.addBreadcrumb(HOME, controllers.routes.Home.home().url());
		if (last != null) {
			breadcrumbs.addBreadcrumb(study.getTitle(),
					controllers.routes.Studies.index(study.getId()).url());
			breadcrumbs.addBreadcrumb(last, "");
		} else {
			breadcrumbs.addBreadcrumb(study.getTitle(), "");
		}
		String breadcrumbsStr = "";
		try {
			breadcrumbsStr = JsonUtils.asJson(breadcrumbs);
		} catch (JsonProcessingException e) {
			Logger.error(CLASS_NAME + ".generateForStudy", e);
			RequestScopeMessaging
					.warning(MessagesStrings.PROBLEM_GENERATING_BREADCRUMBS);
		}
		return breadcrumbsStr;
	}

	public String generateForWorker(Worker worker) {
		return generateForWorker(worker, null);
	}

	public String generateForWorker(Worker worker, String last) {
		Breadcrumbs breadcrumbs = new Breadcrumbs();
		breadcrumbs.addBreadcrumb(HOME, controllers.routes.Home.home().url());
		breadcrumbs.addBreadcrumb("Worker " + worker.getId(), "");
		breadcrumbs.addBreadcrumb(last, "");
		String breadcrumbsStr = "";
		try {
			breadcrumbsStr = JsonUtils.asJson(breadcrumbs);
		} catch (JsonProcessingException e) {
			Logger.error(CLASS_NAME + ".generateForWorker", e);
			RequestScopeMessaging
					.warning(MessagesStrings.PROBLEM_GENERATING_BREADCRUMBS);
		}
		return breadcrumbsStr;
	}

	public String generateForComponent(Study study,
			Component component, String last) {
		Breadcrumbs breadcrumbs = new Breadcrumbs();
		breadcrumbs.addBreadcrumb(HOME, controllers.routes.Home.home().url());
		breadcrumbs.addBreadcrumb(study.getTitle(), controllers.routes.Studies
				.index(study.getId()).url());
		breadcrumbs.addBreadcrumb(component.getTitle(), "");
		breadcrumbs.addBreadcrumb(last, "");
		String breadcrumbsStr = "";
		try {
			breadcrumbsStr = JsonUtils.asJson(breadcrumbs);
		} catch (JsonProcessingException e) {
			Logger.error(CLASS_NAME + ".generateForComponent", e);
			RequestScopeMessaging
					.warning(MessagesStrings.PROBLEM_GENERATING_BREADCRUMBS);
		}
		return breadcrumbsStr;
	}

}
