package cu.alexgi.youchat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;

import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;

import static cu.alexgi.youchat.MainActivity.context;

public class Web_view_fragment extends BaseSwipeBackFragment {

    private static String ruta;
    private WebView webView;
    private TextView titulo;
    private Dialog dialog;
    private Web_view_fragment web_view_fragment;

    public static Web_view_fragment newInstance(String r) {
        ruta = r;
        return new Web_view_fragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        return attachToSwipeBack(view);
//        return
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        web_view_fragment = this;
        titulo=view.findViewById(R.id.titulo);
        titulo.setText(new File(ruta).getName());

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
        webView=view.findViewById(R.id.webView);

        WebSettings ajustesVisorWeb = webView.getSettings();
        ajustesVisorWeb.setJavaScriptEnabled(true);
        ajustesVisorWeb.setSupportZoom(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ajustesVisorWeb.setSafeBrowsingEnabled(true);
        }
        ajustesVisorWeb.setLoadsImagesAutomatically(true);
        ajustesVisorWeb.setAllowFileAccessFromFileURLs(true);

        webView.loadUrl("file:///"+ruta);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                dialog = Utils.mostrarDialogCarga(web_view_fragment,context,"Cargando...");
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished (WebView view, String url){
                super.onPageFinished(view, url);
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
            }
        });

        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        view.findViewById(R.id.open_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(ruta);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    Uri uri = FileProvider.getUriForFile(context,
                            "cu.alexgi.youchat.fileprovider",file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, "text/html");
                    startActivity(intent);
                }
                else {
                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "text/html");
                    startActivity(intent);
                }
            }
        });

//        BufferedReader read = null;
//        StringBuilder data = new StringBuilder();
//        try {
//            read = new BufferedReader(new InputStreamReader(new FileInputStream(ruta), "UTF-8"));
//            String webData;
//            while ((webData = read.readLine()) != null){
//                data.append(webData);
//            }
//        } catch (FileNotFoundException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally {
//            webView.loadData(data.toString(), "text/html", "UTF-8");
//        }
    }
}
