package course.db.controllers;

import course.db.views.AbstractView;
import course.db.views.StatusView;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping(path="/api/service")
public class ServiceController extends AbstractController {
    @RequestMapping(path="/clear", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> clearService() {
        long start = System.nanoTime();
        ResponseEntity m;
        userProfileManager.statusClear();
        threadManager.statusClear();
        postManager.statusClear();
        forumManager.statusClear();
        m =  new ResponseEntity<>(null, null, HttpStatus.OK);
      //  System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (double)(System.nanoTime() - start) / 1000000000.0);
        return m;
    }

    @RequestMapping(path="/status", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> getStatus() {
        long start = System.nanoTime();
        ResponseEntity m;
        StatusView statusView = new StatusView(
                userProfileManager.statusCount(),
                forumManager.statusCount(),
                threadManager.statusCount(),
                postManager.statusCount());

        m = new ResponseEntity<>(statusView, null, HttpStatus.OK);
      //  System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (double)(System.nanoTime() - start) / 1000000000.0);
        return m;
    }
}
