package finalClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.whereismypet.whereismypet.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.regex.Pattern;

import actividades.MainActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public final class GeneralMethod {

    //-----------------------------------VALIDACIONES REGEX-------------------------------------------------------------
    private static String pathTomarFoto;

    public static String getPathTomarFoto() {
        return pathTomarFoto;
    }

    //-----------------------------------Imagen Circular----------------------------------------------------------------
    public static Bitmap getBitmapClip(Bitmap bitmap) {
        int maxLenth = bitmap.getWidth() <= bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(maxLenth,
                maxLenth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, maxLenth, maxLenth);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(maxLenth / 2, maxLenth / 2,
                maxLenth / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    //---------------------------------------VALIDACIONES DE COMPONENTES------------------------------------------------


    public static boolean RegexRegistro(String edit, View view) {
        boolean respuestaValidacion = false;
        Drawable msgerror = view.getResources().getDrawable(R.drawable.icon_error);
        msgerror.setBounds(0, 0, msgerror.getIntrinsicWidth(), msgerror.getIntrinsicHeight());

        final EditText etNombre = view.findViewById(R.id.nombreRegistro),
                etApellido = view.findViewById(R.id.apellidoRegistro),
                etCorreo = view.findViewById(R.id.emailRegistro),
                etConfirmarCorreo = view.findViewById(R.id.confirmar_emailRegistro),
                etContrasena = view.findViewById(R.id.passRegistro),
                etConfimarContrasena = view.findViewById(R.id.confirmpassRegistro);

        switch (edit) {
            case "nombre": {
                if (CheckEditTextIsEmptyOrNot(etNombre)) {
                    etNombre.setError("Campo Vacio", msgerror);
                } else {
                    Pattern p = Pattern.compile(Utils.REGEX_LETRAS);
                    if (!p.matcher(etNombre.getText().toString()).matches()) {
                        etNombre.setError("Este campo permite solo letras", msgerror);
                    } else {
                        etNombre.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }
            break;
            case "apellido": {
                if (CheckEditTextIsEmptyOrNot(etApellido)) {
                    etApellido.setError("Campo Vacio", msgerror);
                } else {
                    Pattern p = Pattern.compile(Utils.REGEX_LETRAS);
                    if (!p.matcher(etApellido.getText().toString()).matches()) {
                        etApellido.setError("Este campo permite solo letras", msgerror);
                    } else {
                        etApellido.setError(null);
                        respuestaValidacion = true;
                    }
                }

            }
            break;
            case "email": {
                if (CheckEditTextIsEmptyOrNot(etCorreo)) {
                    etCorreo.setError("Campo Vacio", msgerror);
                } else {
                    if (!Pattern.compile(Utils.REGEX_EMAIL).matcher(etCorreo.getText().toString()).matches()) {
                        etCorreo.setError("Correo Invalido", msgerror);
                    } else {
                        etCorreo.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }
            break;
            case "confirmaremail": {
                if (CheckEditTextIsEmptyOrNot(etConfirmarCorreo)) {
                    etConfirmarCorreo.setError("Campo Vacio", msgerror);
                } else {
                    if (etCorreo.getText().toString().equals(etConfirmarCorreo.getText().toString())) {
                        respuestaValidacion = true;
                    } else {
                        etConfirmarCorreo.setError("Debe coincidir con el correo ingresado anteriormente ", msgerror);
                    }
                }
            }
            break;
            case "password": {
                if (CheckEditTextIsEmptyOrNot(etContrasena)) {
                    etContrasena.setError("Campo Vacio", msgerror);
                } else {
                    if (!Pattern.compile(Utils.REGEX_PASSWORD).matcher(etContrasena.getText().toString()).matches()) {
                        etContrasena.setError("La contraseña debe contener al menos 8 caracteres alfanumericos, 1 minuscula, 1 mayuscula, 1 numero", msgerror);
                    } else {
                        etContrasena.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }
            break;

            case "confirmacontraseña": {
                if (CheckEditTextIsEmptyOrNot(etConfimarContrasena)) {
                    etConfimarContrasena.setError("Campo Vacio", msgerror);
                } else {
                    if (etContrasena.getText().toString().equals(etConfimarContrasena.getText().toString())) {
                        respuestaValidacion = true;
                    } else {
                        etConfimarContrasena.setError("Debe coincidir con la Contraseña ingresada anteriormente ", msgerror);
                    }
                }
            }
            break;
        }
        return respuestaValidacion;
    }

    public static boolean RegexLogin(String edit, Activity activity) {
        boolean respuestaValidacion = false;
        /*Drawable msgerror = activity.getResources().getDrawable(R.drawable.icon_error);
        msgerror.setBounds(0, 0, msgerror.getIntrinsicWidth(), msgerror.getIntrinsicHeight());*/
        final EditText etCorreoLogin = activity.findViewById(R.id.CorreoLogin),
                etContrasenaLogin = activity.findViewById(R.id.PasswordLogin);
        TextInputLayout ecorreoEroor=activity.findViewById(R.id.InputLayoutEmail),
                econtraseñaEroor=activity.findViewById(R.id.textInputLayoutPassword);

        switch (edit) {
            case "correo": {
                if (CheckEditTextIsEmptyOrNot(etCorreoLogin)) {
                    econtraseñaEroor.setError("Campo");
                } else {
                    Pattern p = Pattern.compile(Utils.REGEX_EMAIL);
                    if (!p.matcher(etCorreoLogin.getText().toString()).matches()) {
                        ecorreoEroor.setError("Correo");
                    } else {
                        etCorreoLogin.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }
            break;

            case "contrasenavacio": {
                if (CheckEditTextIsEmptyOrNot(etContrasenaLogin)) {
                    econtraseñaEroor.setError("Campo Vacio");
                } else {
                    if (!Pattern.compile(Utils.REGEX_PASSWORD).matcher(etContrasenaLogin.getText().toString()).matches()) {
                        econtraseñaEroor.setError("Recuerde que su contraseña contiene al menos 8 caracteres alfanumericos, " +
                                "1 minuscula, 1 mayuscula, 1 numero");
                    } else {
                        econtraseñaEroor.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }
            break;
        }
        return respuestaValidacion;
    }






    public static boolean RegexCargarTienda(String edit, View view) {
        boolean respuestaValidacion = false;
        Drawable msgerror = view.getResources().getDrawable(R.drawable.icon_error);
        msgerror.setBounds(0, 0, msgerror.getIntrinsicWidth(), msgerror.getIntrinsicHeight());

        final EditText etNombreTienda = view.findViewById(R.id.input_nombreShop),
                etDescripcionTienda= view.findViewById(R.id.input_descripcionShop),
                etTelefonoTienda =view.findViewById(R.id.input_telefonoShop),
                eDireccionTienda=view.findViewById(R.id.input_direccionShop);

        switch (edit) {
            case "nombre": {
                if (CheckEditTextIsEmptyOrNot(etNombreTienda)) {
                    etNombreTienda.setError("Campo Vacio", msgerror);
                } else {
                    Pattern p = Pattern.compile(Utils.REGEX_LETRAS);
                    if (!p.matcher(etNombreTienda.getText().toString()).matches()) {
                        etNombreTienda.setError("Este campo permite solo letras,", msgerror);
                    } else {
                        etNombreTienda.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }
            break;
            case "descripcion": {
                if (CheckEditTextIsEmptyOrNot(etDescripcionTienda)) {
                    etDescripcionTienda.setError("Campo Vacio", msgerror);
                } else {
                    Pattern p = Pattern.compile(Utils.REGEX_LETRAS);
                    if (!p.matcher(etDescripcionTienda.getText().toString()).matches()) {
                        etDescripcionTienda.setError("Este campo permite solo letras", msgerror);
                    } else {
                        etDescripcionTienda.setError(null);
                        respuestaValidacion = true;
                    }
                }

            }
            break;
            case "direccion": {
                if (CheckEditTextIsEmptyOrNot(eDireccionTienda)) {
                    eDireccionTienda.setError("Campo Vacio", msgerror);
                } else {
                    if (!Pattern.compile(Utils.Regex_LetrasNumeros).matcher(eDireccionTienda.getText().toString()).matches()) {
                        eDireccionTienda.setError("Este campo permite Permite letras y numeros", msgerror);
                    } else {
                        eDireccionTienda.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }break;
            case "telefono": {
                if (CheckEditTextIsEmptyOrNot(etTelefonoTienda)) {
                    etTelefonoTienda.setError("Campo Vacio", msgerror);
                } else {
                    if (!Pattern.compile(Utils.Regex_Numeros).matcher(etTelefonoTienda.getText().toString()).matches()) {
                        etTelefonoTienda.setError("Este campo permite solo numeros", msgerror);
                    } else {
                        etTelefonoTienda.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }break;



        }
        return respuestaValidacion;
    }

    public static boolean RegexCargarMascota(String edit, View view) {
        boolean respuestaValidacion = false;
        Drawable msgerror = view.getResources().getDrawable(R.drawable.icon_error);
        msgerror.setBounds(0, 0, msgerror.getIntrinsicWidth(), msgerror.getIntrinsicHeight());

        final EditText etNombreMascota = view.findViewById(R.id.input_nombre),
                etDescripcionMascota= view.findViewById(R.id.input_descripcion),
                etTelefonoMascota =view.findViewById(R.id.input_telefono);


        switch (edit) {
            case "nombre": {
                if (CheckEditTextIsEmptyOrNot(etNombreMascota)) {
                    etNombreMascota.setError("Campo Vacio", msgerror);
                } else {
                    Pattern p = Pattern.compile(Utils.REGEX_LETRAS);
                    if (!p.matcher(etNombreMascota.getText().toString()).matches()) {
                        etNombreMascota.setError("Este campo permite solo letras,", msgerror);
                    } else {
                        etNombreMascota.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }break;
            case "descripcion": {
                if (CheckEditTextIsEmptyOrNot(etDescripcionMascota)) {
                    etDescripcionMascota.setError("Campo Vacio", msgerror);
                } else {
                    Pattern p = Pattern.compile(Utils.REGEX_LETRAS);
                    if (!p.matcher(etDescripcionMascota.getText().toString()).matches()) {
                        etDescripcionMascota.setError("Este campo permite solo letras", msgerror);
                    } else {
                        etDescripcionMascota.setError(null);
                        respuestaValidacion = true;
                    }
                }

            }break;

            case "telefono": {
                if (CheckEditTextIsEmptyOrNot(etTelefonoMascota)) {
                    etTelefonoMascota.setError("Campo Vacio", msgerror);
                } else {
                    if (!Pattern.compile(Utils.Regex_Numeros).matcher(etTelefonoMascota.getText().toString()).matches()) {
                        etTelefonoMascota.setError("Este campo permite solo numeros", msgerror);
                    } else {
                        etTelefonoMascota.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }break;



        }
        return respuestaValidacion;
    }

    public static boolean RegexCargarPublicidad(String edit, View view) {
        boolean respuestaValidacion = false;
        Drawable msgerror = view.getResources().getDrawable(R.drawable.icon_error);
        msgerror.setBounds(0, 0, msgerror.getIntrinsicWidth(), msgerror.getIntrinsicHeight());

        final EditText etTituloPubli= view.findViewById(R.id.Titulo_publi),
                etDescripcionPubli= view.findViewById(R.id.Descripcion_publi),
                etPrecioPubli =view.findViewById(R.id.Precio_publi);


        switch (edit) {
            case "titulo": {
                if (CheckEditTextIsEmptyOrNot(etTituloPubli)) {
                    etTituloPubli.setError("Campo Vacio", msgerror);
                } else {
                    Pattern p = Pattern.compile(Utils.REGEX_LETRAS);
                    if (!p.matcher(etTituloPubli.getText().toString()).matches()) {
                        etTituloPubli.setError("Este campo permite solo letras,", msgerror);
                    } else {
                        etTituloPubli.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }break;
            case "descripcion": {
                if (CheckEditTextIsEmptyOrNot(etDescripcionPubli)) {
                    etDescripcionPubli.setError("Campo Vacio", msgerror);
                } else {
                    Pattern p = Pattern.compile(Utils.REGEX_LETRAS);
                    if (!p.matcher(etDescripcionPubli.getText().toString()).matches()) {
                        etDescripcionPubli.setError("Este campo permite solo letras,", msgerror);
                    } else {
                        etDescripcionPubli.setError(null);
                        respuestaValidacion = true;
                    }
                }
            }
            break;
            case "precio": {
                if (CheckEditTextIsEmptyOrNot(etPrecioPubli)) {
                    etPrecioPubli.setError("Campo Vacio", msgerror);
                } else {
                    Pattern p = Pattern.compile(Utils.Regex_Numeros);
                    if (!p.matcher(etPrecioPubli.getText().toString()).matches()) {
                        etPrecioPubli.setError("Este campo permite solo numeros", msgerror);
                    } else {
                        etPrecioPubli.setError(null);
                        respuestaValidacion = true;
                    }
                }

            }break;




        }
        return respuestaValidacion;
    }

















    private static boolean CheckEditTextIsEmptyOrNot(EditText editText) {
        return (TextUtils.isEmpty(editText.getText().toString().trim()));
    }

    //--------------------------CLASE TEXT WATCHER-------------------------------------------------------------
    public static class AddListenerOnTextChange implements TextWatcher {
        private Activity mActivity;
        EditText mEditTextView;

        public AddListenerOnTextChange(Activity activity, EditText editText) {
            super();
            this.mActivity = activity;
            this.mEditTextView = editText;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            RegexLogin("correo", mActivity);
            RegexLogin("correovacio", mActivity);
        }
    }


    //-------------------------------------CAMARA O SELECCION DE IMAGEN--------------------------------
    private static void cargarDialogoRecomendacion(final Activity mActivity) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(mActivity);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mActivity.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
                }
            }
        });
        dialogo.show();
    }

    //-----------------------------------------------------PERMISOS----------------------------------------------------
    public static boolean solicitaPermisosVersionesSuperiores(final Activity mActivity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((mActivity.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && mActivity.checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if ((mActivity.shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE) || (mActivity.shouldShowRequestPermissionRationale(CAMERA)))) {
            cargarDialogoRecomendacion(mActivity);
        } else {
            mActivity.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, Utils.MIS_PERMISOS);
        }

        return false;
    }

    //-------------------------------------------METODO PARA MOSTRAR UN SNACKBAR CON LOS ERRORES(MEJOR QUE UN TOAST)----------------------------------
    public static void showSnackback(String mMsgSnackbar, View view, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        final Snackbar mSnackbarEmptyField = Snackbar.make(view, mMsgSnackbar, Snackbar.LENGTH_LONG)
                .setAction("Aceptar", view1 -> {
                })
                .setActionTextColor(Color.MAGENTA);
        mSnackbarEmptyField.show();
    }


    //------------------------------------------Nombre Random--------------------------------------------------
    public static String getRandomString() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    //-------------------------------------------
    public static String getPath(Uri uri, Activity mActivity) {
        Cursor cursor = mActivity.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = mActivity.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        assert cursor != null;
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    //------------------------------------------------REDUCIR TAMAÑO DE IMAGEN-------------------------

    private static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static Uri reducirTamano(Uri uri, Activity activity) {
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = activity.getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            assert in != null;
            in.close();

            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap resultBitmap;
            in = activity.getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                // resize to desired dimensions
                assert resultBitmap != null;
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x, (int) y, true);
                resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            assert in != null;
            in.close();

            return getImageUri(activity.getApplicationContext(), resultBitmap);
        } catch (IOException e) {
            return null;
        }
    }

    //--------------------------------------------------LEER FOTO URL GLIDE
    public static void GlideUrl(Activity mActivity, String mLoadImage, CircleImageView mIntoImageView) {
        Glide.with(mActivity)
                .load(mLoadImage)
                .into(mIntoImageView);
    }
}
