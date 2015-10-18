package publix.services.personal_multiple;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.StudyModel;
import models.workers.PersonalMultipleWorker;
import publix.exceptions.ForbiddenPublixException;
import publix.services.IStudyAuthorisation;

/**
 * PersonalMultiplePublix's Implementation of IStudyAuthorization
 * 
 * @author Kristian Lange
 */
@Singleton
public class PersonalMultipleStudyAuthorisation implements
		IStudyAuthorisation<PersonalMultipleWorker> {

	private final PersonalMultipleErrorMessages errorMessages;

	@Inject
	PersonalMultipleStudyAuthorisation(
			PersonalMultipleErrorMessages errorMessages) {
		this.errorMessages = errorMessages;
	}

	@Override
	public void checkWorkerAllowedToStartStudy(PersonalMultipleWorker worker,
			StudyModel study) throws ForbiddenPublixException {
		checkWorkerAllowedToDoStudy(worker, study);
	}

	@Override
	public void checkWorkerAllowedToDoStudy(PersonalMultipleWorker worker,
			StudyModel study) throws ForbiddenPublixException {
		if (!study.hasAllowedWorker(worker.getWorkerType())) {
			throw new ForbiddenPublixException(
					errorMessages.workerTypeNotAllowed(worker.getUIWorkerType()));
		}
	}

}
