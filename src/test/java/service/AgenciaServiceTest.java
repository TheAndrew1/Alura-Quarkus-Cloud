package service;

import domain.Agencia;
import domain.Endereco;
import domain.http.AgenciaHttp;
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
        Agencia agencia = criarAgencia();
        Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(null);

        Assertions.assertThrows(AgenciaNaoAtivaOuNaoEncontradaException.class, () -> agenciaService.cadastrar(agencia));

        Mockito.verify(agenciaRepository, Mockito.never()).persist(agencia);
    }

    private Agencia criarAgencia() {
        Endereco endereco = new Endereco(1, "", "", "", 1);
        return new Agencia(1, "", "", "", endereco);
    }

    @Test
    public void deveCadastrarQuandoClientRetornarSituacaoCadastralAtiva() {
        Agencia agencia = criarAgencia();
        Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(criarAgenciaHttp());

        agenciaRepository.persist(agencia);

        Mockito.verify(agenciaRepository).persist(agencia);
    }

    private AgenciaHttp criarAgenciaHttp() {
        return new AgenciaHttp( "Agencia Teste", "Teste Ltda", "123", SituacaoCadastral.ATIVO);
    }
}
