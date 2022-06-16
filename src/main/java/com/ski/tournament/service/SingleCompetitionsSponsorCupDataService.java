package com.ski.tournament.service;

import com.ski.tournament.core.DoubleUtils;
import com.ski.tournament.core.RideStatus;
import com.ski.tournament.core.ScoreTable;
import com.ski.tournament.model.SingleCompetitionsSponsorCupData;
import com.ski.tournament.repository.SingleCompetitionsSponsorCupDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.artur.helpers.CrudService;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

@Service
public class SingleCompetitionsSponsorCupDataService extends CrudService<SingleCompetitionsSponsorCupData, Integer> {

    private final SingleCompetitionsSponsorCupDataRepository singleCompetitionsSponsorCupDataRepository;

    @Autowired
    public SingleCompetitionsSponsorCupDataService(SingleCompetitionsSponsorCupDataRepository singleCompetitionsSponsorCupDataRepository) {
        this.singleCompetitionsSponsorCupDataRepository = singleCompetitionsSponsorCupDataRepository;
    }

    public List<SingleCompetitionsSponsorCupData> getAllSingleCompetitionsSponsorCupData() {
        List<SingleCompetitionsSponsorCupData> singleCompetitionsSponsorCupData = singleCompetitionsSponsorCupDataRepository.findAll();
        return singleCompetitionsSponsorCupData;
    }

    @Override
    protected JpaRepository<SingleCompetitionsSponsorCupData, Integer> getRepository() {
        return singleCompetitionsSponsorCupDataRepository;
    }

    public Page<SingleCompetitionsSponsorCupData> getAllSingleCompetitionsSponsorCupDataByTournamentIdPageable(Pageable pageable, Integer tournamentID) {
        List<SingleCompetitionsSponsorCupData> singleCompetitionsSponsorCupDataList = singleCompetitionsSponsorCupDataRepository.getAllSingleCompetitionsSponsorCupDataByTournamentIdPageable(tournamentID);
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), singleCompetitionsSponsorCupDataList.size());
        final Page<SingleCompetitionsSponsorCupData> page = new PageImpl<>(singleCompetitionsSponsorCupDataList.subList(start, end), pageable, singleCompetitionsSponsorCupDataList.size());
        return page;
    }

    public List<SingleCompetitionsSponsorCupData> getAllSingleCompetitionsSponsorCupDataByTournamentId(Integer tournamentID) {
        List<SingleCompetitionsSponsorCupData> singleCompetitionsSponsorCupDataList = singleCompetitionsSponsorCupDataRepository.getAllSingleCompetitionsSponsorCupDataByTournamentIdPageable(tournamentID);
        return singleCompetitionsSponsorCupDataList;
    }

    public boolean checkIfExistsSingleCompetitionsSponsorCupDataByTournamentId(Integer tournamentID) {
        Integer result = singleCompetitionsSponsorCupDataRepository.checkIfExistsSingleCompetitionsSponsorCupDataByTournamentId(tournamentID);
        if (result != 0) return true;
        return false;
    }

    @Transactional
    public void deleteAllSingleCompetitionsSponsorCupDataByTournamentId(Integer tournamentID) {
        singleCompetitionsSponsorCupDataRepository.deleteAllSingleCompetitionsSponsorCupDataByTournamentId(tournamentID);
    }

    public Integer getScoreByPersonTournamentDataId(Integer personTournamentDataId) {
        Integer score = singleCompetitionsSponsorCupDataRepository.getScoreByPersonTournamentDataId(personTournamentDataId, RideStatus.DS);
        return score == null ? 0 : score;
    }

    public List<SingleCompetitionsSponsorCupData> calculateCorrectedTime(List<SingleCompetitionsSponsorCupData> dataList) {
        if (dataList==null || dataList.isEmpty()) return null;
        Double bestFirstRideTime = dataList.size() > 1 ? dataList.stream().max(Comparator.comparing(v -> v.getFirstRideTime())).get().getFirstRideTime() : dataList.get(0).getFirstRideTime();
        Double bestSecondRideTime = dataList.size() > 1 ? dataList.stream().max(Comparator.comparing(v -> v.getSecondRideTime())).get().getSecondRideTime() : dataList.get(0).getSecondRideTime();

        dataList.forEach(singleCompetitionsSponsorCupData -> {
            singleCompetitionsSponsorCupData.setCorrectedFirstRideTime(calculateCorrectedFirstRideTime(singleCompetitionsSponsorCupData, bestFirstRideTime));
            singleCompetitionsSponsorCupData.setCorrectedSecondRideTime(calculateCorrectedSecondRideTime(singleCompetitionsSponsorCupData, bestSecondRideTime));
            singleCompetitionsSponsorCupData.setSumariseCorrectedRideTime(DoubleUtils.round(singleCompetitionsSponsorCupData.getCorrectedFirstRideTime() +
                    singleCompetitionsSponsorCupData.getCorrectedSecondRideTime(), 2)
            );

        });
        return dataList;
    }

    public List<SingleCompetitionsSponsorCupData> assignTakenPlace(List<SingleCompetitionsSponsorCupData> dataList) {

        dataList.sort((o1, o2) -> o1.getSumariseCorrectedRideTime().compareTo(o2.getSumariseCorrectedRideTime()));
        int takenPlace = 0;
        for (SingleCompetitionsSponsorCupData singleCompetitionsSponsorCupData : dataList) {
            singleCompetitionsSponsorCupData.setTakenPlace(++takenPlace);
        }
        return dataList;
    }

    public List<SingleCompetitionsSponsorCupData> assignScore(List<SingleCompetitionsSponsorCupData> dataList) {

        for (SingleCompetitionsSponsorCupData singleCompetitionsSponsorCupData : dataList) {
            singleCompetitionsSponsorCupData.setScore(ScoreTable.getInstance().get(singleCompetitionsSponsorCupData.getTakenPlace()));
        }
        return dataList;
    }

    private Double calculateCorrectedFirstRideTime(SingleCompetitionsSponsorCupData singleCompetitionsSponsorCupData, Double bestFirstRideTime) {
        Double firstRideTime = singleCompetitionsSponsorCupData.getFirstRideTime().equals(null) ? 0 : singleCompetitionsSponsorCupData.getFirstRideTime();
        LocalDate birthDate = singleCompetitionsSponsorCupData.getPersonTournamentData().getPerson().getDateOfBirth();
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();
        Double D = 4.0688 * Math.log(bestFirstRideTime) + 11.447;
        Double F = (20.34 * Math.log(bestFirstRideTime) - 7.7637) / 10000.0;
        Double correctedTime = firstRideTime - (age - D) * bestFirstRideTime * F;
        return DoubleUtils.round(correctedTime, 2);
    }

    private Double calculateCorrectedSecondRideTime(SingleCompetitionsSponsorCupData singleCompetitionsSponsorCupData, Double bestSecondRideTime) {
        Double secondRideTime = singleCompetitionsSponsorCupData.getSecondRideTime().equals(null) ? 0 : singleCompetitionsSponsorCupData.getSecondRideTime();
        LocalDate birthDate = singleCompetitionsSponsorCupData.getPersonTournamentData().getPerson().getDateOfBirth();
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();
        Double D = 4.0688 * Math.log(bestSecondRideTime) + 11.447;
        Double F = (20.34 * Math.log(bestSecondRideTime) - 7.7637) / 10000.0;
        Double correctedTime = secondRideTime - (age - D) * bestSecondRideTime * F;
        return DoubleUtils.round(correctedTime, 2);
    }


    public List<SingleCompetitionsSponsorCupData> calculate(List<SingleCompetitionsSponsorCupData> dataList) {
        List<SingleCompetitionsSponsorCupData> correctedTimeDataList = calculateCorrectedTime(dataList);
        List<SingleCompetitionsSponsorCupData> assignedTakenPlacesDataList = assignTakenPlace(correctedTimeDataList);
        List<SingleCompetitionsSponsorCupData> assignScoresDataList = assignScore(assignedTakenPlacesDataList);
        return assignScoresDataList;
    }
}
