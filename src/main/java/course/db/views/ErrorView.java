package course.db.views;


public class ErrorView implements AbstractView {
    private String message;

    public ErrorView(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}