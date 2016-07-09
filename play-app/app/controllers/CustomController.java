package controllers;

import components.PlayApp;
import play.mvc.Controller;

import javax.inject.Inject;

public class CustomController extends Controller {
    @Inject
    protected PlayApp playApp;
}
