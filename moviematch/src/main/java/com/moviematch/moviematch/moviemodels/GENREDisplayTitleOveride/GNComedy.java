package com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride;

import com.moviematch.moviematch.moviemodels.MovieMethods;

public class GNComedy implements MovieMethods {
    @Override
    public String displayTitle() {
        return "Comedy";
    }
}