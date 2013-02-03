package jenkins.plugins.redirect;

import com.sun.org.apache.bcel.internal.generic.NEW;
import hudson.Plugin;
import hudson.XmlFile;
import hudson.model.Hudson;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PluginImpl extends Plugin {

	private List<RedirectConfig> configs;

	public List<RedirectConfig> getConfigs() {
		return configs;
	}

	@Override
	public void start() throws Exception {
		try {
			File configFile = getConfigFile();
			if (configFile.exists()) {
                configs = (List<RedirectConfig>) new XmlFile(getConfigFile()).read();
            } else {
                configs = Collections.emptyList();
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File getConfigFile() {
		return new File(Hudson.getInstance().getRootDir(), "redirect.xml");
	}

	public HttpResponse doConfigSubmit(StaplerRequest req) throws ServletException, IOException {
		configs = req.bindJSONToList(RedirectConfig.class, req.getSubmittedForm().get("configs"));
		new XmlFile(getConfigFile()).write(configs);

		return HttpResponses.forwardToPreviousPage();
	}
}
