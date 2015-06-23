package hudson.plugins.flatballs;

import hudson.model.Hudson;
import hudson.model.User;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FlatBallFilter implements Filter {
    final Pattern pathPattern = Pattern.compile("/images/(\\d{2}x\\d{2})/((blue|red|yellow|grey)(_anime)?)\\.(gif|png)$");

    final Logger logger = Logger.getLogger("hudson.plugins.flatballs");

    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) req;
            final HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            final String uri = httpServletRequest.getRequestURI();
            if (uri.endsWith(".gif") || uri.endsWith(".png")) {
                String newImageUrl = mapImage(uri);
                if (newImageUrl != null) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, "Redirecting {0} to {1}", new Object[] { uri, newImageUrl });
                    }
                    RequestDispatcher dispatcher = httpServletRequest.getRequestDispatcher(newImageUrl);
                    dispatcher.forward(httpServletRequest, httpServletResponse);
                    return;
                }
            }
        }
        chain.doFilter(req, resp);
    }

    private String mapImage(String uri) {
        if (uri.contains("plugin/flatballs/")) return null;
        Matcher m;

        if ((m = pathPattern.matcher(uri)).find()) {
            return "/plugin/flatballs/" + m.group(1) + "/" + m.group(2) + ".gif";
        }
        return null;
    }

    public void destroy() {
    }
}
