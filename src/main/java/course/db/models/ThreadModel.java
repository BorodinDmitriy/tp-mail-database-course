package course.db.models;

import course.db.views.ThreadView;

public class ThreadModel {
    private Integer votes;
    private Integer id;
    private String  title;
    private String author;
    private String message;
    private String created;
    private String forum;
    private String slug;

    public ThreadModel() {}

    public ThreadModel(ThreadView threadView) {
        this.author = threadView.getAuthor();
        this.created = threadView.getCreated();
        this.forum = threadView.getForum();
        this.id = threadView.getId();
        this.title = threadView.getTitle();
        this.message = threadView.getMessage();
        this.votes = threadView.getVotes();
        this.slug = threadView.getSlug();
    }

    public ThreadModel(Integer votes, Integer id, String title, String author, String message, String created, String forum, String slug) {
        this.votes = votes;
        this.id = id;
        this.title = title;
        this.author = author;
        this.message = message;
        this.created = created;
        this.forum = forum;
        this.slug = slug;
    }

    public void copy(ThreadModel other) {
        this.setAuthor(other.getAuthor());
        this.setCreated(other.getCreated());
        this.setId(other.getId());
        this.setForum(other.getForum());
        this.setSlug(other.getSlug());
        this.setMessage(other.getMessage());
        this.setTitle(other.getTitle());
        this.setVotes(other.getVotes());
    }

    public ThreadView toView() {
        return new ThreadView(title, author, message, created, id, forum, slug, votes);
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
