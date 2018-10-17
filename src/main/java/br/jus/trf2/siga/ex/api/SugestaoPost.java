package br.jus.trf2.siga.ex.api;

import br.jus.trf2.siga.ex.api.ISigaDoc.ISugestaoPost;
import br.jus.trf2.siga.ex.api.ISigaDoc.SugestaoPostRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.SugestaoPostResponse;

import com.crivano.swaggerservlet.SwaggerUtils;

public class SugestaoPost implements ISugestaoPost {

	@Override
	public void run(SugestaoPostRequest req, SugestaoPostResponse resp)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("Nome: ");
		sb.append(req.nome);
		sb.append("\nEmail: ");
		sb.append(req.email);
		sb.append("\n\nMensagem: ");
		sb.append(req.mensagem);
		Correio.enviar(SwaggerUtils.getRequiredProperty(
				"siga.sugestao.smtp.destinatario",
				"Não foi especificado o destinatário do email de sugestões.",
				false), SwaggerUtils.getProperty("siga.sugestao.smtp.assunto",
				"Siga-Le: Sugestão"), sb.toString(), null, null, null);
	}

	@Override
	public String getContext() {
		return "enviar sugestao";
	}

}
