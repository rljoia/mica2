package org.obiba.mica.core.upgrade;

import javax.inject.Inject;

import org.obiba.mica.access.event.DataAccessRequestUpdatedEvent;
import org.obiba.mica.access.service.DataAccessRequestService;
import org.obiba.runtime.Version;
import org.obiba.runtime.upgrade.UpgradeStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;

@Component
public class ProjectsUpgrade implements UpgradeStep {
  private static final Logger log = LoggerFactory.getLogger(ProjectsUpgrade.class);

  @Inject
  private DataAccessRequestService dataAccessRequestService;

  @Inject
  private EventBus eventBus;

  @Override
  public String getDescription() {
    return "Upgraded with new Research Projects.";
  }

  @Override
  public Version getAppliesTo() {
    return new Version("1.3");
  }

  @Override
  public void execute(Version version) {
    log.info("Create a research project for each data access request.");
    dataAccessRequestService.findAll(null).forEach(dar -> eventBus.post(new DataAccessRequestUpdatedEvent(dar)));
  }
}
