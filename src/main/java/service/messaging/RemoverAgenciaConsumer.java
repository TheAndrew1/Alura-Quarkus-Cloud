package service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.AgenciaMessage;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import repository.AgenciaRepository;

@ApplicationScoped
public class RemoverAgenciaConsumer {
    private ObjectMapper objectMapper;
    private AgenciaRepository agenciaRepository;

    public RemoverAgenciaConsumer(ObjectMapper objectMapper, AgenciaRepository agenciaRepository) {
        this.objectMapper = objectMapper;
        this.agenciaRepository = agenciaRepository;
    }

    @WithTransaction
    @Incoming("banking-service-channel")
    public Uni<Void> consumirMensagem(String mensagem) {
        return Uni.createFrom().item(() -> {
            try {
                Log.info(mensagem);
                return objectMapper.readValue(mensagem, AgenciaMessage.class);
            } catch (JsonProcessingException ex) {
                Log.error(ex.getMessage());
                throw new RuntimeException();
            }
        }).onItem().transformToUni((agenciaMensagem) -> agenciaRepository.findByCnpj(agenciaMensagem.getCnpj())
                .onItem().ifNotNull().transformToUni((agencia) -> agenciaRepository.deleteById(agencia.getId()))
        ).replaceWithVoid();
    }
}
