package Utils;

import domain.Agencia;
import domain.Endereco;
import domain.http.AgenciaHttp;
import domain.http.SituacaoCadastral;
import io.smallrye.mutiny.Uni;

public class AgenciaFixture {
    public static Agencia criarAgencia() {
        Endereco endereco = new Endereco(1, "", "", "", 1);
        return new Agencia(1L, "", "", "", endereco);
    }

    public static Uni<AgenciaHttp> criarAgenciaHttp(SituacaoCadastral situacaoCadastral) {
        return Uni.createFrom().item(new AgenciaHttp( "Agencia Teste", "Teste Ltda", "123", situacaoCadastral));
    }
}
