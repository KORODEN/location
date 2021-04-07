package com.koroden.app7

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class CoordinateAdapter(context: Context, coordinates: ArrayList<Coordinates>) : BaseAdapter() {

    var ctx: Context = context
    var objects: ArrayList<Coordinates> = coordinates
    var inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    // Формирование разметки, содержащей строку данных
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // Если разметка ещё не существует, создаём её по шаблону
        var view = convertView
        if (view == null)
            view = inflater.inflate(R.layout.listview_layout_coordinates, parent, false)

        // Получение объекта с информацией о продукте
        val coordinate = objects[position]

        // Заполнение элементов данными из объекта
        (view?.findViewById(R.id.place_name) as TextView).text = coordinate.placeName
        (view.findViewById(R.id.latitude) as TextView).text = "Широта: " + coordinate.latitude
        (view.findViewById(R.id.longitude) as TextView).text = "Долгота: " + coordinate.longitude

        if(coordinate.statusNumber == 1) {
                (view?.findViewById(R.id.marker) as ImageView).setImageResource(R.drawable.ic_near_place)
                (view.findViewById(R.id.radius) as TextView).text = coordinate.radius.toString() + " м"
        }else{
                (view?.findViewById(R.id.marker) as ImageView).setImageResource(R.drawable.ic_far_place)
                (view.findViewById(R.id.radius) as TextView).text = ">100 м"
        }

        return view
    }

    // Получение количества элементов в списке
    override fun getCount(): Int {
        return objects.size
    }

    // Получение элемента данных в указанной строке
    override fun getItem(position: Int): Any {
        return objects[position]
    }

    // Получение идентификатора элемента в указанной строке
    // Часто вместо него возвращается позиция элемента
    override fun getItemId(position: Int): Long {
        return position.toLong()

    }
}