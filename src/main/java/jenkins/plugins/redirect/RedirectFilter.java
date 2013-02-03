package jenkins.plugins.redirect;

import hudson.init.Initializer;
import hudson.model.Hudson;
import hudson.util.PluginServletFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedirectFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse rsp = (HttpServletResponse) response;

            String url = req.getServletPath() + req.getPathInfo();

            if (!url.endsWith("/postBuild/acceptBuild"))
			for (RedirectConfig config: Hudson.getInstance().getPlugin(PluginImpl.class).getConfigs()) {
                String target = config.shouldRedirect(url);
                if (target != null) {
                    rsp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                    String queryString = req.getQueryString();
                    target = config.getRedirectTarget() + target;
                    if (queryString != null) target = target + "?" + queryString;
                    rsp.setHeader("Location", target);

                    return;
                }
            }
		}

		chain.doFilter(request, response);
	}

	public void destroy() {
	}

	@Initializer
	public static void init() throws ServletException {
		PluginServletFilter.addFilter(new RedirectFilter());
	}
}
