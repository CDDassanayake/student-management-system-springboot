package com.cddassanayake.sms.controller;

import com.cddassanayake.sms.model.Student;
import com.cddassanayake.sms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentController {

    private static final String FORM = "students/form";
    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @InitBinder
    void configureBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id", "createdAt");              // never bind these from user input
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true)); // trim; blank -> null
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/students";
    }

    @GetMapping("/students")
    public String list(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("students", service.search(q));
        model.addAttribute("q", q);
        return "students/list";
    }

    @GetMapping("/students/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("s", service.get(id));
        return "students/detail";
    }

    @GetMapping("/students/new")
    public String newForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("editing", false);
        return FORM;
    }

    @PostMapping("/students")
    public String create(@Valid @ModelAttribute("student") Student student, BindingResult result,
                         Model model, RedirectAttributes flash) {
        rejectDuplicateId(student, null, result);
        if (result.hasErrors()) {
            model.addAttribute("editing", false);
            return FORM;
        }
        Student saved = service.create(student);
        flash.addFlashAttribute("message", "Added " + saved.getName() + ".");
        return "redirect:/students/" + saved.getId();
    }

    @GetMapping("/students/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", service.get(id));
        model.addAttribute("editing", true);
        model.addAttribute("recordId", id);
        return FORM;
    }

    @PostMapping("/students/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("student") Student student,
                         BindingResult result, Model model, RedirectAttributes flash) {
        service.get(id); // 404 if the record no longer exists
        rejectDuplicateId(student, id, result);
        if (result.hasErrors()) {
            model.addAttribute("editing", true);
            model.addAttribute("recordId", id);
            return FORM;
        }
        Student saved = service.update(id, student);
        flash.addFlashAttribute("message", "Saved changes to " + saved.getName() + ".");
        return "redirect:/students/" + id;
    }

    @PostMapping("/students/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        String name = service.get(id).getName();
        service.delete(id);
        flash.addFlashAttribute("message", "Deleted " + name + ".");
        return "redirect:/students";
    }

    private void rejectDuplicateId(Student student, Long excludeId, BindingResult result) {
        if (student.getStudentId() != null && !result.hasFieldErrors("studentId")
                && service.isStudentIdTaken(student.getStudentId(), excludeId)) {
            result.rejectValue("studentId", "duplicate", "This student ID already exists.");
        }
    }
}
