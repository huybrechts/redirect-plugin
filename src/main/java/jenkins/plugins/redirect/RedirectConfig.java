package jenkins.plugins.redirect;

import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Set;
import java.util.TreeSet;

public class RedirectConfig {

	private final String redirectTarget;
	private final String jobsText;
	private final String viewsText;
	private final String nodesText;

	private transient Set<String> nodes;
	private transient Set<String> views;
	private transient Set<String> jobs;

	@DataBoundConstructor
	public RedirectConfig(String redirectTarget, String jobsText, String viewsText, String nodesText) {
		this.redirectTarget = redirectTarget;
		this.jobsText = jobsText;
		this.viewsText = viewsText;
		this.nodesText = nodesText;
	}

	public String getRedirectTarget() {
		return redirectTarget;
	}

	public String getJobsText() {
		return jobsText;
	}

	public String getViewsText() {
		return viewsText;
	}

	public String getNodesText() {
		return nodesText;
	}

	public Set<String> getNodes() {
		if (nodes == null) {
			nodes = split(nodesText);
		}
		return nodes;
	}
	public Set<String> getJobs() {
		if (jobs == null) {
			jobs = split(jobsText);
		}
		return jobs;
	}

	public Set<String> getViews() {
		if (views == null) {
			views = split(viewsText);
		}
		return views;
	}

	private Set<String> split(String text) {
		Set<String> set = new TreeSet<String>();
		if (text != null) for (String s: text.split("\n")) set.add(s.trim());
		return set;
	}

    public String shouldRedirect(String path) {
        if (path.startsWith("/view/")) {
            String rest = path.substring(6);
            int i = rest.indexOf('/');
            String view = i < 0 ? rest : rest.substring(0, i);
            if (getViews().contains(view)) {
                return path;
            } else {
                return redirect(rest.substring(view.length()), "/job/", getJobs());
            }
        }
        String result = redirect(path, "/job/", getJobs());
        if (result != null) return result;
        result = redirect(path, "/computer/", getNodes());
        return result;
    }

    private String redirect(String url, String prefix, Set<String> collection) {
        if (url.startsWith(prefix)) {
            int l = prefix.length();
            int i = url.indexOf('/', l);
            String item = i < 0 ? url.substring(l) : url.substring(l, i);

            return collection.contains(item) ? url : null;
        } else {
            return null;
        }
    }
}
