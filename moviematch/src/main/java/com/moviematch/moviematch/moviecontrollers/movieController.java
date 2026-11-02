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
import com.moviematch.moviematch.sessionmodels.SessionRepository;
import com.moviematch.moviematch.sessionmodels.SessionInformation;
import java.util.List;

@Controller
public class movieController {
    private final MovieRepository MovieRepository;
    private final SessionRepository SessionRepository;


    public movieController(MovieRepository MovieRepository, SessionRepository SessionRepository) {
        this.MovieRepository = MovieRepository;
        this.SessionRepository = SessionRepository;
    }
   
    @GetMapping("/Asessions/{Sid}/movies/new")
    public String newMovie(@PathVariable long Sid, Model model) {
        model.addAttribute("movie", new MovieForm());
        model.addAttribute("genres", GENRE.values());
        model.addAttribute("AsessionId", Sid);
        return "movies/create";
    }


    @GetMapping({"/Asessions/{Sid}/movies/main", "/Asessions/{Sid}/movies/main"})
    public String showMovies(@PathVariable long Sid, Model model) {
        SessionInformation session = SessionRepository.findById(Sid).orElseThrow();
        model.addAttribute("movies", MovieRepository.findBySessionOrderByMovieVoteCountDescUidAsc(session));
        model.addAttribute("AsessionId", Sid);
        model.addAttribute("Asession", session);
        return "movies/showall";
    }


    @PostMapping("/Asessions/{Sid}/movies/add")
    public String addMovie(@Valid @ModelAttribute("movie") MovieForm form, @PathVariable long Sid,
                            BindingResult bindingResult, Model model) {
        // if (form.getMovieTitle() != null && form.getMovieVoteCount() != 0) {
        //     bindingResult.rejectValue("movieTitle", "duplicate", "This movie has already been suggested");
        // }
        if (bindingResult.hasErrors()) {
            return showCreateForm(model);
        }


        MovieInformation movie = new MovieInformation();
        form.applyTo(movie);
        SessionInformation session = SessionRepository.findById(Sid).orElseThrow();
        movie.setSession(session);
        MovieRepository.save(movie);
        return "redirect:/Asessions/"+ Sid +"/movies/main";
    }


    @GetMapping("/Asessions/{Sid}/movies/{id}")
    public String showMovie(@PathVariable long id, @PathVariable long Sid, Model model) {
        model.addAttribute("movie", findMovieInformation(id));
        model.addAttribute("AsessionId", Sid);
        SessionInformation session = SessionRepository.findById(Sid).orElseThrow();
        model.addAttribute("Asession", session);
        return "movies/details";
    }


    @GetMapping("/Asessions/{Sid}/movies/{id}/edit")
    public String editMovie(@PathVariable long id, @PathVariable long Sid, Model model) {
        model.addAttribute("movie", MovieForm.from(findMovieInformation(id)));
        model.addAttribute("movieId", id);
        model.addAttribute("AsessionId", Sid);
        model.addAttribute("genres", GENRE.values());
        return "movies/edit";
    }


    @PostMapping("/Asessions/{Sid}/movies/{id}/update")
    public String updateMovie(@PathVariable long id,
                              @PathVariable long Sid,
                               @Valid @ModelAttribute("movie") MovieForm form,
                               BindingResult bindingResult, Model model) {
        MovieInformation existing = findMovieInformation(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("movieId", id);
             model.addAttribute("AsessionId", Sid);
            model.addAttribute("genres", GENRE.values());
            return "movies/edit";
        }


        form.applyTo(existing);
        MovieRepository.save(existing);
        return "redirect:/Asessions/"+ Sid +"/movies/" + id;
    }


    @GetMapping("/Asessions/{Sid}/movies/{id}/delete")
    public String deletePage(@PathVariable long id, @PathVariable long Sid, Model model) {
        model.addAttribute("movie", findMovieInformation(id));
        model.addAttribute("AsessionId", Sid);
        return "movies/delete";
    }


    @PostMapping("/Asessions/{Sid}/movies/{id}/delete")
    public String deleteMovie(@PathVariable long id, @PathVariable long Sid) {
        MovieInformation movie = findMovieInformation(id);
        MovieRepository.delete(movie);
        return "redirect:/Asessions/"+ Sid +"/movies/main";
    }


    private String showCreateForm(Model model) {
        model.addAttribute("genres", GENRE.values());
        return "movies/create";
    }


    private MovieInformation findMovieInformation(long id) {
        return MovieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
    }


    @PostMapping("/Asessions/{Sid}/movies/{id}/vote")
    public String voteMovie(@PathVariable long Sid, @PathVariable long id) {
    MovieInformation movie = findMovieInformation(id);
    if (movie.getSession() == null || movie.getSession().getUid() != Sid) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not in this session");
    }
    movie.setMovieCount(movie.getMovieVoteCount() + 1);
    MovieRepository.save(movie);
    return "redirect:/Asessions/" + Sid + "/movies/main";
   
    }

    @PostMapping("/Asessions/{Sid}/movies/reset")
    public String resetVotes(@PathVariable long Sid) {
        SessionInformation session = SessionRepository.findById(Sid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        List<MovieInformation> movies = MovieRepository.findBySession(session);
        for (MovieInformation movie : movies) {
            movie.setMovieCount(0);
        }
        MovieRepository.saveAll(movies);
        return "redirect:/Asessions/" + Sid + "/movies/main";
    }
}
