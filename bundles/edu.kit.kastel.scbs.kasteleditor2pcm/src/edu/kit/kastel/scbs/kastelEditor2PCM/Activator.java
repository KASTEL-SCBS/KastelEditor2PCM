package edu.kit.kastel.scbs.kastelEditor2PCM;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


public class Activator extends AbstractUIPlugin {

private static Activator instance;
	
	@Override
	public void start(BundleContext context) throws Exception {
		setInstance(this);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		setInstance(null);
	}

	protected static String getSymbolicName() {
		return Optional.ofNullable(instance).map(Plugin::getBundle).map(Bundle::getSymbolicName).orElse(null);
	}
	
	public static URI getEMFUri(String relativePath) {
		return URI.createPlatformPluginURI("/" + getSymbolicName() + "/" + relativePath, false);
	}
	
	public static URL getFileURL(String relativePath) {
		try {
			return new URL(getEMFUri(relativePath).toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void setInstance(Activator activator) {
		instance = activator;
	}

}
