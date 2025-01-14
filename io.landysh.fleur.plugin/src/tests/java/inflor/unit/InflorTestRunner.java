package inflor.unit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class InflorTestRunner {
  public static void main(String[] args) {
    final Result result = JUnitCore.runClasses(InflorTestSuite.class);
    for (final Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
