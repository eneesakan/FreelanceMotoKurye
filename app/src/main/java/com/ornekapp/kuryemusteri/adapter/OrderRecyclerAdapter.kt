package com.ornekapp.kuryemusteri.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ornekapp.kuryemusteri.R
import com.ornekapp.kuryemusteri.model.Order
import com.ornekapp.kuryemusteri.model.StatusApiResponse
import com.ornekapp.kuryemusteri.service.ApiService
import com.ornekapp.kuryemusteri.util.Singleton
import com.ornekapp.kuryemusteri.view.activity.MainActivity
import com.ornekapp.kuryemusteri.view.fragment.MyOrdersFragment
import kotlinx.android.synthetic.main.recycler_order.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderRecyclerAdapter(var orderList : ArrayList<Order>, var activity: FragmentActivity) : RecyclerView.Adapter<OrderRecyclerAdapter.OrderViewHolder>() {
    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var singleton = Singleton
    private var service = ApiService()
    private var auth = Firebase.auth

// Order'lar için gerekli adapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order,parent,false)
        return OrderViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.itemView.order_recycler_name.text = orderList[position].recipientname
        holder.itemView.order_recycler_address.text = orderList[position].address
        holder.itemView.order_recycler_price.text = orderList[position].price.toString()

        var packetType = ""
        if (orderList[position].packettype == 0) {
            packetType = holder.itemView.context.getString(R.string.box)
        } else {
            packetType = holder.itemView.context.getString(R.string.document)
        }

        holder.itemView.order_recycler_packettype.text = packetType

        if (orderList[position].orderstatus == 0) {
            if (singleton.usertype == 1) {
                holder.itemView.order_recycler_cancel_order.visibility = View.GONE
                holder.itemView.order_recycler_complete_order.visibility = View.VISIBLE
                holder.itemView.order_recycler_status_text.visibility = View.GONE

                holder.itemView.order_recycler_complete_order.setOnClickListener {
                    service.statusUpdateOrder(orderList[position].id,1).enqueue(object :
                        Callback<StatusApiResponse> {
                        override fun onResponse(call: Call<StatusApiResponse>, response: Response<StatusApiResponse>) { response.body()?.let {} }
                        override fun onFailure(call: Call<StatusApiResponse>, t: Throwable) {t.printStackTrace()}
                    })
                    val myOrdersFragment = MyOrdersFragment()
                    val manager = activity.supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.main_activity_frame_layout, myOrdersFragment)
                    transaction.commit()
                }
            } else {
                holder.itemView.order_recycler_cancel_order.visibility = View.VISIBLE
                holder.itemView.order_recycler_complete_order.visibility = View.GONE
                holder.itemView.order_recycler_status_text.visibility = View.GONE

                holder.itemView.order_recycler_cancel_order.setOnClickListener {
                    service.statusUpdateOrder(orderList[position].id,2).enqueue(object :
                        Callback<StatusApiResponse> {
                        override fun onResponse(call: Call<StatusApiResponse>, response: Response<StatusApiResponse>) { response.body()?.let {} }
                        override fun onFailure(call: Call<StatusApiResponse>, t: Throwable) {t.printStackTrace()}
                    })
                    val myOrdersFragment = MyOrdersFragment()
                    val manager = activity.supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.main_activity_frame_layout, myOrdersFragment)
                    transaction.commit()
                }
            }
        } else if (orderList[position].orderstatus == 1) {
            holder.itemView.order_recycler_status_text.text = "Siparişiniz tamamlandı."

            holder.itemView.order_recycler_cancel_order.visibility = View.GONE
            holder.itemView.order_recycler_complete_order.visibility = View.GONE
            holder.itemView.order_recycler_status_text.visibility = View.VISIBLE

        } else {
            holder.itemView.order_recycler_status_text.text = "Siparişiniz iptal edildi."

            holder.itemView.order_recycler_cancel_order.visibility = View.GONE
            holder.itemView.order_recycler_complete_order.visibility = View.GONE
            holder.itemView.order_recycler_status_text.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun updateRecyclerData(newOrderList: List<Order>){
        val size = orderList.size
        orderList.clear()
        orderList.addAll(newOrderList)
        notifyItemRangeRemoved(0, size)
        notifyItemRangeInserted(0, newOrderList.size)
    }
}