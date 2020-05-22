package ru.ifmo.pharmacies.analysis.web.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.ifmo.pharmacies.analysis.web.model.MedicineApiResponse;
import ru.ifmo.pharmacies.analysis.web.model.PharmaciesApiRequest;
import ru.ifmo.pharmacies.analysis.web.model.PharmaciesApiResponse;
import ru.ifmo.pharmacies.analysis.web.service.SearchService;

@RestController
@RequestMapping(path = "/api")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private Gson gson;

    @RequestMapping(
            path = "/medicines",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getFormsList(@RequestParam("name") String name) {

        MedicineApiResponse response = searchService.searchMedicines(name);

        return gson.toJson(response);
    }

    @RequestMapping(
            path = "/pharmacies",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getPharmaciesList(@RequestBody PharmaciesApiRequest apiRequest) {

        PharmaciesApiResponse response = searchService.searchPharmacies(apiRequest);

        return gson.toJson(response);
    }

}
