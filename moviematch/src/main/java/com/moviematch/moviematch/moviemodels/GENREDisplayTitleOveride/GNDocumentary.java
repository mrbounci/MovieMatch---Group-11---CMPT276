package com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride;

import com.moviematch.moviematch.moviemodels.MovieMethods;

public class GNDocumentary implements MovieMethods {
    @Override
    public String displayTitle() {
        return "Documentary";
    }
}