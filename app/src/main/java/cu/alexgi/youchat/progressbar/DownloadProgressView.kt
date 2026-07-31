package cu.alexgi.youchat.progressbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import cu.alexgi.youchat.R
import kotlinx.android.synthetic.main.download_progress_layout.view.*

class DownloadProgressView : RelativeLayout {

    var progressListener: ((downloading: Boolean) -> Unit)? = null
    private var downloading = false

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.download_progress_layout, this)
        circularProgress.setOnClickListener {
            downloading = !downloading
            setProgressViews(downloading)
            progressListener?.invoke(downloading)
        }
    }

    public fun ponerClick(){
        circularProgress.setOnClickListener {
            downloading = !downloading
            setProgressViews(downloading)
            progressListener?.invoke(downloading)
        }
    }

    public fun quitarClick(){
        circularProgress.setOnClickListener(null)
    }

    //TODO any way to improve this?
    fun setProgress(progress: Float) {
        circularProgress.progress = progress
    }

    //TODO any way to improve this?
    fun getProgress(): Float {
        return circularProgress.progress
    }

    fun setDownloading(downloading: Boolean) {
        this.downloading = downloading
        setProgressViews(downloading)
    }

    private fun setProgressViews(downloading: Boolean) {
//        progressImage.setImageResource(if (downloading) R.drawable.ic_clear else R.drawable.ic_arrow)
        if (downloading)
            progressImage.visibility= View.INVISIBLE
        else
            progressImage.visibility= View.VISIBLE

        circularProgress.progressStarted = downloading
    }

}