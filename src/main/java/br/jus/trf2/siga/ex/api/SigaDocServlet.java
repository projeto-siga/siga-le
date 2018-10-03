package br.jus.trf2.siga.ex.api;

import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

import com.crivano.swaggerservlet.SwaggerServlet;
import com.crivano.swaggerservlet.SwaggerUtils;
import com.crivano.swaggerservlet.dependency.TestableDependency;
import com.crivano.swaggerservlet.property.PrivateProperty;
import com.crivano.swaggerservlet.property.PublicProperty;
import com.crivano.swaggerservlet.property.RestrictedProperty;

public class SigaDocServlet extends SwaggerServlet {
	private static final long serialVersionUID = 1756711359239182178L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		super.setAPI(ISigaDoc.class);

		super.setActionPackage("br.jus.trf2.siga.ex.api");

		super.addProperty(new PublicProperty("siga.ex.api.env"));
		super.addProperty(new PublicProperty("siga.ex.api.wootric.token"));

		super.addProperty(new PrivateProperty("siga.ex.api.jwt.secret"));

		super.addProperty(new RestrictedProperty("siga.ex.api.smtp.remetente"));
		super.addProperty(new RestrictedProperty("siga.ex.api.smtp.host"));
		super.addProperty(new RestrictedProperty("siga.ex.api.smtp.host.alt"));
		super.addProperty(new RestrictedProperty("siga.ex.api.smtp.auth"));
		super.addProperty(new RestrictedProperty(
				"siga.ex.api.smtp.auth.usuario"));
		super.addProperty(new PrivateProperty("siga.ex.api.smtp.auth.senha"));
		super.addProperty(new RestrictedProperty("siga.ex.api.smtp.porta"));
		super.addProperty(new RestrictedProperty(
				"siga.ex.api.smtp.destinatario"));
		super.addProperty(new RestrictedProperty("siga.ex.api.smtp.assunto"));

		super.setAuthorizationToProperties(SwaggerUtils.getProperty(
				"siga.ex.api.properties.secret", null));

		// addDependency(new TestableDependency("database", "siga.ex.apids",
		// false, 0, 10000) {
		// @Override
		// public String getUrl() {
		// return SwaggerUtils.getProperty("siga.ex.api.datasource.name",
		// "siga.ex.apids");
		// }
		//
		// @Override
		// public boolean test() throws Exception {
		// return false;
		// // try (Dao dao = new Dao()) {
		// // return dao.obtemData() != null;
		// // }
		// }
		//
		// @Override
		// public boolean isPartial() {
		// return false;
		// }
		// });

	}

	@Override
	public int errorCode(Exception e) {
		return e.getMessage() == null || !e.getMessage().endsWith("(Alerta)") ? super
				.errorCode(e) : 400;
	}

	@Override
	public String getService() {
		return "siga.ex.api";
	}

	@Override
	public String getUser() {
		try {
			Usuario u = TokenCriarPost.assertUsuario();
			return u.usuario;
		} catch (Exception e) {
			return null;
		}
	}

}
