package boot;

public class Msg {
    public String message;
    public Msg() {
        message = "boot camp first index";
    }

    public Msg(String message) {
        this.message = message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
}