#!/bin/sh

CWD=$(pwd)
ACTIVATOR=$(which activator)
MAVEN=$(which mvn)
AKKA_TASK_SCHEDULER=$CWD/akka-task-scheduler
FRAMEWORK_MODULE=$CWD/play-app-plugin-framework
PLUGIN_MODULE=$CWD/play-app-plugins
TEST_PLUGIN_ALPHA=$PLUGIN_MODULE/test-plugin-alpha
PLAY_APP=$CWD/play-app

# compile and publishLocal akka-task-scheduler
rm -r ~/.ivy2/local/akka-task-scheduler/akka-task-scheduler_2.11/1.0.0
cd $AKKA_TASK_SCHEDULER
$ACTIVATOR clean compile
$ACTIVATOR publishLocal;

# compile and install the play-app-plugin-framework
cd $FRAMEWORK_MODULE
$MAVEN clean compile
$MAVEN install

# compile and package the test-plugin-alpha
cd $TEST_PLUGIN_ALPHA
$MAVEN clean compile
$MAVEN package

# compile and run the play-app
cd $PLAY_APP
$ACTIVATOR clean compile
$ACTIVATOR run
