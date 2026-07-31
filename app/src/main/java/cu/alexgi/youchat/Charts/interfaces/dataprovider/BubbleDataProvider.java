package cu.alexgi.youchat.Charts.interfaces.dataprovider;

import cu.alexgi.youchat.Charts.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
