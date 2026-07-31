package cu.alexgi.youchat;

import android.database.Cursor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.mail.Message;
import javax.mail.MessagingException;

import cu.alexgi.youchat.items.ItemAdjuntoCorreo;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemComentarioPost;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemContactoPublico;
import cu.alexgi.youchat.items.ItemDetallesTarjeta;
import cu.alexgi.youchat.items.ItemEstadisticaPersonal;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.items.ItemMensajeCorreo;
import cu.alexgi.youchat.items.ItemPost;
import cu.alexgi.youchat.items.ItemReaccionEstado;
import cu.alexgi.youchat.items.ItemTemas;
import cu.alexgi.youchat.items.ItemUsuario;
import cu.alexgi.youchat.items.ItemUsuarioCorreo;
import cu.alexgi.youchat.items.ItemVistaEstado;

public class Convertidor {

//    public static Bundle createBundleOfChat(ItemChat itemChat){
//        Bundle bundle = new Bundle();
//
//        bundle.putString("id", itemChat.getId());
//        bundle.putInt("tipo_mensaje", itemChat.getTipo_mensaje());
//        bundle.putInt("estado",itemChat.getEstado());
//        bundle.putString("correo",itemChat.getCorreo());
//
//        bundle.putString("mensaje",itemChat.getMensaje());
//        bundle.putString("ruta_Dato",itemChat.getRuta_Dato());
//        bundle.putString("hora",itemChat.getHora());
//        bundle.putString("fecha",itemChat.getFecha());
//
//        bundle.putString("id_msg_resp",itemChat.getId_msg_resp());
//
//        bundle.putString("emisor",itemChat.getEmisor());
//        bundle.putBoolean("es_reenviado",itemChat.EsReenviado());
//
//        bundle.putString("orden",itemChat.getOrden());
//
//        return bundle;
//    }

//    public static ItemChat createChatOfBundle(Bundle bundle){
//        String id = bundle.getString("id", "");
//        int tipo_mensaje = bundle.getInt("tipo_mensaje", 0);
//        int estado = bundle.getInt("estado", 0);
//        String correo = bundle.getString("correo", "");
//        String mensaje = bundle.getString("mensaje", "");
//        String ruta_Dato = bundle.getString("ruta_Dato", "");
//        String hora = bundle.getString("hora", "");
//        String fecha = bundle.getString("fecha", "");
//        String id_msg_resp = bundle.getString("id_msg_resp", "");
//        String emisor = bundle.getString("emisor", "");
//        boolean es_reenviado = bundle.getBoolean("es_reenviado", false);
//        String orden = bundle.getString("orden", "");
//
//        return new ItemChat(id,tipo_mensaje,estado,correo,mensaje,ruta_Dato,hora,fecha,id_msg_resp,emisor,es_reenviado,orden);
//    }

    public static ItemUsuario createItemUsuarioOfCursor(Cursor cursor){
        String correo = cursor.getString(0);

        int esAnclado = cursor.getInt(1);
        int cant_mensajes = cursor.getInt(2);

        int ult_msg_tipo = cursor.getInt(3);
        String ult_msg_texto = cursor.getString(4);
        int ult_msg_estado = cursor.getInt(5);
        String ult_msg_orden = cursor.getString(6);

        return new ItemUsuario(correo,esAnclado,cant_mensajes,ult_msg_tipo,ult_msg_texto,ult_msg_estado,ult_msg_orden);
    }

    public static ItemEstado createItemEstadoOfCursor(Cursor cursor){
        int pos = 0;

        String id = cursor.getString(pos++);
        String correo = cursor.getString(pos++);

        int tipo_estado = cursor.getInt(pos++);
        boolean esta_visto = cursor.getInt(pos++)==1;

        String ruta_img = cursor.getString(pos++);
        String texto = cursor.getString(pos++);
        int cant_me_gusta = cursor.getInt(pos++);
        int cant_me_encanta = cursor.getInt(pos++);
        int cant_me_sonroja = cursor.getInt(pos++);
        int cant_me_divierte = cursor.getInt(pos++);
        int cant_me_asombra = cursor.getInt(pos++);
        int cant_me_entristese = cursor.getInt(pos++);
        int cant_me_enoja = cursor.getInt(pos++);
        String hora = cursor.getString(pos++);
        String fecha = cursor.getString(pos++);
        String orden = cursor.getString(pos++);

        int estilo_texto = cursor.getInt(pos++);

        boolean estaDescargado = cursor.getInt(pos++) == 1;
        long uid = Long.parseLong(cursor.getString(pos++));
        String id_mensaje = cursor.getString(pos++);
        int peso_img = cursor.getInt(pos++);

        return new ItemEstado(id,correo,tipo_estado,esta_visto,ruta_img,texto,
                cant_me_gusta,cant_me_encanta,cant_me_sonroja,cant_me_divierte,
                cant_me_asombra,cant_me_entristese,cant_me_enoja,
                hora,fecha,orden,estilo_texto,estaDescargado,uid,id_mensaje, peso_img);
    }

    public static ItemVistaEstado createItemVistaEstadoOfCursor(Cursor cursor) {
        String idEstado = cursor.getString(0);
        String correo = cursor.getString(1);
        String hora = cursor.getString(2);
        String fecha = cursor.getString(3);

        return new ItemVistaEstado(idEstado,correo,hora,fecha);
    }

    public static ItemReaccionEstado createItemReaccionEstadoOfCursor(Cursor cursor){
        String idEstado = cursor.getString(0);
        String correo = cursor.getString(1);
        int tipo_reaccion = cursor.getInt(2);
        String hora = cursor.getString(3);
        String fecha = cursor.getString(4);

        return new ItemReaccionEstado(idEstado,correo,tipo_reaccion,hora,fecha);
    }

    public static ItemTemas createItemTemasOfCursor(Cursor cursor){
        int pos = 0;

        String id = cursor.getString(pos++);
        String nombre = cursor.getString(pos++);
        int tipo = cursor.getInt(pos++);
        boolean oscuro = cursor.getInt(pos++)==1;
        String rutaImg = cursor.getString(pos++);
        String creador = cursor.getString(pos++);

        String color_barra = cursor.getString(pos++);
        String color_btn = cursor.getString(pos++);
        String color_texto = cursor.getString(pos++);
        String color_fondo = cursor.getString(pos++);
        String color_interior = cursor.getString(pos++);
        String color_msg_izq = cursor.getString(pos++);
        String color_msg_der = cursor.getString(pos++);
        String color_msg_fecha = cursor.getString(pos++);
        String color_accento = cursor.getString(pos++);
        String color_ico_gen = cursor.getString(pos++);
        String font_barra = cursor.getString(pos++);
        String font_msg_izq = cursor.getString(pos++);
        String font_msg_der = cursor.getString(pos++);
        String font_msg_fecha = cursor.getString(pos++);
        String font_ico = cursor.getString(pos++);

        String color_dialogo = cursor.getString(pos++);
        String color_toast = cursor.getString(pos++);
        String color_burbuja = cursor.getString(pos++);
        String font_burbuja = cursor.getString(pos++);

        String color_barchat = cursor.getString(pos++);
        String font_barchat = cursor.getString(pos++);
        String sel_msj = cursor.getString(pos++);
        String status_bar = cursor.getString(pos++);
        if(status_bar==null || status_bar.isEmpty() || status_bar.equals("null"))
            status_bar=Utils.obtenerOscuroDe(color_barra);

        return new ItemTemas(id, nombre, tipo, oscuro, rutaImg, creador,
                color_barra, color_btn, color_fondo, color_texto,
                color_interior, color_msg_izq, color_msg_der, color_msg_fecha,
                color_accento, color_ico_gen, font_barra, font_msg_izq,
                font_msg_der, font_msg_fecha, font_ico, color_dialogo, color_toast,
                color_burbuja, font_burbuja, color_barchat, font_barchat, sel_msj,status_bar);
    }

    public static ItemContacto createItemContactoOfCursor(Cursor cursor){
        String alias = cursor.getString(0);
        String nombre = cursor.getString(1);
        String correo = cursor.getString(2);

        int tipo = cursor.getInt(3);
        int version = cursor.getInt(4);

        String ruta_img = cursor.getString(5);
        String info = cursor.getString(6);
        String telefono = cursor.getString(7);
        String genero = cursor.getString(8);
        String provincia = cursor.getString(9);
        String fecha_nac = cursor.getString(10);

        String ultHoraConex = cursor.getString(11);
        String ultFechaConex = cursor.getString(12);

        int usaYC = cursor.getInt(13);

        int sil = cursor.getInt(14);
        int bloq = cursor.getInt(15);

        //cursor.getString(16); este es el nombre a ordenar
        //bd v3
        int cant_seguidores = cursor.getInt(17);
        if(cant_seguidores<0) cant_seguidores=0;

        return new ItemContacto(alias,nombre,correo,tipo,version,ruta_img,info,telefono,
                genero,provincia,fecha_nac,ultHoraConex,ultFechaConex,usaYC,sil,bloq,cant_seguidores);
    }

    public static ItemContactoPublico createItemContactoPublicoOfCursor(Cursor cursor){
        int pos=0;
        String alias = cursor.getString(pos++);
        String correo = cursor.getString(pos++);
        String info = cursor.getString(pos++);
        String telefono = cursor.getString(pos++);
        String genero = cursor.getString(pos++);
        String provincia = cursor.getString(pos++);
        String fecha_nac = cursor.getString(pos++);

        return new ItemContactoPublico(alias,correo,info,telefono,
                genero,provincia,fecha_nac);
    }

    public static ItemContacto createItemContactoOfMessage(Message message){
        String telefono="", genero="", provincia="", fecha_nac="";
        int version=0, cant_seguidores=0;
        try{
            String[] versiones = message.getHeader(ItemChat.KEY_VERSION);
            if(versiones!=null)
                version=createIntOfString(versiones[0]);

            String[] telefonos = message.getHeader(ItemChat.PERFIL_KEY_TELEFONO);
            if(telefonos!=null)
                telefono=telefonos[0];
            else telefono="";

            String[] generos = message.getHeader(ItemChat.PERFIL_KEY_GENERO);
            if(generos!=null)
                genero=generos[0];
            else genero="";

            String[] provincias = message.getHeader(ItemChat.PERFIL_KEY_PROVINCIA);
            if(provincias!=null)
                provincia=provincias[0];
            else provincia="";

            String[] fecha_nacs = message.getHeader(ItemChat.PERFIL_KEY_FECHA_NAC);
            if(fecha_nacs!=null)
                fecha_nac=fecha_nacs[0];
            else fecha_nac="";

            String[] seguidores = message.getHeader(ItemChat.PERFIL_KEY_SEGUIDORES);
            if(seguidores!=null)
                cant_seguidores=createIntOfString(seguidores[0]);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return new ItemContacto("","","",1,version,"",
                "",telefono,genero,provincia,fecha_nac,"","",
                true,false,false, cant_seguidores);

    }

    public static ItemChat createItemChatOfCursor(Cursor cursor){

        String id = cursor.getString(0);
        int tipo_mensaje = cursor.getInt(1);
        int estado = cursor.getInt(2);
        String correo = cursor.getString(3);
        String mensaje = cursor.getString(4);
        String ruta_Dato = cursor.getString(5);
        String hora = cursor.getString(6);
        String fecha = cursor.getString(7);
        String id_msg_resp = cursor.getString(8);
        String emisor = cursor.getString(9);
        boolean esReenviado = cursor.getInt(10)==1;
        String orden = cursor.getString(11);
        int esEditado;
        if(cursor.getInt(12)!=0 && cursor.getInt(12)!=1) esEditado=0;
        else esEditado = cursor.getInt(12);

        String id_mensaje = cursor.getString(13);
        if(id_mensaje==null || id_mensaje.equals("null")) id_mensaje="";
        int peso = cursor.getInt(14);
        if(peso<0) peso=0;
        int descargadoInt = cursor.getInt(15);
        if(descargadoInt!=0 && descargadoInt!=1) descargadoInt=1;
        boolean descargado = descargadoInt==1;

        return new ItemChat(id,tipo_mensaje,estado,correo,mensaje,ruta_Dato,
                hora,fecha,id_msg_resp,emisor,esReenviado,orden,
                esEditado==1,
                id_mensaje,peso,descargado);
    }

    public static ItemChat createItemChatOfMessage(Message message, String id,
                                                   String correo, String hora , String fecha, String orden){
        String id_msg_resp="",emisor;
        boolean esReenviado=false;
        int tipo=1;
        String[] dato_temp;
        try {
            dato_temp = message.getHeader(ItemChat.KEY_TIPO);
            if(dato_temp!=null) tipo=createIntOfString(dato_temp[0]);

            emisor = correo;

            dato_temp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
            if(dato_temp!=null) id_msg_resp=dato_temp[0];

            dato_temp = message.getHeader(ItemChat.KEY_REENVIADO);
            if(dato_temp!=null) esReenviado = dato_temp[0].equals("1");

        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }

        return new ItemChat(id,tipo,ItemChat.ESTADO_RECIBIDO,correo,"","",
                hora,fecha,id_msg_resp,emisor,esReenviado,orden, false,"",0,true);
    }

    public static ItemEstado createItemEstadoOfMessage(Message message, String correo, String hora , String fecha){
        ItemEstado estado;
        String id="", orden;
        int tipo_estado, estilo_texto;
        try {
            String[] ids = message.getHeader(ItemChat.KEY_ID);
            if(ids!=null){
                id=ids[0];
            }

            String[] tipos = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
            if(tipos!=null)
                tipo_estado=createIntOfString(tipos[0]);
            else tipo_estado=0;

            tipos = message.getHeader(ItemChat.KEY_TIPO_LETRA_EST);
            if(tipos!=null)
                estilo_texto=createIntOfString(tipos[0]);
            else estilo_texto=0;

//            String[] horas = message.getHeader(ItemChat.KEY_HORA);
//            if(horas!=null)
//                hora=horas[0];
//            else hora="";
//
//            String[] fechas = message.getHeader(ItemChat.KEY_FECHA);
//            if(fechas!=null)
//                fecha=fechas[0];
//            else fecha="";

        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        orden = sdf.format(date);

        estado = new ItemEstado(id,correo,tipo_estado,false,"","",
                0,0,0,0,
                0,0,0,
                hora,fecha,orden,estilo_texto,true,0, "",0);
        return estado;
    }

    public static ItemEstado createItemEstadoOfMessage(Message message, String id, String correo, String hora , String fecha){
        ItemEstado estado;
        String orden;
        int tipo_estado, estilo_texto;
        try {

            String[] tipos = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
            if(tipos!=null)
                tipo_estado=createIntOfString(tipos[0]);
            else tipo_estado=0;

            tipos = message.getHeader(ItemChat.KEY_TIPO_LETRA_EST);
            if(tipos!=null)
                estilo_texto=createIntOfString(tipos[0]);
            else estilo_texto=0;

//            String[] horas = message.getHeader(ItemChat.KEY_HORA);
//            if(horas!=null)
//                hora=horas[0];
//            else hora="";
//
//            String[] fechas = message.getHeader(ItemChat.KEY_FECHA);
//            if(fechas!=null)
//                fecha=fechas[0];
//            else fecha="";

        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        orden = sdf.format(date);

        estado = new ItemEstado(id,correo,tipo_estado,false,"","",
                0,0,0,0,
                0,0,0,
                hora,fecha,orden,estilo_texto,true,0, "",0);
        return estado;
    }

    public static ItemEstadisticaPersonal createItemEstadisticaPersonalOfCursor(Cursor cursor) {
        String id = cursor.getString(0);
        int d1 = cursor.getInt(1);
        int d2 = cursor.getInt(2);
        int d3 = cursor.getInt(3);
        int d4 = cursor.getInt(4);
        int d5 = cursor.getInt(5);
        int d6 = cursor.getInt(6);
        int d7 = cursor.getInt(7);
        int d8 = cursor.getInt(8);
        int d9 = cursor.getInt(9);
        int d10 = cursor.getInt(10);
        int d11 = cursor.getInt(11);
        int d12 = cursor.getInt(12);
        int d13 = cursor.getInt(13);
        int d14 = cursor.getInt(14);
        int d15 = cursor.getInt(15);
        int d16 = cursor.getInt(16);
        int d17 = cursor.getInt(17);
        int d18 = cursor.getInt(18);
        int d19 = cursor.getInt(19);
        int d20 = cursor.getInt(20);
        int d21 = cursor.getInt(21);
        int d22 = cursor.getInt(22);
        int d23 = cursor.getInt(23);
        int d24 = cursor.getInt(24);

        return new ItemEstadisticaPersonal(id,d1,d2,d3,d4,d5,d6,d7,d8,d9,d10,d11,d12,d13,
                d14,d15,d16,d17,d18,d19,d20,d21,d22,d23,d24);
    }

    public static ItemTemas createItemTemasOfMensaje(String mensaje) {
        String[] result = mensaje.split("<s,p>");
        if(result.length<27) return null;
        int pos=0;
        String id = result[pos++];
        String nombre = result[pos++];
        int     tipo = 2;
        boolean oscuro = result[pos++].equals("1");
        String rutaImg = result[pos++];
        String creador = result[pos++];
        String color_barra = result[pos++];
        String color_btn = result[pos++];
        String color_fondo = result[pos++];
        String color_texto = result[pos++];
        String color_interior = result[pos++];
        String color_msg_izq = result[pos++];
        String color_msg_der = result[pos++];
        String color_msg_fecha = result[pos++];
        String color_accento = result[pos++];
        String color_ico_gen = result[pos++];
        String font_barra = result[pos++];
        String font_msg_izq = result[pos++];
        String font_msg_der = result[pos++];
        String font_msg_fecha = result[pos++];
        String font_texto_resaltado = result[pos++];
        String color_dialogo = result[pos++];
        String color_toast = result[pos++];
        String color_burbuja = result[pos++];
        String font_burbuja = result[pos++];
        String color_barchat = result[pos++];
        String font_barchat = result[pos++];
        String sel_msj = result[pos++];
        String status_bar;
        if(result.length>=28){
            status_bar = result[pos++];
            if(status_bar==null || status_bar.isEmpty() || status_bar.equals("null"))
                status_bar=Utils.obtenerOscuroDe(color_barra);
        }
        else status_bar=Utils.obtenerOscuroDe(color_barra);
        return new ItemTemas( id,  nombre, tipo, oscuro,
                rutaImg,  creador,  color_barra,
                color_btn,  color_fondo,  color_texto,
                color_interior,  color_msg_izq,  color_msg_der,
                color_msg_fecha,  color_accento,  color_ico_gen,
                font_barra,  font_msg_izq,  font_msg_der,
                font_msg_fecha,  font_texto_resaltado,  color_dialogo,
                color_toast,  color_burbuja,  font_burbuja,
                color_barchat,  font_barchat,  sel_msj, status_bar);
    }

    public static ItemDetallesTarjeta createItemDetallesTarjetaOfMensaje(String mensaje) {
        String[] result = mensaje.split("<s,p>");
        int pos=0;
        int posLottie = createIntOfString(result[pos++]);
        int tipoLottie = createIntOfString(result[pos++]);
        int colorFondo = createIntOfString(result[pos++]);
        return new ItemDetallesTarjeta(posLottie,tipoLottie,colorFondo);
    }

    public static ItemUsuarioCorreo createItemUsuarioCorreoOfCursor(Cursor cursor) {
        int pos = 0;
        String correo = cursor.getString(pos++);
        String nombre = cursor.getString(pos++);
        String groupId = cursor.getString(pos++);
        String hora = cursor.getString(pos++);
        String fecha = cursor.getString(pos++);
        String orden = cursor.getString(pos++);
        return new ItemUsuarioCorreo(correo,nombre,groupId,hora,fecha,orden);
    }

    public static ItemMensajeCorreo createItemMensajeCorreoOfCursor(Cursor cursor) {
        int pos = 0;
        String id = cursor.getString(pos++);
        long uid = Long.parseLong(cursor.getString(pos++));
        boolean esMio = cursor.getInt(pos++) == 1;
        boolean esNuevo = cursor.getInt(pos++) == 1;
        boolean esFav = cursor.getInt(pos++) == 1;
        String correo = cursor.getString(pos++);
        String remitente = cursor.getString(pos++);
        String nombre = cursor.getString(pos++);
        String destinatario = cursor.getString(pos++);
        String asunto = cursor.getString(pos++);
        String texto = cursor.getString(pos++);
        int estado = cursor.getInt(pos++);
        boolean esRespondido = cursor.getInt(pos++) == 1;
        int peso = cursor.getInt(pos++);
        String hora = cursor.getString(pos++);
        String fecha = cursor.getString(pos++);
        String orden = cursor.getString(pos++);
        return new ItemMensajeCorreo(id,uid,esMio,esNuevo,esFav,correo,remitente,nombre,destinatario,asunto,texto,
                estado,esRespondido,peso,hora,fecha,orden);
    }

    public static ItemAdjuntoCorreo createItemAdjuntoCorreoOfCursor(Cursor cursor) {
        int pos = 0;
        String id = cursor.getString(pos++);
        String id_mensaje = cursor.getString(pos++);
        String correo = cursor.getString(pos++);
        int posicion = cursor.getInt(pos++);
        String nombre = cursor.getString(pos++);
        int tipo = cursor.getInt(pos++);
        int peso = cursor.getInt(pos);
        return new ItemAdjuntoCorreo(id,id_mensaje,correo,posicion,nombre,tipo,peso);
    }

    public static ItemPost createItemPostOfCursor(Cursor cursor) {
        int pos = 0;
        String id = cursor.getString(pos++);
        long uid = Long.parseLong(cursor.getString(pos++));
        int tipo_post = cursor.getInt(pos++);
        String nombre = cursor.getString(pos++);
        String correo = cursor.getString(pos++);
        int tipo_usuario = cursor.getInt(pos++);
        int icono = cursor.getInt(pos++);
        String texto = cursor.getString(pos++);
        boolean es_nuevo = cursor.getInt(pos++) == 1;
        String ruta_dato = cursor.getString(pos++);
        int peso_dato = cursor.getInt(pos++);
        String hora = cursor.getString(pos++);
        String fecha = cursor.getString(pos++);
        String orden = cursor.getString(pos);
        return new ItemPost(id,uid,tipo_post,nombre,correo,tipo_usuario,icono,texto,es_nuevo,ruta_dato,peso_dato,hora,fecha,orden);
    }

    public static ItemComentarioPost createItemComentarioPostOfCursor(Cursor cursor) {
        int pos = 0;
        String id = cursor.getString(pos++);
        String id_post = cursor.getString(pos++);
        int tipo_post = cursor.getInt(pos++);
        String nombre = cursor.getString(pos++);
        String correo = cursor.getString(pos++);
        int tipo_usuario = cursor.getInt(pos++);
        int icono = cursor.getInt(pos++);
        String texto = cursor.getString(pos++);
        String ruta_dato = cursor.getString(pos++);
        int peso_dato = cursor.getInt(pos++);
        String hora = cursor.getString(pos++);
        String fecha = cursor.getString(pos++);
        String orden = cursor.getString(pos);
        return new ItemComentarioPost(id,id_post,tipo_post,nombre,correo,tipo_usuario,icono,texto,ruta_dato,peso_dato,hora,fecha,orden);
    }

    public static int createIntOfString(String cad){
        int l=cad.length(), result=0;
        for(int i=0; i<l; i++){
            result+=(cad.charAt(i)-48);
            if(i+1!=l) result*=10;
        }
        return result;
    }

    public static int createIntOfStringHora(String cad){
        if (cad.equals("")) return 0;
        int esPM = 0;
        if(cad.charAt(cad.length()-2)=='p' || cad.charAt(cad.length()-2)=='P')
            esPM=12;
        int l=cad.length()-3, hr=0, min=0;
        boolean yaHora=false;
        for(int i=0; i<l; i++){
            if(cad.charAt(i)==':' && !yaHora){
                yaHora=true;
                if(hr!=12) hr+=esPM;
                if(hr==12 && esPM==0) hr=0;
                hr*=60;
            }
            else if(cad.charAt(i)>='0' && cad.charAt(i)<='9'){
                if(yaHora){
                    min*=10;
                    min+=(cad.charAt(i)-48);
                }
                else {
                    hr*=10;
                    hr+=(cad.charAt(i)-48);
                }
            }
        }
        return hr+min;
    }

    public static String conversionFecha(String fecha) {
        //0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
        //y y y y M M d d H H m m s s
        String dd=fecha.charAt(6)+""+fecha.charAt(7);
        String mm=fecha.charAt(4)+""+fecha.charAt(5);
        String aa=fecha.charAt(0)+""+fecha.charAt(1)+""+fecha.charAt(2)+""+fecha.charAt(3);
        return dd+"/"+mm+"/"+aa;
    }

    public static String conversionHora(String fecha) {
        //0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
        //y y y y M M d d H H m m s s
        int h = (fecha.charAt(8)-48)*10+(fecha.charAt(9)-48);
        String m = fecha.charAt(10)+""+fecha.charAt(11);
        if(h==0) return "12:"+m+" am";
        else if(h>12) return (h-12)+":"+m+" pm";
        else if(h==12) return "12:"+m+" pm";
        return h+":"+m+" am";
    }

    public static String convertirFechaAFechaLinda(String fecha){
        if(fecha.length()<4) return "fecha no encontrada";
        int dd = (fecha.charAt(0)-48)*10 + (fecha.charAt(1)-48);
        int m=(fecha.charAt(3)-48)*10+(fecha.charAt(4)-48);
        String mm ="";
        switch (m){
            case 1: mm=" de ene"; break;
            case 2: mm=" de feb"; break;
            case 3: mm=" de mar"; break;
            case 4: mm=" de abr"; break;
            case 5: mm=" de may"; break;
            case 6: mm=" de jun"; break;
            case 7: mm=" de jul"; break;
            case 8: mm=" de ago"; break;
            case 9: mm=" de sep"; break;
            case 10: mm=" de oct"; break;
            case 11: mm=" de nov"; break;
            case 12: mm=" de dic"; break;
            default: mm=" del calendario apocalíptico";
        }
        return dd+mm;
    }

    public static String generarCodigo() {
        String cad = YouChatApplication.correo;
        String newCad = "";
        int l = cad.length();
        for(int i=0; i<l; i++){
            if(cad.charAt(i)=='@')
                break;
            else
                newCad = newCad+cad.charAt(i);
        }
        newCad = newCad.replace(".", "");
        l = newCad.length();
        int rep = 0;
        String codigo = "";
        for(int i = 0; i<l; i++){
            char actChar = newCad.charAt(i);
            if(rep==2){
                rep=0;
                int a = actChar;
                a%=9;
                String c = ""+(a+48);
                codigo=codigo+c;
            }
            if(actChar=='x' || actChar=='X')
                codigo = codigo+"a";
            else if(actChar=='y' || actChar=='Y')
                codigo = codigo+"b";
            else if(actChar=='z' || actChar=='Z')
                codigo = codigo+"c";
            else if(actChar=='e' || actChar=='E')
                codigo = codigo+"#";
            else if(actChar=='a' || actChar=='A')
                codigo = codigo+"T";
            else if(actChar=='o' || actChar=='O')
                codigo = codigo+"$";
            else if(actChar=='l' || actChar=='L')
                codigo = codigo+"%e";
            else codigo = codigo + (actChar+3);
            rep++;
        }

        return codigo;
    }

    public static File obtenerFileStickerThumb(String nombre){
        File fc = new File(YouChatApplication.RUTA_STICKERS_CACHE+File.separator+"Thumb"+File.separator);
        if(!fc.exists()) fc.mkdirs();

        if(fc.exists()) {
            String dir = YouChatApplication.RUTA_STICKERS_CACHE+File.separator+"Thumb"+File.separator;
            nombre = nombre+".json";

            return new File(dir,nombre);
        }
        return null;
    }

    public static File obtenerFileStickerCache(String ruta_padre_sticker, String nombre){
        File fc = new File(YouChatApplication.RUTA_STICKERS_CACHE+File.separator);
        if(!fc.exists()) fc.mkdirs();
        String[] a = ruta_padre_sticker.split("Stickers");
        if(a.length>0 && fc.exists()) {
            String dir = YouChatApplication.RUTA_STICKERS_CACHE+a[0];
            if(a.length>1)
                dir = YouChatApplication.RUTA_STICKERS_CACHE+a[1];
            nombre = nombre.replace(".tgs",".json");
            if(!nombre.endsWith(".tgs")) nombre+=".tgs";
            File dirFinal = new File(dir);
            if(!dirFinal.exists()) dirFinal.mkdirs();
            if(dirFinal.exists()){
                return new File(dir,nombre);
            }
            else{
                return null;
            }
        }
        return null;
    }

    public static String obtenerRutaStickerCache(String ruta_sticker){
        File fc = new File(YouChatApplication.RUTA_STICKERS_CACHE+File.separator);
        if(!fc.exists()) fc.mkdirs();
        String[] a = ruta_sticker.split("Stickers");
        if(a.length>0) {
            String dir =YouChatApplication.RUTA_STICKERS_CACHE+a[1].replace(".tgs",".json");
            File dirFinal = new File(dir);
//            Log.e("ruta ", "ff"+dirFinal.getParentFile());
            if(!dirFinal.getParentFile().exists()) dirFinal.getParentFile().mkdirs();

            return dir;
        }
        return null;
    }
}
