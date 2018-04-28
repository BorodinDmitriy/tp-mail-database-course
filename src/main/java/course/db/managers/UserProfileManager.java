package course.db.managers;

import course.db.dao.UserProfileDAO;
import course.db.models.UserProfileModel;
import course.db.views.UserProfileView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

//import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class UserProfileManager {
    private final UserProfileDAO userProfileDAO;

    @Autowired
    public UserProfileManager(UserProfileDAO userProfileDAO) {this.userProfileDAO = userProfileDAO; }

    public StatusManagerRequest createUser(UserProfileModel userProfileModel) {
        try {
            userProfileDAO.create(userProfileModel);
        }
        catch (DuplicateKeyException dKx) {
            return new StatusManagerRequest(ManagerResponseCodes.CONFILICT, dKx);
        }
        catch (DataAccessException dAx) {
            return new StatusManagerRequest(ManagerResponseCodes.DB_ERROR, dAx);
        }
        return new StatusManagerRequest(ManagerResponseCodes.OK);
    }

    public StatusManagerRequest getUserByNick(String nickname, UserProfileModel user) {
        try {
            // TODO copy
            UserProfileModel userModel = userProfileDAO.getUserByNick(nickname);
            user.copy(userModel);
        }
        catch (EmptyResultDataAccessException eRx) {
            return new StatusManagerRequest(ManagerResponseCodes.NO_RESULT, eRx);
        }
        catch (DataAccessException dAx) {
            return new StatusManagerRequest(ManagerResponseCodes.DB_ERROR, dAx);
        }
        return new StatusManagerRequest(ManagerResponseCodes.OK);
    }

    public StatusManagerRequest getUsersByNickOrEmail(String nickname, String email, List<UserProfileView> users) {
        try {
            List<UserProfileModel> userProfileModels = userProfileDAO.getUsersByNickOrEmail(nickname, email);
            for (UserProfileModel userProfileModel : userProfileModels) {
                users.add(userProfileModel.toView());
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

    public StatusManagerRequest changeUser(UserProfileModel userProfileModel) {
        try {
            userProfileDAO.change(userProfileModel);
            UserProfileModel model = userProfileDAO.getUserByNick(userProfileModel.getNickname());
            userProfileModel.copy(model);
//            if (res == 0)
//                return new StatusManagerRequest(ManagerResponseCodes.NO_RESULT, "no user found");
        }
        catch (EmptyResultDataAccessException dKx) {
            return new StatusManagerRequest(ManagerResponseCodes.NO_RESULT, dKx);
        }
        catch (DuplicateKeyException dKx){
            return new StatusManagerRequest(ManagerResponseCodes.CONFILICT, dKx);
        }
        catch (DataAccessException dAx) {
            return new StatusManagerRequest(ManagerResponseCodes.DB_ERROR, dAx);
        }
        return new StatusManagerRequest(ManagerResponseCodes.OK);
    }

    public StatusManagerRequest statusClear() {
        try {
            userProfileDAO.clear();
        }
        catch (DataAccessException dAx) {
            return new StatusManagerRequest(ManagerResponseCodes.DB_ERROR, dAx);
        }
        return new StatusManagerRequest(ManagerResponseCodes.OK);
    }

    public Integer statusCount() {
        return userProfileDAO.count();
    }

}

//    user.setNickname(userModel.getNickname());
//    user.setAbout(userModel.getAbout());
//    user.setEmail(userModel.getEmail());
//    user.setFullname(userModel.getFullname());

//        try {
//        }
//        catch (DataAccessException dAx) {
//            return ManagerResponseCodes.DB_ERROR;
//        }
//        return ManagerResponseCodes.OK;