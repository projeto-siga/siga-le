package br.jus.trf2.siga.ex.api;

import java.util.ArrayList;
import java.util.List;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.jus.trf2.siga.ex.api.ISigaDoc.ILotacaoTextoPesquisarGet;
import br.jus.trf2.siga.ex.api.ISigaDoc.LotacaoTextoPesquisarGetRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.LotacaoTextoPesquisarGetResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.ResultadoDePesquisa;
import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

public class LotacaoTextoPesquisarGet implements ILotacaoTextoPesquisarGet {

	@Override
	public void run(LotacaoTextoPesquisarGetRequest req,
			LotacaoTextoPesquisarGetResponse resp) throws Exception {
		String authorization = TokenCriarPost.assertAuthorization();
		Usuario u = TokenCriarPost.assertUsuario();

		resp.list = new ArrayList<>();
		try (ExDB db = ExDB.create(false)) {
			List<DpLotacao> l = db.listarLotacoes(req.texto);

			for (DpLotacao p : l) {
				ResultadoDePesquisa rp = new ResultadoDePesquisa();
				rp.nome = p.getNomeLotacao();
				rp.sigla = p.getSiglaCompleta();
				resp.list.add(rp);
			}
		}

	}

	@Override
	public String getContext() {
		return "selecionar pessoas";
	}
}
