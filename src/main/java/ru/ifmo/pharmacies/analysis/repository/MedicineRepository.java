package ru.ifmo.pharmacies.analysis.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;
import ru.ifmo.pharmacies.analysis.domain.Medicine;

import java.util.ArrayList;
import java.util.List;

@Component
public class MedicineRepository {

    private String medicinesIndex = "test_medicines5";

    private RestHighLevelClient client;

    private MedicineRepository(RestHighLevelClient client) {
        this.client = client;
        initialize();
    }

    private void initialize() {

        try {
            if (!client.indices().exists(new GetIndexRequest(medicinesIndex), RequestOptions.DEFAULT)){
                setUpIndex();
            }
        } catch (Exception e) {
            System.out.println("Error! Cannot initialize MedicineRepository.");
            e.printStackTrace();
        }
    }

    private void setUpIndex() {

        try {
            if (client.indices().exists(new GetIndexRequest(medicinesIndex), RequestOptions.DEFAULT)) {
                client.indices().delete(new DeleteIndexRequest(medicinesIndex), RequestOptions.DEFAULT);
                System.out.println("Index '" + medicinesIndex + "' deleted.");
            }

            CreateIndexResponse createIndexResponse =  client.indices().create(new CreateIndexRequest(medicinesIndex), RequestOptions.DEFAULT);

        } catch(Exception e) {
            System.out.println("Error! Cannot set up index '" + medicinesIndex + "'.");
            e.printStackTrace();
        }
    }

    public void add(Medicine document) {

        try {
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(document);
            IndexRequest indexRequest = new IndexRequest(medicinesIndex, "_doc", document.getId());
            indexRequest.source(json, XContentType.JSON);

            UpdateRequest updateRequest = new UpdateRequest(medicinesIndex, "_doc", document.getId())
                    .doc(json, XContentType.JSON)
                    .upsert(indexRequest);
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch(Exception e) {
            System.out.println("Error! Cannot add document " + document.getId() + " to index '" + medicinesIndex + "'.");
            e.printStackTrace();
        }
    }

    public List<Medicine> find(String name) {

        List<Medicine> results = new ArrayList<>();

        QueryBuilder nameQueryBuilder = QueryBuilders.matchQuery("name", name);

        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(nameQueryBuilder);
        searchRequest.indices(medicinesIndex);
        searchRequest.types("_doc");
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHit[] searchHits = searchResponse.getHits().getHits();
            for (SearchHit searchHit : searchHits) {
                Medicine medicine = new GsonBuilder().create().fromJson(searchHit.getSourceAsString(), Medicine.class);
                results.add(medicine);
            }
        }
        catch (Exception ignored) {}

        return results;
    }
}
