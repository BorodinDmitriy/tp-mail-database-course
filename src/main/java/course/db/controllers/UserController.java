package course.db.controllers;

import course.db.managers.ManagerResponseCodes;
import course.db.managers.StatusManagerRequest;
import course.db.models.UserProfileModel;
import course.db.views.AbstractView;
import course.db.views.ErrorView;
import course.db.views.UserProfileView;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/api/user")
public class UserController extends AbstractController {
    @RequestMapping(path="/{nickname}/create", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(@RequestBody UserProfileView userProfileView,
                                                     @PathVariable(value="nickname") String nickname) {
        userProfileView.setNickname(nickname);
        StatusManagerRequest status = userProfileManager.createUser(new UserProfileModel(userProfileView));

        switch (status.getCode()) {
            case OK:
                return new ResponseEntity<>(userProfileView, null, HttpStatus.CREATED);
            case CONFILICT:
                List<UserProfileView> users = new ArrayList<>();
                StatusManagerRequest status1 = userProfileManager.getUsersByNickOrEmail(userProfileView.getNickname(),
                        userProfileView.getEmail(), users);
                if (status1.getCode() == ManagerResponseCodes.DB_ERROR)
                    return new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(users, null, HttpStatus.CONFLICT);
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path="/{nickname}/profile", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> getProfile(@PathVariable(value = "nickname") String nickname) {
        UserProfileModel userProfileModel = new UserProfileModel();
        StatusManagerRequest status = userProfileManager.getUserByNick(nickname, userProfileModel);

        switch (status.getCode()) {
            case OK:
                return new ResponseEntity<>(userProfileModel.toView(), null, HttpStatus.OK);
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path="/{nickname}/profile", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> setProfile(@RequestBody UserProfileView userProfileView,
                                                   @PathVariable(value = "nickname") String nickname) {
        userProfileView.setNickname(nickname);
        UserProfileModel userProfileModel = new UserProfileModel(userProfileView);
        StatusManagerRequest status = userProfileManager.changeUser(userProfileModel);

        switch (status.getCode()) {
            case OK:
                return new ResponseEntity<>(userProfileModel.toView(), null, HttpStatus.OK);
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
            case CONFILICT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.CONFLICT);
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
