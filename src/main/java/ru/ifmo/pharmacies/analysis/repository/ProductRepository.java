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
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ifmo.pharmacies.analysis.domain.Product;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ProductRepository {

    private String productsIndex = "test_products5";

    public static Date lastUpdateDate;

    private RestHighLevelClient client;

    @Autowired
    private Gson gson;

    private ProductRepository(RestHighLevelClient client) {
        this.client = client;
        lastUpdateDate = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L);
        initialize();
    }

    private void initialize() {

        try {
            if (!client.indices().exists(new GetIndexRequest(productsIndex), RequestOptions.DEFAULT)){
                setUpIndex();
            }
        } catch (Exception e) {
            System.out.println("Error! Cannot initialize ProductRepository.");
            e.printStackTrace();
        }
    }

    private void setUpIndex() {

        try {
            if (client.indices().exists(new GetIndexRequest(productsIndex), RequestOptions.DEFAULT)) {
                client.indices().delete(new DeleteIndexRequest(productsIndex), RequestOptions.DEFAULT);
                System.out.println("Index '" + productsIndex + "' deleted.");
            }

            CreateIndexRequest request = new CreateIndexRequest(productsIndex);

            String mappingString =
                "{\n" +
                "  \"properties\": {\n" +
                "    \"name\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"price\": {\n" +
                "      \"type\": \"float\"\n" +
                "    },\n" +
                "    \"pharmacyId\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"coordinates\": {\n" +
                "      \"type\": \"geo_point\"\n" +
                "    },\n" +
                "    \"lastupdate\": {\n" +
                "      \"type\": \"date\",\n" +
                "      \"format\": \"yyyy-MM-dd HH:mm:ss\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

            request.mapping("_doc", mappingString, XContentType.JSON);

            CreateIndexResponse createIndexResponse =  client.indices().create(request, RequestOptions.DEFAULT);

        } catch(Exception e){
            System.out.println("Error! Cannot set up index '" + productsIndex + "'.");
            e.printStackTrace();
        }
    }

    public void add(Product document) {

        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            String json = gson.toJson(document);
            IndexRequest indexRequest = new IndexRequest(productsIndex, "_doc", document.getId());
            indexRequest.source(json, XContentType.JSON);

            UpdateRequest updateRequest = new UpdateRequest(productsIndex, "_doc", document.getId())
                    .doc(json, XContentType.JSON)
                    .upsert(indexRequest);
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch(Exception e) {
            System.out.println("Error! Cannot add document " + document.getId() + " to index '" + productsIndex + "'.");
            e.printStackTrace();
        }
    }

    public Product find(String fullname, double lat, double lon, double distanceInMeters) {

        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            QueryBuilder geoDistanceQueryBuilderqueryBuilder =
                    QueryBuilders.geoDistanceQuery("coordinates")
                            .distance(distanceInMeters, DistanceUnit.METERS)
                            .geoDistance(GeoDistance.ARC)
                            .point(lat, lon);

            QueryBuilder nameQueryBuilder = QueryBuilders.matchQuery("name", fullname);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            QueryBuilder dateQueryBuilder = QueryBuilders.rangeQuery("lastupdate").gte(dateFormat.format(lastUpdateDate));

            QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(nameQueryBuilder).filter(geoDistanceQueryBuilderqueryBuilder).filter(dateQueryBuilder);

            sourceBuilder.query(queryBuilder).size(1);

            SortBuilder geoDistanceSortBuilder = SortBuilders.geoDistanceSort("coordinates", lat, lon)
                    .order(SortOrder.ASC)
                    .unit(DistanceUnit.METERS)
                    .geoDistance(GeoDistance.ARC);

            SortBuilder priceSortBuilder = SortBuilders.fieldSort("price").order(SortOrder.ASC);

            SearchRequest searchRequest = new SearchRequest(productsIndex)
                    .source(sourceBuilder.sort(priceSortBuilder).sort(geoDistanceSortBuilder));

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHit[] searchHits = searchResponse.getHits().getHits();

            if (searchHits != null && searchHits.length != 0) {
                return gson.fromJson(searchHits[0].getSourceAsString(), Product.class);
            }

        } catch(Exception e) {
            System.out.println("Error! Cannot find document in index '" + productsIndex + "'.");
            e.printStackTrace();
        }

        return null;
    }

    public Product find(String fullname) {

        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            QueryBuilder nameQueryBuilder = QueryBuilders.matchQuery("name", fullname);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            QueryBuilder dateQueryBuilder = QueryBuilders.rangeQuery("lastupdate").gte(dateFormat.format(lastUpdateDate));

            QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(nameQueryBuilder).filter(dateQueryBuilder);

            sourceBuilder.query(queryBuilder).size(1);

            SortBuilder priceSortBuilder = SortBuilders.fieldSort("price").order(SortOrder.ASC);

            SearchRequest searchRequest = new SearchRequest(productsIndex)
                    .source(sourceBuilder.sort(priceSortBuilder));

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHit[] searchHits = searchResponse.getHits().getHits();

            if (searchHits != null && searchHits.length != 0) {
                return gson.fromJson(searchHits[0].getSourceAsString(), Product.class);
            }

        } catch(Exception e) {
            System.out.println("Error! Cannot find document in index '" + productsIndex + "'.");
            e.printStackTrace();
        }

        return null;
    }
}
