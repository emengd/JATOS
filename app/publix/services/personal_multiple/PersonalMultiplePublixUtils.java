package publix.services.personal_multiple;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.workers.PersonalMultipleWorker;
import models.workers.Worker;
import persistance.ComponentDao;
import persistance.ComponentResultDao;
import persistance.StudyDao;
import persistance.StudyResultDao;
import persistance.workers.WorkerDao;
import publix.exceptions.ForbiddenPublixException;
import publix.services.PublixUtils;

/**
 * PersonalMultiplePublix' implementation of PublixUtils
 * 
 * @author Kristian Lange
 */
@Singleton
public class PersonalMultiplePublixUtils extends
		PublixUtils<PersonalMultipleWorker> {

	@Inject
	PersonalMultiplePublixUtils(PersonalMultipleErrorMessages errorMessages,
			StudyDao studyDao, StudyResultDao studyResultDao,
			ComponentDao componentDao, ComponentResultDao componentResultDao,
			WorkerDao workerDao) {
		super(errorMessages, studyDao, studyResultDao, componentDao,
				componentResultDao, workerDao);
	}

	@Override
	public PersonalMultipleWorker retrieveTypedWorker(String workerIdStr)
			throws ForbiddenPublixException {
		Worker worker = retrieveWorker(workerIdStr);
		if (!(worker instanceof PersonalMultipleWorker)) {
			throw new ForbiddenPublixException(
					errorMessages.workerNotCorrectType(worker.getId()));
		}
		return (PersonalMultipleWorker) worker;
	}

}
