package com.ooad.lms.designpattern.mvp.homepage;

import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;

import com.ooad.lms.model.Course;

public class SpringModelHomePageView implements HomePageView {
    private final Model model;

    public SpringModelHomePageView(Model model) {
        this.model = model;
    }

    @Override
    public void showCourses(List<Course> courses) {
        model.addAttribute("courses", courses);
    }

    @Override
    public void showStats(Map<String, Long> stats) {
        model.addAttribute("stats", stats);
    }

    @Override
    public void showSeededAccounts(List<Map<String, String>> accounts) {
        model.addAttribute("seededAccounts", accounts);
    }
}
