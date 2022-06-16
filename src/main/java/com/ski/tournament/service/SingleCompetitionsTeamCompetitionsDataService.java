package com.ski.tournament.service;

import com.ski.tournament.model.SingleCompetitionsOneCompetitionTypeData;
import com.ski.tournament.model.SingleCompetitionsSponsorCupData;
import com.ski.tournament.model.SingleCompetitionsTeamCompetitionData;
import com.ski.tournament.model.Unit;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SingleCompetitionsTeamCompetitionsDataService {

    private final SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService;
    private final SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService;
    private final SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService;
    private final UnitService unitService;

    @Autowired
    public SingleCompetitionsTeamCompetitionsDataService(SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService,
                                                         SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService,
                                                         SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService,
                                                         UnitService unitService) {
        this.singleCompetitionsSponsorCupDataService = singleCompetitionsSponsorCupDataService;
        this.singleCompetitionsUnitMasteryDataService = singleCompetitionsUnitMasteryDataService;
        this.singleCompetitionsOneCompetitionTypeDataService = singleCompetitionsOneCompetitionTypeDataService;
        this.unitService = unitService;
    }

    public List<SingleCompetitionsTeamCompetitionData> getTeamCompetitionDatas(Integer tournamentsId, String competitionType) {

      //  List<SingleCompetitionsTeamCompetitionData> singleCompetitionsTeamCompetitionDataList = getTeamCompetitionDataForSingleCompetitionsSponsorCupData(tournamentsId);
        List<SingleCompetitionsTeamCompetitionData> singleCompetitionsTeamCompetitionDataList = new ArrayList<>();
        
           if(competitionType==null) {
               singleCompetitionsTeamCompetitionDataList.addAll(getTeamCompetitionDataForSingleCompetitionsSponsorCupData(tournamentsId));
               singleCompetitionsTeamCompetitionDataList.addAll(getTeamCompetitionDataForSingleCompetitionsOneCompetitionTypeData(tournamentsId));
           }

           else if(competitionType.equals("SingleCompetitionsSponsorCup"))
                singleCompetitionsTeamCompetitionDataList.addAll(getTeamCompetitionDataForSingleCompetitionsSponsorCupData(tournamentsId));

           else if(competitionType.equals("SingleCompetitionsOneCompetitionType"))
                singleCompetitionsTeamCompetitionDataList.addAll(getTeamCompetitionDataForSingleCompetitionsOneCompetitionTypeData(tournamentsId));
       else new Notification("Nieprawidłowy typ tabeli").open();

        List<SingleCompetitionsTeamCompetitionData> singleCompetitionsTeamCompetitionDataListWithAssignedTakenPlaces = assignTakenPlace(singleCompetitionsTeamCompetitionDataList);
        return singleCompetitionsTeamCompetitionDataListWithAssignedTakenPlaces;
    }

    private List<SingleCompetitionsTeamCompetitionData> getTeamCompetitionDataForSingleCompetitionsSponsorCupData(Integer tournamentsId) {
        List<SingleCompetitionsSponsorCupData> singleCompetitionsSponsorCupDataList = singleCompetitionsSponsorCupDataService.getAllSingleCompetitionsSponsorCupDataByTournamentId(tournamentsId);
        List<SingleCompetitionsTeamCompetitionData> singleCompetitionsTeamCompetitionDataList = new ArrayList<>();
        List<Unit> unitList = unitService.getUnits();

        for (final Unit unit : unitList) {

            List<SingleCompetitionsSponsorCupData> recordsForData = new ArrayList<>();
            singleCompetitionsSponsorCupDataList.forEach(s -> {
                if (s.getPersonTournamentData().getPerson().getUnit().getId().equals(unit.getId()))
                    recordsForData.add(s);
            });
            if (recordsForData.size() < 2 || unit.getShortName().equals("CSiR")) continue;

            List<SingleCompetitionsSponsorCupData> recordsForManData = new ArrayList<>();
            List<SingleCompetitionsSponsorCupData> recordsForWomanData = new ArrayList<>();

            recordsForData.forEach(singleCompetitionsSponsorCupData -> {
                if (singleCompetitionsSponsorCupData.getPersonTournamentData().getPerson().getGender().equals("Mezczyzna"))
                    recordsForManData.add(singleCompetitionsSponsorCupData);
                if (singleCompetitionsSponsorCupData.getPersonTournamentData().getPerson().getGender().equals("Kobieta"))
                    recordsForWomanData.add(singleCompetitionsSponsorCupData);
            });
            SingleCompetitionsTeamCompetitionData singleCompetitionsTeamCompetitionData = new SingleCompetitionsTeamCompetitionData();

            Collections.sort(recordsForManData, Collections.reverseOrder((o1, o2) -> o1.getScore().compareTo(o2.getScore())));
            Collections.sort(recordsForWomanData, Collections.reverseOrder((o1, o2) -> o1.getScore().compareTo(o2.getScore())));

            List<Integer> manScoreList = new ArrayList<>(3);
            List<Integer> womanScoreList = new ArrayList<>(3);

            for (int i = 0; i < 3; i++) {
                if (i < recordsForManData.size()) manScoreList.add(recordsForManData.get(i).getScore());
                else manScoreList.add(0);
                if (i < recordsForWomanData.size()) womanScoreList.add(recordsForWomanData.get(i).getScore());
                else womanScoreList.add(0);
            }

            singleCompetitionsTeamCompetitionData.setBestManScore(manScoreList);
            singleCompetitionsTeamCompetitionData.setBestWomanScore(womanScoreList);
            singleCompetitionsTeamCompetitionData.setUnit(unit);
            Integer sum = (manScoreList.stream()
                    .reduce(0, (a, b) -> a + b)) +
                    (womanScoreList.stream()
                            .reduce(0, (c, d) -> c + d));
            singleCompetitionsTeamCompetitionData.setSumarizedScore(sum);
            singleCompetitionsTeamCompetitionDataList.add(singleCompetitionsTeamCompetitionData);
        }
        return singleCompetitionsTeamCompetitionDataList;
    }


    private List<SingleCompetitionsTeamCompetitionData> getTeamCompetitionDataForSingleCompetitionsOneCompetitionTypeData(Integer tournamentsId) {
        List<SingleCompetitionsOneCompetitionTypeData> singleCompetitionsOneCompetitionTypeDataList = singleCompetitionsOneCompetitionTypeDataService.getAllSingleCompetitionsOneCompetitionTypeDataByTournamentId(tournamentsId);
        List<SingleCompetitionsTeamCompetitionData> singleCompetitionsTeamCompetitionDataList = new ArrayList<>();
        List<Unit> unitList = unitService.getUnits();

        for (final Unit unit : unitList) {

            List<SingleCompetitionsOneCompetitionTypeData> recordsForData = new ArrayList<>();
            singleCompetitionsOneCompetitionTypeDataList.forEach(s -> {
                if (s.getPersonTournamentData().getPerson().getUnit().getId().equals(unit.getId()))
                    recordsForData.add(s);
            });
            if (recordsForData.size() < 2 || unit.getShortName().equals("CSiR")) continue;

            List<SingleCompetitionsOneCompetitionTypeData> recordsForManData = new ArrayList<>();
            List<SingleCompetitionsOneCompetitionTypeData> recordsForWomanData = new ArrayList<>();

            recordsForData.forEach(singleCompetitionsOneCompetitionTypeData -> {
                if (singleCompetitionsOneCompetitionTypeData.getPersonTournamentData().getPerson().getGender().equals("Mezczyzna"))
                    recordsForManData.add(singleCompetitionsOneCompetitionTypeData);
                if (singleCompetitionsOneCompetitionTypeData.getPersonTournamentData().getPerson().getGender().equals("Kobieta"))
                    recordsForWomanData.add(singleCompetitionsOneCompetitionTypeData);
            });
            SingleCompetitionsTeamCompetitionData singleCompetitionsTeamCompetitionData = new SingleCompetitionsTeamCompetitionData();

            Collections.sort(recordsForManData, Collections.reverseOrder((o1, o2) -> o1.getScore().compareTo(o2.getScore())));
            Collections.sort(recordsForWomanData, Collections.reverseOrder((o1, o2) -> o1.getScore().compareTo(o2.getScore())));

            List<Integer> manScoreList = new ArrayList<>(3);
            List<Integer> womanScoreList = new ArrayList<>(3);

            for (int i = 0; i < 3; i++) {
                if (i < recordsForManData.size()) manScoreList.add(recordsForManData.get(i).getScore());
                else manScoreList.add(0);
                if (i < recordsForWomanData.size()) womanScoreList.add(recordsForWomanData.get(i).getScore());
                else womanScoreList.add(0);
            }

            singleCompetitionsTeamCompetitionData.setBestManScore(manScoreList);
            singleCompetitionsTeamCompetitionData.setBestWomanScore(womanScoreList);
            singleCompetitionsTeamCompetitionData.setUnit(unit);
            Integer sum = (manScoreList.stream()
                    .reduce(0, (a, b) -> a + b)) +
                    (womanScoreList.stream()
                            .reduce(0, (c, d) -> c + d));
            singleCompetitionsTeamCompetitionData.setSumarizedScore(sum);
            singleCompetitionsTeamCompetitionDataList.add(singleCompetitionsTeamCompetitionData);
        }
        return singleCompetitionsTeamCompetitionDataList;
    }

    private List<SingleCompetitionsTeamCompetitionData> assignTakenPlace(List<SingleCompetitionsTeamCompetitionData> dataList) {

        Collections.sort(dataList, Collections.reverseOrder((o1, o2) -> o1.getSumarizedScore().compareTo(o2.getSumarizedScore())));
        int takenPlace = 0;
        for(SingleCompetitionsTeamCompetitionData singleCompetitionsTeamCompetitionData : dataList){
            singleCompetitionsTeamCompetitionData.setTakenPlace(++takenPlace);
        }
        return dataList;
    }

}
