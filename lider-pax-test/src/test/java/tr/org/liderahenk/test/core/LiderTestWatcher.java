package tr.org.liderahenk.test.core;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiderTestWatcher extends TestWatcher {
	
	
	 private static final Logger LOG = LoggerFactory.getLogger(LiderTestWatcher.class);

	    @Override
	    protected void starting(Description description) {
	        LOG.info(">>>>>> {} <<<<<" , description.getDisplayName());
	    }

	    @Override
	    protected void failed(Throwable e, Description description) {
	        LOG.error(">>>>>> FAILED: {} , cause: {}", description.getDisplayName(), e.getMessage());
	    }

	    @Override
	    protected void succeeded(Description description) {
	    }

}
