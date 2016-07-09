package controllers.api;

import controllers.CustomController;
import controllers.api.dto.plugins.PluginDto;
import controllers.api.dto.plugins.PluginsDto;
import controllers.api.dto.plugins.SavePluginDto;
import controllers.api.util.ResponseUtil;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.data.Form;
import play.data.format.Formatters;
import play.data.validation.ValidatorProvider;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import ro.fortsoft.pf4j.PluginState;
import ro.fortsoft.pf4j.PluginWrapper;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Plugins extends CustomController {
    private final MessagesApi messagesApi;
    private final Formatters formatters;
    private final ValidatorProvider validatorProvider;

    @Inject
    public Plugins(MessagesApi messagesApi,
                          Formatters formatters,
                          ValidatorProvider validatorProvider) {
        this.messagesApi = messagesApi;
        this.formatters = formatters;
        this.validatorProvider = validatorProvider;
    }

    public Result getPlugins() {
        try {
            List<PluginWrapper> plugins = playApp.getPlugins();
            List<PluginDto> dtos = new ArrayList<>();
            for (PluginWrapper pluginWrapper : plugins) {
                dtos.add(PluginDto.from(pluginWrapper));
            }
            return ok(Json.toJson(PluginsDto.from(dtos)));
        } catch (Exception ex) {
            Logger.error("An unhandled error occurred while attempting to retrieve plugins.", ex);
            return internalServerError(ResponseUtil.getErrorAsJson("An unhandled error occurred while attempting to retrieve plugins: " + ex.getMessage()));
        }
    }

    public Result installPlugin() {
        try {
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart pluginFile = body.getFile("file");

            File plugin = (File) pluginFile.getFile();
            File targetPlugin = new File("./" + playApp.getPluginDirectory() + "/" + pluginFile.getFilename());
            try {
                FileUtils.moveFile(plugin, targetPlugin);
            } catch (FileExistsException fileExistsException) {
                return badRequest(ResponseUtil.getErrorAsJson("A plugin with that name and version already exists."));
            }

            String id = playApp.loadPlugin(targetPlugin);
            playApp.startPlugin(id);

            return ok(ResponseUtil.getSuccess(id));
        } catch (Exception ex) {
            Logger.error("An unhandled error occurred while attempting to install a plugin with id.", ex);
            return internalServerError(ResponseUtil.getErrorAsJson("An unhandled error occurred while attempting to install a plugin: " + ex.getMessage()));
        }
    }

    public Result deletePlugin(String id) {
        try {
            PluginWrapper pluginWrapper = playApp.getPlugin(id);
            if (pluginWrapper == null) {
                return notFound(ResponseUtil.getErrorAsJson("No plugin found with id: " + id + "."));
            }

            boolean deleted = playApp.deletePlugin(id);
            if (!deleted) {
                return internalServerError(ResponseUtil.getErrorAsJson("The plugin was attempted to be deleted, but something went wrong."));
            }

            return ok(ResponseUtil.getSuccess());
        } catch (Exception ex) {
            Logger.error("An unhandled error occurred while attempting to delete a plugin with id: " + id + ".", ex);
            return internalServerError(ResponseUtil.getErrorAsJson("An unhandled error occurred while attempting to delete a plugin with id [" + id + "]: " + ex.getMessage()));
        }
    }

    public Result updatePlugin(String id) {
        try {
            PluginWrapper pluginWrapper = playApp.getPlugin(id);
            if (pluginWrapper == null) {
                return notFound(ResponseUtil.getErrorAsJson("No plugin found with id: " + id + "."));
            }

            Form<SavePluginDto> form = (new Form<>(SavePluginDto.class, messagesApi, formatters, validatorProvider.get())).bind(request().body().asJson());

            // validate
            if (form.hasErrors()) {
                return badRequest(form.errorsAsJson());
            }

            SavePluginDto dto = form.get();

            if (dto.pluginState != null && dto.pluginState.trim().length() > 0) {
                boolean updateResult = false;
                switch (dto.pluginState.toUpperCase()) {
                    case "ENABLE":
                        updateResult = playApp.enablePlugin(id);
                        break;
                    case "DISABLE":
                        updateResult = playApp.disablePlugin(id);
                        break;
                    case "START":
                        PluginState state = playApp.startPlugin(id);
                        updateResult = state == PluginState.STARTED;
                        break;
                    case "STOP":
                        PluginState stateRes = playApp.stopPlugin(id);
                        updateResult = stateRes == PluginState.STOPPED;
                        break;
                }

                if (!updateResult) {
                    return internalServerError(ResponseUtil.getErrorAsJson("The state of the plugin did not change for an unknown reason."));
                }

                return ok(ResponseUtil.getSuccess());
            } else {
                return badRequest(ResponseUtil.getErrorAsJson("The request must supply something to update."));
            }
        } catch (Exception ex) {
            Logger.error("An unhandled error occurred while attempting to update a plugin with id: " + id + ".", ex);
            return internalServerError(ResponseUtil.getErrorAsJson("An unhandled error occurred while attempting to update a plugin with id [" + id + "]: " + ex.getMessage()));
        }
    }
}
