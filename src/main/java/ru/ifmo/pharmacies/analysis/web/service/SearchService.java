package ru.ifmo.pharmacies.analysis.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ifmo.pharmacies.analysis.domain.Medicine;
import ru.ifmo.pharmacies.analysis.domain.Pharmacy;
import ru.ifmo.pharmacies.analysis.domain.Product;
import ru.ifmo.pharmacies.analysis.repository.MedicineRepository;
import ru.ifmo.pharmacies.analysis.repository.PharmacyRepository;
import ru.ifmo.pharmacies.analysis.repository.ProductRepository;
import ru.ifmo.pharmacies.analysis.web.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    public MedicineApiResponse searchMedicines(String name) {
        List<Medicine> medicineList = medicineRepository.find(name);

        List<String> forms = new ArrayList<>();

        for (Medicine medicine : medicineList) {
            forms.addAll(medicine.getForms());
        }

        return new MedicineApiResponse(forms);
    }

    public PharmaciesApiResponse searchPharmacies(PharmaciesApiRequest apiRequest) {
        double lat = apiRequest.getLat();
        double lon = apiRequest.getLon();
        double distance = apiRequest.getDistance();
        boolean isLocationEmpty = lat == 0 && lon == 0;


        List<Product> found = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        for (String medicine : apiRequest.getMedicines()) {

            Product product;

            if (isLocationEmpty) {
                product = productRepository.find(medicine);
            }
            else {
                product = productRepository.find(medicine, lat, lon, distance);
            }

            if (product != null) {
                found.add(product);
            } else {
                notFound.add(medicine);
            }
        }

        Map<String, PharmacyDTO> pharmacyDTOMap = new HashMap<>();

        for (Product product : found) {
            String pharmacyId = product.getPharmacyId();
            Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId);

            if (pharmacyDTOMap.containsKey(pharmacyId)) {
                PharmacyDTO pharmacyDTO = pharmacyDTOMap.get(pharmacyId);
                List<ProductDTO> medicines = pharmacyDTO.getMedicines();
                medicines.add(new ProductDTO(product.getName(), product.getPrice()));
                pharmacyDTOMap.replace(pharmacyId, new PharmacyDTO(pharmacy.getName(), pharmacy.getAddress(), pharmacy.getContacts(), product.getCoordinates(), pharmacy.getWorkHours(), medicines));
            }
            else {
                List<ProductDTO> medicines = new ArrayList<>();
                medicines.add(new ProductDTO(product.getName(), product.getPrice()));
                pharmacyDTOMap.put(pharmacyId, new PharmacyDTO(pharmacy.getName(), pharmacy.getAddress(), pharmacy.getContacts(), product.getCoordinates(), pharmacy.getWorkHours(), medicines));
            }
        }

        List<PharmacyDTO> pharmacies = new ArrayList<>(pharmacyDTOMap.values());

        return new PharmaciesApiResponse(pharmacies, notFound);
    }
}
