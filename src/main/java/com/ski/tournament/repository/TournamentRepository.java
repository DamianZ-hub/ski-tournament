package com.ski.tournament.repository;

import com.ski.tournament.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament,Integer> {

    List<Tournament> findByIdIn(List<Integer> ids);

    @Query("Select t from Tournament t where t.status = 'INCOMING' or t.status = 'ONGOING'")
    List<Tournament> getIncomingAndOngoingTournaments();
}
