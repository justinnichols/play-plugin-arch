var plugins = (function() {
    var initHasRun = false;
    var $plugins = $("#plugins");
    var $pluginList = $plugins.find("#plugin-list");
    var $pluginListItemTemplate = $pluginList.find("#plugin-list-item");
    var $confirmDeleteModal = $plugins.find(".confirm-delete-plugin");
    var $pluginFileInput = $plugins.find("#pluginFileInput");
    var $pluginFileText = $plugins.find("#pluginFileText");
    var $installPluginButton = $plugins.find("#installPlugin");
    var $noplugins = $('<tr><td colspan="5" style="text-align: center; vertical-align: middle; font-style: italic; height: 100px;">No Plugins</td></tr>');

    function init() {
        if (initHasRun) return;

        initHasRun = true;

        loadAllPlugins();
    }

    function loadAllPlugins() {
        $.ajax({
            url: "/api/plugins",
            type: "GET",
            dataType: 'json',
            contentType: "application/json"
        })
            .success(function(data) {
                if (data.plugins && data.plugins.length > 0) {
                    $pluginList.html(
                        $pluginListItemTemplate.render(data.plugins, {})
                    );
                } else {
                    $pluginList.html($noplugins);
                }
            })
            .error(function(error) {
                console.log(error);
            });
    }

    ///////////////////// INSTALL PLUGIN ////////////////////////
    $pluginFileInput.on("change", function() {
        var fileName = $(this).val();
        if (fileName) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        }
        if (fileName && fileName.trim() !== '') {
            $pluginFileText.val(fileName);
            $installPluginButton.removeClass("disabled");
        } else {
            $installPluginButton.addClass("disabled");
        }
    });

    $installPluginButton.click(function() {
        var ladda = startLadda(this);
        var formData = new FormData();
        formData.append('file', $pluginFileInput[0].files[0]);

        $.ajax({
            url: "/api/plugins",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false
        })
            .success(function(data) {
                stopLadda(ladda);
                $pluginFileInput.wrap('<form>').closest('form').get(0).reset();
                $pluginFileInput.unwrap();
                $installPluginButton.addClass("disabled");
                $pluginFileText.val('');
                MessageController
                    .container("#message-container")
                    .template("#plugins-install-success")
                    .replace();
                loadAllPlugins();
            })
            .error(function(error) {
                stopLadda(ladda);
                MessageController
                    .container("#message-container")
                    .template("#generic-error")
                    .error(error.responseJSON)
                    .replace();
            });
    });
    /////////////////////////////////////////////////////////////
    
    ////////////////////// CHANGE STATE ////////////////////////
    $pluginList.on("click", ".btn.change-state", function() {
        var ladda = startLadda(this);
        var id = $(this).data("id");
        var data = {
            pluginState: $(this).data("state").toUpperCase()
        };
        
        $.ajax({
            url: "/api/plugins/" + id,
            type: "PUT",
            dataType: 'json',
            contentType: "application/json",
            data: JSON.stringify(data)
        })
            .success(function(data) {
                stopLadda(ladda);
                loadAllPlugins();
            })
            .error(function(error) {
                stopLadda(ladda);
                MessageController
                    .container("#message-container")
                    .template("#generic-error")
                    .error(error.responseJSON)
                    .replace();
            });
    });
    ////////////////////////////////////////////////////////////
    
    ///////////////////////// DELETE ///////////////////////////
    // delete buttons
    $pluginList.on("click", ".btn.delete", function() {
        resetConfirmDeleteModal();

        var id = $(this).data("id");
        $confirmDeleteModal.find("#deleteName").html(id);
        $confirmDeleteModal.find(".btn.yes").data("id", id);

        $confirmDeleteModal.modal("show");
    });

    $confirmDeleteModal.on("click", ".btn.yes", function() {
        var ladda = startLadda(this);
        var id = $(this).data("id");
        $.ajax({
            url: "/api/plugins/" + id,
            type: "DELETE",
            dataType: 'json',
            contentType: "application/json"
        })
            .success(function(data) {
                stopLadda(ladda);
                $confirmDeleteModal.modal("hide");
                MessageController
                    .container("#message-container")
                    .template("#plugins-delete-success")
                    .replace();
                loadAllPlugins();
            })
            .error(function(error) {
                stopLadda(ladda);
                $confirmDeleteModal.modal("hide");
                MessageController
                    .container("#message-container")
                    .template("#plugins-delete-error")
                    .error(error.responseJSON)
                    .replace();
            });
    });

    $confirmDeleteModal.on("click", ".btn.no", function() {
        $confirmDeleteModal.modal("hide");
    });

    $confirmDeleteModal.on("hidden.bs.modal", function() {
        resetConfirmDeleteModal();
    });

    function resetConfirmDeleteModal() {
        $confirmDeleteModal.find("#deleteName").html("");
        $confirmDeleteModal.find(".btn.yes").attr("data-id", "");
    }
    ////////////////////////////////////////////////////////////
    
    // public interface
    return {
        init: init
    }
}());