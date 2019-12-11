package bootTest;
import boot.Msg;

import javax.inject.Inject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



public class HttpRequestHandler {
    private Client client;
    private Msg msg;
    private String endPoint ;
    private String path;

    @Inject
    public HttpRequestHandler(Client client ,Msg msg ,String endPoint ){
        this.client = client;
        this.msg = msg;
        this.endPoint = endPoint;
        this.path = "";
    }

    public Response postRequest() {
        String REST_URI = endPoint;
        Invocation.Builder request = client.target(REST_URI).path(path).request().header(HttpHeaders.USER_AGENT,"Mozilla/5.0 ( Macintosh ; Intel Mac OS X)");
        return  request.accept(MediaType.TEXT_PLAIN).post(Entity.entity(msg, MediaType.APPLICATION_JSON));
    }

    public Response getRequest() {
        String REST_URI = endPoint + path;
        Invocation.Builder request = client.target(REST_URI).request(MediaType.APPLICATION_JSON);
        return  request.accept(MediaType.TEXT_PLAIN).get();
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
    public void setMsg(Msg msg) {
        this.msg = msg ;
    }
    public String getEndPoint(String endPoint) {
        return this.endPoint;
    }
    public String getMsg(String  msg) {
        return this.msg.getMessage();
    }
    public void setPath(String path){
       this.path = path;
    }
    public String getPath(String path){
        return this.path;
    }
}
