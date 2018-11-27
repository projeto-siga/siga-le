package br.jus.trf2.siga.ex.api;

import java.util.ArrayList;
import java.util.List;

import br.gov.jfrj.siga.cp.CpAcesso;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.jus.trf2.siga.ex.api.ISigaDoc.AcessoItem;
import br.jus.trf2.siga.ex.api.ISigaDoc.AcessosGetRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.AcessosGetResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.IAcessosGet;
import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

public class AcessosGet implements IAcessosGet {

	@Override
	public void run(AcessosGetRequest req, AcessosGetResponse resp) throws Exception {
		Usuario u = TokenCriarPost.assertUsuario();

		resp.list = new ArrayList<>();

		try (ExDB db = ExDB.create(false)) {
			DpPessoa cadastrante = db.getPessoaPorPrincipal(u.usuario);

			List<CpAcesso> l = db.consultarAcessosRecentes(cadastrante);

			for (CpAcesso a : l) {
				AcessoItem ai = new AcessoItem();
				ai.datahora = a.getDtInicio();
				ai.ip = a.getAuditIP();
				resp.list.add(ai);
			}
		}
	}

	@Override
	public String getContext() {
		return "listar acessos recentes";
	}
}
