package com.ski.tournament.repository;

import com.ski.tournament.model.SingleCompetitionsUnitMasteryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingleCompetitionsUnitMasteryDataRepository extends JpaRepository<SingleCompetitionsUnitMasteryData,Integer> {

    @Query("Select s from SingleCompetitionsUnitMasteryData s where s.personTournamentData.tournament.id = ?1 ")
    List<SingleCompetitionsUnitMasteryData> getAllSingleCompetitionsUnitMasteryDataByTournamentIdPageable(Integer tournamentID);

    @Query("Select count(s) from SingleCompetitionsUnitMasteryData s where s.personTournamentData.tournament.id = ?1 ")
    Integer checkIfExistsSingleCompetitionsUnitMasteryDataByTournamentId(Integer tournamentID);

    @Modifying
    @Query(value = "delete from SINGLE_COMPETITIONS_UNIT_MASTERY_DATA where PERSON_TOURNAMENT_ID IN (select id from PERSON_TOURNAMENT_DATA where TOURNAMENT_ID = :tournamentID)",nativeQuery = true)
    void deleteAllSingleCompetitionsUnitMasteryDataByTournamentId(@Param("tournamentID") Integer tournamentID);


}
