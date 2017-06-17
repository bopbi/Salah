package com.arjunalabs.serpong.android.salah.schedule

import android.content.Context
import android.content.Intent
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arjunalabs.serpong.android.salah.R
import com.arjunalabs.serpong.android.salah.qibla.QiblaActivity

/**
 * Created by bobbyadiprabowo on 27/05/17.
 */

class ScheduleView : CoordinatorLayout, ScheduleContract.View {

    private var recyclerSchedule : RecyclerView? = null
    private var scheduleAdapter : ScheduleRecyclerAdapter? = null
    var viewModel : ScheduleViewModel? = null


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            openQiblaActivity()
        }

        recyclerSchedule = findViewById(R.id.recycler_schedule) as RecyclerView?
        recyclerSchedule?.layoutManager = LinearLayoutManager(context)
        scheduleAdapter = ScheduleRecyclerAdapter()

        recyclerSchedule?.adapter = scheduleAdapter
    }

    override fun openQiblaActivity() {
        if (viewModel?.lat != null && viewModel?.lng != null) {
            val intent = Intent(context, QiblaActivity::class.java)
            val lat = viewModel?.lat
            val lng = viewModel?.lng
            intent.putExtra("LAT", lat)
            intent.putExtra("LNG", lng)
            context.startActivity(intent)
        }
    }

    override fun displaySchedule(prayTimeNames: ArrayList<String>, prayTimes: ArrayList<String>) {
        scheduleAdapter?.prayTimeNames = prayTimeNames
        scheduleAdapter?.prayTimes = prayTimes
        scheduleAdapter?.notifyDataSetChanged()
    }

    class ScheduleRecyclerAdapter : RecyclerView.Adapter<ScheduleViewHolder>() {

        var prayTimeNames: ArrayList<String>? = null
        var prayTimes: ArrayList<String>? = null

        override fun onBindViewHolder(viewholder: ScheduleViewHolder?, position: Int) {
            viewholder?.textScheduleName?.text = prayTimeNames?.get(position)
            viewholder?.textScheduleTime?.text = prayTimes?.get(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup?, p1: Int): ScheduleViewHolder {

            val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_schedule, null, false)
            return ScheduleViewHolder(view)
        }

        override fun getItemCount(): Int {
            if (prayTimeNames == null) {
                return 0
            }
            return prayTimeNames?.size as Int
        }

    }

    class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textScheduleName : TextView = view.findViewById(R.id.text_schedule_name) as TextView
        val textScheduleTime : TextView = view.findViewById(R.id.text_schedule_time) as TextView

    }
}
