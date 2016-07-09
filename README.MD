# play-plugin-arch
This is an example play application depicting the use of an akka-task-scheduler plugin and an embedded
plugin architecture using [PF4J](https://github.com/decebals/pf4j).

## Dependencies for using build-and-run.sh
* Linux/Unix
* [Play Framework Activator](https://www.playframework.com/download)
* [Maven](https://maven.apache.org/download.cgi)

## Running build-and-run.sh
```
$> cd /path/to/cloned/repo
$> chmod +x ./build-and-run.sh
$> ./build-and-run.sh
```

## Viewing/Administering the application
1. Navigate to http://localhost:9000 in a web browser
2. On the left you will see three menu items:
    * Dashboard
        * Here you can view the list of available tasks that are scheduled which can be manually started and stopped.
    * Configurations
        * Here you can see the list of configurations of tasks as well as Add, Edit, and Delete them.
        * Note: These do not persist between app reloads (no database is included).
    * Plugins
        * Here you can manage the plugins.  You can browse for the test-plugin-alpha/target/test-plugin-alpha-{version}.zip to install.
        * Note: A plugin must be installed for a configuration processor to be chosen.