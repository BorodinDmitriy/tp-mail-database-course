package course.db.db_queries;

public class QueryForForums {

    static public String create() {
        return "INSERT INTO forums(owner_id, title, slug) VALUES (?, ?, ?::CITEXT)";
    }


    static public String findFullForumModel() {
        return "SELECT id, title, posts, threads, slug, owner_name FROM forums WHERE slug = ?::CITEXT";
    }

    public static String findForumBySlug() {
        return "SELECT  owner_name, title, slug,  posts, threads FROM FORUMS WHERE slug = ?::CITEXT";
    }


    public static String findForumIdBySlug() {
        return "SELECT id FROM forums WHERE slug = ?::CITEXT";
    }

    static public String findThreads() {
        return "SELECT author_name, forum_slug, id, slug, created, title, message, votes FROM threads " +
                "WHERE slug = ?::CITEXT";
    }

    public static String findThreadsById() {
        return "SELECT id, author_name, slug, forum_slug, created, title, message, votes FROM threads WHERE forum_id = ? ";
    }

    // OR subquery
    static public String findUsers() {
               return "SELECT DISTINCT _user.id, _user.nickname, _user.about, _user.fullname, _user.email " +
                "FROM forums_and_users forums_and_user JOIN userprofiles _user ON (forums_and_user.user_id = _user.id)" +
                "WHERE forums_and_user.forum_id=?";
    }

    static public String addUser() {
        return "INSERT INTO forums_and_users(user_id, forum_id) VALUES(?,?)";
    }

    static public String count() {
        return "SELECT COUNT(*) FROM forums";
    }

    static public String clear() {
        return "DELETE FROM forums";
    }

    // ----------------------------------------------------------------------------------------------

    // TODO anot
    static public String oldfindUsers() {
        return "SELECT DISTINCT _user.id, _user.nickname, _user.about, _user.fullname, _user.email " +
                "FROM forums forum LEFT JOIN threads thread ON (thread.forum_id=forum.id) " +
                "LEFT JOIN posts post ON (forum.id = post.forum_id) " +
                "JOIN userprofiles _user ON (_user.id = thread.author_id OR _user.id=post.author_id) " +
                "WHERE forum.id = ?";
    }
    static public String incThreadCount() {
        return "UPDATE forums SET threads=threads+1 WHERE id = ?";
    }

    static public String oldfindFullForumModel() { return "SELECT forum.id, forum.title, forum.posts, forum.threads, forum.slug, _user.nickname " +
            "FROM forums forum JOIN userprofiles _user ON (forum.owner_id=_user.id) " +
            "WHERE forum.slug = ?::CITEXT";
    }

    // TODO save user in database
//    static public String create() {
//        return "INSERT INTO forums(owner_id, title, slug) VALUES (?, ?, ?::CITEXT) RETURNING *";
//    }


    public static String oldfindForumBySlug() {
        return "SELECT forum.title, forum.posts, forum.threads, forum.slug, _user.nickname " +
                "FROM forums forum JOIN userprofiles _user ON (forum.owner_id=_user.id) " +
                "WHERE forum.slug = ?::CITEXT";
    }

    static public String oldfindThreads() {
        return "SELECT _user.nickname, forum.slug as forum_slug, thread.id, thread.slug as thread_slug," +
                "thread.created, thread.title, thread.message, thread.votes " +
                "FROM threads thread JOIN userprofiles _user ON (thread.author_id = _user.id)" +
                "JOIN forums forum ON (thread.forum_id = forum.id)" +
                "WHERE thread.slug = ?::CITEXT ";
    }


    public static String oldfindThreadsById() {
        return "SELECT thread.id, thread.slug as thread_slug, forum.slug as forum_slug, thread.created, thread.title, thread.message, thread.votes, " +
                " _user.nickname FROM userprofiles _user JOIN (SELECT * FROM threads WHERE forum_id=?) " +
                "thread  ON(thread.author_id=_user.id) JOIN forums forum ON (forum.id = thread.forum_id)";
    }
}



//    public static String findForumIdBySlug() {
//        return "SELECT forum.title, forum.posts, forum.threads, forum.slug, _user.nickname " +
//                "FROM forums forum JOIN userprofiles _user ON (forum.owner_id=_user.id) " +
//                "WHERE forum.slug = ?";
//    }

//    public static String incrementThreads() {
//        return "";
//    }

//        return "SELECT _user.id, _user.nickname, _user.about, _user.fullname, _user.email " +
//                "FROM userprofiles _user JOIN forums forum ON(forum.owner_id=_user.id) " +
//                "JOIN threads thread ON (thread.author_id=_user.id) " +
//                "JOIN posts post ON (post.author_id=_user.id) " +
//                "WHERE forum.id = ? OR post.forum_id=? OR thread.forum_id=? ";

//        return "SELECT _user.nicname thread.id, thread.slug, thread.created, thread.title, thread.message," +
//                " thread.votes FROM (SELECT * FOROM threads WHERE forum_id=?) thread JOIN userprofiles _uesr ON" +
//                "(thread.author_id=_user.id)";