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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.moviematch.moviematch.controllers.CurrentUserAdvice;
import com.moviematch.moviematch.sessionmodels.SessionInformation;
import com.moviematch.moviematch.sessionmodels.SessionRepository;
import com.moviematch.moviematch.tmdb.TmdbClient;

@Controller
public class sessionController {
    private final TmdbClient tmdbClient;
    private final SessionRepository SessionRepository;

    public sessionController(SessionRepository SessionRepository, TmdbClient tmdbClient) {
        this.SessionRepository = SessionRepository;
        this.tmdbClient = tmdbClient;
    }

    @GetMapping("/Asessions/new")
    public String newSession(Model model) {
        model.addAttribute("Asession", new SessionForm());
        model.addAttribute("tmdbConfigured", tmdbClient.isConfigured());
        return "Asessions/create";
    }

    // the Sessions dashboard replaced the old lobby list,
    // so old links and redirects to /Asessions/main end up there
    @GetMapping("/Asessions/main")
    public String showSessions() {
        return "redirect:/dashboard";
    }

    @PostMapping("/Asessions/add")
    public String addSession(@Valid @ModelAttribute("Asession") SessionForm form,
                            BindingResult bindingResult, Model model,
                            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return showCreateForm(model);
        }

        SessionInformation session = new SessionInformation();
        form.applyTo(session);
        // whoever creates the session becomes its host
        session.setHostId(CurrentUserAdvice.currentUserId(request));
        SessionRepository.save(session);
        return "redirect:/Asessions/main";
    }

    @GetMapping("/Asessions/{id}")
    public String showSession(@PathVariable long id, Model model) {
        model.addAttribute("Asession", findSessionInformation(id));
        return "Asessions/details";
    }

    @GetMapping("/Asessions/{id}/edit")
    public String editSession(@PathVariable long id, Model model, HttpServletRequest request) {
        SessionInformation session = findSessionInformation(id);
        checkIsHost(session, request);
        model.addAttribute("Asession", SessionForm.from(session));
        model.addAttribute("AsessionId", id);
        model.addAttribute("tmdbConfigured", tmdbClient.isConfigured());
        return "Asessions/edit";
    }

    @PostMapping("/Asessions/{id}/update")
    public String updateSession(@PathVariable long id,
                               @Valid @ModelAttribute("Asession") SessionForm form,
                               BindingResult bindingResult, Model model,
                               HttpServletRequest request) {
        SessionInformation existing = findSessionInformation(id);
        checkIsHost(existing, request);
        if (bindingResult.hasErrors()) {
            model.addAttribute("AsessionId", id);
            model.addAttribute("tmdbConfigured", tmdbClient.isConfigured());
            return "Asessions/edit";
        }

        form.applyTo(existing);
        SessionRepository.save(existing);
        return "redirect:/Asessions/" + id;
    }

    @GetMapping("/Asessions/{id}/delete")
    public String deletePage(@PathVariable long id, Model model, HttpServletRequest request) {
        SessionInformation session = findSessionInformation(id);
        checkIsHost(session, request);
        model.addAttribute("Asession", session);
        return "Asessions/delete";
    }

    @PostMapping("/Asessions/{id}/delete")
    public String deleteSession(@PathVariable long id, HttpServletRequest request) {
        SessionInformation session = findSessionInformation(id);
        checkIsHost(session, request);
        SessionRepository.delete(session);
        return "redirect:/Asessions/main";
    }

    // stops anyone who isn't the host, even if they type the URL in directly
    private void checkIsHost(SessionInformation session, HttpServletRequest request) {
        Integer userId = CurrentUserAdvice.currentUserId(request);
        if (!session.canBeManagedBy(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the host can change this session");
        }
    }

    private String showCreateForm(Model model) {
        return "Asessions/create";
    }

    private SessionInformation findSessionInformation(long id) {
        return SessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ASession not found"));
    }
}
