var dashboard = (function() {
    var initHasRun = false;
    var $dashboard = $("#dashboard");
    var $taskList = $dashboard.find("#task-list");
    var $tasklistitemTemplate = $taskList.find("#task-list-item");
    var $noTasksFound = $('<tr><td colspan="4" style="text-align: center; vertical-align: middle; font-style: italic; height: 100px;">No Tasks</td></tr>');

    function init() {
        if (initHasRun) return;

        initHasRun = true;

        loadTasks();
    }

    function loadTasks() {
        $.ajax({
            url: "/api/tasks",
            type: "GET",
            dataType: 'json',
            contentType: "application/json"
        })
            .success(function(data) {
                if (data.tasks && data.tasks.length > 0) {
                    $taskList.html(
                        $tasklistitemTemplate.render(data.tasks, {})
                    );
                } else {
                    $taskList.html($noTasksFound);
                }
            })
            .error(function(error) {
                console.log(error);
            });
    }

    $taskList.on("click", ".start", function() {
        startTask(this, $(this).data("id"))
    });

    $taskList.on("click", ".stop", function() {
        stopTask(this, $(this).data("id"))
    });

    function startTask(element, id) {
        var ladda = startLadda(element);
        $.ajax({
            url: "/api/tasks/" + id,
            type: "PUT",
            dataType: 'json',
            contentType: "application/json",
            data: JSON.stringify({
                status: "START"
            })
        })
            .success(function(data) {
                stopLadda(ladda);
                loadTasks();
            })
            .error(function(error) {
                stopLadda(ladda);
                console.log(error);
            });

    }

    function stopTask(element, id) {
        console.log(element);
        var ladda = startLadda(element);
        $.ajax({
            url: "/api/tasks/" + id,
            type: "PUT",
            dataType: 'json',
            contentType: "application/json",
            data: JSON.stringify({
                status: "STOP"
            })
        })
            .success(function(data) {
                stopLadda(ladda);
                loadTasks();
            })
            .error(function(error) {
                stopLadda(ladda);
                console.log(error);
            });

    }

    // public interface
    return {
        init: init
    }
}());