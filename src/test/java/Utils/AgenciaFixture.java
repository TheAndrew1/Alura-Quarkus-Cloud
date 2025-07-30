package Utils;

import domain.Agencia;
import domain.Endereco;
import domain.http.AgenciaHttp;
import domain.http.SituacaoCadastral;

public class AgenciaFixture {
    public static Agencia criarAgencia() {
        Endereco endereco = new Endereco(1, "", "", "", 1);
        return new Agencia(1, "", "", "", endereco);
    }

    public static AgenciaHttp criarAgenciaHttp(SituacaoCadastral situacaoCadastral) {
        return new AgenciaHttp( "Agencia Teste", "Teste Ltda", "123", situacaoCadastral);
    }
}
