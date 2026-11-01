package com.moviematch.moviematch.moviecontrollers;

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

import com.moviematch.moviematch.moviemodels.GENRE;
import com.moviematch.moviematch.moviemodels.MovieInformation;
import com.moviematch.moviematch.moviemodels.MovieRepository;

@Controller
public class movieController {
    private final MovieRepository MovieRepository;

    public movieController(MovieRepository MovieRepository) {
        this.MovieRepository = MovieRepository;
    }

    @GetMapping("/movies/new")
    public String newMovie(Model model) {
        model.addAttribute("movie", new MovieForm());
        model.addAttribute("genres", GENRE.values());
        return "movies/create";
    }

    @GetMapping({"/", "/movies/main"})
    public String showMovies(Model model) {
        model.addAttribute("movies", MovieRepository.findAll());
        return "movies/showall";
    }

    @PostMapping("/movies/add")
    public String addMovie(@Valid @ModelAttribute("movie") MovieForm form,
                            BindingResult bindingResult, Model model) {
        // if (form.getMovieTitle() != null && form.getMovieVoteCount() != 0) {
        //     bindingResult.rejectValue("movieTitle", "duplicate", "This movie has already been suggested");
        // }
        if (bindingResult.hasErrors()) {
            return showCreateForm(model);
        }

        MovieInformation movie = new MovieInformation();
        form.applyTo(movie);
        MovieRepository.save(movie);
        return "redirect:/movies/main";
    }

    @GetMapping("/movies/{id}")
    public String showMovie(@PathVariable long id, Model model) {
        model.addAttribute("movie", findMovieInformation(id));
        return "movies/details";
    }

    @GetMapping("/movies/{id}/edit")
    public String editMovie(@PathVariable long id, Model model) {
        model.addAttribute("movie", MovieForm.from(findMovieInformation(id)));
        model.addAttribute("movieId", id);
        model.addAttribute("genres", GENRE.values());
        return "movies/edit";
    }

    @PostMapping("/movies/{id}/update")
    public String updateMovie(@PathVariable long id,
                               @Valid @ModelAttribute("movie") MovieForm form,
                               BindingResult bindingResult, Model model) {
        MovieInformation existing = findMovieInformation(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("movieId", id);
            model.addAttribute("genres", GENRE.values());
            return "movies/edit";
        }

        form.applyTo(existing);
        MovieRepository.save(existing);
        return "redirect:/movies/" + id;
    }

    @GetMapping("/movies/{id}/delete")
    public String deletePage(@PathVariable long id, Model model) {
        model.addAttribute("movie", findMovieInformation(id));
        return "movies/delete";
    }

    @PostMapping("/movies/{id}/delete")
    public String deleteMovie(@PathVariable long id) {
        MovieInformation movie = findMovieInformation(id);
        MovieRepository.delete(movie);
        return "redirect:/movies/main";
    }

    private String showCreateForm(Model model) {
        model.addAttribute("genres", GENRE.values());
        return "movies/create";
    }

    private MovieInformation findMovieInformation(long id) {
        return MovieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
    }
}