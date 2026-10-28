package com.cddassanayake.sms.repository;

import com.cddassanayake.sms.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentIdIgnoreCase(String studentId);

    /** Case-insensitive search across student ID, name and course. An empty query returns everyone. */
    @Query("""
           select s from Student s
           where lower(s.studentId) like lower(concat('%', :q, '%'))
              or lower(s.name) like lower(concat('%', :q, '%'))
              or lower(s.course) like lower(concat('%', :q, '%'))
           order by lower(s.name)
           """)
    List<Student> search(@Param("q") String q);
}
