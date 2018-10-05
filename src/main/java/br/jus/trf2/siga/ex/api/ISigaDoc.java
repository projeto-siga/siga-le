package br.jus.trf2.siga.ex.api;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.crivano.swaggerservlet.ISwaggerMethod;
import com.crivano.swaggerservlet.ISwaggerModel;
import com.crivano.swaggerservlet.ISwaggerRequest;
import com.crivano.swaggerservlet.ISwaggerResponse;
import com.crivano.swaggerservlet.ISwaggerResponseFile;
import com.crivano.swaggerservlet.ISwaggerResponsePayload;

public interface ISigaDoc {
	public class MesaItem implements ISwaggerModel {
		public String tipo;
		public Date datahora;
		public String tempoRelativo;
		public String codigo;
		public String sigla;
		public String descr;
		public String origem;
		public String grupo;
		public String grupoNome;
		public String grupoOrdem;
		public List<Marca> list;
	}

	public class Marca implements ISwaggerModel {
		public String pessoa;
		public String lotacao;
		public String nome;
		public String icone;
		public String titulo;
		public Date inicio;
		public Date termino;
		public Boolean daPessoa;
		public Boolean deOutraPessoa;
		public Boolean daLotacao;
	}

	public class Acao implements ISwaggerModel {
		public String nome;
		public String icone;
		public Boolean ativa;
	}

	public class Documento implements ISwaggerModel {
		public String sigla;
		public String html;
		public String data;
		public String descr;
		public List<Marca> marca;
		public List<Acao> acao;
	}

	public class Error implements ISwaggerModel {
		public String errormsg;
	}

	public class MesaGetRequest implements ISwaggerRequest {
	}

	public class MesaGetResponse implements ISwaggerResponse {
		public List<MesaItem> list;
	}

	public interface IMesaGet extends ISwaggerMethod {
		public void run(MesaGetRequest req, MesaGetResponse resp)
				throws Exception;
	}

	public class DocSiglaGetRequest implements ISwaggerRequest {
		public String sigla;
	}

	public class DocSiglaGetResponse implements ISwaggerResponse,
			ISwaggerResponseFile {
		public String contenttype = "application/pdf";
		public String contentdisposition = "attachment";

		public Long contentlength;
		public InputStream inputstream;

		public String getContenttype() {
			return contenttype;
		}

		public void setContenttype(String contenttype) {
			this.contenttype = contenttype;
		}

		public String getContentdisposition() {
			return contentdisposition;
		}

		public void setContentdisposition(String contentdisposition) {
			this.contentdisposition = contentdisposition;
		}

		public Long getContentlength() {
			return contentlength;
		}

		public void setContentlength(Long contentlength) {
			this.contentlength = contentlength;
		}

		public InputStream getInputstream() {
			return inputstream;
		}

		public void setInputstream(InputStream inputstream) {
			this.inputstream = inputstream;
		}
	}

	public interface IDocSiglaGet extends ISwaggerMethod {
		public void run(DocSiglaGetRequest req, DocSiglaGetResponse resp)
				throws Exception;
	}

	public class DocSiglaPdfGetRequest implements ISwaggerRequest {
		public String sigla;
	}

	public class DocSiglaPdfGetResponse implements ISwaggerResponse {
		public String jwt;
	}

	public interface IDocSiglaPdfGet extends ISwaggerMethod {
		public void run(DocSiglaPdfGetRequest req, DocSiglaPdfGetResponse resp)
				throws Exception;
	}

	public class DocSiglaPdfCompletoGetRequest implements ISwaggerRequest {
		public String sigla;
	}

	public class DocSiglaPdfCompletoGetResponse implements ISwaggerResponse,
			ISwaggerResponsePayload {
		public byte[] payload;
		public String contenttype;
	}

	public interface IDocSiglaPdfCompletoGet extends ISwaggerMethod {
		public void run(DocSiglaPdfCompletoGetRequest req,
				DocSiglaPdfCompletoGetResponse resp) throws Exception;
	}

	public class DocSiglaMovIdPdfGetRequest implements ISwaggerRequest {
		public String sigla;
		public String id;
	}

	public class DocSiglaMovIdPdfGetResponse implements ISwaggerResponse {
		public String jwt;
	}

	public interface IDocSiglaMovIdPdfGet extends ISwaggerMethod {
		public void run(DocSiglaMovIdPdfGetRequest req,
				DocSiglaMovIdPdfGetResponse resp) throws Exception;
	}

	public class DownloadJwtFilenameGetRequest implements ISwaggerRequest {
		public String jwt;
		public String filename;
		public String disposition;
	}

	public class DownloadJwtFilenameGetResponse implements ISwaggerResponse,
			ISwaggerResponseFile {
		public String contenttype = "application/pdf";
		public String contentdisposition = "attachment";

		public Long contentlength;
		public InputStream inputstream;

		public String getContenttype() {
			return contenttype;
		}

		public void setContenttype(String contenttype) {
			this.contenttype = contenttype;
		}

		public String getContentdisposition() {
			return contentdisposition;
		}

		public void setContentdisposition(String contentdisposition) {
			this.contentdisposition = contentdisposition;
		}

		public Long getContentlength() {
			return contentlength;
		}

		public void setContentlength(Long contentlength) {
			this.contentlength = contentlength;
		}

		public InputStream getInputstream() {
			return inputstream;
		}

		public void setInputstream(InputStream inputstream) {
			this.inputstream = inputstream;
		}
	}

	public interface IDownloadJwtFilenameGet extends ISwaggerMethod {
		public void run(DownloadJwtFilenameGetRequest req,
				DownloadJwtFilenameGetResponse resp) throws Exception;
	}

	public class DocSiglaAssinarComSenhaPostRequest implements ISwaggerRequest {
		public String sigla;
		public String username;
		public String password;
	}

	public class DocSiglaAssinarComSenhaPostResponse implements
			ISwaggerResponse {
		public String status;
	}

	public interface IDocSiglaAssinarComSenhaPost extends ISwaggerMethod {
		public void run(DocSiglaAssinarComSenhaPostRequest req,
				DocSiglaAssinarComSenhaPostResponse resp) throws Exception;
	}

	public class DocSiglaTramitarPostRequest implements ISwaggerRequest {
		public String sigla;
		public String lotacao;
		public String matricula;
	}

	public class DocSiglaTramitarPostResponse implements ISwaggerResponse {
		public String status;
	}

	public interface IDocSiglaTramitarPost extends ISwaggerMethod {
		public void run(DocSiglaTramitarPostRequest req,
				DocSiglaTramitarPostResponse resp) throws Exception;
	}

	public class DocSiglaPesquisarSiglaGetRequest implements ISwaggerRequest {
		public String sigla;
		public String matricula;
	}

	public class DocSiglaPesquisarSiglaGetResponse implements ISwaggerResponse {
		public String codigo;
		public String sigla;
	}

	public interface IDocSiglaPesquisarSiglaGet extends ISwaggerMethod {
		public void run(DocSiglaPesquisarSiglaGetRequest req,
				DocSiglaPesquisarSiglaGetResponse resp) throws Exception;
	}

	public class SugestaoPostRequest implements ISwaggerRequest {
		public String nome;
		public String email;
		public String mensagem;
	}

	public class SugestaoPostResponse implements ISwaggerResponse {
		public String status;
	}

	public interface ISugestaoPost extends ISwaggerMethod {
		public void run(SugestaoPostRequest req, SugestaoPostResponse resp)
				throws Exception;
	}

	public class TokenCriarPostRequest implements ISwaggerRequest {
		public String username;
		public String password;
	}

	public class TokenCriarPostResponse implements ISwaggerResponse {
		public String id_token;
	}

	public interface ITokenCriarPost extends ISwaggerMethod {
		public void run(TokenCriarPostRequest req, TokenCriarPostResponse resp)
				throws Exception;
	}

}