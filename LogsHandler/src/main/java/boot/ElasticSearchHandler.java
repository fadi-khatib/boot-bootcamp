package boot;



import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
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

@Singleton
public class ElasticSearchHandler {

    private Msg msg;
    private String index;
    private Map<String,String> map;


    private  final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost(ServerConfiguration.elasticHost, ServerConfiguration.elasticPort, "http"),
                    new HttpHost(ServerConfiguration.elasticHost, ServerConfiguration.additionalElasticPort, "http")));
    @Inject
    public ElasticSearchHandler (){
        this.msg = new Msg("defult bootcamp message");
        this.index = "defult";
    }

    public IndexResponse index(){
        IndexRequest request = new IndexRequest(index);

        request.source(new Gson().toJson(map) ,XContentType.JSON);
        IndexResponse indexResponse = null ;
        try{
            indexResponse= client.index((IndexRequest) request, RequestOptions.DEFAULT);
            System.out.println("elastic searchhandler.index:  "+indexResponse.status().toString());

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return indexResponse;
    }

    public String search(SearchRequest searchRequest){
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search((SearchRequest) searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
        }catch(Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return getQueryHits(searchResponse);
    }

    public SearchRequest buildSearchQuery(Map<String, String> map , String index ){
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()){
            boolQuery.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
        }
        searchSourceBuilder.query(boolQuery);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }
    public String getQueryHits(SearchResponse res ){
        StringBuilder builder = new StringBuilder();
        try {
            SearchHits searchHits = res.getHits();

            for (SearchHit hit : searchHits) {
                builder.append(hit.getSourceAsString() + "\n");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return builder.toString();
    }

    public void setMsg(Msg msg){
        this.msg = msg;
    }

    public void setIndex(String index){
        this.index = index;
    }
    public void setMap(Map map){
        this.map = map;
    }
}
