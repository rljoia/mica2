/*
 * Copyright (c) 2018 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.variable.search.rest;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.obiba.mica.core.domain.DocumentSet;
import org.obiba.mica.dataset.service.VariableSetService;
import org.obiba.mica.web.model.Dtos;
import org.obiba.mica.web.model.Mica;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/variables/set/{id}")
@Scope("request")
@RequiresAuthentication
public class PublishedDatasetVariablesSetResource {

  @Inject
  private VariableSetService variableSetService;

  @Inject
  private Dtos dtos;

  @PathParam("id")
  private String id;

  @GET
  public Mica.DocumentSetDto get() {
    return dtos.asDto(variableSetService.get(id));
  }

  @DELETE
  public Response delete() {
    variableSetService.delete(variableSetService.get(id));
    return Response.ok().build();
  }

  @GET
  @Path("/variables")
  public List<Mica.DatasetVariableDto> getVariables(@QueryParam("from") @DefaultValue("0") int from, @QueryParam("limit") @DefaultValue("10") int limit) {
    return variableSetService.getVariables(variableSetService.get(id), from, limit).stream()
      .map(var -> dtos.asDto(var)).collect(Collectors.toList());
  }

  @GET
  @Path("/variables/_export")
  @Produces(MediaType.TEXT_PLAIN)
  public Response exportVariables() {
    DocumentSet documentSet = variableSetService.get(id);
    StreamingOutput stream = os -> {
      documentSet.getIdentifiers().forEach(id -> {
        try {
          os.write((id + "\n").getBytes());
        } catch (IOException e) {
          // ignore
        }
      });
      os.flush();
    };
    return Response.ok(stream)
      .header("Content-Disposition", String.format("attachment; filename=\"%s-variables.txt\"", id)).build();
  }

  @POST
  @Path("/variables/_import")
  @Consumes(MediaType.TEXT_PLAIN)
  public Response importVariables(String body) {
    if (Strings.isNullOrEmpty(body)) return Response.ok().build();
    variableSetService.addIdentifiers(id, variableSetService.extractIdentifiers(body));
    return Response.ok().build();
  }

  @DELETE
  @Path("/variables")
  public Response deleteVariables() {
    variableSetService.setIdentifiers(id, Lists.newArrayList());
    return Response.ok().build();
  }

}