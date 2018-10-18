package br.jus.trf2.siga.ex.api;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.persistencia.ExMobilDaoFiltro;
import br.jus.trf2.siga.ex.api.ISigaDoc.DocSiglaPdfCompletoGetRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.DocSiglaPdfCompletoGetResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.IDocSiglaPdfCompletoGet;
import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

public class DocSiglaPdfCompletoGet implements IDocSiglaPdfCompletoGet {

	@Override
	public void run(DocSiglaPdfCompletoGetRequest req,
			DocSiglaPdfCompletoGetResponse resp) throws Exception {
		Usuario u = TokenCriarPost.assertUsuario();

		try (ExDB db = ExDB.create(false)) {
			DpPessoa cadastrante = db.getPessoaPorPrincipal(u.usuario);
			DpPessoa titular = cadastrante;
			DpLotacao lotaTitular = cadastrante.getLotacao();

			ExMobilDaoFiltro flt = new ExMobilDaoFiltro();
			flt.setSigla(req.sigla);
			ExMobil mob = db.consultarPorSigla(flt);

			Utils.assertAcesso(mob, titular, lotaTitular);

			resp.jwt = DownloadJwtFilenameGet.jwt(u.usuario,
					mob.getCodigoCompacto());
		}
	}

	@Override
	public String getContext() {
		return "obter documento";
	}

}
