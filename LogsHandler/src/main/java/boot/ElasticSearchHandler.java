package boot;

import com.google.gson.Gson;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Singleton
public class ElasticSearchHandler {

    private static Logger logger = LogManager.getLogger(ElasticSearchHandler.class);
    private Msg msg;
    private String index;
    private Map<String, String> map;
    private RestHighLevelClient client;

    @Inject
    public ElasticSearchHandler(RestHighLevelClient client) {
        this.client = client;
    }

    public IndexResponse index() {
        IndexRequest request = new IndexRequest(index, "_doc");
        request.source(new Gson().toJson(map), XContentType.JSON);
        IndexResponse indexResponse = null;
        try {
            indexResponse = client.index((IndexRequest) request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return indexResponse;
    }

    public String search(SearchRequest searchRequest) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search((SearchRequest) searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return e.getMessage();
        }
        SearchHits hits = searchResponse.getHits();
        return getQueryHits(searchResponse);
    }

    public SearchRequest buildSearchQuery(Map<String, String> map, String index) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            boolQuery.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
        }
        searchSourceBuilder.query(boolQuery);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    public String getQueryHits(SearchResponse res) {
        StringBuilder builder = new StringBuilder();
        SearchHits searchHits = res.getHits();
        for (SearchHit hit : searchHits) {
            builder.append(hit.getSourceAsString() + "\n");
        }
        return builder.toString();
    }

    public void setMsg(Msg msg) {
        this.msg = msg;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
