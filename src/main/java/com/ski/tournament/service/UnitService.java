package com.ski.tournament.service;

import com.ski.tournament.model.Unit;
import com.ski.tournament.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UnitService extends CrudService<Unit,Integer> {

    private final UnitRepository unitRepository;

    @Autowired
    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public List<Unit> getUnits() {
        List<Unit> units = unitRepository.findAll();
        return units;
    }

    public Unit getUnitByFullName(String fullName) {
        Unit unit = unitRepository.findByfullName(fullName);
        return unit;
    }

    @Transactional
    public Unit addUnit(Unit unit) {

        unitRepository.save(unit);
        return unit;
    }


    @Override
    protected JpaRepository<Unit, Integer> getRepository() {
        return unitRepository;
    }
}
