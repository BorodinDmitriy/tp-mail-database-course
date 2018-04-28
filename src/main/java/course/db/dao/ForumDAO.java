package course.db.dao;

import course.db.db_queries.QueryForForums;
import course.db.db_queries.QueryForUserProfile;
import course.db.models.ForumModel;
import course.db.models.ThreadModel;
import course.db.models.UserProfileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Repository
public class ForumDAO extends AbstractDAO {
    @NotNull
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ForumDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void findForumId(ForumModel model) {
        Integer id = jdbcTemplate.queryForObject(QueryForForums.findForumIdBySlug(), new Object[]{model.getSlug()}, Integer.class);
        model.setId(id);
    }

    public void create(@NotNull ForumModel forumModel) {
        int userId = jdbcTemplate.queryForObject(QueryForUserProfile.getIdByNick(), new Object[]
                {forumModel.getUser()}, Integer.class);
        jdbcTemplate.update(QueryForForums.create(), userId, forumModel.getTitle(), forumModel.getSlug());
    }

    public ForumModel findFullForumBySlug(ForumModel model){
        return jdbcTemplate.queryForObject(QueryForForums.findFullForumModel(), new Object[] {model.getSlug()}, _getFullForumModel);
    }

    public ForumModel findForumBySlug(String slug) {
        return jdbcTemplate.queryForObject(QueryForForums.findForumBySlug(), new Object[] {slug}, _getForumModel);
    }

    // TODO tr
    public List<UserProfileModel> findUsers(ForumModel forumModel, Integer limit, String since, Boolean desc) {
        StringBuilder builder = new StringBuilder(QueryForForums.findUsers());
        List<Object> list = new ArrayList<>();
        list.add(forumModel.getId());

        if (since != null) {
            builder.append(" AND _user.nickname " + (desc == Boolean.TRUE  ? "< ?::CITEXT " : "> ?::CITEXT "));
            list.add(since);
        }

        builder.append("ORDER by _user.nickname " + (desc == Boolean.TRUE  ? "DESC " : " "));

        if (limit != null) {
            builder.append("LIMIT ? ");
            list.add(limit);
        }
        return jdbcTemplate.query(builder.toString(), list.toArray(),
                _getUserModel);
    }

    public List<ThreadModel> findThreads(ForumModel forumModel, Integer limit, String since, Boolean desc) {
        StringBuilder builder = new StringBuilder(QueryForForums.findThreadsById());
        List<Object> list = new ArrayList<>();
        list.add(forumModel.getId());

        if (since != null) {
//            builder.append(" AND thread.created " + (desc == Boolean.TRUE  ? " <= ?::TIMESTAMPTZ " : " >= ?::TIMESTAMPTZ "));
            builder.append(" AND created " + (desc == Boolean.TRUE  ? " <= ?::TIMESTAMPTZ " : " >= ?::TIMESTAMPTZ "));
            list.add(since);
        }

//        builder.append("ORDER by thread.created " + (desc == Boolean.TRUE  ? "DESC " : " "));
        builder.append("ORDER by created " + (desc == Boolean.TRUE  ? "DESC " : " "));

        if (limit != null) {
            builder.append("LIMIT ? ");
            list.add(limit);
        }

        List<ThreadModel> models = jdbcTemplate.query(builder.toString(), list.toArray(),
                _getThreadModel);

        for (ThreadModel model : models) {
            model.setForum(forumModel.getSlug());
        }
        return models;
    }

    @Override
    public Integer count(){
        return jdbcTemplate.queryForObject(QueryForForums.count(), Integer.class);
    }

    @Override
    public void clear() {
        jdbcTemplate.execute(QueryForForums.clear());
    }
}
