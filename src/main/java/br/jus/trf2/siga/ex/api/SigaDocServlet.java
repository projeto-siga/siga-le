package br.jus.trf2.siga.ex.api;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.crivano.swaggerservlet.SwaggerServlet;

import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

public class SigaDocServlet extends SwaggerServlet {
	private static final long serialVersionUID = 1756711359239182178L;

	@Override
	public void initialize(ServletConfig config) throws ServletException {

		super.setAPI(ISigaDoc.class);

		super.setActionPackage("br.jus.trf2.siga.ex.api");

		addPublicProperty("env");
		addPublicProperty("wootric.token", null);
		addPublicProperty("graphviz.url");

		addPrivateProperty("jwt.secret");

		addRestrictedProperty("smtp.remetente");
		addRestrictedProperty("smtp.host");
		addRestrictedProperty("smtp.host.alt", null);
		addRestrictedProperty("smtp.auth", "false");
		addRestrictedProperty("smtp.auth.usuario", null);
		addPrivateProperty("smtp.auth.senha", null);
		addRestrictedProperty("smtp.porta", "25");
		addRestrictedProperty("smtp.destinatario");
		addRestrictedProperty("smtp.assunto");

		addPublicProperty("username.restriction", null);
		addPublicProperty("jwt.issuer");
		addPublicProperty("jwt.secret");

		addPublicProperty("sugestao.smtp.destinatario");
		addPublicProperty("sugestao.smtp.assunto", "Siga-Le: Sugestão");
		addPublicProperty("assijus.endpoint");
	}

	@Override
	public int errorCode(Exception e) {
		return e.getMessage() == null || !e.getMessage().endsWith("(Alerta)") ? super.errorCode(e) : 400;
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
