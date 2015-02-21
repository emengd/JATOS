package controllers.publix.open_standalone;

import models.ComponentModel;
import models.StudyModel;
import models.workers.OpenStandaloneWorker;
import persistance.IComponentResultDao;
import persistance.IStudyResultDao;
import persistance.workers.WorkerDao;
import play.Logger;
import play.mvc.Result;
import utils.JsonUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import controllers.publix.IPublix;
import controllers.publix.Publix;
import exceptions.publix.PublixException;

/**
 * Implementation of JATOS' public API for open standalone study runs (open to
 * everyone).
 * 
 * @author Kristian Lange
 */
@Singleton
public class OpenStandalonePublix extends Publix<OpenStandaloneWorker>
		implements IPublix {

	public static final String COOKIE = "JATOS_OPENSTANDALONE";
	public static final String OPENSTANDALONE = "openStandalone";

	private static final String CLASS_NAME = OpenStandalonePublix.class
			.getSimpleName();

	private final OpenStandalonePublixUtils publixUtils;
	private final WorkerDao workerDao;

	@Inject
	OpenStandalonePublix(OpenStandalonePublixUtils publixUtils,
			IComponentResultDao componentResultDao, JsonUtils jsonUtils,
			IStudyResultDao studyResultDao, WorkerDao workerDao) {
		super(publixUtils, componentResultDao, jsonUtils, studyResultDao);
		this.publixUtils = publixUtils;
		this.workerDao = workerDao;
	}

	@Override
	public Result startStudy(Long studyId) throws PublixException {
		Logger.info(CLASS_NAME + ".startStudy: studyId " + studyId);
		StudyModel study = publixUtils.retrieveStudy(studyId);
		publixUtils.checkAllowedToDoStudy(study);
		publixUtils.addStudyToCookie(study);

		OpenStandaloneWorker worker = new OpenStandaloneWorker();
		workerDao.create(worker);
		publixUtils.checkWorkerAllowedToStartStudy(worker, study);
		session(WORKER_ID, worker.getId().toString());

		publixUtils.finishAllPriorStudyResults(worker, study);
		studyResultDao.create(study, worker);

		ComponentModel firstComponent = publixUtils
				.retrieveFirstActiveComponent(study);
		return redirect(controllers.publix.routes.PublixInterceptor
				.startComponent(studyId, firstComponent.getId()));
	}

}
