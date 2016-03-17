package tr.org.liderahenk.test;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class SampleITest {
	
	private static Logger LOG = LoggerFactory.getLogger(SampleITest.class);
	
	@Configuration
	public Option[] config() {
	    MavenArtifactUrlReference karafUrl = maven()
	        .groupId("org.apache.karaf")
	        .artifactId("apache-karaf")
	        .version("4.0.4")
	        .type("tar.gz");
	    MavenUrlReference karafStandardRepo = maven()
	        .groupId("org.apache.karaf.features")
	        .artifactId("standard")
	        .classifier("features")
	        .type("xml")
	        .versionAsInProject();
	    return new Option[] {
	        // KarafDistributionOption.debugConfiguration("5005", true),
	        karafDistributionConfiguration()
	            .frameworkUrl(karafUrl)
	            .unpackDirectory(new File("target/exam"))
	            .useDeployFolder(false),
	        keepRuntimeFolder(),
	        features(karafStandardRepo , "scr"),
//	        mavenBundle()
//	            .groupId("org.ops4j.pax.exam.samples")
//	            .artifactId("pax-exam-sample8-ds")
//	            .versionAsInProject().start(),
	   };
	}

//	    public static String karafVersion() {
//	        ConfigurationManager cm = new ConfigurationManager();
//	        String karafVersion = cm.getProperty("pax.exam.karaf.version", "4.0.4");
//	        return karafVersion;
//	    }


	    @Test
	    public void testAdd() {
	    	int result = 3;
	    	System.out.println("hallloo");
	    }

}
