package archit.fastsms.DatabseManager;

public class Data {
    private String Message = "";
    private String Phone = "";
    private String type = "1";
    // TODO add new user status

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
