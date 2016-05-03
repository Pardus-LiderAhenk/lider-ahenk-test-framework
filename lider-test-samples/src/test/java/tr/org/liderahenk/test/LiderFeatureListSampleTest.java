package tr.org.liderahenk.test;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.liderahenk.test.LiderKarafTestContainer;
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
    
    
	
	
}
