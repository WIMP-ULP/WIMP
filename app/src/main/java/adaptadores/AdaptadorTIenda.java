package adaptadores;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whereismypet.whereismypet.R;

import java.util.ArrayList;

import Modelo.Tienda;
import de.hdodenhof.circleimageview.CircleImageView;
import finalClass.GeneralMethod;

public class AdaptadorTIenda extends RecyclerView.Adapter<AdaptadorTIenda.ViewHolderTienda>{
   private ArrayList<Tienda>ListaTienda;
   private Context context;

   public AdaptadorTIenda(ArrayList<Tienda> datos, Context context) {
       this.ListaTienda = datos;
       this.context=context;
   }

   @NonNull
   @Override
   public ViewHolderTienda onCreateViewHolder(@NonNull ViewGroup parent, int i) {
       View view= LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_tienda,null,false );
       return new ViewHolderTienda(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolderTienda holder, int i) {
       holder.nombreTienda.setText(ListaTienda.get(i).getNombre());
       holder.descripcionTienda.setText(ListaTienda.get(i).getDescripcion());
       GeneralMethod.GlideUrl((Activity) context, ListaTienda.get(i).getImagen(),holder.imagenTienda);
       holder.telefonoTienda.setText(ListaTienda.get(i).getTelefono());
       holder.direccionTienda.setText(ListaTienda.get(i).getDireccion());
   }


   class ViewHolderTienda extends RecyclerView.ViewHolder {
       private CircleImageView imagenTienda;
       private TextView nombreTienda;
       private TextView descripcionTienda;
       private TextView telefonoTienda;
       private TextView direccionTienda;

       ViewHolderTienda(View itemView) {
           super(itemView);
           nombreTienda = itemView.findViewById(R.id.eNombreTienda);
           descripcionTienda = itemView.findViewById(R.id.eDescripcionTienda);
           imagenTienda = itemView.findViewById(R.id.imgMiTienda);
           telefonoTienda=itemView.findViewById(R.id.eTelefonoTienda);
           direccionTienda=itemView.findViewById(R.id.eDireccionTienda);
       }

   }

   @Override
   public int getItemCount() {
       return ListaTienda.size();
   }






}
