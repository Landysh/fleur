package inflor.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({FCSFileReaderTest.class, 
               EventFrameTest.class,
               CompensatorTest.class
               })
public class InflorTestSuite {
}
