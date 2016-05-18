package ar.com.p39.localshare.receiver.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ar.com.p39.localshare.R
import ar.com.p39.localshare.receiver.models.DownloadFile
import butterknife.bindView
import com.squareup.picasso.Picasso

/**
 * Simple adaptor to show available images to download
 *
 * Created by gazer on 5/15/16.
 */
class DownloadFileAdapter(val picasso: Picasso, var files:List<DownloadFile>) : RecyclerView.Adapter<DownloadFileAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.download_file_item, parent, false);

        return ViewHolder(view);
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = files[position]
        if (holder != null) {
            holder.fileName.text = item.name
            picasso.load("http://192.168.0.104:8080/get/$position").error(R.drawable.ic_error).into(holder.imageView)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: TextView by bindView(R.id.name)
        val imageView: ImageView by bindView(R.id.image)
    }
}
