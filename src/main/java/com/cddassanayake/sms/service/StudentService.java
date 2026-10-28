package com.cddassanayake.sms.service;

import com.cddassanayake.sms.model.Student;
import com.cddassanayake.sms.repository.StudentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Student> search(String query) {
        return repository.search(query == null ? "" : query.trim());
    }

    @Transactional(readOnly = true)
    public Student get(Long id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public boolean isStudentIdTaken(String studentId, Long excludeId) {
        return repository.findByStudentIdIgnoreCase(studentId)
                .filter(found -> excludeId == null || !found.getId().equals(excludeId))
                .isPresent();
    }

    public Student create(Student student) {
        return repository.save(student);
    }

    public Student update(Long id, Student form) {
        Student existing = get(id);
        existing.setStudentId(form.getStudentId());
        existing.setName(form.getName());
        existing.setEmail(form.getEmail());
        existing.setPhone(form.getPhone());
        existing.setCourse(form.getCourse());
        existing.setStudyYear(form.getStudyYear());
        return existing; // saved automatically when the transaction commits
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }
}
