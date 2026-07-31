package cu.alexgi.youchat.Charts.interfaces.dataprovider;

import cu.alexgi.youchat.Charts.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
