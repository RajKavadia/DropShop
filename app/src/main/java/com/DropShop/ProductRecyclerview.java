package com.DropShop;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductRecyclerview extends RecyclerView.Adapter<ProductRecyclerview.viewholder> {
    private List<Product> items= new ArrayList<>();
    private Context context;

    public ProductRecyclerview(List<Product> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductRecyclerview.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_row, parent, false);
        return new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerview.viewholder holder, int position) {
        try {
            holder.productDesc.setText(items.get(position).productDesc);
            holder.brandName.setText(items.get(position).brandCode);
            holder.mrp.setText(Integer.toString(items.get(position).mrp));
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class viewholder extends RecyclerView.ViewHolder{
        TextView productDesc,mrp,brandName;
        viewholder(@NonNull View itemView) {
            super(itemView);
            productDesc= itemView.findViewById(R.id.productDesc);
            mrp = itemView.findViewById(R.id.mrp);
            brandName = itemView.findViewById(R.id.brandName);
        }
    }
}
