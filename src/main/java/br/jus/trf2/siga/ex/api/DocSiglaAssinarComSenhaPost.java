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
import br.jus.trf2.siga.ex.api.ISigaDoc.DocSiglaAssinarComSenhaPostRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.DocSiglaAssinarComSenhaPostResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.IDocSiglaAssinarComSenhaPost;
import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DocSiglaAssinarComSenhaPost implements
		IDocSiglaAssinarComSenhaPost {

	@Override
	public void run(DocSiglaAssinarComSenhaPostRequest req,
			DocSiglaAssinarComSenhaPostResponse resp) throws Exception {
		String authorization = TokenCriarPost.assertAuthorization();
		Usuario u = TokenCriarPost.assertUsuario();

		try (ExDB db = ExDB.create(true)) {
			try {
				DpPessoa cadastrante = db.getPessoaPorPrincipal(u.usuario);
				DpPessoa titular = cadastrante;
				DpLotacao lotaTitular = cadastrante.getLotacao();

				ExMobilDaoFiltro flt = new ExMobilDaoFiltro();
				flt.setSigla(req.sigla);
				ExMobil mob = db.consultarPorSigla(flt);
				ExDocumento doc = mob.doc();

				assertAcesso(mob, titular, lotaTitular);

				if (!Ex.getInstance().getComp()
						.podeAssinarComSenha(titular, lotaTitular, mob))
					throw new Exception("O documento " + req.sigla
							+ " não pode ser assinado com senha por "
							+ titular.getSiglaCompleta() + "/"
							+ lotaTitular.getSiglaCompleta());

				Ex.getInstance()
						.getBL()
						.assinarDocumentoComSenha(cadastrante, lotaTitular,
								doc, null, req.username, req.password, titular,
								false, null, false);

				db.commit();
				resp.status = "OK";
			} catch (Exception ex) {
				db.rollback(ex);
			}
		}
	}

	private void assertAcesso(final ExMobil mob, DpPessoa titular,
			DpLotacao lotaTitular) throws Exception {
		if (!Ex.getInstance().getComp()
				.podeAcessarDocumento(titular, lotaTitular, mob)) {
			String s = "";
			s += mob.doc().getListaDeAcessosString();
			s = "(" + s + ")";
			s = " " + mob.doc().getExNivelAcessoAtual().getNmNivelAcesso()
					+ " " + s;

			Map<ExPapel, List<Object>> mapa = mob.doc().getPerfis();
			boolean isInteressado = false;

			for (ExPapel exPapel : mapa.keySet()) {
				Iterator<Object> it = mapa.get(exPapel).iterator();

				if ((exPapel != null)
						&& (exPapel.getIdPapel() == exPapel.PAPEL_INTERESSADO)) {
					while (it.hasNext() && !isInteressado) {
						Object item = it.next();
						isInteressado = item.toString().equals(
								titular.getSigla()) ? true : false;
					}
				}

			}

			if (mob.doc().isSemEfeito()) {
				if (!mob.doc().getCadastrante().equals(titular)
						&& !mob.doc().getSubscritor().equals(titular)
						&& !isInteressado) {
					throw new AplicacaoException("Documento " + mob.getSigla()
							+ " cancelado ");
				}
			} else {
				throw new AplicacaoException("Documento " + mob.getSigla()
						+ " inacessível ao usuário " + titular.getSigla() + "/"
						+ lotaTitular.getSiglaCompleta() + "." + s);
			}
		}
	}

	@Override
	public String getContext() {
		return "obter classe processual";
	}

	/**
	 * This TypeAdapter unproxies Hibernate proxied objects, and serializes them
	 * through the registered (or default) TypeAdapter of the base class.
	 */
	private static class HibernateProxyTypeAdapter extends
			TypeAdapter<HibernateProxy> {

		public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
			@Override
			@SuppressWarnings("unchecked")
			public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
				return (HibernateProxy.class
						.isAssignableFrom(type.getRawType()) ? (TypeAdapter<T>) new HibernateProxyTypeAdapter(
						gson) : null);
			}
		};
		private final Gson context;

		private HibernateProxyTypeAdapter(Gson context) {
			this.context = context;
		}

		@Override
		public HibernateProxy read(JsonReader in) throws IOException {
			throw new UnsupportedOperationException("Not supported");
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void write(JsonWriter out, HibernateProxy value)
				throws IOException {
			if (value == null) {
				out.nullValue();
				return;
			}
			// Retrieve the original (not proxy) class
			Class<?> baseType = Hibernate.getClass(value);
			// Get the TypeAdapter of the original class, to delegate the
			// serialization
			TypeAdapter delegate = context.getAdapter(TypeToken.get(baseType));
			// Get a filled instance of the original class
			Object unproxiedValue = ((HibernateProxy) value)
					.getHibernateLazyInitializer().getImplementation();
			// Serialize the value
			// delegate.write(out, unproxiedValue);
			delegate.write(out, "__OMITIDO__");
		}
	}

}
