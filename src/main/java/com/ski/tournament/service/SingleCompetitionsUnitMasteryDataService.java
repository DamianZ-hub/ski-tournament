package com.ski.tournament.service;

import com.ski.tournament.core.DoubleUtils;
import com.ski.tournament.model.SingleCompetitionsUnitMasteryData;
import com.ski.tournament.repository.SingleCompetitionsUnitMasteryDataRepository;
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
public class SingleCompetitionsUnitMasteryDataService extends CrudService<SingleCompetitionsUnitMasteryData,Integer> {

    private final SingleCompetitionsUnitMasteryDataRepository singleCompetitionsUnitMasteryDataRepository;

    @Autowired
    public SingleCompetitionsUnitMasteryDataService(SingleCompetitionsUnitMasteryDataRepository singleCompetitionsUnitMasteryDataRepository) {
        this.singleCompetitionsUnitMasteryDataRepository = singleCompetitionsUnitMasteryDataRepository;
    }

    public List<SingleCompetitionsUnitMasteryData> getAllSingleCompetitionsUnitMasteryData() {
        List<SingleCompetitionsUnitMasteryData> singleCompetitionsUnitMasteryData = singleCompetitionsUnitMasteryDataRepository.findAll();
        return singleCompetitionsUnitMasteryData;
    }

    @Override
    protected JpaRepository<SingleCompetitionsUnitMasteryData, Integer> getRepository() {
        return singleCompetitionsUnitMasteryDataRepository;
    }

    public Page<SingleCompetitionsUnitMasteryData> getAllSingleCompetitionsUnitMasteryDataByTournamentIdPageable(Pageable pageable, Integer tournamentID) {
        List<SingleCompetitionsUnitMasteryData> singleCompetitionsUnitMasteryDataList = singleCompetitionsUnitMasteryDataRepository.getAllSingleCompetitionsUnitMasteryDataByTournamentIdPageable(tournamentID);
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), singleCompetitionsUnitMasteryDataList.size());
        final Page<SingleCompetitionsUnitMasteryData> page = new PageImpl<>(singleCompetitionsUnitMasteryDataList.subList(start, end), pageable, singleCompetitionsUnitMasteryDataList.size());
        return page;
    }
    public List<SingleCompetitionsUnitMasteryData> getAllSingleCompetitionsUnitMasteryDataByTournamentId(Integer tournamentID) {
        List<SingleCompetitionsUnitMasteryData> singleCompetitionsUnitMasteryDataList = singleCompetitionsUnitMasteryDataRepository.getAllSingleCompetitionsUnitMasteryDataByTournamentIdPageable(tournamentID);
        return singleCompetitionsUnitMasteryDataList;
    }
    public boolean checkIfExistsSingleCompetitionsUnitMasteryDataByTournamentId(Integer tournamentID){
        Integer result = singleCompetitionsUnitMasteryDataRepository.checkIfExistsSingleCompetitionsUnitMasteryDataByTournamentId(tournamentID);
        if(result!=0) return true;
        return false;
    }

    @Transactional
    public void deleteAllSingleCompetitionsUnitMasteryDataByTournamentId(Integer tournamentID){
        singleCompetitionsUnitMasteryDataRepository.deleteAllSingleCompetitionsUnitMasteryDataByTournamentId(tournamentID);
    }

    public List<SingleCompetitionsUnitMasteryData> calculateTime(List<SingleCompetitionsUnitMasteryData> dataList) {

        dataList.forEach(singleCompetitionsUnitMasteryData -> {
            singleCompetitionsUnitMasteryData.setSumariseRideTime(DoubleUtils.round(singleCompetitionsUnitMasteryData.getFirstRideTime() +
                    singleCompetitionsUnitMasteryData.getSecondRideTime(), 2)
            );
        });
        return dataList;
    }
    public List<SingleCompetitionsUnitMasteryData> assignTakenPlace(List<SingleCompetitionsUnitMasteryData> dataList){

        dataList.sort((o1,o2) -> o1.getSumariseRideTime().compareTo(o2.getSumariseRideTime()));
        int takenPlace = 0;
        for(SingleCompetitionsUnitMasteryData singleCompetitionsUnitMasteryData : dataList){
            singleCompetitionsUnitMasteryData.setTakenPlace(++takenPlace);
        }
        return dataList;
    }

    public List<SingleCompetitionsUnitMasteryData> calculate(List<SingleCompetitionsUnitMasteryData> dataList) {
        List<SingleCompetitionsUnitMasteryData> sumarizedTimeDataList = calculateTime(dataList);
        List<SingleCompetitionsUnitMasteryData> assignedTakenPlacesDataList = assignTakenPlace(sumarizedTimeDataList);
        return assignedTakenPlacesDataList;
    }
}
