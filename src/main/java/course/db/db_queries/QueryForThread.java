package course.db.db_queries;


public class QueryForThread {

    // TODO maybe function in db
    static public String createNoDate() {
        return "INSERT INTO threads (author_id, forum_id, title, created, message, votes, slug)" +
                "VALUES(?, ?, ?,  NOW(), ?, ?, ?) RETURNING id";
    }

    static private String findThread() {
        return "SELECT author_name, created, forum_slug, id, message, slug, title, votes FROM threads";
//                "FROM threads thread JOIN userprofiles _user ON (thread.author_id = _user.id)" +
//                "  JOIN forums forum ON (thread.forum_id = forum.id) ";
    }

    public static String findThreadIdBySlug() {
        return "SELECT id FROM threads WHERE slug = ?::CITEXT";
    }

    static public String findThreadById() {
        return findThread() + "  WHERE id = ?";
    }

    static public String findForum() {
        return "SELECT forum_id FROM threads WHERE id = ?";
    }

    static public String oldfindThreadById() {
        return findThread() + "  WHERE thread.id = ?";
    }


    static public String findThreadBySlug() {
        return findThread() + "  WHERE slug = ?::CITEXT";
    }

    static public String oldfindThreadBySlug() {
        return findThread() + "  WHERE thread.slug = ?::CITEXT";
    }

    static public String updatePostCount() {
        return "UPDATE forums SET posts = posts + ? WHERE forums.id = ?";
    }

    static public String createOrUpdateVote() {return "select create_or_update_vote(?, ?, ?)";}
    static public String insertVote() {return "INSERT INTO votes (owner_id, thread_id, vote) VALUES(?,?,?)";}
    static public String updateVote() {return "UPDATE votes SET vote = ? WHERE owner_id =? AND thread_id = ?";}
    static public String getVoteSum() {return "SELECT SUM(vote) FROM votes WHERE thread_id = ?";}
    static public String updateVotes() {return "UPDATE threads SET votes = ? WHERE id = ?";}


    //    static public String create() {
//        return "INSERT INTO threads (author_id, forum_id, title, created, message, vote, slug)" +
//                "VALUES(?, ?, ?, CASE WHEN ? IS NULL THEN NOW() ELSE ? END"
//    }

//    static public String findThreadById() {
//        return "SELECT _user.nickname, thread.created, forum.slug AS forum_slug, thread.id, thread.message, thread.slug AS thread_slug, " +
//                "thread.title, thread.votes " +
//                "FROM threads thread JOIN userprofiles _user ON (thread.author_id = _user.id)" +
//                "  JOIN forums forum ON (thread.forum_id = forum.id) " +
//                "  WHERE thread.id = ?";
//
//    }

//    static public String findThreadBySlug() {
//        return "SELECT _user.nickname, thread.created, forum.slug AS forum_slug, thread.id, thread.message, thread.slug AS thread_slug, " +
//                "thread.title, thread.votes " +
//                "FROM threads thread JOIN userprofiles _user ON (thread.author_id = _user.id)" +
//                "  JOIN forums forum ON (thread.forum_id = forum.id) " +
//                "  WHERE thread.slug = ?::CITEXT";
//    }

    // not correct
//    static public String findById() {
//        return "SELECT * FROM threads WHERE id = ?";
//    }
//
//    static public String findBySlug() {
//        return "SELECT * FROM threads WHERE slug = ?::CITEXT";
//    }
//
//    static public String findBySlugOrId() {return "";}

    static public String createWithDate() {
        return "INSERT INTO threads (author_id, forum_id, title, created, message, votes, slug)" +
                "VALUES(?, ?, ?, ?::TIMESTAMPTZ, ?, ?, ?) RETURNING id";
    }


    static public String count() {
        return "SELECT COUNT(*) FROM threads";
    }

    static public String clear() {
        return "DELETE FROM threads";
    }
}
