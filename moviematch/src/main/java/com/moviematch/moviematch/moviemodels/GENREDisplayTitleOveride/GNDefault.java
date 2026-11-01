package com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride;

import com.moviematch.moviematch.moviemodels.MovieMethods;

public class GNDefault implements MovieMethods {
    @Override
    public String displayTitle() {
        return "Default or No Genre";
    }
}