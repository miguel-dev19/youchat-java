package cu.alexgi.youchat.Charts.interfaces.dataprovider;

import cu.alexgi.youchat.Charts.components.YAxis;
import cu.alexgi.youchat.Charts.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
