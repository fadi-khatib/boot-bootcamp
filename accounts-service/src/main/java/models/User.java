package models;

public class User {
    private Integer id;
    private String name;
    private String token;
    private String esIndexName;
    public User(){

    }
    public User( Integer id, String name, String token, String esIndexName ) {
        this.name = name;
        this.token = token;
        this.esIndexName = esIndexName;
        this.id = id;
    }
    public User(String name, String token, String esIndexName) {
        this.name = name;
        this.token = token;
        this.esIndexName = esIndexName;
    }

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getToken() {
        return token;
    }
    public String getEsIndexName() { return esIndexName; }

    public void steId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setToken(String Token) { this.token = token; }
    public void setEsIndexName(String esIndexName) { this.esIndexName = esIndexName; }
}
