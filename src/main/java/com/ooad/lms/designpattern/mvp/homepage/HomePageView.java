package com.ooad.lms.designpattern.mvp.homepage;

import java.util.List;
import java.util.Map;

import com.ooad.lms.model.Course;

public interface HomePageView {
    void showCourses(List<Course> courses);

    void showStats(Map<String, Long> stats);

    void showSeededAccounts(List<Map<String, String>> accounts);
}
