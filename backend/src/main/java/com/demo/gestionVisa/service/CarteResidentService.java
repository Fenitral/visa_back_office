package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.CarteResident;
import com.demo.gestionVisa.repository.CarteResidentRepository;
import org.springframework.stereotype.Service;

@Service
public class CarteResidentService {

    private final CarteResidentRepository carteResidentRepository;

    public CarteResidentService(CarteResidentRepository carteResidentRepository) {
        this.carteResidentRepository = carteResidentRepository;
    }
}
