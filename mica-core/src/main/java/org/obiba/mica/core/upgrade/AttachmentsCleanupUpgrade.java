package org.obiba.mica.core.upgrade;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.obiba.mica.access.DataAccessRequestRepository;
import org.obiba.mica.core.repository.AttachmentRepository;
import org.obiba.mica.file.FileService;
import org.obiba.mica.study.StudyRepository;
import org.obiba.runtime.Version;
import org.obiba.runtime.upgrade.UpgradeStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

@Component
public class AttachmentsCleanupUpgrade implements UpgradeStep {
  private static final Logger log = LoggerFactory.getLogger(AttachmentsCleanupUpgrade.class);

  @Inject
  private AttachmentRepository attachmentRepository;

  @Inject
  private StudyRepository studyRepository;

  @Inject
  private DataAccessRequestRepository dataAccessRequestRepository;

  @Inject
  private FileService fileService;

  @Override
  public String getDescription() {
    return "Orphaned attachments cleanup";
  }

  @Override
  public Version getAppliesTo() {
    return new Version("0.9.2");
  }

  @Override
  public void execute(Version version) {
    log.info("Executing orphaned attachments cleanup");
    deleteOrphaned(studyRepository, "^/study/([^/]+)/");
    deleteOrphaned(dataAccessRequestRepository, "^/data-access-request/([^/]+)/");
  }

  private <T extends MongoRepository> void deleteOrphaned(T repository, String idPattern) {
    Pattern pattern = Pattern.compile(idPattern);
    Set<String> missingIds = Sets.newHashSet();

    attachmentRepository.findAll().forEach(a -> {
      Matcher m = pattern.matcher(a.getPath());

      if(m.find()) {
        String id = m.group(1);

        if(missingIds.contains(id) || repository.findOne(id) == null) {
          if(!missingIds.contains(id)) missingIds.add(id);
          attachmentRepository.delete(a);
          fileService.delete(a.getId());
        }
      }
    });
  }
}
