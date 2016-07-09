var edit_configuration = (function() {
    var initHasRun = false;
    var $editconfiguration = $("#edit-configuration");
    var $saveButton = $editconfiguration.find(".btn.save");

    function init() {
        if (initHasRun) return;

        initHasRun = true;
    }

    $saveButton.click(function() {
        var ladda = startLadda(this);
        var taskProcessor = $editconfiguration.find("#task-processor").val().split('@');
        var data = {
            enabled: $editconfiguration.find("#enabled").val(),
            name: $editconfiguration.find("#name").val(),
            processorPluginName: taskProcessor[0],
            processorPluginVersion: taskProcessor[1],
            processorPluginExtension: taskProcessor[2],
            schedule: {
                startTimeHour: $editconfiguration.find("#taskStartTimeHour").val(),
                startTimeMinute: $editconfiguration.find("#taskStartTimeMinute").val(),
                startTimeAMPM: $editconfiguration.find("#taskStartTimeAMPM").val(),
                delayTimeVal: $editconfiguration.find("#taskDelayTimeVal").val(),
                delayTimeUnits: $editconfiguration.find("#taskDelayTimeUnits").val(),
                intervalTimeVal: $editconfiguration.find("#taskIntervalTimeVal").val(),
                intervalTimeUnits: $editconfiguration.find("#taskIntervalTimeUnits").val(),
                autoStart: $editconfiguration.find("#taskAutoStart").val()
            }
        };
        $.ajax({
                url: "/api/configurations/" + $(this).data("id"),
                type: "PUT",
                dataType: 'json',
                contentType: "application/json",
                data: JSON.stringify(data)
            })
            .success(function(data) {
                stopLadda(ladda);
                MessageController
                    .container("#message-container")
                    .template("#configuration-edit-success")
                    .replace();
            })
            .error(function(error) {
                console.log(error);
                stopLadda(ladda);
                MessageController
                    .container("#message-container")
                    .template("#configuration-edit-error")
                    .error(error.responseJSON)
                    .replace();
            });
    });

    // public interface
    return {
        init: init
    }
}());