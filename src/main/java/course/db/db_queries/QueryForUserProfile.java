package course.db.db_queries;

public class QueryForUserProfile {
    static public String create() {
        return "INSERT INTO userprofiles (about, email, fullname, nickname) VALUES(?, ?::CITEXT, ?, ?::CITEXT)";
    }

    static public String getUserByNickOrEmail() {
        return "SELECT * FROM userprofiles WHERE nickname=?::CITEXT OR email=?::CITEXT";
    }

    static public String getIdByNick() {
        return "SELECT id FROM userprofiles WHERE nickname=?::CITEXT";
    }

    static public String count() {
        return "SELECT COUNT(*) FROM userprofiles";
    }

    static public String clear() {
        return "DELETE FROM userprofiles";
    }
}
