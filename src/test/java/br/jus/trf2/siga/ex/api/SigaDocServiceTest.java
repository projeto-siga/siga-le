package br.jus.trf2.siga.ex.api;

import com.crivano.swaggerservlet.SwaggerTestSupport;

public class SigaDocServiceTest extends SwaggerTestSupport {

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getAPI() {
		return SigaDocServlet.class;
	}

	@Override
	protected String getPackage() {
		// TODO Auto-generated method stub
		return "br.jus.trf2.siga.ex.api";
	}

	public void testNothing_Simple_Success() {
		assertEquals("1", "1");
	}

}
