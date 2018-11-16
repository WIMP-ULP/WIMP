package Modelo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.whereismypet.whereismypet.R;

import finalClass.GeneralMethod;


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater;
    private String nombre;
    private String imagen;
    private Activity activity;

    public CustomInfoWindowAdapter(LayoutInflater inflater, Mascota pet, Activity activity){
        this.inflater = inflater;
        nombre = pet.getNombre();
        imagen = pet.getImagen();
        this.activity = activity;
    }

    public CustomInfoWindowAdapter(LayoutInflater inflater, Tienda shop, Activity activity){
        this.inflater = inflater;
        nombre = shop.getNombre();
        imagen = shop.getImagen();
        this.activity = activity;
    }

    @Override
    public View getInfoContents(final Marker m) {
        //Carga layout personalizado.
        View v = inflater.inflate(R.layout.dialog_marker, null);
        ((TextView)v.findViewById(R.id.tvNombreMarcador)).setText(nombre);
        GeneralMethod.GlideUrl(activity,imagen, v.findViewById(R.id.imgMarcadorMascota));
        return v;
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }
}
