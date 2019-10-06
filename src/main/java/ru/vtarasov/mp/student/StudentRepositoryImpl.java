package ru.vtarasov.mp.student;

import java.util.Optional;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * @author vtarasov
 * @since 21.09.2019
 */
@Singleton
@Transactional
public class StudentRepositoryImpl implements StudentRepository {
    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    @Override
    public Optional<Student> get(String id) {
        return Optional.ofNullable(em.find(Student.class, id));
    }

    @Override
    public void delete(String id) {
        get(id).ifPresent(removed -> em.remove(removed));
    }

    @Override
    public Student save(Student student) {
        Student registered = new Student(null, student.getName(), student.getAge());
        em.persist(registered);
        return registered;
    }
}
