package ru.ifmo.pharmacies.analysis.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.stereotype.Component;
import ru.ifmo.pharmacies.analysis.domain.Contact;
import ru.ifmo.pharmacies.analysis.domain.Pharmacy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PharmacyRepository {

    private String pharmaciesIndex = "test_pharmacies5";

    private RestHighLevelClient client;

    private PharmacyRepository(RestHighLevelClient client) {
        this.client = client;
        initialize();
    }

    private void initialize() {

        try {
            if (!client.indices().exists(new GetIndexRequest(pharmaciesIndex), RequestOptions.DEFAULT)) {
                setUpIndex();
            }
        } catch (Exception e) {
            System.out.println("Error! Cannot initialize PharmacyRepository.");
            e.printStackTrace();
        }
    }

    private void setUpIndex() {

        try {
            if (client.indices().exists(new GetIndexRequest(pharmaciesIndex), RequestOptions.DEFAULT)) {
                client.indices().delete(new DeleteIndexRequest(pharmaciesIndex), RequestOptions.DEFAULT);
                System.out.println("Index '" + pharmaciesIndex + "' deleted.");
            }

            CreateIndexRequest request = new CreateIndexRequest(pharmaciesIndex);

            String mappingString =
                "{\n" +
                "  \"properties\": {\n" +
                "    \"name\": {\n" +
                "      \"type\": \"text\"\n" +
                "    },\n" +
                "    \"address\": {\n" +
                "      \"type\": \"text\"\n" +
                "    },\n" +
                "    \"contacts\": {" +
                "      \"properties\": {\n" +
                "        \"telephone\": {\n" +
                "          \"type\": \"text\"\n" +
                "        },\n" +
                "        \"extra\": {\n" +
                "          \"type\": \"text\"\n" +
                "        }" +
                "      }\n" +
                "    },\n" +
                "    \"district\": {\n" +
                "      \"type\": \"text\"\n" +
                "    },\n" +
                "    \"subway\": {\n" +
                "      \"type\": \"text\"\n" +
                "    },\n" +
                "    \"workHours\": {\n" +
                "      \"type\": \"text\"\n" +
                "    },\n" +
                "    \"lastupdate\": {\n" +
                "      \"type\": \"date\",\n" +
                "      \"format\": \"yyyy-MM-dd HH:mm:ss\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

            request.mapping("_doc", mappingString, XContentType.JSON);

            CreateIndexResponse createIndexResponse =  client.indices().create(request, RequestOptions.DEFAULT);

        } catch(Exception e) {
            System.out.println("Error! Cannot set up index '" + pharmaciesIndex + "'.");
            e.printStackTrace();
        }
    }

    public void add(Pharmacy document) {

        try {
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(document);
            IndexRequest indexRequest = new IndexRequest(pharmaciesIndex, "_doc", document.getId());
            indexRequest.source(json, XContentType.JSON);

            UpdateRequest updateRequest = new UpdateRequest(pharmaciesIndex, "_doc", document.getId())
                    .doc(json, XContentType.JSON)
                    .upsert(indexRequest);
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch(Exception e) {
            System.out.println("Error! Cannot add document " + document.getId() + " to index '" + pharmaciesIndex + "'.");
            e.printStackTrace();
        }
    }

    public Pharmacy findById(String id) {

        try {
            GetRequest getRequest = new GetRequest(pharmaciesIndex, "_doc", id);
            GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);

            if (response.isExists()) {
                return decodePharmacy(id, response.getSource());
            }

            return null;
        } catch(Exception e) {
            System.out.println("Error! Cannot find document " + id + " in index '" + pharmaciesIndex + "'.");
            e.printStackTrace();
            return null;
        }
    }

    public boolean exists(String id) {

        try {
            GetRequest getRequest = new GetRequest(pharmaciesIndex, "_doc", id);
            getRequest.fetchSourceContext(new FetchSourceContext(false));
            getRequest.storedFields("_none_");
            return client.exists(getRequest, RequestOptions.DEFAULT);
        } catch(Exception e) {
            System.out.println("Error! Cannot find document " + id + " in index '" + pharmaciesIndex + "'.");
            e.printStackTrace();
            return false;
        }
    }

    private Pharmacy decodePharmacy(String id, Map<String, Object> source) {

        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(id);

        if (source != null) {
             if (source.containsKey("name")) {
                 pharmacy.setName((String)source.get("name"));
             }

            if (source.containsKey("address")) {
                pharmacy.setAddress((String)source.get("address"));
            }

            if (source.containsKey("contacts")) {
                pharmacy.setContacts((List<Contact>)source.get("contacts"));
            }

            if (source.containsKey("district")) {
                pharmacy.setDistrict((String)source.get("district"));
            }

            if (source.containsKey("subway")) {
                pharmacy.setSubway((String)source.get("subway"));
            }

            if (source.containsKey("workHours")) {
                pharmacy.setWorkHours((String)source.get("workHours"));
            }

            if (source.containsKey("lastUpdate")) {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss a");
                try {
                    pharmacy.setLastUpdate(formatter.parse((String) source.get("lastUpdate")));
                } catch (ParseException ex) {
                    pharmacy.setLastUpdate(new Date());
                }
            }
        }

        return pharmacy;
    }
}
