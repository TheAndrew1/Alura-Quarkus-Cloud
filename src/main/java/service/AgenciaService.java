package service;

import domain.Agencia;
import domain.http.AgenciaHttp;
import domain.http.SituacaoCadastral;
import exceptions.AgenciaNaoAtivaOuNaoEncontradaException;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import repository.AgenciaRepository;
import service.http.SituacaoCadastralHttpService;

@ApplicationScoped
public class AgenciaService {
    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    @Inject
    private AgenciaRepository agenciaRepository;

    @Inject
    private MeterRegistry meterRegistry;

    @WithTransaction
    @CircuitBreaker(
            requestVolumeThreshold = 5,
            failureRatio = 0.5,
            delay = 2000,
            successThreshold = 2
    )
    @Fallback(fallbackMethod = "chamarFallback")
    public Uni<Void> cadastrar(Agencia agencia) {
        Uni<AgenciaHttp> agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());
        return agenciaHttp
                .onItem().ifNull().failWith(new AgenciaNaoAtivaOuNaoEncontradaException())
                .onItem().transformToUni(item -> persistirSeAtiva(agencia, item));
    }

    private Uni<Void> persistirSeAtiva(Agencia agencia, AgenciaHttp agenciaHttp) {
        if(agenciaHttp.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
            Log.info("A agencia com o CNPJ " + agencia.getCnpj() + " foi cadastrada");
            meterRegistry.counter("agencia_adicionada_counter").increment();
            return agenciaRepository.persist(agencia).replaceWithVoid();
        } else {
            Log.info("A agencia com o CNPJ " + agencia.getCnpj() + " não foi cadastrada");
            meterRegistry.counter("agencia_nao_adicionada_counter").increment();
            return Uni.createFrom().failure(new AgenciaNaoAtivaOuNaoEncontradaException());
        }
    }

    public Uni<Void> chamarFallback(Agencia agencia) {
        Log.info("A agência com o CNPJ " + agencia.getCnpj() + " não foi adicionada pois houve um erro");
        return Uni.createFrom().nullItem();
    }

    @WithSession
    public Uni<Agencia> buscarPorId(Long id) {
        return agenciaRepository.findById(id);
    }

    @WithTransaction
    public Uni<Void> deletar(Long id) {
        Log.info("A agencia com o id " + id + " foi deletada");
        return agenciaRepository.deleteById(id).replaceWithVoid();
    }

    @WithTransaction
    public Uni<Void> alterar(Agencia agencia) {
        return agenciaRepository.update(
                "nome = ?1, razaoSocial = ?2, cnpj = ?3 WHERE id = ?4",
                agencia.getNome(), agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getId()
        ).replaceWithVoid();
    }
}
