package com.ski.tournament.repository;

import com.ski.tournament.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place,Integer>{
}
