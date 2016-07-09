var add_configuration = (function() {
    var initHasRun = false;
    var $addconfiguration = $("#add-configuration");
    var $saveButton = $addconfiguration.find(".btn.save");

    function init() {
        if (initHasRun) return;

        initHasRun = true;
    }

    function enableSave() {
        $saveButton.removeAttr("disabled");
    }

    function disableSave() {
        $saveButton.attr("disabled", "disabled");
    }

    $saveButton.click(function() {
        var ladda = startLadda(this);
        var taskProcessor = $addconfiguration.find("#task-processor").val().split('@');
        var data = {
            enabled: $addconfiguration.find("#enabled").val(),
            name: $addconfiguration.find("#name").val(),
            processorPluginName: taskProcessor[0],
            processorPluginVersion: taskProcessor[1],
            processorPluginExtension: taskProcessor[2],
            schedule: {
                startTimeHour: $addconfiguration.find("#taskStartTimeHour").val(),
                startTimeMinute: $addconfiguration.find("#taskStartTimeMinute").val(),
                startTimeAMPM: $addconfiguration.find("#taskStartTimeAMPM").val(),
                delayTimeVal: $addconfiguration.find("#taskDelayTimeVal").val(),
                delayTimeUnits: $addconfiguration.find("#taskDelayTimeUnits").val(),
                intervalTimeVal: $addconfiguration.find("#taskIntervalTimeVal").val(),
                intervalTimeUnits: $addconfiguration.find("#taskIntervalTimeUnits").val(),
                autoStart: $addconfiguration.find("#taskAutoStart").val()
            }
        };
        $.ajax({
                url: "/api/configurations",
                type: "POST",
                dataType: 'json',
                contentType: "application/json",
                data: JSON.stringify(data)
            })
            .success(function(data) {
                stopLadda(ladda);
                MessageController.container("#message-container").empty();
                window.location.href="/admin/configurations";
            })
            .error(function(error) {
                console.log(error);
                stopLadda(ladda);
                MessageController
                    .container("#message-container")
                    .template("#configuration-add-error")
                    .error(error.responseJSON)
                    .replace();
            });
    });

    // public interface
    return {
        init: init
    }
}());