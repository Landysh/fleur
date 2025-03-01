package inflor.unit;

import static org.junit.Assert.assertEquals;

import java.io.RandomAccessFile;

import org.junit.Test;

import fleur.core.data.FCSFrame;
import fleur.core.fcs.FCSFileReader;

public class FCSFileReaderTest {
  // Define Constants

  String path1 = "src/resources/fcs/int-15_scatter_events.fcs";
  String logiclePath = "src/resources/fcs/logicle-example.fcs";

  @Test
  public void testInitialization() throws Exception {
    // Setup
    final FCSFileReader r;
    try (RandomAccessFile raf = new RandomAccessFile(path1, "r")) {
      r = new FCSFileReader(path1, raf);
    }
    
    // Test

    // Assert
    assertEquals(r.getPathToFile(), path1);
    assertEquals(r.getBeginText(), (Integer) 256);
    assertEquals(r.getEndText(), (Integer) 511);
    assertEquals(r.getBeginData(), (Integer) 576);
    assertEquals(r.getDataType(), "I");
    // assertEquals( r.bitMap, new Integer[] {16,16});
    System.out.println("FCSFileReaderTest testInitialization completed.");

  }

  @Test
  public void testReadAllData15ScatterEvents() throws Exception {
    // Setup
    final double[] testSSC;
    final double[] testFCS;
    
    try (RandomAccessFile raf = new RandomAccessFile(path1, "r")) {
      final FCSFrame dataStore = FCSFileReader.read(path1);
      testFCS = dataStore.getDimension("FCS").getData();
      testSSC = dataStore.getDimension("SSC").getData();
    }
    
    // Test

    final double[] fcs =
        {400, 600, 300, 500, 600, 500, 800, 200, 300, 800, 900, 400, 200, 600, 400};
    final double[] ssc =
        {300, 300, 600, 200, 800, 500, 600, 400, 100, 200, 400, 800, 900, 700, 500};


    // Assert
    for (int i = 0; i < fcs.length; i++) {
      assertEquals(fcs[i], testFCS[i], Double.MIN_VALUE);
      assertEquals(ssc[i], testSSC[i], Double.MIN_VALUE);
    }
    System.out.println("FCSFileReaderTes::testReadAllData15ScatterEvents completed.");

  }

  @Test
  public void testCytoZoo() throws Exception {
    // Setup
    String novocytePath =
        "/media/hartaa1/cold/all_fcs/6736459-new T cell compensation panel_D5_BL5 PE-Cy7.fcs";
    
    String fcsVersion;

    try (RandomAccessFile raf = new RandomAccessFile(novocytePath, "r")) {
      final FCSFileReader reader = new FCSFileReader(novocytePath, raf);
      fcsVersion = reader.readFCSVersion(new RandomAccessFile(novocytePath, "r"));
    }
    assertEquals(fcsVersion, "FCS3.1");
    System.out.println("FCSFileReaderTes::testReadAllData15ScatterEvents completed.");
  }

  @Test
  public void testReadAllLogicleDataNoComp() throws Exception {
    // Setup
    int trueRowCount = 30000;
    double tolerance = 0.1;
    final double[] trueFSCFirstFew = {58998.59765625, 53811.0, 52696.796875, 65865.6015625,
        24123.599609375, 63445.5, 61881.296875, 64310.3984375, 73426.5, 75041.1015625};
    final double[] trueSSCFirstFew = {2111.969970703125, 3435.199951171875, 2412.550048828125,
        1323.22998046875, 1257.68994140625, 1195.5400390625, 1101.75, 1400.0699462890625,
        1187.6300048828125, 1676.9200439453125};
    // Test
    
    final FCSFrame dataStore;
    try (RandomAccessFile raf = new RandomAccessFile(logiclePath, "r")) {
      final FCSFileReader reader = new FCSFileReader(logiclePath, raf);
      reader.readData();
      dataStore = reader.getFCSFrame();
    }

    final int testRowCount = dataStore.getRowCount();


    double[] testFCS = dataStore.getDimension("FSC-A").getData();
    double[] testSSC = dataStore.getDimension("SSC-A").getData();

    int fewCount = 10;
    double[] testFSCFirstFew = new double[fewCount];
    double[] testSSCFirstFew = new double[fewCount];

    for (int i = 0; i < fewCount; i++) {
      testFSCFirstFew[i] = testFCS[i];
      testSSCFirstFew[i] = testSSC[i];
    }

    // Assert
    assertEquals("Row count", trueRowCount, testRowCount);
    for (int i = 0; i < trueFSCFirstFew.length; i++) {
      assertEquals(trueFSCFirstFew[i], testFSCFirstFew[i], tolerance);
      assertEquals(trueSSCFirstFew[i], testSSCFirstFew[i], tolerance);
    }
    System.out.println("FCSFileReaderTes::testReadAllLogicleDataNoComp completed.");
  }
}
