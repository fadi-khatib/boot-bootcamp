package boot;
import javax.inject.Inject;

public class Msg {
    //private int id;
    public String message;
    //@Inject
    public Msg() {
        //this.id = id;
        message = "boot camp first index";
    }
    @Inject
    public Msg(String message) {
        //this.id = id;
        this.message = message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
}