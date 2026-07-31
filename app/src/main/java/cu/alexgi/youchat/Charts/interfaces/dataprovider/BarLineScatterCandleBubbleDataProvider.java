package cu.alexgi.youchat.Charts.interfaces.dataprovider;

import cu.alexgi.youchat.Charts.components.YAxis.AxisDependency;
import cu.alexgi.youchat.Charts.data.BarLineScatterCandleBubbleData;
import cu.alexgi.youchat.Charts.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
