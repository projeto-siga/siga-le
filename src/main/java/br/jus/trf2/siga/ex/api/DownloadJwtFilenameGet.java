package br.jus.trf2.siga.ex.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import br.jus.trf2.siga.ex.api.ISigaDoc.DownloadJwtFilenameGetRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.DownloadJwtFilenameGetResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.IDownloadJwtFilenameGet;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

public class DownloadJwtFilenameGet implements IDownloadJwtFilenameGet {

	@Override
	public void run(DownloadJwtFilenameGetRequest req,
			DownloadJwtFilenameGetResponse resp) throws Exception {
		Map<String, Object> map = verify(req.jwt);
		String username = (String) map.get("username");
		String name = (String) map.get("name");
		String file = (String) map.get("file");
		String numProc = (String) map.get("proc");
		String numDoc = (String) map.get("doc");
		String orgao = (String) map.get("orgao");
		String type = (String) map.get("typ");
		String text = (String) map.get("text");
		String cargo = (String) map.get("cargo");
		String empresa = (String) map.get("empresa");
		String unidade = (String) map.get("unidade");
		String disposition = "attachment".equals(req.disposition) ? "attachment"
				: "inline";
		if (!"download".equals(type))
			throw new Exception("Tipo de token JWT inválido");
		if (text != null) {
			byte[] pdf = null;
			resp.contentdisposition = "inline";
			resp.contentlength = (long) pdf.length;
			resp.contenttype = "application/pdf";
			resp.inputstream = new ByteArrayInputStream(pdf);
		} else if (numDoc != null) {
			// Peça Processual
			byte[] ab = null;
			resp.contentdisposition = disposition + ";filename=" + numProc
					+ "-peca-" + numDoc + ".pdf";
			resp.contentlength = (long) ab.length;
			resp.inputstream = new ByteArrayInputStream(ab);
		}
	}

	@Override
	public String getContext() {
		return "obter arquivo";
	}

	public static Map<String, Object> verify(String jwt)
			throws InvalidKeyException, NoSuchAlgorithmException,
			IllegalStateException, SignatureException, IOException,
			JWTVerifyException {
		final JWTVerifier verifier = new JWTVerifier(Utils.getJwtSecret());
		Map<String, Object> map;
		map = verifier.verify(jwt);
		return map;
	}

	public static String jwt(String origin, String username, String nome,
			String orgao, String processo, String documento, String arquivo,
			String texto, String cargo, String empresa, String unidade) {
		final String issuer = Utils.getJwtIssuer();
		final long iat = System.currentTimeMillis() / 1000L; // issued at claim
		// token expires in 10min or 12h
		final long exp = iat + (documento != null ? 10 * 60L : 12 * 60 * 60L);

		final JWTSigner signer = new JWTSigner(Utils.getJwtSecret());
		final HashMap<String, Object> claims = new HashMap<String, Object>();
		if (issuer != null)
			claims.put("iss", issuer);
		claims.put("exp", exp);
		claims.put("iat", iat);

		if (origin != null)
			claims.put("origin", origin);
		claims.put("username", username);
		if (nome != null)
			claims.put("name", nome);
		if (orgao != null)
			claims.put("orgao", orgao);
		if (processo != null)
			claims.put("proc", processo);
		if (documento != null)
			claims.put("doc", documento);
		if (arquivo != null)
			claims.put("file", arquivo);
		if (texto != null)
			claims.put("text", texto);
		if (cargo != null)
			claims.put("cargo", cargo);
		if (empresa != null)
			claims.put("empresa", empresa);
		if (unidade != null)
			claims.put("unidade", unidade);
		claims.put("typ", "download");

		final String jwt = signer.sign(claims);
		return jwt;
	}

}
