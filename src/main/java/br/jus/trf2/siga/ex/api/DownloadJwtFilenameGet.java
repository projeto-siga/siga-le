package br.jus.trf2.siga.ex.api;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.persistencia.ExMobilDaoFiltro;
import br.jus.trf2.siga.ex.api.ISigaDoc.DownloadJwtFilenameGetRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.DownloadJwtFilenameGetResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.IDownloadJwtFilenameGet;
import br.jus.trf2.siga.ex.api.SigaDocPdfUtils.InputStreamDownload;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.crivano.swaggerservlet.SwaggerServlet;

public class DownloadJwtFilenameGet implements IDownloadJwtFilenameGet {

	@Override
	public void run(DownloadJwtFilenameGetRequest req,
			DownloadJwtFilenameGetResponse resp) throws Exception {
		Map<String, Object> map = verify(req.jwt);
		String username = (String) map.get("username");
		String sigla = (String) map.get("doc");
		String type = (String) map.get("typ");
		String disposition = "attachment".equals(req.disposition) ? "attachment"
				: "inline";
		if (!"download".equals(type))
			throw new Exception("Tipo de token JWT inválido");

		try (ExDB db = ExDB.create(false)) {
			DpPessoa cadastrante = db.getPessoaPorPrincipal(username);
			DpPessoa titular = cadastrante;
			DpLotacao lotaTitular = cadastrante.getLotacao();

			ExMobilDaoFiltro flt = new ExMobilDaoFiltro();
			flt.setSigla(sigla);
			ExMobil mob = db.consultarPorSigla(flt);

			Utils.assertAcesso(mob, titular, lotaTitular);

			InputStreamDownload isd = SigaDocPdfUtils.exibir(mob.getSigla(),
					false, mob.getSigla() + ".pdf", null, null, null, null, true, false, titular,
					lotaTitular, db.consultarDataEHoraDoServidor(),
					SwaggerServlet.getHttpServletRequest(),
					SwaggerServlet.getHttpServletResponse());

			resp.inputstream = isd.is;
			resp.contenttype = isd.contentyType;
			resp.contentdisposition = resp.contentdisposition = disposition
					+ ";filename=" + isd.fileName;
			if (isd.contentLength != null)
				resp.contentlength = (long) isd.contentLength;
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

	public static String jwt(String username, String sigla) {
		final String issuer = Utils.getJwtIssuer();
		final long iat = System.currentTimeMillis() / 1000L;
		final long exp = iat + (10 * 60L); // token expires in 10min

		final JWTSigner signer = new JWTSigner(Utils.getJwtSecret());
		final HashMap<String, Object> claims = new HashMap<String, Object>();
		if (issuer != null)
			claims.put("iss", issuer);
		claims.put("exp", exp);
		claims.put("iat", iat);

		claims.put("username", username);
		claims.put("doc", sigla);
		claims.put("typ", "download");

		final String jwt = signer.sign(claims);
		return jwt;
	}

}
