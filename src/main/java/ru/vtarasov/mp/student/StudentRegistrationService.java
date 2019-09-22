package ru.vtarasov.mp.student;

/**
 * @author vtarasov
 * @since 21.09.2019
 */
public interface StudentRegistrationService {
    Student register(String name);
    void unregister(Student student);
    Student find(String id);
}
