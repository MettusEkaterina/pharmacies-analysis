package ru.ifmo.pharmacies.analysis.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ifmo.pharmacies.analysis.domain.Coordinates;
import ru.ifmo.pharmacies.analysis.domain.Medicine;
import ru.ifmo.pharmacies.analysis.domain.Pharmacy;
import ru.ifmo.pharmacies.analysis.domain.Product;
import ru.ifmo.pharmacies.analysis.repository.MedicineRepository;
import ru.ifmo.pharmacies.analysis.repository.PharmacyRepository;
import ru.ifmo.pharmacies.analysis.repository.ProductRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MedicinesParser {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    PharmacyParser pharmacyParser;

    private static String baseUrl = "siteUrl";

    public void parse() {
        try {
            Document mainPage = Jsoup.connect(baseUrl).get();

            List<String> alphabetLinks = getAlphabetLinks(mainPage);

            for (var alphabetLink : alphabetLinks) {
                System.out.println("GO TO " + baseUrl + alphabetLink);
                Document medicineListPage = Jsoup.connect(baseUrl + alphabetLink).get();
                parseMedicinesList(medicineListPage);
            }


        } catch (IOException ioex) {

        }
    }

    private void parseMedicinePage(String link) {
        try {
            System.out.println("GO TO " + link);
            Document medicinePage = Jsoup.connect(link).get();

            String name = medicinePage.select("h1.drug").get(0).ownText();

            String currMedicineLink = medicinePage.select("form#SearchForm").attr("action");

            if (currMedicineLink.contains("trade")) {
                Elements similars = medicinePage.select("div.trades").select("option");

                for (int i = 1; i < similars.size(); i++) {
                    //String similarMedicine = similars.get(i).ownText();

                    String similarLink = similars.get(i).attr("value");

                    parseMedicinePage(baseUrl + similarLink);
                }
            }
            else {
                Elements formsElements = medicinePage.select("div.sel-form").select("option");

                String[] temp = currMedicineLink.split("/");
                String id = temp[temp.length - 1];

                List<String> forms = new ArrayList<>();

                if (formsElements != null && formsElements.size() != 0) {
                    for (int i = 2; i < formsElements.size(); i++) {
                        String form = name.toLowerCase() + " " + formsElements.get(i).ownText();
                        forms.add(form);
                    }

                    Medicine medicine = new Medicine(id, name, forms);
                    medicineRepository.add(medicine);

                    parseProducts(medicinePage, baseUrl + currMedicineLink);
                }
                else {
                    Elements table = medicinePage.select("div.table");

                    if (table != null && table.size() != 0) {
                        Element product = table.select("div.trow").not("div.thead").get(0);
                        String productName = product.select("div.name").select("p").text();
                        forms.add(productName);

                        Medicine medicine = new Medicine(id, name, forms);
                        medicineRepository.add(medicine);

                        parseProducts(medicinePage, baseUrl + currMedicineLink);
                    }
                }
            }

        } catch (IOException ioex) {

        }
    }

    private void parseProductsPage(Document page) {
        Elements products = page.select("div.table").select("div.trow").not("div.thead");

        for (var element : products) {
            Elements productElement = element.select("div.name").select("p");

            String productName = productElement.text();
            String productPriceString = element.select("div.pricefull").text();
            double productPrice = Double.parseDouble(productPriceString);
            String id = productElement.attr("onclick").replaceAll("[^\\d.]", "");

            Elements pharmacyElement = element.select("div.pharm");
            String pharmacyLink = pharmacyElement.get(0).select("a").attr("href");
            String[] temp = pharmacyLink.split("=");
            String pharmacyId = temp[1].split("&")[0];

            Elements addressElement = element.select("div.address");
            addressElement.select("span").remove();
            String mapLink = addressElement.get(0).select("a").attr("href");
            String[] coordinates = mapLink.substring(mapLink.lastIndexOf("=") + 1).split(",");
            double latitude = Double.parseDouble(coordinates[0]);
            double longitude = Double.parseDouble(coordinates[1]);
            Coordinates coordinate = new Coordinates(latitude, longitude);

            Product product = new Product(id, productName, productPrice, pharmacyId, coordinate, new Date());
            productRepository.add(product);

            if (!pharmacyRepository.exists(pharmacyId)) {
                Pharmacy pharmacy = pharmacyParser.buildPharmacy(pharmacyId);
                pharmacyRepository.add(pharmacy);
            }
        }
    }

    private void parseProducts(Document page, String link) {
        String productsCountMessage = page.select("p.red").get(0).ownText();
        productsCountMessage = productsCountMessage.replaceAll("[^\\d]", "");

        if (!productsCountMessage.equals("")) {
            parseProductsPage(page);

            int productsCount = Integer.parseInt(productsCountMessage);
            int pagesCount = (int) Math.ceil(productsCount / 50.0);

            for (int i = 2; i <= pagesCount; i++) {
                try {
                    System.out.println("GO TO " + link + "?page=" + i);
                    Document subpage = Jsoup.connect(link + "?page=" + i).get();
                    parseProductsPage(subpage);
                } catch (IOException ioex) {

                }
            }
        }

    }

    private void parseMedicinesList(Document page) {
        Elements medicinePagesElements = page.select("td");

        for (var element : medicinePagesElements) {
            String medicinePageLink = element.select("a").attr("href");
            parseMedicinePage(baseUrl + "/" + medicinePageLink);
        }
    }

    private List<String> getAlphabetLinks(Document page) {
        List<String> alphabetLinks = new ArrayList<>();
        Elements alphabetPagesElements = page.select("ul.alphabet").select("li");

        for (var element : alphabetPagesElements) {
            alphabetLinks.add("/" + element.select("a").attr("href").replaceFirst("#page-top", ""));
        }

        return alphabetLinks;
    }
}
