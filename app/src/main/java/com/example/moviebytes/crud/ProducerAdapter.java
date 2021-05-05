package com.example.moviebytes.crud;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebytes.R;
import com.example.moviebytes.models.Producer;

import java.util.List;

public class ProducerAdapter extends RecyclerView.Adapter<ProducerAdapter.ProducerHolder> {

    private List<Producer> producerList;
    private static onProducerItemClick producerItemClick;

    public ProducerAdapter(List<Producer> producerList) {
        this.producerList = producerList;
    }

    public interface onProducerItemClick {
        void onProducerClick(int position);
        void onProducerLongClick(int position);
    }

    public void setProducerItemClick(onProducerItemClick producerListener) {
        producerItemClick = producerListener;
    }

    @NonNull
    @Override
    public ProducerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_producer_item,parent,false);
        return new ProducerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProducerHolder holder, int position) {
        Producer prod = producerList.get(position);
        holder.tvProdName.setText(prod.getName());
        holder.tvProdEmail.setText(prod.getEmail());
        holder.tvProdWebsite.setText(prod.getWebsite());
    }

    @Override
    public int getItemCount() {
        return producerList.size();
    }

    public static class ProducerHolder extends RecyclerView.ViewHolder {
        TextView tvProdName, tvProdEmail, tvProdWebsite;
        CardView cardProd;
        public ProducerHolder(@NonNull View itemView) {
            super(itemView);
            tvProdName = itemView.findViewById(R.id.tvProdName);
            tvProdEmail = itemView.findViewById(R.id.tvProdEmail);
            tvProdWebsite = itemView.findViewById(R.id.tvProdWebsite);
            cardProd = itemView.findViewById(R.id.cardProducer);

            cardProd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(producerItemClick != null){
                        producerItemClick.onProducerClick(getAdapterPosition());
                    }
                }
            });


            cardProd.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(producerItemClick != null){
                        producerItemClick.onProducerLongClick(getAdapterPosition());
                    }
                    return true;
                }
            });
        }
    }
}
