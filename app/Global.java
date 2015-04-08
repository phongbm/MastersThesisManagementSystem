import com.avaje.ebean.Ebean;
import models.UserAccount;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;
import play.libs.F.Promise;
import play.libs.Yaml;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

import java.util.List;
import java.util.Map;

import static play.mvc.Results.notFound;

public class Global extends GlobalSettings {

    public void onStart(Application app) {
        Logger.info("Application has started");
        InitialData.insert(app);
    }

    static class InitialData {
        public static void insert(Application app) {
            Map<String, List<Object>> all =
                    (Map<String, List<Object>>) Yaml.load("initial-data.yml");
            if (Ebean.find(UserAccount.class).findRowCount() == 0) {
                Ebean.save(all.get("user_accounts"));
            }
        }
    }

    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }

    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{CSRFFilter.class};
    }

    public Promise<Result> onHandlerNotFound(RequestHeader request) {
        return Promise.<Result>pure(notFound(
                views.html.notfoundpage.render(request.uri())
        ));
    }



}