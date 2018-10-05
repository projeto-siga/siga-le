package br.jus.trf2.siga.ex.api;

import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.persistencia.ExMobilDaoFiltro;
import br.jus.trf2.siga.ex.api.ISigaDoc.DocSiglaPesquisarSiglaGetRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.DocSiglaPesquisarSiglaGetResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.IDocSiglaPesquisarSiglaGet;
import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

public class DocSiglaPesquisarSiglaGet implements IDocSiglaPesquisarSiglaGet {

	@Override
	public void run(DocSiglaPesquisarSiglaGetRequest req,
			DocSiglaPesquisarSiglaGetResponse resp) throws Exception {
		Usuario u = TokenCriarPost.assertUsuario();

		try (ExDB db = ExDB.create(false)) {
			DpPessoa cadastrante = db.getPessoaPorPrincipal(u.usuario);
			ExMobilDaoFiltro flt = new ExMobilDaoFiltro();
			flt.setSigla(req.sigla);
			ExMobil mob = db.consultarPorSigla(flt);
			if (mob != null) {
				resp.sigla = mob.getSigla();
				resp.codigo = mob.getCodigoCompacto();
			}
		}
	}

	@Override
	public String getContext() {
		return "pesquisando mobil por sigla";
	}
}
