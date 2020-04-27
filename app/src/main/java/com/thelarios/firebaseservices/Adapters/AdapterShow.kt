package com.thelarios.firebaseservices.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.thelarios.firebaseservices.Data.DataItem
import com.thelarios.firebaseservices.R
import kotlinx.android.synthetic.main.item.view.*
import java.lang.Exception

class AdapterShow(private val data: List<DataItem>) : RecyclerView.Adapter<AdapterShow.Holder>()
{
    class Holder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindView(item: DataItem?) {
            item?.let {
                with(it)
                {
                    try {
                        Picasso.get()
                            .load(image)
                            .into(itemView.imgItem)
                    }
                    catch (e : Exception)
                    {
                        itemView.imgItem.setImageBitmap(null)
                    }
                    itemView.txtTitleItem.text = objectText
                    itemView.txtConfidenceItem.text = "$confidence de confianza"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Holder(
            layoutInflater.inflate(
                R.layout.item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(data[position])
    }
}
