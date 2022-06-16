package com.ski.tournament.service;

import com.ski.tournament.model.Tournament;
import com.ski.tournament.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;


@Service
public class TournamentService extends CrudService<Tournament, Integer> {

    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public List<Tournament> getTournaments(){
        return tournamentRepository.findAll();
    }

    public List<Tournament> findByIdIn(List<Integer> ids){
        return tournamentRepository.findByIdIn(ids);
    }

    @Override
    protected JpaRepository<Tournament, Integer> getRepository() {
        return tournamentRepository;
    }

    public List<Tournament> getIncomingAndOngoingTournaments() {
        return tournamentRepository.getIncomingAndOngoingTournaments();
    }
}
