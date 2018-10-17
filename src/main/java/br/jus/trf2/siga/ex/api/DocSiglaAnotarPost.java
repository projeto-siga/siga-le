package br.jus.trf2.siga.ex.api;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExDocumento;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.ExPapel;
import br.gov.jfrj.siga.ex.bl.Ex;
import br.gov.jfrj.siga.persistencia.ExMobilDaoFiltro;
import br.jus.trf2.siga.ex.api.ISigaDoc.DocSiglaAnotarPostRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.DocSiglaAnotarPostResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.IDocSiglaAnotarPost;
import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DocSiglaAnotarPost implements IDocSiglaAnotarPost {

	@Override
	public void run(DocSiglaAnotarPostRequest req,
			DocSiglaAnotarPostResponse resp) throws Exception {
		String authorization = TokenCriarPost.assertAuthorization();
		Usuario u = TokenCriarPost.assertUsuario();

		try (ExDB db = ExDB.create(true)) {
			try {
				DpPessoa cadastrante = db.getPessoaPorPrincipal(u.usuario);
				DpLotacao lotaCadastrante = cadastrante.getLotacao();
				DpPessoa titular = cadastrante;
				DpLotacao lotaTitular = cadastrante.getLotacao();

				ExMobilDaoFiltro flt = new ExMobilDaoFiltro();
				flt.setSigla(req.sigla);
				ExMobil mob = db.consultarPorSigla(flt);
				ExDocumento doc = mob.doc();

				Utils.assertAcesso(mob, titular, lotaTitular);

				Ex.getInstance()
						.getBL()
						.anotar(cadastrante, lotaCadastrante, mob, null, null,
								null, null, null, req.anotacao, null);
				db.commit();
				resp.status = "OK";
			} catch (Exception ex) {
				db.rollback(ex);
			}
		}
	}

	@Override
	public String getContext() {
		return "Anotar documento";
	}
}
