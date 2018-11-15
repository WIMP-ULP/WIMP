package adaptadores;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.whereismypet.whereismypet.R;
import java.util.ArrayList;

import Modelo.Publicidad;
import de.hdodenhof.circleimageview.CircleImageView;
import finalClass.GeneralMethod;

public class AdaptadorPublicidades extends RecyclerView.Adapter<AdaptadorPublicidades.ViewHolderPublicidades> implements View.OnClickListener{

    private View.OnClickListener listenerOfertas;
    private ArrayList<Publicidad> ListaPublicidades;
    private Context context;

    public AdaptadorPublicidades(ArrayList<Publicidad> ofertas,Context context) {
        this.ListaPublicidades = ofertas;
        this.context=context;
    }


    @NonNull
    @Override
    public ViewHolderPublicidades onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_oferta,null,false );
        return new ViewHolderPublicidades( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPublicidades holder, int pos) {
        holder.tvtitulo.setText( ListaPublicidades.get(pos).getTituloOferta() );
        holder.tvprecio.setText( ListaPublicidades.get(pos).getPrecio() );
        holder.tvdescripcion.setText( ListaPublicidades.get(pos).getDescripcionOferta() );
        GeneralMethod.GlideUrl((Activity) context, ListaPublicidades.get(pos).getImgOferta(),holder.ivOferta);


    }



    class ViewHolderPublicidades extends RecyclerView.ViewHolder {

        private TextView tvtitulo;
        private TextView tvprecio;
        private TextView tvdescripcion;
        private CircleImageView ivOferta;
        ViewHolderPublicidades(View itemView) {
            super( itemView );


            tvtitulo = itemView.findViewById( R.id.TvTituloTienda);
            tvprecio = itemView.findViewById( R.id.TvPrecioTienda );
            tvdescripcion = itemView.findViewById( R.id.TvDescripcionTienda );
            ivOferta = itemView.findViewById( R.id.IvOfertaTienda );
        }

    }





    @Override
    public int getItemCount()
    {
        return ListaPublicidades.size();
    }


    @Override
    public void onClick(View v) {

    }

 /*   public void setOnClickListener(View.OnClickListener listener)
    {
        listenerOfertas = listener;
    }
    @Override
    public void onClick(View v) {
        if(listenerOfertas!=null)
            listenerOfertas.onClick( v );
    }
*/

}
