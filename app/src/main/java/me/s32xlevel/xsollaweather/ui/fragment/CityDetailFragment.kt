package me.s32xlevel.xsollaweather.ui.fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_city_detail.*
import me.s32xlevel.xsollaweather.R
import me.s32xlevel.xsollaweather.business.model.WeatherEntity
import me.s32xlevel.xsollaweather.business.network.api
import me.s32xlevel.xsollaweather.business.network.asyncCall
import me.s32xlevel.xsollaweather.ui.recyclers.CustomLinearDividerItemDecoration
import me.s32xlevel.xsollaweather.ui.recyclers.DatesRecyclerAdapter
import me.s32xlevel.xsollaweather.ui.recyclers.WeatherRecyclerAdapter
import me.s32xlevel.xsollaweather.util.DbUtils
import me.s32xlevel.xsollaweather.util.NavigationManager.changeFragment
import me.s32xlevel.xsollaweather.util.PreferencesManager
import me.s32xlevel.xsollaweather.util.PreferencesManager.getIntFromPreferences
import me.s32xlevel.xsollaweather.util.PreferencesManager.getLongFromPreferences

class CityDetailFragment : BaseFragment(R.layout.fragment_city_detail) {

    companion object {
        // Префы + сравнение времени посл. обновления и текущего
        fun newInstance(): Fragment {
            return CityDetailFragment()
        }
    }

    private val lastUpdate: Long by lazy { context?.getLongFromPreferences(PreferencesManager.LAST_NETWORK_CONNECT)!! }

    private val currentCityId: Int by lazy { context?.getIntFromPreferences(PreferencesManager.SAVED_CITY)!! }

    private lateinit var selectedDay: String

    // TODO: !!!!Остановился здесь. Нужно нормально работать с сетью. Допилить offline mode
    // TODO: !!!! + При заходе отображение лого, а также "о себе"
    // Если не загрузилось на этом моменте сеть, знач ничего нет, значит показ ошибки
    // Пустые списки для ресайклера
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val cityWeather = weatherRepository.findAllByCityId(currentCityId)
        if (cityWeather.weathers.isEmpty()) {
            api.getForecast5day3hours(currentCityId)
                .enqueue(asyncCall { DbUtils.saveWeatherToDb(it.body()!!) })
        }
        selectedDay = weatherRepository.findAllByCityId(currentCityId).weathers[0].dateTxt
        configureToolbar()
        configureRecyclerForDates()
        configureRecyclerForWeather()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.changeFragment(CityChooseFragment.newInstance(), cleanStack = true)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun configureToolbar() {
        val city = cityRepository.findById(currentCityId)
        with((activity as AppCompatActivity).supportActionBar!!) {
            title = city.name
            setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)
    }

    private fun configureRecyclerForDates() {
        val weathers = weatherRepository.findAllByCityId(currentCityId).weathers
        val set = mutableSetOf<String>()
        for (weather in weathers) {
            set.add(weather.dateTxt.split(" ")[0])
        }

        with(dates_rv) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = DatesRecyclerAdapter(set).apply {
                setOnClickListener {
                    selectedDay = it
                    weather_rv.adapter = WeatherRecyclerAdapter(getWeatherByCurrentDate())
                }
            }
        }
    }

    private fun configureRecyclerForWeather() {
        weather_rv.layoutManager = LinearLayoutManager(context)
        weather_rv.addItemDecoration(
            CustomLinearDividerItemDecoration(
                drawBeforeFirst = true,
                drawAfterLast = true
            )
        )
        weather_rv.adapter = WeatherRecyclerAdapter(getWeatherByCurrentDate())
    }

    private fun getWeatherByCurrentDate(): List<WeatherEntity> {
        val weatherList = weatherRepository.findAllByCityId(currentCityId).weathers
        val date = selectedDay.split(" ")[0]

        val list = mutableListOf<WeatherEntity>()

        for (weatherEntity in weatherList) {
            if (weatherEntity.dateTxt.contains(date)) {
                list.add(weatherEntity)
            }
        }

        return list
    }
}
