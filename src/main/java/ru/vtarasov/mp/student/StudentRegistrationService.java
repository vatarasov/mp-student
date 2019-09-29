package ru.vtarasov.mp.student;

import java.util.Optional;

/**
 * @author vtarasov
 * @since 21.09.2019
 */
public interface StudentRegistrationService {
    Student register(Student student);
    void unregister(Student student);
    Optional<Student> find(String id);
}
