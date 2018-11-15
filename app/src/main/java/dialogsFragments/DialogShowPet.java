package dialogsFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whereismypet.whereismypet.R;

import java.util.ArrayList;
import java.util.Objects;

import Modelo.Comentario;
import Modelo.Marcadores;
import Modelo.Mascota;
import adaptadores.AdaptadorComentarios;
import de.hdodenhof.circleimageview.CircleImageView;
import finalClass.GeneralMethod;

@SuppressLint("ValidFragment")
public class DialogShowPet extends DialogFragment implements View.OnClickListener{
    private Marcadores mDatosMascotas;
    private EditText eComentario;
    private int mEstadoTeclado = 0;
    private ArrayList<Comentario> mListaComentario;
    private RecyclerView mRecyclerComentarios;
    DatabaseReference mDatabase;

    public DialogShowPet(Mascota mDatosMascotas){
        this.mDatosMascotas = mDatosMascotas;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.dialog_marcador, null);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mListaComentario = new ArrayList<>();
        eComentario = content.findViewById(R.id.eComentarMarcador);
        eComentario.setOnClickListener(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(content);
        builder.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
            }
            return false;
        });
        CargarDatosMascota(content,(Mascota) mDatosMascotas);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eComentarMarcador:
                InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if(mEstadoTeclado == 0){
                    if (imm != null) {
                        eComentario.setText("");
                        mEstadoTeclado = 1;
                    }
                }break;
        }

    }

    private void CargarDatosMascota(View view, Mascota mMascota) {
        final TextView eDescripcionMascota = view.findViewById(R.id.eDescripcionMascota),
                eNombreMascota = view.findViewById(R.id.eNombreMascota);
        final CircleImageView imgMascota = view.findViewById(R.id.imgFotomascota);

        eDescripcionMascota.setText(mMascota.getDescripcion());
        eNombreMascota.setText(mMascota.getNombre());
        GeneralMethod.GlideUrl(this.getActivity(), mMascota.getImagen(),imgMascota);
        CargarComentariosMascota(mMascota,view);
    }

    // ver que el id de mascota esta en los comentarios relacion de 1 a muchos
    private void CargarComentariosMascota(Mascota mMascota,View view) {
        mDatabase.child("Usuarios").child(Objects.requireNonNull(mMascota.getIdMarcador())).child("Marcadores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("COMENTARIO", dataSnapshot.toString());
                Comentario mComentario = dataSnapshot.getValue(Comentario.class);
                mListaComentario.add(mComentario);
                if(mListaComentario.get(0) != null){
                    mRecyclerComentarios = view.findViewById(R.id.RecViewComentario);
                    mRecyclerComentarios.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    AdaptadorComentarios adapter = new AdaptadorComentarios(mListaComentario,view.getContext());
                    mRecyclerComentarios.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}