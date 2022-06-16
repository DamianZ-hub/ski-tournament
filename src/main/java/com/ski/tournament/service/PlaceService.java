package com.ski.tournament.service;

import com.ski.tournament.model.Place;
import com.ski.tournament.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PlaceService extends CrudService<Place,Integer> {

    private final PlaceRepository placeRepository;

    @Autowired
    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<Place> getPlaces() {
        List<Place> places = placeRepository.findAll();
        return places;
    }

    @Transactional
    public Place addPlace(Place place) {

        placeRepository.save(place);
        return place;
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void addTestData(){

    }

    @Override
    protected JpaRepository<Place, Integer> getRepository() {
        return placeRepository;
    }
}
