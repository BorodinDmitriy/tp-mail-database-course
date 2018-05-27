DROP INDEX IF EXISTS userprofiles_nickname_idx;
DROP INDEX IF EXISTS forums_slug_idx;
DROP INDEX IF EXISTS forums_owners_idx;
DROP INDEX IF EXISTS threads_users_idx;
DROP INDEX IF EXISTS threads_forums_idx;
DROP INDEX IF EXISTS posts_threads_idx;
DROP INDEX IF EXISTS posts_roots_idx;
DROP INDEX IF EXISTS posts_threads_by_id_multiple_idx;
DROP INDEX IF EXISTS posts_threads_by_parent_id_multiple_idx;
DROP INDEX IF EXISTS forums_and_users_users_idx;
DROP INDEX IF EXISTS forums_and_users_forums_idx;
DROP INDEX IF EXISTS votes_threads_idx;
DROP INDEX IF EXISTS votes_threads_and_users_multiple_idx;

---------- userprofiles ------------------
CREATE INDEX IF NOT EXISTS userprofiles_idx ON userprofiles(id);
CREATE INDEX IF NOT EXISTS userprofiles_nickname_idx ON userprofiles USING hash (nickname);

---------- forums ------------------------
CREATE INDEX IF NOT EXISTS forums_idx ON forums(id);
CREATE INDEX IF NOT EXISTS forums_slug_idx ON forums(slug);
CREATE INDEX IF NOT EXISTS forums_owners_idx ON forums(owner_id);
CREATE INDEX IF NOT EXISTS forums_threads_idx ON forums USING btree (id, threads);

---------- threads -----------------------
CREATE INDEX IF NOT EXISTS threads_idx ON threads(id);
CREATE INDEX IF NOT EXISTS threads_users_idx ON threads(author_id);
CREATE INDEX IF NOT EXISTS threads_forums_idx ON threads(forum_id);
CREATE INDEX IF NOT EXISTS threads_slugs_idx ON threads(slug);
CREATE INDEX IF NOT EXISTS threads_created_idx ON threads(created);

---------- posts -------------------------
CREATE INDEX IF NOT EXISTS posts_idx ON posts(id);
CREATE INDEX IF NOT EXISTS posts_users_idx ON posts(author_id);
CREATE INDEX IF NOT EXISTS posts_forums_idx ON posts(forum_id);
CREATE INDEX IF NOT EXISTS posts_threads_idx ON posts(thread_id);
CREATE INDEX IF NOT EXISTS posts_paths_idx ON posts(path_to_post);
CREATE INDEX IF NOT EXISTS posts_roots_idx ON posts(id_of_root);
CREATE INDEX IF NOT EXISTS posts_path_thread_id_multiple_idx ON posts (thread_id, path_to_post);
CREATE INDEX IF NOT EXISTS posts_threads_by_id_multiple_idx ON posts(thread_id, id);
CREATE INDEX IF NOT EXISTS posts_threads_by_parent_id_multiple_idx ON posts(thread_id, parent_id);

---------- forums_and_users --------------
CREATE INDEX IF NOT EXISTS forums_and_users_users_idx ON forums_and_users(user_id);
CREATE INDEX IF NOT EXISTS forums_and_users_forums_idx ON forums_and_users(forum_id);

---------- votes -------------------------
CREATE INDEX IF NOT EXISTS votes_threads_idx ON votes(thread_id);
CREATE INDEX IF NOT EXISTS  votes_threads_and_users_multiple_idx ON votes(owner_id, thread_id);
