package com.ooad.lms.repository;

import com.ooad.lms.model.Course;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CourseRepository {
    private final InMemoryDataStore dataStore;

    public CourseRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<Course> findAll() {
        return dataStore.courses().values().stream().collect(Collectors.toList());
    }

    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(dataStore.courses().get(id));
    }

    public Course save(Course course) {
        if (course.getCourseId() == null) {
            course = new Course(dataStore.nextCourseId(), course.getTitle(), course.getDescription());
        }
        dataStore.courses().put(course.getCourseId(), course);
        dataStore.persist();
        return course;
    }

    public boolean existsById(Long id) {
        return dataStore.courses().containsKey(id);
    }

    public void deleteById(Long id) {
        dataStore.courses().remove(id);
        dataStore.persist();
    }
}