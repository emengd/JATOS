package publix.services.jatos;

import javax.inject.Singleton;

import models.workers.JatosWorker;
import publix.services.PublixErrorMessages;

/**
 * JatosPublix' implementation of PublixErrorMessages (studies or components
 * started via JATOS' UI).
 * 
 * @author Kristian Lange
 */
@Singleton
public class JatosErrorMessages extends PublixErrorMessages {

	public static final String NO_USER_LOGGED_IN = "No user logged in";
	public static final String STUDY_NEVER_STARTED_FROM_JATOS = "This study was never started from within JATOS.";
	public static final String STUDY_OR_COMPONENT_NEVER_STARTED_FROM_JATOS = "This study or component was never started from within JATOS.";

	@Override
	public String workerNotCorrectType(Long workerId) {
		return "The worker with ID " + workerId + " isn't a "
				+ JatosWorker.UI_WORKER_TYPE + " worker.";
	}

	public String userNotExist(String email) {
		return "An user with email " + email + " doesn't exist.";
	}

}
