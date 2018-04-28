package course.db.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ForumView implements AbstractView {
    private String title;
    private String user;
    private String slug;
    private Integer posts;
    private Integer threads;

    @JsonCreator
    public ForumView(@JsonProperty("title") String title, @JsonProperty("user") String user,
                     @JsonProperty("slug") String slug) {
        this.title = title;
        this.user = user;
        this.slug = slug;
    }

    public ForumView() {}
    public ForumView(String title, String user, String slug, Integer posts, Integer threads){
        this.title = title;
        this.user = user;
        this.slug = slug;
        this.posts = posts;
        this.threads = threads;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getPosts() {
        return posts;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }
}
