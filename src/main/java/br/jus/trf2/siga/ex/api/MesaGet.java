package br.jus.trf2.siga.ex.api;

import java.util.ArrayList;
import java.util.List;

import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.bl.Mesa;
import br.jus.trf2.siga.ex.api.ISigaDoc.IMesaGet;
import br.jus.trf2.siga.ex.api.ISigaDoc.Marca;
import br.jus.trf2.siga.ex.api.ISigaDoc.MesaGetRequest;
import br.jus.trf2.siga.ex.api.ISigaDoc.MesaGetResponse;
import br.jus.trf2.siga.ex.api.ISigaDoc.MesaItem;
import br.jus.trf2.siga.ex.api.TokenCriarPost.Usuario;

public class MesaGet implements IMesaGet {

	@Override
	public void run(MesaGetRequest req, MesaGetResponse resp) throws Exception {
		String authorization = TokenCriarPost.assertAuthorization();
		Usuario u = TokenCriarPost.assertUsuario();

		try (ExDB db = ExDB.create(false)) {
			DpPessoa cadastrante = db.getPessoaPorPrincipal(u.usuario);

			List<br.gov.jfrj.siga.ex.bl.Mesa.MesaItem> l = Mesa.getMesa(db, cadastrante, cadastrante.getLotacao());
			resp.list = new ArrayList<>();

			// Copiar propriedades para compatibilizar com o tipo MesaItem do
			// SwaggerServlet.
			// Tentei fazer com BeansUtils.copyProperties, mas não funcioncou, acho que por
			// conta do Hibernate.
			// Copiando uma a uma temos a certeza que as propriedades estão iguais nas duas
			// classes.
			for (br.gov.jfrj.siga.ex.bl.Mesa.MesaItem i : l) {
				MesaItem mi = new MesaItem();
				mi.codigo = i.codigo;
				mi.datahora = i.datahora;
				mi.descr = i.descr;
				mi.grupo = i.grupo;
				mi.grupoNome = i.grupoNome;
				mi.grupoOrdem = i.grupoOrdem;
				mi.origem = i.origem;
				mi.sigla = i.sigla;
				mi.tempoRelativo = i.tempoRelativo;
				mi.tipo = i.tipo;

				mi.list = new ArrayList<>();
				for (br.gov.jfrj.siga.ex.bl.Mesa.Marca im : i.list) {
					Marca mim = new Marca();
					mim.daLotacao = im.daLotacao;
					mim.daPessoa = im.daPessoa;
					mim.deOutraPessoa = im.deOutraPessoa;
					mim.icone = im.icone;
					mim.inicio = im.inicio;
					mim.lotacao = im.lotacao;
					mim.nome = im.nome;
					mim.pessoa = im.pessoa;
					mim.termino = im.termino;
					mim.titulo = im.titulo;

					mi.list.add(mim);
				}

				resp.list.add(mi);
			}
		}
	}

	@Override
	public String getContext() {
		return "obter classe processual";
	}
}
