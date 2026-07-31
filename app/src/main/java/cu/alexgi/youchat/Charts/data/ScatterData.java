
package cu.alexgi.youchat.Charts.data;

import java.util.List;

import cu.alexgi.youchat.Charts.interfaces.datasets.IScatterDataSet;

public class ScatterData extends BarLineScatterCandleBubbleData<IScatterDataSet> {

    public ScatterData() {
        super();
    }

    public ScatterData(List<IScatterDataSet> dataSets) {
        super(dataSets);
    }

    public ScatterData(IScatterDataSet... dataSets) {
        super(dataSets);
    }

    /**
     * Returns the maximum shape-size across all DataSets.
     *
     * @return
     */
    public float getGreatestShapeSize() {

        float max = 0f;

        for (IScatterDataSet set : mDataSets) {
            float size = set.getScatterShapeSize();

            if (size > max)
                max = size;
        }

        return max;
    }
}
