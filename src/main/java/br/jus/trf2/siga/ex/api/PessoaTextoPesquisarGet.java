package br.jus.trf2.siga.ex.api;

import java.util.ArrayList;
import java.util.List;

import br.gov.jfrj.siga.dp.DpPessoa;
import br.jus.trf2.siga.ex.api.ISigaDoc.IPessoaTextoPesquisarGet;
import br.jus.trf2.siga.ex.api.ISigaDoc.PessoaTextoPesquisarGetRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.PessoaTextoPesquisarGetResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.ResultadoDePesquisa;
import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

public class PessoaTextoPesquisarGet implements IPessoaTextoPesquisarGet {

	@Override
	public void run(PessoaTextoPesquisarGetRequest req,
			PessoaTextoPesquisarGetResponse resp) throws Exception {
		String authorization = TokenCriarPost.assertAuthorization();
		Usuario u = TokenCriarPost.assertUsuario();

		resp.list = new ArrayList<>();
		try (ExDB db = ExDB.create(false)) {
			List<DpPessoa> l = db.listarPessoas(req.texto);

			for (DpPessoa p : l) {
				ResultadoDePesquisa rp = new ResultadoDePesquisa();
				rp.nome = p.getNomePessoa();
				rp.sigla = p.getSigla();
				resp.list.add(rp);
			}
		}

	}

	@Override
	public String getContext() {
		return "selecionar pessoas";
	}
}
