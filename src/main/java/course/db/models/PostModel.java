package course.db.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import course.db.views.PostView;

public class PostModel {
    private Integer id;
    private Integer parent;
    private String author;
    private String message;
    @JsonProperty("isEdited")
    private Boolean isEdited;
    private String forum;
    private Integer thread;
    private String created;

    public PostModel() {}

    public PostModel(Integer id, Integer parent, String author, String message, Boolean isEdited, String forum, Integer thread, String created) {
        this.id = id;
        this.parent = parent;
        this.author = author;
        this.message = message;
        this.isEdited = isEdited;
        this.forum = forum;
        this.thread = thread;
        this.created = created;
    }

    public PostModel(PostView postView) {
        this.setAuthor(postView.getAuthor());
        this.setCreated(postView.getCreated());
        this.setId(postView.getId());
        this.setForum(postView.getForum());
        this.setEdited(postView.getIsEdited());
        this.setMessage(postView.getMessage());
        this.setParent(postView.getParent());
        this.setThread(postView.getThread());
    }

    public void copy(PostModel other) {
        this.setAuthor(other.getAuthor());
        this.setCreated(other.getCreated());
        this.setId(other.getId());
        this.setForum(other.getForum());
        this.setEdited(other.getIsEdited());
        this.setMessage(other.getMessage());
        this.setParent(other.getParent());
        this.setThread(other.getThread());
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

    public void setEdited(Boolean edited) {
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

    public PostView toView() {
        return new PostView(id, parent, author, message, isEdited, forum, thread, created);
    }
}
