package inflor.unit;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import fleur.core.compensation.SpilloverCompensator;
import fleur.core.data.FCSFrame;
import fleur.core.fcs.FCSFileReader;

public class CompensatorTest {

  @Test
  public void testParseSpillover() throws Exception {
    // Setup
    final HashMap<String, String> keywords = new HashMap<String, String>();
    final String key = "SPILL";
    final String value =
        "12,FITC-A,Pacific Blue-A,Violet Green-A,Pacific Orange-A,Qdot 605-A,APC-A,APC-Cy7-A,PE-A,PE-Texas-Red-A,PE-Cy5-A,PE-Cy55-A,PE-Cy7-A,1,0,0.0428365753515287,0.04350589942357581,0.008366519501286475,0,0,0.020378566681808347,0.004670088180699849,0.0006368301313411251,0,0,0,1,0.12352051414142143,0.05854947810491659,0.012182068499059034,0,0.00010574236032065014,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0.2488646865703377,1,0.6126249019692379,0,0.0015255329553227224,0,0,0,0,0,0,0.03490265176052338,0.0013023361684801119,0.0036465451304040697,1,0,0,0.06567322760651334,0.21895204948020755,0.0016108526361247517,0,0.00012391174587817746,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0.03130423566811879,1,0,0,0.003842847283607365,0.003551722391525895,0.08669697049781142,0.00362253121287718,0,0,0.04839968025295413,0.033236762299180225,0,0,1,0.19474184016389673,0.049109173948911025,0.033731551978730394,0.006366003733924787,0,0,0,0,0,0,0,0,1,0,0,0,0.0016793785359924555,0,0,0.00008349594914702962,0.00023657226823005497,0.9057278951682789,0.1704024628613011,0.06019119106064934,0.014326192752304304,1,1.1009719111434542,0.3014326283280875,0.0009602413044384785,0,0,0.007549153707905638,0.004837397436444851,0.0319258994679381,0.06401613566403627,0.22571194393896188,0.04468733514657176,0.04175775149181647,1,0.32207599481827437,0,0,0,0,0,0,0,0,0,0,0,1";
    keywords.put(key, value);
    final String[] trueCompParameters =
        {"FITC-A", "Pacific Blue-A", "Violet Green-A", "Pacific Orange-A", "Qdot 605-A", "APC-A",
            "APC-Cy7-A", "PE-A", "PE-Texas-Red-A", "PE-Cy5-A", "PE-Cy55-A", "PE-Cy7-A"};
    final double[][] trueMatrix = {
        {1.0, 0.0, 0.0428365753515287, 0.04350589942357581, 0.008366519501286475, 0.0, 0.0,
            0.020378566681808347, 0.004670088180699849, 6.368301313411251E-4, 0.0, 0.0},
        {0.0, 1.0, 0.12352051414142143, 0.05854947810491659, 0.012182068499059034, 0.0,
            1.0574236032065014E-4, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 0.2488646865703377, 1.0, 0.6126249019692379, 0.0, 0.0015255329553227224, 0.0,
            0.0, 0.0, 0.0, 0.0},
        {0.0, 0.03490265176052338, 0.0013023361684801119, 0.0036465451304040697, 1.0, 0.0, 0.0,
            0.06567322760651334, 0.21895204948020755, 0.0016108526361247517, 0.0,
            1.2391174587817746E-4},
        {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.03130423566811879, 1.0, 0.0, 0.0, 0.003842847283607365,
            0.003551722391525895, 0.08669697049781142},
        {0.00362253121287718, 0.0, 0.0, 0.04839968025295413, 0.033236762299180225, 0.0, 0.0, 1.0,
            0.19474184016389673, 0.049109173948911025, 0.033731551978730394, 0.006366003733924787},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0},
        {0.0016793785359924555, 0.0, 0.0, 8.349594914702962E-5, 2.3657226823005497E-4,
            0.9057278951682789, 0.1704024628613011, 0.06019119106064934, 0.014326192752304304, 1.0,
            1.1009719111434542, 0.3014326283280875},
        {9.602413044384785E-4, 0.0, 0.0, 0.007549153707905638, 0.004837397436444851,
            0.0319258994679381, 0.06401613566403627, 0.22571194393896188, 0.04468733514657176,
            0.04175775149181647, 1.0, 0.32207599481827437},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0}};

    // Test
    final SpilloverCompensator comp = new SpilloverCompensator(keywords);
    final String[] testCompParameters = comp.getCompParameterNames();
    final double[][] testMatrix = comp.getMatrix();
    // Assert
    final String assert1 = "Parsed correct value ";
    for (int i = 0; i < trueMatrix.length; i++) {
      for (int j = 0; j < trueMatrix.length; j++) {
        assertEquals(assert1 + i * j + "/" + trueMatrix.length * trueMatrix.length,
            trueMatrix[i][j], testMatrix[i][j], Double.MIN_VALUE);
      }
    }
    for (int i = 0; i < trueCompParameters.length; i++) {
      assertEquals(trueCompParameters[i], testCompParameters[i]);
    }

    System.out.println("EventFrameTest testSpillover completed (succefully or otherwise)");
  }

  @Test
  public void testCompensateFrame() throws Exception {
    // Setup
    String logiclePath = "src/io/landysh/inflor/tests/extData/logicle-example.fcs";
    FCSFrame dataFrame = FCSFileReader.read(logiclePath);

    // Test
    SpilloverCompensator compr = new SpilloverCompensator(dataFrame.getKeywords());
    dataFrame = compr.compensateFCSFrame(dataFrame, false);
    double[] actual = dataFrame.getDimension("FSC-A").getData();
    double[] truth = dataFrame.getDimension("FSC-A").getData();

    // Assert
    for (int i = 0; i < truth.length; i++) {
      assertEquals(Integer.toString(i), truth[i], actual[i], Double.MIN_VALUE);//TODO
    }
  }
}
