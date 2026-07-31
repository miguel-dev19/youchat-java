package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;

import java.util.ArrayList;

import cu.alexgi.youchat.YouChatApplication;

public class TextViewPostGI extends TextViewFontGenGI {

    public TextViewPostGI(Context context) {
        super(context);
        super.setLinksClickable(true);
        super.setLinkTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado()));
        super.setAutoLinkMask(Linkify.ALL);
    }

    public TextViewPostGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setLinksClickable(true);
        super.setLinkTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado()));
        super.setAutoLinkMask(Linkify.ALL);
    }

    public TextViewPostGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setLinksClickable(true);
        super.setLinkTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado()));
        super.setAutoLinkMask(Linkify.ALL);
    }

    @Override
    public synchronized void setText(CharSequence text, BufferType type) {
        SpannableString s;
        String result = "";
        String cad = text.toString();
        int l = cad.length();

        boolean encontroNegrita = false;
        boolean encontroMono = false;
        boolean encontroCursi = false;
        boolean encontroHastTag = false;
        int resta=0;

        ArrayList<objetivo> markDown = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            char car = cad.charAt(i);
            if (car == '#') {
                if(i+1<l && Character.isLetterOrDigit(cad.charAt(i + 1)))
                    if(!encontroHastTag){
                        encontroHastTag = true;
                        agregarIni(markDown,i-resta,0);
                    }
            }
            else if(encontroHastTag){
                if (!Character.isLetterOrDigit(car) && car!='_'){
                    encontroHastTag = false;
                    agregarFin(markDown,i-resta,0);
                }
                else if(i+1==l){
                    encontroHastTag = false;
                    agregarFin(markDown,i-resta+1,0);
                }
            }

            if(!encontroHastTag){
                if(car == '*'){
                    if(encontroNegrita){
                        encontroNegrita=false;
                        agregarFin(markDown,i-resta,1);
                    }
                    else {
                        encontroNegrita=true;
                        agregarIni(markDown,i-resta,1);
                    }
                    resta++;
                }
                else if(car == '\''){
                    if(encontroMono){
                        encontroMono=false;
                        agregarFin(markDown,i-resta,2);
                    }
                    else {
                        encontroMono=true;
                        agregarIni(markDown,i-resta,2);
                    }
                    resta++;
                }
                else if(car == '_'){
                    if(encontroCursi){
                        encontroCursi=false;
                        agregarFin(markDown,i-resta,3);
                    }
                    else {
                        encontroCursi=true;
                        agregarIni(markDown,i-resta,3);
                    }
                    resta++;
                }
                else {
                    result+=""+car;
                }
            }
            else {
                result+=""+car;
            }

        }
        resta=0;
        int cantMark = markDown.size();
        for(int k=0 ; k<cantMark ; k++){
            if(markDown.get(k).fin==-1){
                int pos = markDown.get(k).ini+resta;
                if(markDown.get(k).getTipo()==1)
                    result = result.substring(0,pos)+"*"+result.substring(pos);
                else if(markDown.get(k).getTipo()==2)
                    result = result.substring(0,pos)+"\'"+result.substring(pos);
                else if(markDown.get(k).getTipo()==3)
                    result = result.substring(0,pos)+"_"+result.substring(pos);
                resta++;
            }
        }
        s = new SpannableString(result);
        for(int k=0 ; k<cantMark ; k++){
            objetivo obj = markDown.get(k);
            if(obj.fin!=-1){
                if(obj.tipo==0)
                    s.setSpan(new ForegroundColorSpan(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado())), obj.ini+resta, obj.fin+resta, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                else if(obj.tipo==1)
                    s.setSpan(new StyleSpan(Typeface.BOLD), obj.ini+resta, obj.fin+resta, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                else if(obj.tipo==2)
                    s.setSpan(new TypefaceSpan("monospace"), obj.ini+resta, obj.fin+resta, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                else if(obj.tipo==3)
                    s.setSpan(new StyleSpan(Typeface.ITALIC), obj.ini+resta, obj.fin+resta, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            else resta--;
        }
//        l = s.length();
//        for(int i=0 ; i<l; i++){
//            if (s.charAt(i) == '#') {
//                if (i + 1 < l && Character.isLetterOrDigit(s.charAt(i + 1))) {
//                    fin = i+1;
//                    for (int j = i+1; j < l; j++) {
//                        if (Character.isLetterOrDigit(s.charAt(j)) || s.charAt(j)=='_') fin++;
//                        else break;
//                    }
//                    s.setSpan(new ForegroundColorSpan(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado())), i, fin, 0);
//                }
//            }
//        }

        super.setText(s, type);
    }

    void agregarIni(ArrayList<objetivo> markDown, int pos, int tipo){
        markDown.add(0, new objetivo(tipo, pos));
    }

    void agregarFin(ArrayList<objetivo> markDown, int pos, int tipo){
        int l = markDown.size();
        for(int i=0; i<l; i++){
            if(markDown.get(i).getTipo() == tipo){
                markDown.get(i).setFin(pos);
                break;
            }
        }
    }

    class objetivo{
        int tipo;
        int ini;
        int fin;

        public objetivo(int tipo) {
            this.tipo = tipo;
            this.ini = -1;
            this.fin = -1;
        }

        public objetivo(int tipo, int ini) {
            this.tipo = tipo;
            this.ini = ini;
            this.fin = -1;
        }

        public int getTipo() {
            return tipo;
        }

        public void setTipo(int tipo) {
            this.tipo = tipo;
        }

        public int getIni() {
            return ini;
        }

        public void setIni(int ini) {
            this.ini = ini;
        }

        public int getFin() {
            return fin;
        }

        public void setFin(int fin) {
            this.fin = fin;
        }
    }
}
