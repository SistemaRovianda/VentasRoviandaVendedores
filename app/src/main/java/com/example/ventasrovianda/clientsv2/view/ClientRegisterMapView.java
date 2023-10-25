package com.example.ventasrovianda.clientsv2.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ClientRegisterMapView extends Fragment implements View.OnClickListener, OnMapReadyCallback,ClientRegisterMapViewContrat {


    private GoogleMap mMap;
    private Double latitude,longitude;
    private TextView latitudeTextView,longitudeTextView;
    private Button saveCoords;
    private String username=null,action=null;
    private Integer currentClientId=0;
    private Integer currentClientRovId=0;
    private NavController navController;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clientv2_register_map_view,container,false);
        latitudeTextView = view.findViewById(R.id.latitudeTextView);
        longitudeTextView = view.findViewById(R.id.longitudeTextView);
        saveCoords = view.findViewById(R.id.saveCoords);
        saveCoords.setOnClickListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String latitudeStr = ClientRegisterMapViewArgs.fromBundle(getArguments()).getLatitude();
        String longitudeStr = ClientRegisterMapViewArgs.fromBundle(getArguments()).getLongitude();
        username = ClientRegisterMapViewArgs.fromBundle(getArguments()).getUsername();
        action =ClientRegisterMapViewArgs.fromBundle(getArguments()).getAction();
        currentClientId= ClientRegisterMapViewArgs.fromBundle(getArguments()).getClientId();
        currentClientRovId = ClientRegisterMapViewArgs.fromBundle(getArguments()).getClientRovId();
        if(latitudeStr!=null && longitudeStr!=null && !latitudeStr.equals("null") && longitudeStr.equals("null") && latitudeStr.equals("0.0") && longitudeStr.equals("0.0")){
            System.out.println("Latitud: "+latitudeStr);
            System.out.println("Longitud: "+longitudeStr);
            latitudeTextView.setText("Latitud: "+latitudeStr);
            longitudeTextView.setText("Longitud: "+longitudeStr);
            latitude = Double.parseDouble(latitudeStr);
            longitude = Double.parseDouble(longitudeStr);
        }
        this.navController = NavHostFragment.findNavController(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveCoords:
                backToDetails();
            break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(latitude!=null && longitude!=null) {
            LatLng clientLocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(clientLocation)
                    .title("Ubicación del cliente"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clientLocation, 15));
        }else{
            mMap.clear();
            LatLng orizaba = new LatLng(18.849463,-97.098212);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(orizaba,15));
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Ubicación del cliente");
                latitudeTextView.setText("Latitud: "+latLng.latitude);
                longitudeTextView.setText("Longitud: "+latLng.longitude);
                latitude=latLng.latitude;
                longitude=latLng.longitude;
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    public void backToDetails() {
        this.navController.navigate(ClientRegisterMapViewDirections.actionClientRegisterMapViewToClientGeneralDataRegisterView(username,currentClientId,String.valueOf(latitude),String.valueOf(longitude),currentClientRovId).setAction(action));
    }
}
