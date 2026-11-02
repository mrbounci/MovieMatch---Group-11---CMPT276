package com.moviematch.moviematch.sessioncontrollers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

import com.moviematch.moviematch.sessionmodels.SessionInformation;
import com.moviematch.moviematch.sessionmodels.SessionRepository;

@Controller
public class sessionController {
    private final SessionRepository SessionRepository;

    public sessionController(SessionRepository SessionRepository) {
        this.SessionRepository = SessionRepository;
    }

    @GetMapping("/Asessions/new")
    public String newSession(Model model) {
        model.addAttribute("Asession", new SessionForm());
        return "Asessions/create";
    }

    @GetMapping({"/", "/Asessions/main"})
    public String showSessions(Model model) {
        model.addAttribute("Asessions", SessionRepository.findAll());
        return "Asessions/showall";
    }

    @PostMapping("/Asessions/add")
    public String addSession(@Valid @ModelAttribute("Asession") SessionForm form,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showCreateForm(model);
        }

        SessionInformation session = new SessionInformation();
        form.applyTo(session);
        SessionRepository.save(session);
        return "redirect:/Asessions/main";
    }

    @GetMapping("/Asessions/{id}")
    public String showSession(@PathVariable long id, Model model) {
        model.addAttribute("Asession", findSessionInformation(id));
        return "Asessions/details";
    }

    @GetMapping("/Asessions/{id}/edit")
    public String editSession(@PathVariable long id, Model model) {
        model.addAttribute("Asession", SessionForm.from(findSessionInformation(id)));
        model.addAttribute("AsessionId", id);
        return "Asessions/edit";
    }

    @PostMapping("/Asessions/{id}/update")
    public String updateSession(@PathVariable long id,
                               @Valid @ModelAttribute("Asession") SessionForm form,
                               BindingResult bindingResult, Model model) {
        SessionInformation existing = findSessionInformation(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("AsessionId", id);
            return "Asessions/edit";
        }

        form.applyTo(existing);
        SessionRepository.save(existing);
        return "redirect:/Asessions/" + id;
    }

    @GetMapping("/Asessions/{id}/delete")
    public String deletePage(@PathVariable long id, Model model) {
        model.addAttribute("Asession", findSessionInformation(id));
        return "Asessions/delete";
    }

    @PostMapping("/Asessions/{id}/delete")
    public String deleteSession(@PathVariable long id) {
        SessionInformation session = findSessionInformation(id);
        SessionRepository.delete(session);
        return "redirect:/Asessions/main";
    }

    private String showCreateForm(Model model) {
        return "Asessions/create";
    }

    private SessionInformation findSessionInformation(long id) {
        return SessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ASession not found"));
    }
}