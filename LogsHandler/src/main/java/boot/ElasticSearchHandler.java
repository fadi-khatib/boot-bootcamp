package boot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import static java.util.Objects.requireNonNull;


@Singleton
public class ElasticSearchHandler {

    private static Logger logger = LogManager.getLogger(ElasticSearchHandler.class);
    private RestHighLevelClient client;

    @Inject
    public ElasticSearchHandler(RestHighLevelClient client) {
        this.client = requireNonNull(client);
    }


    public String search(SearchRequest searchRequest) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search((SearchRequest) searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return e.getMessage();
        }
        return getQueryHits(searchResponse);
    }

    public SearchRequest buildSearchQuery(String index) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        BoolQueryBuilder boolQkuery = new BoolQueryBuilder();
        MatchAllQueryBuilder matchAllQuery = new MatchAllQueryBuilder();
        //boolQuery.
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            boolQuery.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
//        }
        searchSourceBuilder.query(matchAllQuery);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    private String getQueryHits(SearchResponse res) {
        StringBuilder builder = new StringBuilder();
        SearchHits searchHits = res.getHits();
        for (SearchHit hit : searchHits) {
            builder.append(hit.getSourceAsString() + "\n");
        }
        return builder.toString();
    }

}
