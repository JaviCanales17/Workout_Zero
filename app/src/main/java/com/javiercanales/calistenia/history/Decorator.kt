package com.javiercanales.calistenia.history

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.javiercanales.calistenia.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.DayViewDecorator

class HighlightDaysDecorator(private val dates: Set<CalendarDay>, private val context: Context) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        // Verifica si el día está en la lista de fechas para resaltar
        return day != null && dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        // Aquí se aplica el fondo para los días que cumplen con la condición
        view?.apply {
            setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.highlighted_day_background) as Drawable)
        }
    }
}

