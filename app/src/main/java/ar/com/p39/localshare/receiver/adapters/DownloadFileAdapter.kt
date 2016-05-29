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
            picasso.load(item.url).resize(200, 200).error(R.drawable.ic_error).into(holder.imageView)
            if (item.status == 1) {
                holder.statusImage.setImageResource(R.drawable.ic_downloading)
            } else if (item.status == 2) {
                holder.statusImage.setImageResource(R.drawable.ic_downloaded)
            } else {
                holder.statusImage.setImageBitmap(null)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: TextView by bindView(R.id.name)
        val imageView: ImageView by bindView(R.id.image)
        val statusImage: ImageView by bindView(R.id.status)
    }
}
