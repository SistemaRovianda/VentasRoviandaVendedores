package com.example.tiendas.cotizaciones.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.tiendas.R;
import com.example.tiendas.Utils.Models.ClientVisitDTO;
import com.example.tiendas.Utils.enums.ClientVisitStatus;
import com.example.tiendas.cotizaciones.presenter.VisitsPresenterContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AdapterListClientVisit extends BaseAdapter {
    Context context;
    ClientVisitDTO visits[];
    VisitsPresenterContract visitsPresenter;
    boolean inVisit;
    public AdapterListClientVisit(Context appContext, ClientVisitDTO[] visits, VisitsPresenterContract visitsPresenter,boolean inVisit){
        this.context=appContext;
        this.visits=visits;
        this.visitsPresenter=visitsPresenter;
        this.inVisit=inVisit;
    }

    @Override
    public int getCount() {
        return this.visits.length;
    }

    @Override
    public Object getItem(int position) {
        return this.visits[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view  = convertView;
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        view= layoutInflater.inflate(R.layout.visit_item, null);
        TextView clientName = view.findViewById(R.id.visitClientName);
        TextView statusText = view.findViewById(R.id.visitStatus);
        clientName.setText((position+1)+".-"+this.visits[position].getClient().getKeyClient()+"\n"+this.visits[position].getClient().getName());
        ClientVisitStatus clientVisitStatus =this.visits[position].getVisitedStatus();
        MaterialButton startVisit = view.findViewById(R.id.visitStartButton);
        MaterialButton endVisit = view.findViewById(R.id.visitEndButton);
        startVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inVisit==false) {
                    AlertDialog dialog = new MaterialAlertDialogBuilder(context).setTitle("Proceso de visita")
                            .setMessage("¿Está seguro que desea realizar la visita?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startVisit.setEnabled(false);
                                    visitsPresenter.startVisit(visits[position].getClient().getId(),visits[position].getClient());
                                }
                            }).setNegativeButton("Cancenlar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(false).create();
                    dialog.show();

                }else{
                    genericMessage("Estas con un cliente","Primero termina la visita");
                }
            }
        });
        endVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endVisit.setEnabled(false);
                visitsPresenter.endVisit(visits[position].getClient().getId());
            }
        });
        switch (clientVisitStatus){
            case PENDING:
                statusText.setText("Estatus: No Visitado");
                break;
            case INVISIT:
                startVisit.setVisibility(View.GONE);
                endVisit.setVisibility(View.VISIBLE);
                statusText.setText("Estatus: En visita");
                visitsPresenter.setClientVisit(visits[position].getClient());
                break;
            case VISITED:
                startVisit.setVisibility(View.VISIBLE);
                startVisit.setText("Visitar");
                endVisit.setVisibility(View.GONE);
                statusText.setTextSize(20);
                statusText.setTextColor(Color.GREEN);
                statusText.setText("Estatus: Visitado");
                break;
        }
        return  view;
    }


    public void genericMessage(String title,String msg){

        AlertDialog dialog = new MaterialAlertDialogBuilder(this.context).setTitle(title)
                .setMessage(msg).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                }).create();
        dialog.show();
    }
}
