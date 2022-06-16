package com.ski.tournament.repository;

import com.ski.tournament.core.RideStatus;
import com.ski.tournament.model.SingleCompetitionsOneCompetitionTypeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingleCompetitionsOneCompetitionTypeDataRepository extends JpaRepository<SingleCompetitionsOneCompetitionTypeData,Integer> {

    @Query("Select s from SingleCompetitionsOneCompetitionTypeData s where s.personTournamentData.tournament.id = ?1 ")
    List<SingleCompetitionsOneCompetitionTypeData> getAllSingleCompetitionsOneCompetitionTypeDataByTournamentIdPageable(Integer tournamentID);

    @Query("Select count(s) from SingleCompetitionsOneCompetitionTypeData s where s.personTournamentData.tournament.id = ?1 ")
    Integer checkIfExistsSingleCompetitionsOneCompetitionTypeDataByTournamentId(Integer tournamentID);

    @Modifying
    @Query(value = "delete from SINGLE_COMPETITIONS_ONE_COMPETITION_TYPE_DATA where PERSON_TOURNAMENT_ID IN (select id from PERSON_TOURNAMENT_DATA where TOURNAMENT_ID = :tournamentID)",nativeQuery = true)
    void deleteAllSingleCompetitionsOneCompetitionTypeDataByTournamentId(@Param("tournamentID") Integer tournamentID);

    @Query("Select s.score from SingleCompetitionsOneCompetitionTypeData s where s.personTournamentData.id = ?1 and s.rideStatus = ?2 ")
    Integer getScoreByPersonTournamentDataId(Integer personTournamentDataID, RideStatus rideStatus);
}
