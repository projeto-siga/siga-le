package br.jus.trf2.siga.ex.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Date;
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
import br.gov.jfrj.sigale.ex.vo.ExDocumentoVO;
import br.jus.trf2.siga.ex.api.ISigaDoc.DocSiglaGetRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.DocSiglaGetResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.IDocSiglaGet;
import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DocSiglaGet implements IDocSiglaGet {

	@Override
	public void run(DocSiglaGetRequest req, DocSiglaGetResponse resp) throws Exception {
		String authorization = TokenCriarPost.assertAuthorization();
		Usuario u = TokenCriarPost.assertUsuario();

		try (ExDB db = ExDB.create(false)) {
			DpPessoa cadastrante = db.getPessoaPorPrincipal(u.usuario);
			DpPessoa titular = cadastrante;
			DpLotacao lotaTitular = cadastrante.getLotacao();

			ExMobilDaoFiltro flt = new ExMobilDaoFiltro();
			flt.setSigla(req.sigla);
			ExMobil mob = db.consultarPorSigla(flt);
			ExDocumento doc = mob.doc();

			Utils.assertAcesso(mob, titular, lotaTitular);

			// Recebimento automático
			if (Ex.getInstance().getComp().podeReceberEletronico(titular, lotaTitular, mob)) {
				try {
					db.upgradeToTransactional();
					Ex.getInstance().getBL().receber(cadastrante, lotaTitular, mob, new Date());
					db.commit();
				} catch (Exception ex) {
					db.rollback(ex);
				}
			}

			// Mostra o último volume de um processo ou a primeira via de um
			// expediente
			if (mob == null || mob.isGeral()) {
				if (mob.getDoc().isFinalizado()) {
					if (doc.isProcesso())
						mob = doc.getUltimoVolume();
					else
						mob = doc.getPrimeiraVia();
				}
			}

			final ExDocumentoVO docVO = new ExDocumentoVO(doc, mob, cadastrante, titular, lotaTitular, true, false);

			Type collectionType = new TypeToken<org.hibernate.proxy.HibernateProxy>() {
			}.getType();
			// Gson gson = new GsonBuilder().registerTypeAdapter(collectionType,
			// new OutroParametroSerializer())
			// .setExclusionStrategies(new
			// ConsultaProcessualExclStrat()).create();
			Gson gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY).create();
			String json = gson.toJson(docVO);

			resp.inputstream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
			resp.contenttype = "application/json";
		}

	}

	@Override
	public String getContext() {
		return "obter documento";
	}

	/**
	 * This TypeAdapter unproxies Hibernate proxied objects, and serializes them
	 * through the registered (or default) TypeAdapter of the base class.
	 */
	private static class HibernateProxyTypeAdapter extends TypeAdapter<HibernateProxy> {

		public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
			@Override
			@SuppressWarnings("unchecked")
			public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
				return (HibernateProxy.class.isAssignableFrom(type.getRawType())
						? (TypeAdapter<T>) new HibernateProxyTypeAdapter(gson) : null);
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
		public void write(JsonWriter out, HibernateProxy value) throws IOException {
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
			Object unproxiedValue = ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation();
			// Serialize the value
			// delegate.write(out, unproxiedValue);
			delegate.write(out, "__OMITIDO__");
		}
	}

}
