package archit.fastsms.DatabseManager;

public class Data {
    private String Message = "Hey There!";
    private String Phone = "9891192474";
    private String type = "1";

    public Data(String message, String phone, String type) {
        Message = message;
        Phone = phone;
        this.type = type;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
