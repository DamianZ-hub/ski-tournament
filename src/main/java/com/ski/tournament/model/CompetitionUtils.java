package com.ski.tournament.model;

import com.ski.tournament.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompetitionUtils {

    private static TournamentService tournamentService;

    public CompetitionUtils(@Autowired TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    public static Set<Tournament> IdsToTournaments(Set<Integer> ids){
        List<Tournament> tournamentList = tournamentService.getTournaments();

        Set<Tournament> tournamentSet = new HashSet<>();
        tournamentList.forEach(tournament -> {
            if(ids.contains(tournament.getId())) tournamentSet.add(tournament);
        });
        return tournamentSet;
    }

    public static Set<Integer> TournamentsToIds(Set<Tournament> tournamentSet){

            Set<Integer> ids = new HashSet<>();
            tournamentSet.forEach(tournament -> {
                ids.add(tournament.getId());
            });
            return ids;
    }

}
