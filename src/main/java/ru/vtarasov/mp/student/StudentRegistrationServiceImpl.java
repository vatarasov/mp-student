package ru.vtarasov.mp.student;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.java.Log;

/**
 * @author vtarasov
 * @since 21.09.2019
 */
@Log
@Singleton
public class StudentRegistrationServiceImpl implements StudentRegistrationService {
    @Inject
    private StudentRepository repository;

    @Override
    public Student register(Student student) {
        Student saved = repository.save(student);
        LOG.info("Student was saved. Id: " + student.getId() + ", Name: " + student.getName() + ", Age: " + student.getAge());
        return saved;
    }

    @Override
    public void unregister(Student student) {
        repository.delete(student.getId());
        LOG.info("Student was deleted. Id: " + student.getId() + ", Name: " + student.getName() + ", Age: " + student.getAge());
    }

    @Override
    public Optional<Student> find(String id) {
        Optional<Student> student = repository.get(id);
        student.ifPresent(student1 ->
            LOG.info("Student was found. Id: " + student1.getId() + ", Name: " + student1.getName() + ", Age: " + student1.getAge()));
        return student;
    }
}
