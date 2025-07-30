package service;

import Utils.AgenciaFixture;
import domain.Agencia;
import domain.http.SituacaoCadastral;
import exceptions.AgenciaNaoAtivaOuNaoEncontradaException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repository.AgenciaRepository;
import service.http.SituacaoCadastralHttpService;

@QuarkusTest
public class AgenciaServiceTest {
    @InjectMock
    private AgenciaRepository agenciaRepository;

    @InjectMock
    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    @Inject
    AgenciaService agenciaService;

    @Test
    public void deveNaoCadastrarQuandoClientRetornarNull() {
        Agencia agencia = AgenciaFixture.criarAgencia();
        Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(null);

        Assertions.assertThrows(AgenciaNaoAtivaOuNaoEncontradaException.class, () -> agenciaService.cadastrar(agencia));

        Mockito.verify(agenciaRepository, Mockito.never()).persist(agencia);
    }

    @Test
    public void deveNaoCadastrarQuandoClientSituacaoCadastralInativa() {
        Agencia agencia = AgenciaFixture.criarAgencia();
        Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(AgenciaFixture.criarAgenciaHttp(SituacaoCadastral.INATIVO));

        Assertions.assertThrows(AgenciaNaoAtivaOuNaoEncontradaException.class, () -> agenciaService.cadastrar(agencia));

        Mockito.verify(agenciaRepository, Mockito.never()).persist(agencia);
    }

    @Test
    public void deveCadastrarQuandoClientRetornarSituacaoCadastralAtiva() {
        Agencia agencia = AgenciaFixture.criarAgencia();
        Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(AgenciaFixture.criarAgenciaHttp(SituacaoCadastral.ATIVO));

        agenciaRepository.persist(agencia);

        Mockito.verify(agenciaRepository).persist(agencia);
    }
}
