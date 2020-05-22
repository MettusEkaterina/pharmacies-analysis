package ru.ifmo.pharmacies.analysis.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.ifmo.pharmacies.analysis.domain.Contact;
import ru.ifmo.pharmacies.analysis.domain.Pharmacy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PharmacyParser {

    static String pharmacyUrl = "siteUrl";

    public Pharmacy buildPharmacy(String pharmacyId) {
        try {
            Document page = Jsoup.connect(pharmacyUrl + pharmacyId).get();

            String pharmacyName = page.select("h1.box__ttl").first().ownText();

            Elements pharmacyInformation = page.select("div.apt__info--row");

            String pharmacyAddress = null,
                    telephone = null,
                    district = null,
                    subway = null,
                    workHours = null;

            for (var pharmacyInfo : pharmacyInformation) {
                Elements left = pharmacyInfo.select("div.apt__info--coll__left");
                Elements right = pharmacyInfo.select("div.apt__info--coll").not("div.apt__info--coll__left");

                if (right.size() != 0) {
                    String parameter = left.get(0).ownText();

                    switch (parameter) {
                        case "Адрес:" :
                            pharmacyAddress = right.select("a").select("span").text();
                            break;
                        case "Телефон:" :
                            telephone = right.select("a").select("span").text();
                            break;
                        case "Район:" :
                            district = right.text();
                            break;
                        case "Метро:" :
                            subway = right.text();
                            break;
                        case "ЧасыРаб:" :
                            workHours = right.text();
                            break;
                    }
                }
            }

            /*
            String lastUpdateMessage = page.select("div.time__update").first().ownText();
            String lastUpdate = lastUpdateMessage.substring(lastUpdateMessage.lastIndexOf(":") + 2);

            Date lastUpdateDate;
            try {
                lastUpdateDate = new SimpleDateFormat("dd.MM.yy").parse(lastUpdate);
            }
            catch (ParseException parseException) {
                lastUpdateDate = new Date();
            }
            */

            List<Contact> contacts = new ArrayList<>();

            if (telephone != null) {

                String[] numbers = telephone.split("; ");

                for (var number : numbers) {
                    String[] parts = number.split(" доб.");
                    Contact contact = new Contact(parts[0]);

                    if (parts.length > 1) {
                        contact.setExtra(parts[1]);
                    }

                    contacts.add(contact);
                }
            }


            return new Pharmacy(
                    pharmacyId,
                    pharmacyName,
                    pharmacyAddress,
                    contacts,
                    district,
                    subway,
                    workHours,
                    new Date()
            );
        }
        catch (IOException ioex) {
            return null;
        }
    }

}
