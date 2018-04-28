package course.db.views;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThreadView implements AbstractView {
    private Integer votes;
    private Integer id;
    private String  title;
    private String author;
    private String message;
    private String created;
    private String forum;
    private String slug;

    @JsonCreator
    public ThreadView(@JsonProperty("title") String title,
                      @JsonProperty("author") String author,
                      @JsonProperty("message") String message,
                      @JsonProperty("created") String created) {
        this.message = message;
        this.author = author;
        this.created = created;
        this.title = title;
    }

    public ThreadView(String title, String author, String message, String created, Integer id, String forum, String slug, Integer votes) {
        this.title = title;
        this.author = author;
        this.message = message;
        this.created = created;
        this.id = id;
        this.forum = forum;
        this.slug = slug;
        this.votes = votes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }
}
