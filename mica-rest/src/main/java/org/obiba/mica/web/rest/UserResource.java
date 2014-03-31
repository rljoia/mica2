package org.obiba.mica.web.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.obiba.mica.jpa.domain.User;
import org.obiba.mica.jpa.repository.UserRepository;
import org.obiba.mica.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/ws/users")
public class UserResource {

  private static final Logger log = LoggerFactory.getLogger(UserResource.class);

  @Inject
  private UserRepository userRepository;

  /**
   * GET  /rest/users/:login -> get the "login" user.
   */
  @RequestMapping(value = "/{login}", method = GET, produces = "application/json")
  @Timed
  @RolesAllowed(AuthoritiesConstants.ADMIN)
  public User getUser(@PathVariable String login, HttpServletResponse response) {
    log.debug("REST request to get User : {}", login);
    User user = userRepository.findOne(login);
    if(user == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    return user;
  }
}
