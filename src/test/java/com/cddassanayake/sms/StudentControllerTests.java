package com.cddassanayake.sms;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cddassanayake.sms.model.Student;
import com.cddassanayake.sms.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // each test rolls back, so tests never affect each other
class StudentControllerTests {

    @Autowired MockMvc mvc;
    @Autowired StudentRepository repository;

    private MockHttpServletRequestBuilder submit(String url, String studentId, String name, String email, String year) {
        return post(url).param("studentId", studentId).param("name", name).param("email", email)
                .param("phone", "+94 77 123 4567").param("course", "Computer Science").param("studyYear", year);
    }

    private Student save(String studentId, String name, String course) {
        Student s = new Student();
        s.setStudentId(studentId);
        s.setName(name);
        s.setEmail(name.split(" ")[0].toLowerCase() + "@example.com");
        s.setCourse(course);
        s.setStudyYear(2);
        return repository.save(s);
    }

    @Test
    void createStoresStudentAndRedirects() throws Exception {
        mvc.perform(submit("/students", "CS/001", "Nimal Perera", "nimal@example.com", "2"))
                .andExpect(status().is3xxRedirection());
        assertEquals(1, repository.count());
        assertTrue(repository.findByStudentIdIgnoreCase("cs/001").isPresent());
    }

    @Test
    void invalidInputShowsFieldErrorsAndSavesNothing() throws Exception {
        mvc.perform(submit("/students", "CS/001", "", "bad", "9"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("student", "name", "email", "studyYear"));
        assertEquals(0, repository.count());
    }

    @Test
    void duplicateStudentIdIsRejected() throws Exception {
        save("CS/001", "Nimal Perera", "Computer Science");
        mvc.perform(submit("/students", "cs/001", "Someone Else", "else@example.com", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("student", "studentId"));
        assertEquals(1, repository.count());
    }

    @Test
    void updateChangesTheRecord() throws Exception {
        Student s = save("CS/001", "Nimal Perera", "Computer Science");
        mvc.perform(submit("/students/" + s.getId(), "CS/001", "Nimal P. Perera", "nimal@example.com", "3"))
                .andExpect(status().is3xxRedirection());
        Student updated = repository.findById(s.getId()).orElseThrow();
        assertEquals("Nimal P. Perera", updated.getName());
        assertEquals(3, updated.getStudyYear());
    }

    @Test
    void deleteRemovesTheRecord() throws Exception {
        Student s = save("CS/001", "Nimal Perera", "Computer Science");
        mvc.perform(post("/students/" + s.getId() + "/delete")).andExpect(status().is3xxRedirection());
        assertFalse(repository.existsById(s.getId()));
    }

    @Test
    void searchFiltersByNameIdOrCourse() throws Exception {
        save("CS/001", "Nimal Perera", "Computer Science");
        save("IT/002", "Kamala Silva", "Information Technology");
        mvc.perform(get("/students").param("q", "kamala"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Kamala Silva")))
                .andExpect(content().string(not(containsString("Nimal Perera"))));
    }

    @Test
    void missingStudentReturns404() throws Exception {
        mvc.perform(get("/students/999")).andExpect(status().isNotFound());
    }
}
