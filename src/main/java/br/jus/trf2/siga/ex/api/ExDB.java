package br.jus.trf2.siga.ex.api;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;

import org.hibernate.Query;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.bl.Ex;
import br.gov.jfrj.siga.hibernate.ExDao;
import br.gov.jfrj.siga.hibernate.ExStarter;
import br.gov.jfrj.siga.hibernate.ext.IMontadorQuery;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.model.dao.ModeloDao;
import br.gov.jfrj.siga.persistencia.ExMobilDaoFiltro;

public class ExDB extends ExDao implements AutoCloseable {
	public static EntityManagerFactory emf;

	boolean transactional;

	public List listarDocumentosPorPessoaOuLotacao(DpPessoa titular,
			DpLotacao lotaTitular) {

		long tempoIni = System.nanoTime();
		Query query = getSessao()
				.createQuery(
						"select marca, marcador, mobil from ExMarca marca"
								+ " inner join marca.cpMarcador marcador"
								+ " inner join marca.exMobil mobil"
								+ " where (marca.dtIniMarca is null or marca.dtIniMarca < sysdate)"
								+ " and (marca.dtFimMarca is null or marca.dtFimMarca > sysdate)"
								+ (titular != null ? " and (marca.dpPessoaIni = :titular)"
										: " and (marca.dpLotacaoIni = :lotaTitular)"));

		if (titular != null)
			query.setLong("titular", titular.getIdPessoaIni());
		else if (lotaTitular != null)
			query.setLong("lotaTitular", lotaTitular.getIdLotacaoIni());

		List l = query.list();
		long tempoTotal = System.nanoTime() - tempoIni;
		System.out.println("consultarPorFiltroOtimizado: " + tempoTotal
				/ 1000000 + " ms -> " + query + ", resultado: " + l);
		return l;
	}

	public static ExDB create(boolean transactional) {
		System.out.println("*****************PASSEI NO EXDB**************");
		if (emf == null)
			emf = Persistence.createEntityManagerFactory("default-ex");

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
