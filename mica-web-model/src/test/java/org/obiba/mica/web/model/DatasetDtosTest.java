/*
 * Copyright (c) 2017 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.web.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.obiba.mica.dataset.HarmonizationDatasetStateRepository;
import org.obiba.mica.dataset.domain.HarmonizationDataset;
import org.obiba.mica.dataset.domain.HarmonizationDatasetState;
import org.obiba.mica.dataset.domain.StudyDataset;
import org.obiba.mica.micaConfig.domain.MicaConfig;
import org.obiba.mica.micaConfig.service.MicaConfigService;

import java.util.Arrays;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatasetDtosTest {

  @InjectMocks
  private DatasetDtos datasetDtos;

  @SuppressWarnings("unused")
  @Spy
  private LocalizedStringDtos localizedStringDtos;

  @Mock
  private MicaConfigService micaConfigService;

  @Mock
  private HarmonizationDatasetStateRepository harmonizationDatasetStateRepository;

  @Mock
  private StudySummaryDtos studySummaryDtos;

  @Mock
  private PermissionsDtos permissionsDtos;

  @Before
  public void before() {
    MicaConfig config = new MicaConfig();
    config.setLocales(Arrays.asList(Locale.ENGLISH, Locale.FRENCH));
    when(micaConfigService.getConfig()).thenReturn(config);

    when(studySummaryDtos.asDto(anyString())).thenReturn(Mica.StudySummaryDto.newBuilder().setId("123").setPublished(true).build());
    when(permissionsDtos.asDto(any(StudyDataset.class))).thenReturn(Mica.PermissionsDto.getDefaultInstance());
    when(permissionsDtos.asDto(any(HarmonizationDataset.class))).thenReturn(Mica.PermissionsDto.getDefaultInstance());
  }

  @Test
  public void test_study_dataset_dto() throws Exception {
    StudyDataset studyDataset = createStudyDataset();
    Mica.DatasetDto dto = datasetDtos.asDto(studyDataset);

    assertThat(dto.getId(), is("123"));
  }

  @Test
  public void test_harmonized_dataset_dto() throws Exception {
    when(harmonizationDatasetStateRepository.findOne(anyString())).thenReturn(new HarmonizationDatasetState());
    HarmonizationDataset harmonizationDataset = createHarmonizedDataset();
    Mica.DatasetDto dto = datasetDtos.asDto(harmonizationDataset);

    assertThat(dto.getId(), is("123"));
  }

  private StudyDataset createStudyDataset() {
    StudyDataset studyDataset = new StudyDataset();
    studyDataset.setId("123");
    return studyDataset;
  }

  private HarmonizationDataset createHarmonizedDataset() {
    HarmonizationDataset harmonizationDataset = new HarmonizationDataset();
    harmonizationDataset.setId("123");
    harmonizationDataset.setProject("project123");
    harmonizationDataset.setTable("table123");
    return harmonizationDataset;
  }
}
