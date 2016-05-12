package tr.org.liderahenk.test;

import static org.junit.Assert.assertFalse;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.liderahenk.test.LiderKarafTestContainer;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;





@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class LiderFeatureListSampleTest extends LiderKarafTestContainer {
	

    @Test
    public void listCommand() throws Exception {
        String featureListOutput = executeCommand("list");
        System.out.println(featureListOutput);
        assertFalse(featureListOutput.isEmpty());
    }
    
    
	@Override
	public Option[] getCustomOptions() {
		Option[] options = new Option[1];
		
		options[0] = mavenBundle().groupId("org.freemarker").artifactId("freemarker").version("2.3.20");
		
		return options;
	}
	
}
