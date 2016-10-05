package io.landysh.inflor.java.core.plots;

import java.util.UUID;

import org.jfree.chart.JFreeChart;

public abstract class AbstractFCPlot {

	/**
	 * @Param newUUID creates a new UUID for this plot definition.
	 */

	public final String uuid;
	protected JFreeChart chart;
	protected ChartSpec spec;

	public AbstractFCPlot(String priorUUID, ChartSpec spec) {
		// Create new UUID if needed.
		if (priorUUID == null) {
			uuid = UUID.randomUUID().toString();
		} else {
			uuid = priorUUID;
		}
		
		this.spec = spec;
		
	}
	
	public abstract void update(ChartSpec spec);
	
	public abstract JFreeChart createChart(double[] xData, double[] yData);


}