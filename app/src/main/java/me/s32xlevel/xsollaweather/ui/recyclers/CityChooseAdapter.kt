package me.s32xlevel.xsollaweather.ui.recyclers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.city_card_item.view.*
import me.s32xlevel.xsollaweather.R
import me.s32xlevel.xsollaweather.business.model.CityChoose

class CityChooseAdapter(private val data: List<CityChoose>) :
    RecyclerView.Adapter<CityChooseAdapter.CityMainViewHolder>() {

    private var onClickListener: (cityId: Int) -> Unit = {}
    private var onLongClickListener: (city: CityChoose) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CityMainViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.city_card_item,
                parent,
                false
            )
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CityMainViewHolder, position: Int) {
        val cityChoose = data[position]
        with(holder.itemView) {
            city_id.text = cityChoose.id.toString()
            city_name_tv.text = cityChoose.name
            weather_iv.setImageBitmap(cityChoose.weatherImage)
            weather_range_tv.text = "${cityChoose.tempMin}° .. ${cityChoose.tempMax}°"

            setOnClickListener { onClickListener.invoke(cityChoose.id) }
            setOnLongClickListener { onLongClickListener.invoke(cityChoose); true }
        }
    }

    override fun getItemCount() = data.size

    class CityMainViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun setOnCityClickListener(onClickListener: (cityId: Int) -> Unit) {
        this.onClickListener = onClickListener
    }

    fun setOnCityLongClickListener(onLongClickListener: (city: CityChoose) -> Unit) {
        this.onLongClickListener = onLongClickListener
    }
}