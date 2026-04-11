package com.ooad.lms.repository;

import com.ooad.lms.model.Assignment;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AssignmentRepository {
    private final InMemoryDataStore dataStore;

    public AssignmentRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Optional<Assignment> findById(Long id) {
        return Optional.ofNullable(dataStore.assignments().get(id));
    }

    public Assignment save(Assignment assignment) {
        if (assignment.getAssignmentId() == null) {
            assignment = new Assignment(dataStore.nextAssignmentId(), assignment.getDescription(), assignment.getDeadline());
        }
        dataStore.assignments().put(assignment.getAssignmentId(), assignment);
        dataStore.persist();
        return assignment;
    }
}