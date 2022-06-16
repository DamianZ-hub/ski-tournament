package com.ski.tournament.repository;

import com.ski.tournament.core.SignStatus;
import com.ski.tournament.model.PersonTournamentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonTournamentDataRepository extends JpaRepository<PersonTournamentData,Integer> {

    @Query("Select max(t.nr) from PersonTournamentData t where t.signStatus = ?1 and t.tournament.id = ?2 ")
    Integer findMaxNrForTournamentAndStatus(SignStatus status, Integer tournamentId);

    @Query("Select t from PersonTournamentData t where t.tournament.id = ?1 and t.signStatus = ?2")
    List<PersonTournamentData> findAllByTournamentIdAndStatus(Integer tournamentId, SignStatus status);

    List<PersonTournamentData> findByNrNotNull();

    List<PersonTournamentData> findByNrNull();

    @Query("Select t from PersonTournamentData t where t.tournament.id = ?1 and t.nr is not null")
    List<PersonTournamentData> findAllByTournamentIdAndNrNotNull(Integer tournamentId);

    List<PersonTournamentData> findDistinctByTournamentIdIn(List<Integer> ids);

    @Query("Select p from PersonTournamentData p where p.tournament.id = ?1 and p.person.id = ?2")
    PersonTournamentData findOnePersonTournamentDataByTournamentIdAndPersonId(Integer tournamentID, Integer personID);

    @Query("Select count(p) from PersonTournamentData p where p.tournament.id = ?1 and p.person.id = ?2")
    Integer checkIfExistsPersonTournamentDataByTournamentIdAndPersonId(Integer tournamentID, Integer personID);

}
