package com.ski.tournament.service;

import com.ski.tournament.core.Competition;
import com.ski.tournament.model.*;
import com.ski.tournament.repository.ClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassificationService extends CrudService<Classification, Integer> {

    private final ClassificationRepository classificationRepository;
    private final PersonTournamentDataService personTournamentDataService;
    private final SingleCompetitionsTeamCompetitionsDataService singleCompetitionsTeamCompetitionsDataService;
    private final UnitService unitService;
    private final TournamentService tournamentService;

    @Autowired
    public ClassificationService(ClassificationRepository classificationRepository,
                                 PersonTournamentDataService personTournamentDataService,
                                 SingleCompetitionsTeamCompetitionsDataService singleCompetitionsTeamCompetitionsDataService,
                                 UnitService unitService,
                                 TournamentService tournamentService) {
        this.classificationRepository = classificationRepository;
        this.personTournamentDataService = personTournamentDataService;
        this.singleCompetitionsTeamCompetitionsDataService = singleCompetitionsTeamCompetitionsDataService;
        this.unitService = unitService;
        this.tournamentService = tournamentService;
    }

    public List<Classification> getClassifications(){
        return classificationRepository.findAll();
    }
    @Override
    protected JpaRepository<Classification, Integer> getRepository() {
        return classificationRepository;
    }

    public List<ClassificationDataView> getClassificationDataViewList(List<Integer> tournamentsIds, List<PersonTournamentData> personTournamentDataList, Classification classification) {

        List<ClassificationDataView> classificationDataViewList = new ArrayList<>();

       List<Person> persons = new ArrayList<>();
       for(final PersonTournamentData personTournamentData : personTournamentDataList) {
           persons.add(personTournamentData.getPerson());
       }
       List<Person> personsDistinct = persons.stream()
               .distinct()
               .collect(Collectors.toList());
        personsDistinct.forEach(person -> {
            String firstName = person.getFirstName();
            String lastName = person.getLastName();
            ClassificationDataView classificationDataView = new ClassificationDataView();
            classificationDataView.setFirstName(firstName);
            classificationDataView.setLastName(lastName);

            tournamentsIds.forEach(tournamentsId -> {
                PersonTournamentData personTournamentData = personTournamentDataList.stream()
                        .filter(personTournamentData1 -> person.getId().equals(personTournamentData1.getPerson().getId()) && tournamentsId.equals(personTournamentData1.getTournament().getId()))
                        .findAny()
                        .orElse(null);
                if(personTournamentData!=null) {
                    Integer tournamentScore = personTournamentDataService.getScoreByPersonTournamentData(personTournamentData);
                    classificationDataView.getScoreMap().put(tournamentsId,tournamentScore);
                }
                else classificationDataView.getScoreMap().put(tournamentsId,0);
            });
            classificationDataViewList.add(classificationDataView);
        });
        return classificationDataViewList;
    }


    public List<TeamGeneralClassificationDataView> getTeamGeneralClassificationDataViewList(List<Integer> tournamentsIds) {

        List<TeamGeneralClassificationDataView> teamGeneralClassificationDataViewList = new ArrayList<>();

        List<SingleCompetitionsTeamCompetitionData> singleCompetitionsTeamCompetitionDataList = new ArrayList<>();

        tournamentsIds.forEach(id -> {
            singleCompetitionsTeamCompetitionDataList.addAll(singleCompetitionsTeamCompetitionsDataService.getTeamCompetitionDatas(id, "SingleCompetitionsSponsorCup"));
        });

        List<Unit> unitList = unitService.getUnits();
        unitList.removeIf(obj -> obj.getShortName().equals("CSiR"));

        for (final Unit unit : unitList) {

            List<SingleCompetitionsTeamCompetitionData> dataListForUnit = singleCompetitionsTeamCompetitionDataList.stream()
                    .filter(s -> s.getUnit().getShortName().equals(unit.getShortName()))
                    .collect(Collectors.toList());
            int sum = dataListForUnit.stream().mapToInt(SingleCompetitionsTeamCompetitionData::getSumarizedScore).sum();
            if(sum==0) continue;
            TeamGeneralClassificationDataView teamGeneralClassificationDataView = new TeamGeneralClassificationDataView();
            teamGeneralClassificationDataView.setUnit(unit);
            teamGeneralClassificationDataView.setSumarizedScore(sum);
            teamGeneralClassificationDataViewList.add(teamGeneralClassificationDataView);
        }
        List<TeamGeneralClassificationDataView> teamGeneralClassificationDataViewListWithAssignedTakenPlaces = assignTakenPlace(teamGeneralClassificationDataViewList);

        return teamGeneralClassificationDataViewListWithAssignedTakenPlaces;
    }

    private List<TeamGeneralClassificationDataView> assignTakenPlace(List<TeamGeneralClassificationDataView> dataList) {

        Collections.sort(dataList, Collections.reverseOrder((o1, o2) -> o1.getSumarizedScore().compareTo(o2.getSumarizedScore())));
        int takenPlace = 0;
        for(TeamGeneralClassificationDataView teamGeneralClassificationDataView : dataList){
            teamGeneralClassificationDataView.setTakenPlace(++takenPlace);
        }
        return dataList;
    }

    public int calculateSumForRectorCupClassification(HashMap<Integer,Integer> scoreMap){
        int scoreSum = calculateSumForClassification(scoreMap);
        if(scoreMap.size()<=5) return  scoreSum;
        else if(scoreMap.size()==6 || scoreMap.size()==7) {
            int minScore =  Collections.min(scoreMap.values());
            return (scoreSum - minScore);
        }
        else {
            int numberOfSlaloms = 0;
            int numberOfGigants = 0;
            for(var entry : scoreMap.entrySet()) {
              if(tournamentService.get(entry.getKey()).orElseThrow().getCompetition().equals(Competition.SLALOM)) numberOfSlaloms++;
              else numberOfGigants++;
            }

            Competition competitionToSubtractScores = null;
            if(numberOfSlaloms<=2) competitionToSubtractScores = Competition.GIGANT;
            else if(numberOfGigants<=2) competitionToSubtractScores = Competition.SLALOM;


            if(competitionToSubtractScores != null) {
                for(var entry : scoreMap.entrySet()) {
                    if(!tournamentService.get(entry.getKey()).orElseThrow().getCompetition().equals(competitionToSubtractScores)) scoreMap.remove(entry.getKey());
                }
            }
           List<Integer> listOfTwoMinimalScores = scoreMap.keySet().stream().sorted().limit(2).map(key -> scoreMap.get(key)).collect(Collectors.toList());
            scoreSum -= listOfTwoMinimalScores.get(0);
            scoreSum -= listOfTwoMinimalScores.get(1);
            return scoreSum;
        }
    }

    private int calculateSumForClassification(HashMap<Integer,Integer> scoreMap){
       int scoreSum = scoreMap.entrySet().stream().mapToInt(entry -> (Integer) entry.getValue()).sum();
       return scoreSum;
    }
}
