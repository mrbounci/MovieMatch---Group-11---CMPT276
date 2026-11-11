// Movie title lookup for the "Submit a movie" form.
// As you type a title it asks our server (/api/tmdb/search) for matches from TMDB
// and shows them with posters. Clicking one fills in the title and genre
// and shows the poster under the title box.
//
// Needs the page to have <input id="movieTitle"> and <select id="movieGenre">.

(function () {
    const titleInput = document.getElementById("movieTitle");
    const genreSelect = document.getElementById("movieGenre");
    const tmdbIdInput = document.getElementById("tmdbMovieId");
    const synopsis = document.getElementById("movieSynopsis");
    const posterUrlInput = document.getElementById("moviePosterUrl");
    const yearInput = document.getElementById("movieYear");
    if (!titleInput) {
        return;
    }

    // the dropdown goes right under the title box
    const field = titleInput.parentElement;
    field.classList.add("lookup-field");
    titleInput.setAttribute("autocomplete", "off");

    const list = document.createElement("ul");
    list.className = "suggestions";
    list.hidden = true;
    titleInput.insertAdjacentElement("afterend", list);

    // poster + genre of the movie you picked
    const picked = document.createElement("div");
    picked.className = "picked-movie";
    picked.hidden = true;
    field.appendChild(picked);

    let timer = null;
    let latestSearch = "";

    titleInput.addEventListener("input", function () {
        clearTimeout(timer);
        picked.hidden = true;
        if (tmdbIdInput) { tmdbIdInput.value = ""; }
        if (yearInput) { yearInput.value = ""; }
        if (posterUrlInput) { posterUrlInput.value = ""; }
        const text = titleInput.value.trim();
        if (text.length < 2) {
            hideList();
            return;
        }
        // wait until they stop typing for a moment so we don't search on every key
        timer = setTimeout(function () { search(text); }, 300);
    });

    async function search(text) {
        latestSearch = text;
        try {
            const response = await fetch("/api/tmdb/search?q=" + encodeURIComponent(text));
            if (!response.ok) {
                hideList();
                return;
            }
            const movies = await response.json();
            // ignore answers to older searches that came back late
            if (text === latestSearch) {
                showList(movies);
            }
        } catch (error) {
            hideList();
        }
    }

    function showList(movies) {
        list.innerHTML = "";
        if (!Array.isArray(movies) || movies.length === 0) {
            hideList();
            return;
        }
        movies.forEach(function (movie) {
            const item = document.createElement("li");
            const button = document.createElement("button");
            button.type = "button";

            button.appendChild(posterImage(movie, "suggestion-poster"));

            const name = document.createElement("span");
            name.className = "suggestion-title";
            name.textContent = movie.title;
            button.appendChild(name);

            if (movie.year) {
                const year = document.createElement("span");
                year.className = "suggestion-year";
                year.textContent = movie.year;
                button.appendChild(year);
            }

            button.addEventListener("click", function () { choose(movie); });
            item.appendChild(button);
            list.appendChild(item);
        });
        list.hidden = false;
    }

    function choose(movie) {
        titleInput.value = movie.title;
        if (tmdbIdInput) { tmdbIdInput.value = movie.id; }

        if (yearInput) { yearInput.value = movie.year || ""; }

        if (synopsis) {synopsis.value = movie.synopsis; }
        
        if (tmdbIdInput) { tmdbIdInput.value = movie.id; }
        if (posterUrlInput) { posterUrlInput.value = movie.posterUrl || ""; }

        let genreText = "Pick a genre below, we couldn't match this one.";
        if (movie.genre && genreSelect) {
            genreSelect.value = movie.genre;
            genreText = "Genre set to " + movie.genre + ".";
        }

        picked.innerHTML = "";
        picked.appendChild(posterImage(movie, "picked-poster"));
        const info = document.createElement("p");
        info.textContent = movie.title + (movie.year ? " (" + movie.year + "). " : ". ") + genreText;
        picked.appendChild(info);
        picked.hidden = false;

        hideList();
    }

    // <img> for a poster, or a grey box if TMDB doesn't have one
    function posterImage(movie, className) {
        if (movie.posterUrl) {
            const img = document.createElement("img");
            img.src = movie.posterUrl;
            img.alt = "";
            img.className = className;
            img.loading = "lazy";
            return img;
        }
        const blank = document.createElement("span");
        blank.className = className + " no-poster";
        return blank;
    }

    function hideList() {
        list.hidden = true;
        list.innerHTML = "";
    }

    // close the dropdown when clicking somewhere else or pressing Escape
    document.addEventListener("click", function (event) {
        if (!field.contains(event.target)) {
            hideList();
        }
    });
    titleInput.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            hideList();
        }
    });
})();
