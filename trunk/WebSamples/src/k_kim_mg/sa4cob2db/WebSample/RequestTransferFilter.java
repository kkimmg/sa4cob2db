package k_kim_mg.sa4cob2db.WebSample;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class RequestTransferFilter implements Filter {
	private FilterConfig config = null;
	private ServletContext context = null;
	@Override
	public void destroy() {
		this.config = null;
		this.context = null;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		Enumeration<String> keys = req.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String val = req.getParameter(key);
			req.setAttribute(key, val);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		this.context = config.getServletContext();
	}

}
