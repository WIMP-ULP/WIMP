package dialogsFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import com.whereismypet.whereismypet.R;

import java.util.Calendar;
import java.util.Date;

import Modelo.Usuario;
import finalClass.GeneralMethod;
import finalClass.Utils;

public class DialogPremium  extends DialogFragment implements View.OnClickListener {//IMPLEMENTAR EL CLICK EN EL DIALOGO
    private String tipoLinkMembresia = "mensual";
    private FirebaseUser mUserFireBase;
    private DatabaseReference mDatabase;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.dialog_premium, null);
        //// ESTO AGREGUE
        CardView mMensual = content.findViewById(R.id.btnMensual),
                mTrimestral = content.findViewById(R.id.btnTrimestral),
                mSemestral = content.findViewById(R.id.btnSemestral);
        mMensual.setOnClickListener(this);
        mTrimestral.setOnClickListener(this);
        mSemestral.setOnClickListener(this);
        mUserFireBase = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        /*Bundle bundle = getArguments();
        mDatabase = (DatabaseReference) bundle.getSerializable("mDataBase");
        mUserFireBase = (FirebaseUser) bundle.getSerializable("mUserFireBase");*/
        EscuchadorDinamicLinkPremium();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(content);
        builder.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
            }
            return false;
        });
        return builder.create();
    }


    private void EscuchadorDinamicLinkPremium(){
        FirebaseDynamicLinks.getInstance().getDynamicLink(this.getActivity().getIntent())
                .addOnSuccessListener(pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null && pendingDynamicLinkData.getLink().toString().equals(Utils.PREMIUM)) {
                        Calendar fechaActual = Calendar.getInstance();
                        fechaActual.setTime(new Date());
                        Usuario.Premium mUserPremium= new Usuario.Premium()
                                .setIdPremium(GeneralMethod.getRandomString())
                                .setIdUsuario(mUserFireBase.getUid())
                                .setEstado("vigente")
                                .setFechaInicio(fechaActual.toString());
                        switch (tipoLinkMembresia){
                            case "mensual":{
                                fechaActual.add(Calendar.MONTH, 1);
                                mUserPremium.setFechaFin(fechaActual.getTime().toString());
                            }break;
                            case "trimestal":{
                                fechaActual.add(Calendar.MONTH, 3);
                                mUserPremium.setFechaFin(fechaActual.getTime().toString());
                            }break;
                            case "semestral":{
                                fechaActual.add(Calendar.MONTH, 6);
                                mUserPremium.setFechaFin(fechaActual.getTime().toString());
                            }break;
                        }
                        mDatabase.child("Usuarios").child(mUserFireBase.getUid()).child("Premium").setValue(mUserPremium);
                        mDatabase.child("Usuarios").child(mUserFireBase.getUid()).child("Historial Premium").child(mUserPremium.getIdPremium()).setValue(mUserPremium);
                    }
                })
                .addOnFailureListener(e -> Log.w("mLinks", "getDynamicLink:onFailure", e));
    }


    public void onClick(View v) {
        Uri mUriPremium = null;
        switch (v.getId()) {

            case R.id.btnMensual: {
                tipoLinkMembresia = "mensual";
                mUriPremium = Uri.parse("https://www.mercadopago.com/mla/checkout/start?pref_id=149281286-5c596eae-72a0-4cc9-93b4-9708bf50f9d7");
            }break;
            case R.id.btnTrimestral: {
                tipoLinkMembresia = "trimestral";
            }break;
            case R.id.btnSemestral: {
                tipoLinkMembresia = "semestral";
            }break;
        }
        startActivity(new Intent(Intent.ACTION_VIEW, mUriPremium));
    }
}
