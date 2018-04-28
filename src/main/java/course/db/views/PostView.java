package course.db.views;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PostView implements AbstractView {
    private Integer id;
    private Integer parent;
    private String author;
    private String message;
    private Boolean isEdited;
    private String forum;
    private Integer thread;
    private String created;

    public PostView(){

    }

    public PostView(@JsonProperty("id") Integer id,
                    @JsonProperty("parent") Integer parent,
                    @JsonProperty("author") String author,
                    @JsonProperty("message") String message,
                    @JsonProperty("isEdited") Boolean isEdited,
                    @JsonProperty("forum") String forum,
                    @JsonProperty("thread") Integer thread,
                    @JsonProperty("created") String created) {
        this.id = id;
        if (parent != null)
            this.parent = parent;
        else
            this.parent = 0;
        this.author = author;
        this.message = message;
        this.isEdited = isEdited;
        this.forum = forum;
        this.thread = thread;
        this.created = created;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(Boolean edited) {
        isEdited = edited;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public Integer getThread() {
        return thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
