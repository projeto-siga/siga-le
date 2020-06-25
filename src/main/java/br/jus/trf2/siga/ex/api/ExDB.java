package br.jus.trf2.siga.ex.api;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletException;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.bl.Ex;
import br.gov.jfrj.siga.hibernate.ExDao;
import br.gov.jfrj.siga.hibernate.ExStarter;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.model.dao.ModeloDao;

public class ExDB extends ExDao implements AutoCloseable {
	public static EntityManagerFactory emf;

	boolean transactional;

	@SuppressWarnings("unchecked")
	public List<DpPessoa> listarPessoas(String texto) {
		if (texto == null || texto.trim().length() == 0)
			return new ArrayList<>();
		Query query = em().createQuery(
						"from DpPessoa pes "
								+ "  where ((upper(pes.nomePessoaAI) like upper('%' || :nome || '%')) or ((pes.sesbPessoa || pes.matricula) like upper('%' || :nome || '%')))"
								+ "   	and pes.dataFimPessoa = null"
								+ "   	order by pes.nomePessoa");
		texto = texto.replaceAll("\\s+", "%");
		query.setParameter("nome", texto);

		final List<DpPessoa> l = query.getResultList();
		return l;
	}

	@SuppressWarnings("unchecked")
	public List<DpLotacao> listarLotacoes(String texto) {
		if (texto == null || texto.trim().length() == 0)
			return new ArrayList<>();
		Query query = em()
				.createQuery(
						"select lot from DpLotacao lot join lot.orgaoUsuario org "
								+ "  where ((upper(lot.nomeLotacaoAI) like upper('%' || :nome || '%')) or (org.siglaOrgaoUsu || upper(lot.siglaLotacao) like upper('%' || :nome || '%')))"
								+ "   	and lot.dataFimLotacao = null"
								+ "   	order by lot.nomeLotacao");
		texto = texto.replaceAll("\\s+", "%");
		query.setParameter("nome", texto);

		final List<DpLotacao> l = query.getResultList();
		return l;
	}

	public List listarDocumentosPorPessoaOuLotacao(DpPessoa titular,
			DpLotacao lotaTitular) {

		long tempoIni = System.nanoTime();
		Query query = em()
				.createQuery(
						"select marca, marcador, mobil from ExMarca marca"
								+ " inner join marca.cpMarcador marcador"
								+ " inner join marca.exMobil mobil"
								+ " where (marca.dtIniMarca is null or marca.dtIniMarca < sysdate)"
								+ " and (marca.dtFimMarca is null or marca.dtFimMarca > sysdate)"
								+ (titular != null ? " and (marca.dpPessoaIni = :titular)"
										: " and (marca.dpLotacaoIni = :lotaTitular)"));

		if (titular != null)
			query.setParameter("titular", titular.getIdPessoaIni());
		else if (lotaTitular != null)
			query.setParameter("lotaTitular", lotaTitular.getIdLotacaoIni());

		List l = query.getResultList();
 		long tempoTotal = System.nanoTime() - tempoIni;
		// System.out.println("consultarPorFiltroOtimizado: " + tempoTotal
		// / 1000000 + " ms -> " + query + ", resultado: " + l);
		return l;
	}

	public static ExDB create(boolean transactional) {
		if (emf == null)
			emf = Persistence.createEntityManagerFactory("default");

		EntityManager em = ExStarter.emf.createEntityManager();
		ContextoPersistencia.setEntityManager(em);

		// Inicialização padronizada
		// CurrentRequest.set(new RequestInfo(config.getServletContext(),
		// (HttpServletRequest) request, (HttpServletResponse) response));

		ModeloDao.freeInstance();
		ExDB db = ModeloDao.getInstance(ExDB.class);
		db.transactional = transactional;

		try {
			Ex.getInstance().getConf().limparCacheSeNecessario();
		} catch (Exception e1) {
			throw new RuntimeException(
					"Não foi possível atualizar o cache de configurações", e1);
		}

		if (db.transactional)
			em.getTransaction().begin();
		return db;
	}
	
	public void upgradeToTransactional() {
		if (transactional)
			return;
		ContextoPersistencia.em().getTransaction().begin();
		transactional = true;
	}

	@Override
	public void close() throws Exception {
		if (transactional)
			throw new RuntimeException(
					"É necessário realizar um 'commit' antes de fechar a conexão");
		EntityManager em = ContextoPersistencia.em();
		if (em != null)
			em.close();
		ContextoPersistencia.setEntityManager(null);
	}

	public void commit() throws Exception {
		if (transactional)
			transactional = false;
		else
			throw new RuntimeException(
					"Não é permitido realizar um 'commit' em uma conexão não transacional");
		EntityManager em = ContextoPersistencia.em();
		if (em != null)
			em.getTransaction().commit();
	}

	public void rollback(Exception e) throws Exception {
		if (transactional)
			transactional = false;
		else
			throw new RuntimeException(
					"Não é permitido realizar um 'commit' em uma conexão não transacional");
		EntityManager em = ContextoPersistencia.em();
		if (em != null && em.getTransaction().isActive())
			em.getTransaction().rollback();
		if (e != null)
			throw new ServletException(e);
	}

}
