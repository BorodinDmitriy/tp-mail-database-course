package course.db.models;

import course.db.views.*;

public class PostDetailsModel {
    UserProfileModel author;
    PostModel post;
    ThreadModel thread;
    ForumModel forum;

    public PostDetailsModel() {}

    public PostDetailsModel(UserProfileModel author, PostModel post, ThreadModel thread, ForumModel forum) {
        this.author = author;
        this.post = post;
        this.thread = thread;
        this.forum = forum;
    }

    public PostDetailsView toView() {

        return new PostDetailsView(this.author == null ? null : this.author.toView(),
                this.post == null ? null :  this.post.toView(),
                this.thread == null ? null : this.thread.toView()
                ,this.forum == null ? null : this.forum.toView());
    }

    public void copy(PostDetailsModel other) {
        this.author = other.getAuthor();
        this.post = other.getPost();
        this.thread = other.getThread();
        this.forum = other.getForum();
    }

    public UserProfileModel getAuthor() {
        return author;
    }

    public void setAuthor(UserProfileModel author) {
        this.author = author;
    }

    public PostModel getPost() {
        return post;
    }

    public void setPost(PostModel post) {
        this.post = post;
    }

    public ThreadModel getThread() {
        return thread;
    }

    public void setThread(ThreadModel thread) {
        this.thread = thread;
    }

    public ForumModel getForum() {
        return forum;
    }

    public void setForum(ForumModel forum) {
        this.forum = forum;
    }
}
