package service.http;

import domain.http.AgenciaHttp;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/situacao-cadastral")
@RegisterRestClient(configKey = "situacao-cadastral-api")
public interface SituacaoCadastralHttpService {
    @GET
    @Path("{cnpj}")
    Uni<AgenciaHttp> buscarPorCnpj(String cnpj);
}
