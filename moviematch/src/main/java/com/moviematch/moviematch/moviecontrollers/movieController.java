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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


import com.moviematch.moviematch.controllers.CurrentUserAdvice;
import com.moviematch.moviematch.moviemodels.GENRE;
import com.moviematch.moviematch.moviemodels.MovieInformation;
import com.moviematch.moviematch.moviemodels.MovieRepository;
import com.moviematch.moviematch.sessionmodels.SessionRepository;
import com.moviematch.moviematch.sessionmodels.SessionInformation;
import com.moviematch.moviematch.moviemodels.MovieVoteRepository;
import com.moviematch.moviematch.moviemodels.Vote;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.moviematch.moviematch.tmdb.TmdbClient;

@Controller
public class movieController {
    private final MovieRepository MovieRepository;
    private final SessionRepository SessionRepository;
    private final TmdbClient TmdbClient;
    private final MovieVoteRepository MovieVoteRepository;

    public movieController(MovieRepository MovieRepository, SessionRepository SessionRepository, TmdbClient TmdbClient, MovieVoteRepository MovieVoteRepository) {
        this.MovieRepository = MovieRepository;
        this.SessionRepository = SessionRepository;
        this.TmdbClient = TmdbClient;
        this.MovieVoteRepository = MovieVoteRepository;
    }
   
    @GetMapping("/Asessions/{Sid}/movies/new")
    public String newMovie(@PathVariable long Sid, Model model) {
        model.addAttribute("movie", new MovieForm());
        model.addAttribute("genres", GENRE.values());
        model.addAttribute("AsessionId", Sid);
        return "movies/create";
    }


    @GetMapping({"/Asessions/{Sid}/movies/main", "/Asessions/{Sid}/movies/main"})
    public String showMovies(@PathVariable long Sid, Model model, HttpServletRequest request) {
        SessionInformation session = SessionRepository.findById(Sid).orElseThrow();
        model.addAttribute("movies", MovieRepository.findBySessionOrderByMovieVoteCountDescUidAsc(session));
        model.addAttribute("AsessionId", Sid);
        model.addAttribute("Asession", session);

        Integer voter = CurrentUserAdvice.currentUserId(request);
        Optional<Vote> existingVote = Optional.empty();
        if (voter != null) {
            existingVote = MovieVoteRepository.findBySessionIdAndUserId(Sid, voter.longValue());
        }
        model.addAttribute("hasVoted", existingVote.isPresent());
        model.addAttribute("votedMovieId", existingVote.map(Vote::getMovieId).orElse(null));
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

        if (form.getTmdbMovieId() != null && TmdbClient.isConfigured()) {

            try {
                Map<String, String> crew = TmdbClient.crew(form.getTmdbMovieId());
                if (isBlank(movie.getMovieDirector())) movie.setMovieDirector(crew.get("director"));
                if (isBlank(movie.getMovieWriter())) movie.setMovieWriter(crew.get("writer"));
                if (isBlank(movie.getMovieComposer())) movie.setMovieComposer(crew.get("composer"));
                if (isBlank(movie.getMovieCinematographer())) movie.setMovieCinematographer(crew.get("cinematographer"));
                movie.setMovieCast(crew.get("cast"));
            } catch (RuntimeException e) {
                System.out.println("TMDB look up failed");
            }
        }

        SessionInformation session = SessionRepository.findById(Sid).orElseThrow();
        movie.setSession(session);
        MovieRepository.save(movie);
        return "redirect:/Asessions/"+ Sid +"/movies/main";
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
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
    public String editMovie(@PathVariable long id, @PathVariable long Sid, Model model,
                            HttpServletRequest request) {
        MovieInformation movie = findMovieInformation(id);
        checkIsHost(movie.getSession(), request);
        model.addAttribute("movie", MovieForm.from(movie));
        model.addAttribute("movieId", id);
        model.addAttribute("AsessionId", Sid);
        model.addAttribute("genres", GENRE.values());
        return "movies/edit";
    }


    @PostMapping("/Asessions/{Sid}/movies/{id}/update")
    public String updateMovie(@PathVariable long id,
                              @PathVariable long Sid,
                               @Valid @ModelAttribute("movie") MovieForm form,
                               BindingResult bindingResult, Model model,
                               HttpServletRequest request) {
        MovieInformation existing = findMovieInformation(id);
        checkIsHost(existing.getSession(), request);
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
    public String deletePage(@PathVariable long id, @PathVariable long Sid, Model model,
                             HttpServletRequest request) {
        MovieInformation movie = findMovieInformation(id);
        checkIsHost(movie.getSession(), request);
        model.addAttribute("movie", movie);
        model.addAttribute("AsessionId", Sid);
        return "movies/delete";
    }


    @PostMapping("/Asessions/{Sid}/movies/{id}/delete")
    public String deleteMovie(@PathVariable long id, @PathVariable long Sid,
                              HttpServletRequest request) {
        MovieInformation movie = findMovieInformation(id);
        checkIsHost(movie.getSession(), request);
        MovieRepository.delete(movie);
        return "redirect:/Asessions/"+ Sid +"/movies/main";
    }


    private String showCreateForm(Model model) {
        model.addAttribute("genres", GENRE.values());
        return "movies/create";
    }


    // only the session's host (or an admin) can edit/delete movies or reset votes,
    // even if someone types the URL in directly
    private void checkIsHost(SessionInformation session, HttpServletRequest request) {
        if (session != null && CurrentUserAdvice.isAdmin(request)) {
            return;
        }
        Integer userId = CurrentUserAdvice.currentUserId(request);
        if (session == null || !session.canBeManagedBy(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the host can do that");
        }
    }

    private MovieInformation findMovieInformation(long id) {
        return MovieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
    }


    /*@PostMapping("/Asessions/{Sid}/movies/{id}/vote")
    public String voteMovie(@PathVariable long Sid, @PathVariable long id, HttpServletRequest request) {
        MovieInformation movie = findMovieInformation(id);
        if (movie.getSession() == null || movie.getSession().getUid() != Sid) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not in this session");
        }
        Integer voter = CurrentUserAdvice.currentUserId(request);

        Optional<Vote> existing = MovieVoteRepository.findBySessionIdAndUserId(Sid, voter);
        movie.setMovieCount(movie.getMovieVoteCount() + 1);
        MovieRepository.save(movie);
        return "redirect:/Asessions/" + Sid + "/movies/main";
   
    }*/

    @PostMapping("/Asessions/{Sid}/movies/{id}/vote")
    public String voteMovie(@PathVariable long Sid, @PathVariable long id, HttpServletRequest request) {
        MovieInformation movie = findMovieInformation(id);

        Integer voter = CurrentUserAdvice.currentUserId(request);
        if (voter == null) {
            return "redirect:/login/";
        }
        if (MovieVoteRepository.findBySessionIdAndUserId(Sid, voter.longValue()).isPresent()) {
            return "redirect:/Asessions/" + Sid + "/movies/main";
        }

        MovieVoteRepository.save(new Vote(null, Sid, voter.longValue(), id));
        movie.setMovieCount(movie.getMovieVoteCount() + 1);
        MovieRepository.save(movie);
        return "redirect:/Asessions/" + Sid + "/movies/main";
    }

    @PostMapping("/Asessions/{Sid}/movies/reset")
    public String resetVotes(@PathVariable long Sid, HttpServletRequest request) {
        SessionInformation session = SessionRepository.findById(Sid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        checkIsHost(session, request);
        List<MovieInformation> movies = MovieRepository.findBySession(session);
        for (MovieInformation movie : movies) {
            movie.setMovieCount(0);
        }
        MovieRepository.saveAll(movies);
        return "redirect:/Asessions/" + Sid + "/movies/main";
    }
}
