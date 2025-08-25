package controller;

import domain.Agencia;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.RestResponse;
import service.AgenciaService;

@Path("/agencias")
@Transactional
public class AgenciaController {
    @Inject
    private AgenciaService agenciaService;

    @POST
    @NonBlocking
    @Transactional
    public Uni<RestResponse<Void>> cadastrar(Agencia agencia, @Context UriInfo uriInfo) {
        return this.agenciaService.cadastrar(agencia).replaceWith(RestResponse.created(uriInfo.getAbsolutePath()));
    }

    @GET
    @Path("{id}")
    public Uni<RestResponse<Agencia>> buscarPorId(Long id) {
        return this.agenciaService.buscarPorId(id).onItem().transform(RestResponse::ok);
    }

    @DELETE
    @Transactional
    @Path("{id}")
    public Uni<RestResponse<Void>> deletar(Long id) {
        return this.agenciaService.deletar(id).replaceWith(RestResponse::ok);
    }

    @PUT
    @Transactional
    public Uni<RestResponse<Void>> alterar(Agencia agencia) {
        return this.agenciaService.alterar(agencia).onItem().transform(RestResponse::ok);
    }
}
