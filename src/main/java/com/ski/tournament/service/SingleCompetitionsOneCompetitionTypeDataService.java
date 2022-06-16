package com.ski.tournament.service;

import com.ski.tournament.core.DoubleUtils;
import com.ski.tournament.core.RideStatus;
import com.ski.tournament.core.ScoreTable;
import com.ski.tournament.model.SingleCompetitionsOneCompetitionTypeData;
import com.ski.tournament.repository.SingleCompetitionsOneCompetitionTypeDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class SingleCompetitionsOneCompetitionTypeDataService extends CrudService<SingleCompetitionsOneCompetitionTypeData,Integer> {


    private final SingleCompetitionsOneCompetitionTypeDataRepository singleCompetitionsOneCompetitionTypeDataRepository;

    @Autowired
    public SingleCompetitionsOneCompetitionTypeDataService(SingleCompetitionsOneCompetitionTypeDataRepository singleCompetitionsOneCompetitionTypeDataRepository) {
        this.singleCompetitionsOneCompetitionTypeDataRepository = singleCompetitionsOneCompetitionTypeDataRepository;
    }

    public List<SingleCompetitionsOneCompetitionTypeData> getAllSingleCompetitionsOneCompetitionTypeData() {
        List<SingleCompetitionsOneCompetitionTypeData> singleCompetitionsOneCompetitionTypeDataList = singleCompetitionsOneCompetitionTypeDataRepository.findAll();
        return singleCompetitionsOneCompetitionTypeDataList;
    }

    @Override
    protected JpaRepository<SingleCompetitionsOneCompetitionTypeData, Integer> getRepository() {
        return singleCompetitionsOneCompetitionTypeDataRepository;
    }

    public Page<SingleCompetitionsOneCompetitionTypeData> getAllSingleCompetitionsOneCompetitionTypeDataByTournamentIdPageable(Pageable pageable, Integer tournamentID) {
        List<SingleCompetitionsOneCompetitionTypeData> singleCompetitionsOneCompetitionTypeDataList = singleCompetitionsOneCompetitionTypeDataRepository.getAllSingleCompetitionsOneCompetitionTypeDataByTournamentIdPageable(tournamentID);
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), singleCompetitionsOneCompetitionTypeDataList.size());
        final Page<SingleCompetitionsOneCompetitionTypeData> page = new PageImpl<>(singleCompetitionsOneCompetitionTypeDataList.subList(start, end), pageable, singleCompetitionsOneCompetitionTypeDataList.size());
        return page;
    }

    public List<SingleCompetitionsOneCompetitionTypeData> getAllSingleCompetitionsOneCompetitionTypeDataByTournamentId(Integer tournamentID) {
        List<SingleCompetitionsOneCompetitionTypeData> singleCompetitionsOneCompetitionTypeDataList = singleCompetitionsOneCompetitionTypeDataRepository.getAllSingleCompetitionsOneCompetitionTypeDataByTournamentIdPageable(tournamentID);
        return singleCompetitionsOneCompetitionTypeDataList;
    }

    public boolean checkIfExistsSingleCompetitionsOneCompetitionTypeDataByTournamentId(Integer tournamentID){
        Integer result = singleCompetitionsOneCompetitionTypeDataRepository.checkIfExistsSingleCompetitionsOneCompetitionTypeDataByTournamentId(tournamentID);
        if(result!=0) return true;
        return false;

    }
    @Transactional
    public void deleteAllSingleCompetitionsOneCompetitionTypeDataByTournamentId(Integer tournamentID){
        singleCompetitionsOneCompetitionTypeDataRepository.checkIfExistsSingleCompetitionsOneCompetitionTypeDataByTournamentId(tournamentID);
    }

    public Integer getScoreByPersonTournamentDataId(Integer personTournamentDataId) {
       Integer score = singleCompetitionsOneCompetitionTypeDataRepository.getScoreByPersonTournamentDataId(personTournamentDataId, RideStatus.DS);
       return score == null ? 0 : score;
    }

    public List<SingleCompetitionsOneCompetitionTypeData> calculateTime(List<SingleCompetitionsOneCompetitionTypeData> dataList) {

        dataList.forEach(singleCompetitionsOneCompetitionTypeData -> {
            singleCompetitionsOneCompetitionTypeData.setSumariseRideTime(DoubleUtils.round(singleCompetitionsOneCompetitionTypeData.getFirstRideTime() +
                    singleCompetitionsOneCompetitionTypeData.getSecondRideTime(), 2)
            );
        });
        return dataList;
    }
    public List<SingleCompetitionsOneCompetitionTypeData> assignTakenPlace(List<SingleCompetitionsOneCompetitionTypeData> dataList){

        dataList.sort((o1,o2) -> o1.getSumariseRideTime().compareTo(o2.getSumariseRideTime()));
        int takenPlace = 0;
        for(SingleCompetitionsOneCompetitionTypeData singleCompetitionsOneCompetitionTypeData : dataList){
            singleCompetitionsOneCompetitionTypeData.setTakenPlace(++takenPlace);
        }
        return dataList;
    }

    public List<SingleCompetitionsOneCompetitionTypeData> assignScore(List<SingleCompetitionsOneCompetitionTypeData> dataList){

        for(SingleCompetitionsOneCompetitionTypeData singleCompetitionsOneCompetitionTypeData : dataList){
            singleCompetitionsOneCompetitionTypeData.setScore(ScoreTable.getInstance().get(singleCompetitionsOneCompetitionTypeData.getTakenPlace()));
        }
        return dataList;
    }

    public List<SingleCompetitionsOneCompetitionTypeData> calculate(List<SingleCompetitionsOneCompetitionTypeData> dataList) {
        List<SingleCompetitionsOneCompetitionTypeData> sumarizedTimeDataList = calculateTime(dataList);
        List<SingleCompetitionsOneCompetitionTypeData> assignedTakenPlacesDataList = assignTakenPlace(sumarizedTimeDataList);
        List<SingleCompetitionsOneCompetitionTypeData> assignScoresDataList = assignScore(assignedTakenPlacesDataList);
        return assignScoresDataList;
    }

}
