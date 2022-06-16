package com.ski.tournament.service;

import com.ski.tournament.core.CompetitionType;
import com.ski.tournament.core.SignStatus;
import com.ski.tournament.model.Person;
import com.ski.tournament.model.PersonTournamentData;
import com.ski.tournament.model.Tournament;
import com.ski.tournament.repository.PersonTournamentDataRepository;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.*;

@Service
public class PersonTournamentDataService extends CrudService<PersonTournamentData,Integer> {

    private final PersonTournamentDataRepository personTournamentDataRepository;
    private final SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService;
    private final SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService;
    private final SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService;

    @Autowired
    public PersonTournamentDataService(PersonTournamentDataRepository personTournamentDataRepository,
                                       SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService,
                                       SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService,
                                       SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService) {
        this.personTournamentDataRepository = personTournamentDataRepository;
        this.singleCompetitionsSponsorCupDataService = singleCompetitionsSponsorCupDataService;
        this.singleCompetitionsOneCompetitionTypeDataService = singleCompetitionsOneCompetitionTypeDataService;
        this.singleCompetitionsUnitMasteryDataService = singleCompetitionsUnitMasteryDataService;
    }

    public List<PersonTournamentData> getAllPersonTournamentData() {
        List<PersonTournamentData> personTournamentDataList = personTournamentDataRepository.findAll();
        return personTournamentDataList;
    }

    public List<PersonTournamentData> getAllPersonTournamentDataForTournamentAndSignStatus(Integer tournamentId,SignStatus signStatus) {
        List<PersonTournamentData> personTournamentDataList = personTournamentDataRepository.findAllByTournamentIdAndStatus(tournamentId,signStatus);
        return personTournamentDataList;
    }

    public int getMaxNrForTournament(Integer tournamentId, SignStatus status){

        Integer maxNr = personTournamentDataRepository.findMaxNrForTournamentAndStatus(status, tournamentId);
        return maxNr==null ? 0 : maxNr;

    }

    public LinkedHashSet<PersonTournamentData> generateStartList(List<PersonTournamentData> availableContenders, int numberOfConntenders, Set<String> genders){
        LinkedHashSet<PersonTournamentData> generatedStartList = new LinkedHashSet<>();
        List<PersonTournamentData> contenders;
        if(genders!=null && genders.size()==1 ){
            contenders = new ArrayList<>();
            String gender = genders.iterator().next();
            availableContenders.stream().forEach(personTournamentData -> {
                if(personTournamentData.getPerson().getGender().equals(gender)) contenders.add(personTournamentData);
            });
        }
        else contenders = new ArrayList<>(availableContenders);

        PersonTournamentData contender;
        if(numberOfConntenders>contenders.size()) numberOfConntenders=contenders.size();
        Random randomizer = new Random();
        for(int i=0; i<numberOfConntenders; i++){
            contender = contenders.get(randomizer.nextInt(contenders.size()));
            generatedStartList.add(contender);
            contenders.remove(contender);
        }

        return generatedStartList;
    }

    public List<PersonTournamentData> findDistinctByTournamentIdIn(List<Integer> ids){
        List<PersonTournamentData> personTournamentDataList = personTournamentDataRepository.findDistinctByTournamentIdIn(ids);
        List<PersonTournamentData> personTournamentDataList1 = new ArrayList<>();
        personTournamentDataList.forEach(p -> {
            if(p.getSignStatus().equals(SignStatus.ACCEPTED) && p.getNr()!=null) personTournamentDataList1.add(p);
        });
        return personTournamentDataList1;
    }

    @Override
    protected JpaRepository<PersonTournamentData, Integer> getRepository() {
        return personTournamentDataRepository;
    }

    public List<PersonTournamentData> findAllByTournamentIdAndNrNotNull(Integer tournamentID) {
        List<PersonTournamentData> personTournamentDataList = personTournamentDataRepository.findAllByTournamentIdAndNrNotNull(tournamentID);
        return personTournamentDataList;
    }

    public HashMap<String,Object> applyForTournament(Tournament tournament, Person person){
       PersonTournamentData personTournamentData = new PersonTournamentData();
       personTournamentData.setSignStatus(SignStatus.PENDING);
       personTournamentData.setTournament(tournament);
       personTournamentData.setPerson(person);

       PersonTournamentData personTournamentData1 = personTournamentDataRepository.save(personTournamentData);
       HashMap<String,Object> result = new HashMap<>();
       result.put("OK",personTournamentData1==null ? false : true);
       result.put("DETAILS", personTournamentData1==null ? "Wystąpił błąd. Nie udało ci się zapisać na zawody. Skontaktuj się z obsługą" :
               "Udało ci się zapisać na zawody. Twoje zgłoszenie oczekuje teraz na rozpatrzenie");
       return result;
    }
    public HashMap<String,Object> unapplyForTournament(Tournament tournament, Person person){
        HashMap<String,Object> result = new HashMap<>();
        PersonTournamentData personTournamentData = this.findOneByTournamentIdAndPersonId(tournament.getId(),person.getId());
        Integer id = personTournamentData.getId();
        if(personTournamentData == null || id==null) {
            result.put("OK",false);
            result.put("DETAILS","Taki rekord nie istnieje");
            return result;
        }

        if(personTournamentData.getSignStatus().equals(SignStatus.REJECTED)) {
            result.put("OK",false);
            result.put("DETAILS","Zgłoszenie zostało odrzucone. Nie można go anulować");
            return result;
        }
        personTournamentDataRepository.deleteById(id);
        if(this.checkIfExistsPersonTournamentDataByTournamentIdAndPersonId(tournament.getId(),person.getId())){
            result.put("OK",false);
            result.put("DETAILS","Wystąpił błąd. Zgłoszenie nie zostało usunięte");
            return result;
        }
        else{
            result.put("OK",true);
            result.put("DETAILS","Zgłoszenie zostało prawidłowo usunięte");
            return result;
        }
    }
    public PersonTournamentData findOneByTournamentIdAndPersonId(Integer tournamentID, Integer personID) {
        PersonTournamentData personTournamentData = personTournamentDataRepository.findOnePersonTournamentDataByTournamentIdAndPersonId(tournamentID,personID);
        return personTournamentData;
    }

    public SignStatus getSignStatusOfPersonTournamentDataByTournamentIdAndPersonId(Integer tournamentID, Integer personID) {
        PersonTournamentData personTournamentData = personTournamentDataRepository.findOnePersonTournamentDataByTournamentIdAndPersonId(tournamentID,personID);
        if(personTournamentData!=null) return personTournamentData.getSignStatus();
        else return null;
    }

    public Integer getScoreByPersonTournamentData(PersonTournamentData personTournamentData){
       CompetitionType competitionType = personTournamentData.getTournament().getCompetitionType();
        Integer personTournamentDataId = personTournamentData.getId();
        Integer score;
        switch(competitionType) {
            case SINGLE_COMPETITIONS_ONE_COMPETITION_TYPE:
                score = singleCompetitionsOneCompetitionTypeDataService.getScoreByPersonTournamentDataId(personTournamentDataId);
                break;
            case SINGLE_COMPETITIONS_SPONSOR_CUP:
                score = singleCompetitionsSponsorCupDataService.getScoreByPersonTournamentDataId(personTournamentDataId);
                break;
            default:
                score = 0;
                new Notification("Nieprawidłowy typ tabeli").open();
                break;
        }
        return score;
    }

    public boolean checkIfExistsPersonTournamentDataByTournamentIdAndPersonId(Integer tournamentID, Integer personID){
        Integer result = personTournamentDataRepository.checkIfExistsPersonTournamentDataByTournamentIdAndPersonId(tournamentID, personID);
        if(result!=0) return true;
        return false;
    }
}
