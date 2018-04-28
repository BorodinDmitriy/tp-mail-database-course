package course.db.managers;

public class StatusManagerRequest {
    private ManagerResponseCodes code;
    private String message;

    public StatusManagerRequest() {}

    public StatusManagerRequest(ManagerResponseCodes code) {
        this.code = code;
    }

    public StatusManagerRequest(ManagerResponseCodes code, Exception ex) {
        this.code = code;
        this.message = ex.getMessage();
    }

    public StatusManagerRequest(ManagerResponseCodes code, String message) {
        this.code = code;
        this.message = message;
    }

    public ManagerResponseCodes getCode() {return this.code;}

    public String getMessage() {
        return this.message;
    }

}
