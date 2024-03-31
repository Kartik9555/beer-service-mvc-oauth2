package com.example.practice.beerservicemvc.service;

import com.example.practice.beerservicemvc.model.BeerCSVRecord;
import java.io.File;
import java.util.List;

public interface BeerCsvService {
    List<BeerCSVRecord> convertCSV(File csvFile);
}
