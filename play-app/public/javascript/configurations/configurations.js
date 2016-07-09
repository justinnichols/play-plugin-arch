var configurations = (function() {
    var initHasRun = false;
    var $integrations = $("#configurations");
    var $configurationList = $integrations.find("#configuration-list");
    var $configurationListItemTemplate = $configurationList.find("#configuration-list-item");
    var $confirmDeleteModal = $integrations.find(".confirm-delete-configuration");
    var $noconfigurations = $('<tr><td colspan="4" style="text-align: center; vertical-align: middle; font-style: italic; height: 100px;">No Configurations</td></tr>');

    function init() {
        if (initHasRun) return;

        initHasRun = true;

        loadAllConfigurations();
    }

    function loadAllConfigurations() {
        $.ajax({
                url: "/api/configurations",
                type: "GET",
                dataType: 'json',
                contentType: "application/json"
            })
            .success(function(data) {
                if (data.configurations && data.configurations.length > 0) {
                    $.each(data.configurations, function(index, item) {
                        if (item.processorPluginExtension) {
                            item.processorPluginExtension = item.processorPluginExtension.substring(item.processorPluginExtension.lastIndexOf(".") + 1);
                        }
                    });
                    $configurationList.html(
                        $configurationListItemTemplate.render(data.configurations, {})
                    );
                } else {
                    $configurationList.html($noconfigurations);
                }
            })
            .error(function(error) {
                console.log(error);
            });
    }

    // edit buttons
    $configurationList.on("click", ".btn.edit", function() {
        window.location.href="/admin/configurations/edit/" + $(this).data("id");
    });

    // delete buttons
    $configurationList.on("click", ".btn.delete", function() {
        resetConfirmDeleteModal();

        var id = $(this).data("id");
        var name = $(this).data("name");
        $confirmDeleteModal.find("#deleteName").html(name);
        $confirmDeleteModal.find(".btn.yes").attr("data-id", id);

        $confirmDeleteModal.modal("show");
    });

    $confirmDeleteModal.on("click", ".btn.yes", function() {
        var ladda = startLadda(this);
        var id = $(this).data("id");
        $.ajax({
                url: "/api/configurations/" + id,
                type: "DELETE",
                dataType: 'json',
                contentType: "application/json"
            })
            .success(function(data) {
                stopLadda(ladda);
                $confirmDeleteModal.modal("hide");
                MessageController
                    .container("#message-container")
                    .template("#configuration-delete-success")
                    .replace();
                loadAllConfigurations();
            })
            .error(function(error) {
                stopLadda(ladda);
                $confirmDeleteModal.modal("hide");
                MessageController
                    .container("#message-container")
                    .template("#configuration-delete-error")
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

    // public interface
    return {
        init: init
    }
}());