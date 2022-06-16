package com.ski.tournament.repository;

import com.ski.tournament.core.RideStatus;
import com.ski.tournament.model.SingleCompetitionsSponsorCupData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingleCompetitionsSponsorCupDataRepository extends JpaRepository<SingleCompetitionsSponsorCupData,Integer> {

    @Query("Select s from SingleCompetitionsSponsorCupData s where s.personTournamentData.tournament.id = ?1")
    List<SingleCompetitionsSponsorCupData> getAllSingleCompetitionsSponsorCupDataByTournamentIdPageable(Integer tournamentID);

    @Query("Select count(s) from SingleCompetitionsSponsorCupData s where s.personTournamentData.tournament.id = ?1")
    Integer checkIfExistsSingleCompetitionsSponsorCupDataByTournamentId(Integer tournamentID);

    @Modifying
    @Query(value = "delete from SINGLE_COMPETITIONS_SPONSOR_CUP_DATA where PERSON_TOURNAMENT_ID IN (select id from PERSON_TOURNAMENT_DATA where TOURNAMENT_ID = :tournamentID)",nativeQuery = true)
    void deleteAllSingleCompetitionsSponsorCupDataByTournamentId(@Param("tournamentID") Integer tournamentID);

    @Query("Select s.score from SingleCompetitionsSponsorCupData s where s.personTournamentData.id = ?1 and s.rideStatus = ?2")
    Integer getScoreByPersonTournamentDataId(Integer personTournamentDataID, RideStatus rideStatus);
}
