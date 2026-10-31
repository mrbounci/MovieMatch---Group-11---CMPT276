package com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride;

import com.moviematch.moviematch.moviemodels.MovieMethods;

public class GNRomance implements MovieMethods {
    @Override
    public String displayTitle() {
        return "Romance";
    }
}