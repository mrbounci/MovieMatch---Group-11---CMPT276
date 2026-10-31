package com.moviematch.moviematch.moviemodels;

import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNDefault;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNAction;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNAdventure;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNComedy;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNCrime;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNFantasy;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNHistorical;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNHorror;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNRomance;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNSatire;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNScifi;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNSpeculative;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNThriller;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNDrama;
import com.moviematch.moviematch.moviemodels.GENREDisplayTitleOveride.GNDocumentary;

public class GenreConstructor {
    private GenreConstructor() {
    }

    public static MovieMethods create(GENRE Genre) {
        if (Genre == null) {
            return new GNDefault();
        }
        return switch (Genre) {
            case Action -> new GNAction();
            case Adventure -> new GNAdventure();
            case Comedy -> new GNComedy();
            case Crime -> new GNCrime();
            case Fantasy -> new GNFantasy();
            case Historical -> new GNHistorical();
            case Horror -> new GNHorror();
            case Romance -> new GNRomance();
            case Satire -> new GNSatire();
            case SciFi-> new GNScifi();
            case Speculative -> new GNSpeculative();
            case Thriller -> new GNThriller();
            case Drama -> new GNDrama();
            case Documentary -> new GNDocumentary();
        };
    }
}