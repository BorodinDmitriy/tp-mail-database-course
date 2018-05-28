CREATE EXTENSION IF NOT EXISTS CITEXT;

-- SET SYNCHRONOUS_COMMIT = 'off';

CREATE TABLE IF NOT EXISTS userprofiles (
  id       SERIAL PRIMARY KEY,
  about    TEXT DEFAULT NULL,
  email    CITEXT UNIQUE,
  fullname TEXT DEFAULT NULL,
  nickname CITEXT COLLATE ucs_basic UNIQUE
);

CREATE TABLE IF NOT EXISTS forums (
  id      SERIAL PRIMARY KEY,
  owner_id INTEGER REFERENCES userprofiles (id) ON DELETE CASCADE NOT NULL,
  owner_name CITEXT,
  title   TEXT NOT NULL,
  slug    CITEXT UNIQUE                                   NOT NULL,
  posts   INTEGER DEFAULT 0,
  threads INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS threads (
  id SERIAL PRIMARY KEY,
  author_id  INTEGER REFERENCES userprofiles (id) ON DELETE CASCADE  NOT NULL,
  author_name CITEXT,
  forum_id INTEGER REFERENCES forums (id) ON DELETE CASCADE NOT NULL,
  forum_slug CITEXT,
  title    TEXT  NOT NULL,
  created  TIMESTAMPTZ DEFAULT NOW(),
  message  TEXT        DEFAULT NULL,
  votes    INTEGER     DEFAULT 0,
  slug     CITEXT UNIQUE
);


CREATE TABLE IF NOT EXISTS posts (
  id SERIAL PRIMARY KEY,
  parent_id    INTEGER     DEFAULT 0,
  author_id   INTEGER REFERENCES userprofiles (id) ON DELETE CASCADE   NOT NULL,
  author_name CITEXT,
  created   TIMESTAMPTZ DEFAULT NOW(),
  forum_id  INTEGER REFERENCES forums (id) ON DELETE CASCADE  NOT NULL,
  forum_slug CITEXT,
  is_edited BOOLEAN     DEFAULT FALSE,
  message   TEXT        DEFAULT NULL,
  thread_id INTEGER REFERENCES threads (id) ON DELETE CASCADE NOT NULL,
  id_of_root INTEGER,
  path_to_post INTEGER []
);

CREATE TABLE IF NOT EXISTS forums_and_users (
  user_id INTEGER REFERENCES userprofiles (id) ON DELETE CASCADE NOT NULL,
  forum_id INTEGER REFERENCES forums (id) ON DELETE CASCADE NOT NULL
);


CREATE TABLE IF NOT EXISTS votes (
  owner_id INTEGER REFERENCES userprofiles (id) ON DELETE CASCADE,
  thread_id  INTEGER REFERENCES threads (id) ON DELETE CASCADE,
  vote INTEGER DEFAULT 0,
  CONSTRAINT one_owner_thread_pair UNIQUE (owner_id, thread_id)
);

--------------------- TRIGGER FOR UPDATE forums ------------------------
CREATE OR REPLACE FUNCTION insert_forum_func() RETURNS TRIGGER AS
$insert_forums_trigger$
  BEGIN
    UPDATE forums SET owner_name = (SELECT nickname FROM userprofiles WHERE id = NEW.owner_id)
      WHERE id = NEW.id;
    RETURN NULL;
  END;
$insert_forums_trigger$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS insert_forums_trigger ON forums;
CREATE TRIGGER insert_forums_trigger AFTER INSERT ON forums
  FOR EACH ROW EXECUTE PROCEDURE insert_forum_func();
---------------------------------------------------------------------



------------------- TRIGGER FOR UPDATE threads ---------------
CREATE OR REPLACE FUNCTION insert_threads_func() RETURNS TRIGGER AS
$insert_threads_trigger$
  BEGIN
      UPDATE threads SET author_name = (SELECT nickname FROM userprofiles WHERE id = NEW.author_id),
                      forum_slug = (SELECT slug FROM forums WHERE id = NEW.forum_id)
      WHERE id = NEW.id;
      UPDATE forums SET threads = threads + 1 WHERE id = NEW.forum_id;
       INSERT INTO forums_and_users(user_id, forum_id) VALUES(NEW.author_id, NEW.forum_id);
    RETURN NULL;
  END;
$insert_threads_trigger$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS insert_threads_trigger ON threads;
CREATE TRIGGER insert_threads_trigger AFTER INSERT ON threads
  FOR EACH ROW EXECUTE PROCEDURE insert_threads_func();

-------------------------------------------------------------------


------------------- TRIGGER FOR UPDATE posts ------------------------

CREATE OR REPLACE FUNCTION insert_posts_func() RETURNS TRIGGER AS
$insert_posts_trigger$
  BEGIN
      UPDATE posts SET author_name = (SELECT nickname FROM userprofiles WHERE id = NEW.author_id),
                      forum_slug = (SELECT slug FROM forums WHERE id = NEW.forum_id)
      WHERE id = NEW.id;
      INSERT INTO forums_and_users(user_id, forum_id) VALUES(NEW.author_id, NEW.forum_id);
    RETURN NULL;
  END;
$insert_posts_trigger$ LANGUAGE plpgsql;
--
DROP TRIGGER IF EXISTS insert_posts_trigger ON posts;
CREATE TRIGGER insert_posts_trigger AFTER INSERT ON posts
  FOR EACH ROW EXECUTE PROCEDURE insert_posts_func();


---------------------- vote -----------------------------------------
CREATE OR REPLACE FUNCTION create_or_update_vote(u_id INTEGER, t_id INTEGER, v INTEGER)
  RETURNS VOID AS '
BEGIN
  INSERT INTO votes (owner_id, thread_id, vote) VALUES (u_id, t_id, v)
  ON CONFLICT (owner_id, thread_id)
    DO UPDATE SET vote = v;
  UPDATE threads
  SET votes = (SELECT SUM(vote)
               FROM votes
               WHERE thread_id = t_id)
  WHERE id = t_id;
END;'
LANGUAGE plpgsql;

/*static private String findThread() {
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
    static public String updateVotes() {return "UPDATE threads SET votes = ? WHERE id = ?";}*/


create index if not exists user_name_idx on userprofiles USING hash (nickname);

create index if not exists forums_slug_idx on forums(slug);
CREATE INDEX IF NOT EXISTS forums_userprofiles_for_id_idx ON forums (owner_id);

CREATE INDEX IF NOT EXISTS threads_user_id_idx ON threads (author_id);
CREATE INDEX IF NOT EXISTS threads_forum_id_idx ON threads (forum_id);

create index if not exists post_thread on posts(thread_id);
create index if not exists post_thread_post on posts(thread_id, id);
create index if not exists post_root ON posts(id_of_root);
CREATE INDEX IF NOT EXISTS posts_multi_idx ON posts (thread_id, parent_id);

CREATE INDEX IF NOT EXISTS forum_users_user_id_idx ON forums_and_users (user_id);
CREATE INDEX IF NOT EXISTS forum_users_forum_id_idx ON forums_and_users (forum_id);

create index if not exists thread_vote_user on votes(owner_id, thread_id);
create index if not exists thread_vote on votes(thread_id);


-- CREATE INDEX IF NOT EXISTS user_name_idx ON userprofiles USING hash (nickname);
--
-- CREATE INDEX IF NOT EXISTS forums_idx ON forums(id);
-- CREATE INDEX IF NOT EXISTS forums_slug_idx ON forums(slug);
-- CREATE INDEX IF NOT EXISTS forums_userprofiles_for_id_idx ON forums (owner_id);
--
-- CREATE INDEX IF NOT EXISTS threads_created_idx ON threads(created);
-- CREATE INDEX IF NOT EXISTS threads_id_idx ON threads(id);
-- CREATE INDEX IF NOT EXISTS threads_user_id_idx ON threads (author_id);
-- CREATE INDEX IF NOT EXISTS threads_forum_id_idx ON threads (forum_id);
-- CREATE INDEX IF NOT EXISTS threads_slug_idx ON threads(slug);
-- CREATE INDEX IF NOT EXISTS threads_forum_slug_idx ON threads(forum_slug);
-- CREATE INDEX IF NOT EXISTS threads_message_idx ON threads(message);
-- CREATE INDEX IF NOT EXISTS threads_title_idx ON threads(title);
-- CREATE INDEX IF NOT EXISTS threads_votes_idx ON threads(votes);
--
-- CREATE INDEX IF NOT EXISTS threads_id_slug_idx ON threads USING btree(id,slug);
-- CREATE INDEX IF NOT EXISTS threads_forum_id_id_idx ON threads USING btree(forum_id,id);
-- CREATE INDEX IF NOT EXISTS threads_mult_idx ON threads USING btree ( author_name, created, forum_slug, id, message, slug, title, votes);
--
-- CREATE INDEX IF NOT EXISTS post_thread ON posts(thread_id);
-- CREATE INDEX IF NOT EXISTS post_thread_post ON posts(thread_id, id);
-- CREATE INDEX IF NOT EXISTS post_root ON posts(id_of_root);
-- CREATE INDEX IF NOT EXISTS posts_multi_idx ON posts (thread_id, parent_id);
--
-- CREATE INDEX IF NOT EXISTS forum_users_user_id_idx ON forums_and_users (user_id);
-- CREATE INDEX IF NOT EXISTS forum_users_forum_id_idx ON forums_and_users (forum_id);
-- CREATE INDEX IF NOT EXISTS thread_vote_user ON votes(owner_id, thread_id);
-- CREATE INDEX IF NOT EXISTS thread_vote ON votes(thread_id);
--
--
-- CREATE INDEX IF NOT EXISTS posts_user_id_idx ON posts (author_id);
-- CREATE INDEX IF NOT EXISTS posts_forum_id_idx ON posts (forum_id);
-- CREATE INDEX IF NOT EXISTS posts_path_idx ON posts (path_to_post);
-- CREATE INDEX IF NOT EXISTS posts_path_thread_id_idx ON posts (thread_id, path_to_post);
--
--
--
-- CREATE INDEX IF NOT EXISTS users_id_idx ON userprofiles(id);
-- CREATE INDEX IF NOT EXISTS post_root_id_path_idx ON posts(id_of_root, path_to_post);
-- CREATE INDEX IF NOT EXISTS post_thread_parent_id_idx ON posts(thread_id, parent_id, id);
