package course.db.managers;

import course.db.dao.ForumDAO;
import course.db.models.ForumModel;
import course.db.models.ThreadModel;
import course.db.models.UserProfileModel;
import course.db.views.ThreadView;
import course.db.views.UserProfileView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

//import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class ForumManager {
    private final ForumDAO forumDAO;

    @Autowired
    public ForumManager(ForumDAO forumDAO) {this.forumDAO = forumDAO;}

    public StatusManagerRequest create(ForumModel forumModel) {
        try {
            forumDAO.create(forumModel);
            ForumModel model = forumDAO.findForumBySlug(forumModel.getSlug());
            forumModel.copy(model);
        }
        catch (EmptyResultDataAccessException eRx) {
            return new StatusManagerRequest(ManagerResponseCodes.NO_RESULT, eRx);
        }
        catch(DuplicateKeyException dKx) {
            return new StatusManagerRequest(ManagerResponseCodes.CONFILICT, dKx);
        }
        catch (DataAccessException dAx) {
            return new StatusManagerRequest(ManagerResponseCodes.DB_ERROR, dAx);
        }
        return new StatusManagerRequest(ManagerResponseCodes.OK);
    }

    public StatusManagerRequest findForum(ForumModel forumModel) {//, List<ForumView> forumViewList) {
        try {
            ForumModel forum = forumDAO.findForumBySlug(forumModel.getSlug());
            forumModel.copy(forum);
        }
        catch (EmptyResultDataAccessException eRx) {
            return new StatusManagerRequest(ManagerResponseCodes.NO_RESULT, eRx);
        }
        catch (DataAccessException dAx) {
            return new StatusManagerRequest(ManagerResponseCodes.DB_ERROR, dAx);
        }
        return new StatusManagerRequest(ManagerResponseCodes.OK);
    }

    public StatusManagerRequest findThreads(ForumModel forumModel, Integer limit, String since, Boolean desc,
                                            List<ThreadView> threadViewList) {
        try {
            ForumModel findedForum = forumDAO.findFullForumBySlug(forumModel);

            List<ThreadModel> userProfileModels = forumDAO.findThreads(findedForum, limit, since, desc);
            for (ThreadModel model : userProfileModels) {
                threadViewList.add(model.toView());
            }
        }
        catch (EmptyResultDataAccessException eRx) {
            return new StatusManagerRequest(ManagerResponseCodes.NO_RESULT, eRx);
        }
        catch (DataAccessException dAx) {
            return new StatusManagerRequest(ManagerResponseCodes.DB_ERROR, dAx);
        }
        return new StatusManagerRequest(ManagerResponseCodes.OK);
    }

    public StatusManagerRequest findUsers(ForumModel forumModel, Integer limit, String since, Boolean desc,
                                          List<UserProfileView> userProfileViewList) {
        try {
            forumDAO.findForumId(forumModel);
            List<UserProfileModel> userProfileModels = forumDAO.findUsers(forumModel, limit, since, desc);

            for (UserProfileModel model : userProfileModels) {
                userProfileViewList.add(model.toView());
            }
        }
        catch (EmptyResultDataAccessException eRx) {
            return new StatusManagerRequest(ManagerResponseCodes.NO_RESULT, eRx);
        }
        catch (DataAccessException dAx) {
            return new StatusManagerRequest(ManagerResponseCodes.DB_ERROR, dAx);
        }
        return new StatusManagerRequest(ManagerResponseCodes.OK);
    }

    public StatusManagerRequest statusClear() {
        try {
            forumDAO.clear();
        }
        catch (DataAccessException dAx) {
            return new StatusManagerRequest(ManagerResponseCodes.DB_ERROR, dAx);
        }
        return new StatusManagerRequest(ManagerResponseCodes.OK);
    }

    public Integer statusCount() {
        return forumDAO.count();
    }
}