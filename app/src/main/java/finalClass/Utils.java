package finalClass;

import java.util.Arrays;
import java.util.List;

public final class Utils {
    /*//FIREBASE
    public static final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    public static final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static final StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    public static final FirebaseUser mUserFireBase = mFirebaseAuth.getCurrentUser();
    public static final String UserId = Objects.requireNonNull(mUserFireBase).getUid();


    public static final CallbackManager mCallbackManagerFacebook  = CallbackManager.Factory.create();  */
    //FACEBOOK--
    public static final List<String> mPermisosNecesariosFacebook = Arrays.asList("email", "user_birthday", "user_friends", "public_profile");

    //IMAGEN
    public static final int COD_SELECCIONA = 10;
    public static final int COD_FOTO = 20;
    public static final int MIS_PERMISOS = 100;
    //GOOGLE
    public static final int RC_SIGN_IN = 1;
    //PREMIUM
    public static final String PREMIUM = "http://wimp/pagos";

    //IMAGEN CARPETA
    public static final String DIRECTORIO_IMAGEN = "WIMP/imagenes";//ruta carpeta de directorios
    //IMAGEN DEFAULT FIREBASE
    public static  final String mDefaultUser = "https://firebasestorage.googleapis.com/v0/b/wimp-219219.appspot.com/o/Imagenes%2FPerfil%2FdefaultUser.jpg?alt=media&token=0651674e-50a9-45f6-990e-f36e3928fe98";
    public static final String mDefaultPet = "https://firebasestorage.googleapis.com/v0/b/wimp-219219.appspot.com/o/Imagenes%2FMarcadores%2FPet%2FdefaultPet.png?alt=media&token=9b6a329a-58ca-4ff7-81ec-46def18e9798";
    public static final String mDefautMarkerShop = "https://firebasestorage.googleapis.com/v0/b/wimp-219219.appspot.com/o/Imagenes%2FMarcadores%2FShop%2FdefaultShop.png?alt=media&token=ca7b5630-d219-489c-b0f5-a0a75daed0ac";
public static final String mDefaultItemOferta="https://firebasestorage.googleapis.com/v0/b/wimp-219219.appspot.com/o/Imagenes%2FPublicidad%2FDefaultProducto.png?alt=media&token=56348866-fda6-40ff-b014-41615b6b6295";
    //PATH
    public static String pathTomarFoto;
    //VALIDACIONES
    static final String REGEX_LETRAS = "^[a-zA-ZáÁéÉíÍóÓúÚñÑüÜ\\s]+$";
    static final String REGEX_EMAIL ="^[a-zA-Z0-9\\._-]+@[a-zA-Z0-9-]{2,}[.][a-zA-Z]{2,4}([.][a-zA-Z]{2,4})?$";
    static final String REGEX_PASSWORD = "^(?=\\w*\\d)(?=\\w*[A-Z])(?=\\w*[a-z])\\S{8,}$";
    static final String Regex_LetrasNumeros= "^[a-zA0-9-ZáÁéÉíÍóÓúÚñÑüÜ\\s]+$";
    static final String Regex_Numeros="^[0-9]+$";

    //STRING DE LOGUEO
    public static final String mFacebook = "facebook.com";
    public static final String mGoogle = "google.com";
    public static final String mPassword = "password";

    //LOCALIZACION
    public static final int PETICION_PERMISO_LOCALIZACION = 0;

    //ESTILOS DE MAPA
    public static boolean mSuccessMapStyle;

}
