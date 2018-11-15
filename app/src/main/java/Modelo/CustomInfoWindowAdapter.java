package Modelo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.whereismypet.whereismypet.R;

import de.hdodenhof.circleimageview.CircleImageView;
import finalClass.GeneralMethod;


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "CustomInfoWindowAdapter";
    private LayoutInflater inflater;
    private String nombre;
    private String descripcion;
    private String imagen;
    private Activity activity;

    public CustomInfoWindowAdapter(LayoutInflater inflater, Mascota pet, Activity activity){
        this.inflater = inflater;
        nombre = pet.getNombre();
        descripcion = pet.getDescripcion();
        imagen = pet.getImagen();
        this.activity = activity;
    }

    public CustomInfoWindowAdapter(LayoutInflater inflater, Tienda shop, Activity activity){
        this.inflater = inflater;
        nombre = shop.getNombre();
        descripcion = shop.getDescripcion();
        imagen = shop.getImagen();
        this.activity = activity;
    }

    @Override
    public View getInfoContents(final Marker m) {
        //Carga layout personalizado.
        View v = inflater.inflate(R.layout.dialog_marker, null);
        String[] info = m.getTitle().split("&");
        String url = m.getSnippet();
        ((TextView)v.findViewById(R.id.tvNombreMarcador)).setText(nombre);
        ((TextView)v.findViewById(R.id.tvDescrpcionMarcador)).setText(descripcion);
        GeneralMethod.GlideUrl(activity,imagen,(CircleImageView)v.findViewById(R.id.imgMarcadorMascota));
        return v;
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }
}
