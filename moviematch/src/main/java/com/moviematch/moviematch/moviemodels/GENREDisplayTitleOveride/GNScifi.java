package com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride;

import com.moviematch.moviematch.moviemodels.MovieMethods;

public class GNScifi implements MovieMethods {
    @Override
    public String displayTitle() {
        return "Sci Fi";
    }
}