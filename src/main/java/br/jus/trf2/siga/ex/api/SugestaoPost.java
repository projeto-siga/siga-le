package br.jus.trf2.siga.ex.api;

import com.crivano.swaggerservlet.SwaggerServlet;

import br.jus.trf2.siga.ex.api.ISigaDoc.ISugestaoPost;
import br.jus.trf2.siga.ex.api.ISigaDoc.SugestaoPostRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.SugestaoPostResponse;

public class SugestaoPost implements ISugestaoPost {

	@Override
	public void run(SugestaoPostRequest req, SugestaoPostResponse resp) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("Nome: ");
		sb.append(req.nome);
		sb.append("\nEmail: ");
		sb.append(req.email);
		sb.append("\n\nMensagem: ");
		sb.append(req.mensagem);
		Correio.enviar(SwaggerServlet.getProperty("sugestao.smtp.destinatario"),
				SwaggerServlet.getProperty("sugestao.smtp.assunto"), sb.toString(), null, null, null);
	}

	@Override
	public String getContext() {
		return "enviar sugestao";
	}

}
