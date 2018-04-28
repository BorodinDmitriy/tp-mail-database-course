package course.db.views;

public class PostDetailsView implements AbstractView {
    UserProfileView author;
    PostView post;
    ThreadView thread;
    ForumView forum;

    public PostDetailsView(UserProfileView author, PostView post, ThreadView thread, ForumView forum) {
        this.author = author;
        this.post = post;
        this.thread = thread;
        this.forum = forum;
    }

    public UserProfileView getAuthor() {
        return author;
    }

    public void setAuthor(UserProfileView author) {
        this.author = author;
    }

    public PostView getPost() {
        return post;
    }

    public void setPost(PostView post) {
        this.post = post;
    }

    public ThreadView getThread() {
        return thread;
    }

    public void setThread(ThreadView thread) {
        this.thread = thread;
    }

    public ForumView getForum() {
        return forum;
    }

    public void setForum(ForumView forum) {
        this.forum = forum;
    }
}
