package common;

import play.Logger;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Http;
import play.mvc.SimpleResult;
import exceptions.publix.PublixException;

/**
 * For all actions in a controller that is annotated with PublixAction catch
 * {@link PublixException} and return a SimpleResult generated by the exception.
 * 
 * @author Kristian Lange
 */
public class PublixAction extends play.mvc.Action.Simple {

	public F.Promise<SimpleResult> call(Http.Context ctx) throws Throwable {
		Promise<SimpleResult> call;
		try {
			call = delegate.call(ctx);
		} catch (PublixException e) {
			SimpleResult result = e.getSimpleResult();
			Logger.info("PublixException: " + e.getMessage());
			call = Promise.<SimpleResult> pure(result);
		}
		return call;
	}

}
