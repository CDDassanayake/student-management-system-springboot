package com.cddassanayake.sms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Enter a student ID.")
    @Pattern(regexp = "^[A-Za-z0-9/-]{3,20}$", message = "Use 3-20 letters, numbers, / or -.")
    @Column(name = "student_id", nullable = false, unique = true, length = 20)
    private String studentId;

    @NotBlank(message = "Enter the student's full name.")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters.")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Enter an email address.")
    @Email(message = "Enter a valid email address, like name@example.com.")
    @Column(nullable = false, length = 150)
    private String email;

    @Pattern(regexp = "^[0-9+\\-\\s()]{7,20}$", message = "Use 7-20 digits; +, -, spaces and brackets are allowed.")
    @Column(length = 20)
    private String phone;

    @NotBlank(message = "Enter the course or programme.")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String course;

    @NotNull(message = "Choose a year between 1 and 6.")
    @Min(value = 1, message = "Choose a year between 1 and 6.")
    @Max(value = 6, message = "Choose a year between 1 and 6.")
    @Column(name = "study_year", nullable = false)
    private Integer studyYear;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public Integer getStudyYear() { return studyYear; }
    public void setStudyYear(Integer studyYear) { this.studyYear = studyYear; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
