package course.db.db_queries;

public class QueryForPost {

    static public String createPost() {
        return "INSERT INTO posts (id, parent_id, author_id, created, forum_id, message, thread_id, id_of_root, path_to_post) " +
                "VALUES(?, ?, ?, ?::TIMESTAMPTZ, ?, ?, ?, ?, array_append(?, ?))";
    }

    public static String getId() {
        return "SELECT nextval('posts_id_seq')";
    }

    static public String getPath() {
        return "SELECT path_to_post FROM posts WHERE id = ?";
    }

    static public String getById() {
        return "SELECT created, id, is_edited, message, parent_id, thread_id, forum_slug, author_name " +
                "FROM posts WHERE id = ? ";
    };

    static public String oldgetById() {
        return "SELECT post.created, post.id, post.is_edited, post.message, post.parent_id, post.thread_id, forum.slug, _user.nickname " +
                "FROM posts post JOIN userprofiles _user ON (_user.id = post.author_id) JOIN forums forum ON (forum.id = post.forum_id) " +
                "WHERE post.id = ? ";
    };

    static public String findPosts() {
        return "SELECT created, forum_slug, id, is_edited, message, parent_id, thread_id, author_name FROM posts ";
    }

    public static String flatOrTreeposts() {
        return findPosts() + "WHERE thread_id =? ";
    }

    static public String count() {
        return "SELECT COUNT(*) FROM posts";
    }

    static public String clear() {
        return "DELETE FROM posts";
    }
}
