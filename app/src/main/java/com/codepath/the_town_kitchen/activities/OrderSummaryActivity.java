package com.codepath.the_town_kitchen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.adapters.OrderItemAdapter;
import com.codepath.the_town_kitchen.fragments.ProgressBarDialog;
import com.codepath.the_town_kitchen.models.Order;
import com.codepath.the_town_kitchen.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderSummaryActivity extends ActionBarActivity {
    Button bSubmitOrder;
    Button bPaymentInfo;
    ProgressBarDialog progressBarDialog;

    private ListView lvOrderItems;
    private OrderItemAdapter orderItemAdapter;
    private List<OrderItem> orderItems;
    private TextView tvOrderTotal;
    private TextView tvDeliveryTime;
    private TextView tvAddress;
    private Order orderToSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        bSubmitOrder = (Button) findViewById(R.id.bSubmitOrder);
        bSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPogressBarDialog();
            }
        });

        bPaymentInfo = (Button) findViewById(R.id.bPaymentInfo);
        bPaymentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderSummaryActivity.this, PaymentInfoActivity.class);
                startActivity(i);
            }
        });


        //order items
        lvOrderItems = (ListView) findViewById(R.id.lvOrderItems);
        tvDeliveryTime = (TextView) findViewById(R.id.tvDeliveryTime);
        Order.getOrderByDate(TheTownKitchenApplication.orderDate, new Order.IOrderReceivedListener() {
            @Override
            public void handle(Order order, List<OrderItem> orderItems) {
                if (order != null) {
                    tvDeliveryTime.setText(order.getDate() + " " + order.getTime());

                    if (orderItems == null)
                        orderItems = new ArrayList<>();
                    orderItemAdapter = new OrderItemAdapter(OrderSummaryActivity.this, orderItems, null);
                    lvOrderItems.setAdapter(orderItemAdapter);
                    tvOrderTotal = (TextView) findViewById(R.id.tvOrderTotal);
                    tvOrderTotal.setText("$" + order.getCost() + "");

                    tvAddress = (TextView) findViewById(R.id.tvAddress);
                    tvAddress.setText(order.getDeliveryLocation());


                    orderToSave = order;
                }
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPogressBarDialog() {
        FragmentManager fm = getSupportFragmentManager();
        progressBarDialog = ProgressBarDialog.newInstance();
        progressBarDialog.show(fm, "fragment_progress_bar");


        Handler handler = null;
        handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                saveOrder();
                progressBarDialog.dismiss();
                startMealListActivity();
            }
        }, 1000);
    }

    private void saveOrder() {
        orderToSave.setIsPlaced(true);
        orderToSave.saveInBackground();
    }

    private void startMealListActivity(){
        Intent startIntent = new Intent(this, MealListActivity.class);
        this.startActivity(startIntent);
    }

    public void onCouponCodeSubmit(View view) {
        EditText etCouponCode = (EditText) findViewById(R.id.etCouponCode);

        orderToSave.setCost(orderToSave.getCost() - (.15 * orderToSave.getCost()));
        tvOrderTotal.setText("$" + orderToSave.getCost() + "");

        etCouponCode.setText("");
    }
}
