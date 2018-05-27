
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



CREATE OR REPLACE FUNCTION insert_posts_func() RETURNS TRIGGER AS
$insert_posts_trigger$
  DECLARE
    arr INTEGER[];
    root INTEGER;
  BEGIN
      IF NEW.parent_id = 0 THEN
       SELECT array_append(NULL, NEW.id) into arr;
       root = NEW.id;
      ELSE
        SELECT array_append((SELECT path_to_post from posts WHERE id = NEW.parent_id), NEW.id) into arr;
        root = arr[1];
      END IF;
      NEW.path_to_post = arr;
      NEW.id_of_root = root;
      UPDATE forums set posts = posts + 1 WHERE id = NEW.forum_id;
      INSERT INTO forums_and_users(user_id, forum_id) VALUES(NEW.author_id, NEW.forum_id);
    RETURN NEW;
  END;
$insert_posts_trigger$ LANGUAGE plpgsql;
--

DROP TRIGGER IF EXISTS insert_posts_trigger ON posts;
CREATE TRIGGER insert_posts_trigger BEFORE INSERT ON posts
  FOR EACH ROW EXECUTE PROCEDURE insert_posts_func();


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



--------------------- TRIGGER FOR UPDATE forums ------------------------
--CREATE OR REPLACE FUNCTION insert_forum_func() RETURNS TRIGGER AS
--$insert_forums_trigger$
--  BEGIN
--    UPDATE forums SET owner_name = (SELECT nickname FROM userprofiles WHERE id = NEW.owner_id)
--      WHERE id = NEW.id;
--    RETURN NULL;
--  END;
--$insert_forums_trigger$ LANGUAGE plpgsql;

--DROP TRIGGER IF EXISTS insert_forums_trigger ON forums;
--CREATE TRIGGER insert_forums_trigger AFTER INSERT ON forums
--  FOR EACH ROW EXECUTE PROCEDURE insert_forum_func();
---------------------------------------------------------------------



------------------- TRIGGER FOR UPDATE threads ---------------
CREATE OR REPLACE FUNCTION insert_threads_func() RETURNS TRIGGER AS
$insert_threads_trigger$
   DECLARE
    u_id integer;
    t_id integer;
    f_id integer;
    forum_slug citext;
    u_nickname citext;
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

--CREATE OR REPLACE FUNCTION insert_posts_func() RETURNS TRIGGER AS
--$insert_posts_trigger$
--  BEGIN
--      UPDATE posts SET author_name = (SELECT nickname FROM userprofiles WHERE id = NEW.author_id),
--                      forum_slug = (SELECT slug FROM forums WHERE id = NEW.forum_id)
--      WHERE id = NEW.id;
--      INSERT INTO forums_and_users(user_id, forum_id) VALUES(NEW.author_id, NEW.forum_id);
--    RETURN NULL;
--  END;
--$insert_posts_trigger$ LANGUAGE plpgsql;
--
--DROP TRIGGER IF EXISTS insert_posts_trigger ON posts;
--CREATE TRIGGER insert_posts_trigger AFTER INSERT ON posts
--  FOR EACH ROW EXECUTE PROCEDURE insert_posts_func();
