package com.ooad.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ooad.lms.designpattern.mvp.homepage.HomePagePresenter;
import com.ooad.lms.designpattern.mvp.homepage.SpringModelHomePageView;

@Controller
public class WebController {
    private final HomePagePresenter homePagePresenter;

    public WebController(HomePagePresenter homePagePresenter) {
        this.homePagePresenter = homePagePresenter;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard/student")
    public String studentDashboard() {
        return "student";
    }

    @GetMapping("/dashboard/instructor")
    public String instructorDashboard() {
        return "instructor";
    }

    @GetMapping("/dashboard/admin")
    public String adminDashboard() {
        return "admin";
    }

    @GetMapping("/")
    public String index(Model model) {
        homePagePresenter.present(new SpringModelHomePageView(model));
        return "index";
    }
}