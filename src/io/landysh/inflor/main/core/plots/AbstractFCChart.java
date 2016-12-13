package io.landysh.inflor.main.core.plots;

import java.util.UUID;

import org.jfree.chart.JFreeChart;

import io.landysh.inflor.main.core.dataStructures.FCSFrame;

public abstract class AbstractFCChart {

  /**
   * @Param newUUID creates a new UUID for this plot definition.
   */

  public final String uuid;
  protected ChartSpec spec;

  public AbstractFCChart(String priorUUID, ChartSpec spec) {
    // Create new UUID if needed.
    if (priorUUID == null) {
      uuid = UUID.randomUUID().toString();
    } else {
      uuid = priorUUID;
    }
    this.spec = spec;
  }

  public void setSpec(ChartSpec spec) {
    this.spec = spec;
  }

  public ChartSpec getSpec() {
    return this.spec;
  }

  public abstract JFreeChart createChart(FCSFrame dataFrame);
}