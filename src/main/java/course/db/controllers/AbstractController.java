package course.db.controllers;

import course.db.managers.ForumManager;
import course.db.managers.PostManager;
import course.db.managers.ThreadManager;
import course.db.managers.UserProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class AbstractController {
    @Autowired
    protected UserProfileManager userProfileManager;

    @Autowired
    protected ForumManager forumManager;

    @Autowired
    protected PostManager postManager;

    @Autowired
    protected ThreadManager threadManager;
}
