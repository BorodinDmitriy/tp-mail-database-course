package course.db.controllers;

import course.db.views.AbstractView;
import course.db.views.StatusView;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping(path="/api/service")
public class ServiceController extends AbstractController {
    @RequestMapping(path="/clear", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> clearService() {
        userProfileManager.statusClear();
        threadManager.statusClear();
        postManager.statusClear();
        forumManager.statusClear();
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }

    @RequestMapping(path="/status", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> getStatus() {
        StatusView statusView = new StatusView(
                userProfileManager.statusCount(),
                forumManager.statusCount(),
                threadManager.statusCount(),
                postManager.statusCount());

        return new ResponseEntity<>(statusView, null, HttpStatus.OK);
    }
}
