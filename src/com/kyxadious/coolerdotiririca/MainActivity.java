package com.kyxadious.coolerdotiririca;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kyxadious.coolerdotiririca.model.Audios;
import com.kyxadious.coolerdotiririca.model.NomeArquivosDeAudio;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;

public class MainActivity extends ActionBarActivity {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		CustomBroadcastReceiver customBroadcastReceiver = new CustomBroadcastReceiver();
		registerReceiver(customBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
				
		/* Criando o diretório CoolerDoTiririca */
		File directory = new File(Environment.getExternalStorageDirectory() + "/CoolerDoTiririca");
		if(!directory.exists() && !directory.isDirectory()) {
			directory.mkdirs();
		}
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
			
		} if (id == R.id.action_share) {
			String appName = getResources().getString(R.string.app_name);
            String appNotas = "Confira \""+ appName +"\" - https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain"); 
            share.putExtra(Intent.EXTRA_TEXT, appNotas);
            startActivity(Intent.createChooser(share, "Compartilhar \""+ appName +"\" via"));
			return true;
			
		} if (id == R.id.action_about) {
			Intent intent = new Intent(getApplicationContext(), SobreActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}

    private class CustomBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            @SuppressWarnings("deprecation")
			NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            AdView adView = (AdView) findViewById(R.id.adView);
    		AdRequest adRequest = new AdRequest.Builder().build();
            
            if (currentNetworkInfo.isConnected()) {
            	
            	adView.loadAd(adRequest);
        
            	if (adView.getVisibility() == AdView.GONE) {
       		     	adView.setVisibility(View.VISIBLE);
            	}
        
            } else {
            	if (adView.getVisibility() == AdView.VISIBLE) {
       		     	adView.setVisibility(View.GONE);
            	}
            }
		}
    	
    }
	
	public static class PlaceholderFragment extends Fragment {
		
		private MediaPlayer mediaPlayer;
		private static final String TAG = PlaceholderFragment.class.getSimpleName();
		
		public PlaceholderFragment() {
			mediaPlayer = new MediaPlayer();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			
			ListView listView = (ListView) rootView.findViewById(R.id.list_view);
			NomeArquivosDeAudio nomeArquivosDeAudio =  new NomeArquivosDeAudio();
			ArrayList<String> lista = nomeArquivosDeAudio.getListaNomes();
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item, R.id.tv_item_texto, lista);
			listView.setAdapter(arrayAdapter);
			
			/* Click no item */
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					TextView textViewItem = (TextView) view.findViewById(R.id.tv_item_texto);
					String nomeItem = textViewItem.getText().toString();
					
					Audios audios = new Audios();
					String nomeArquivo = audios.getNomeArquivoAudio(nomeItem);
					
					copiarUmArquivoAssets(nomeArquivo);
					
					try {
					    AssetFileDescriptor assetFile = getActivity().getAssets().openFd(nomeArquivo);
					    mediaPlayer.reset();
					    mediaPlayer.setDataSource(assetFile.getFileDescriptor(), assetFile.getStartOffset(), assetFile.getLength());
					    mediaPlayer.prepare();
					    mediaPlayer.start();
					} catch (IOException e) {
						Log.d(TAG, "Erro ao reproduzir o áudio");
						Toast.makeText(getActivity(), "Erro ao reproduzir o áudio", Toast.LENGTH_LONG).show();
					}
					
				}
			});
			
			/* Click longo */
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {

				final String[] lista = {"Toque", "Alarme", "Notificação", "Compartilhar"}; 
				final ArrayAdapter<String> arrayAdapterLista = new ArrayAdapter<String>(getActivity(), R.layout.my_alert_dialog, R.id.tv_texto_alert_dialog, lista);
				
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
					TextView textViewItem = (TextView) view.findViewById(R.id.tv_item_texto);
					final String nomeItem = textViewItem.getText().toString();
					Audios audios = new Audios();
					final String nomeArquivo = audios.getNomeArquivoAudio(nomeItem);
					copiarUmArquivoAssets(nomeArquivo);
					
					/* Finalizar ação */
					AlertDialog.Builder msgBuilder = new AlertDialog.Builder(getActivity());
					msgBuilder.setTitle(nomeItem);
					msgBuilder.setAdapter(arrayAdapterLista, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String nomeItemAlertDialog = arrayAdapterLista.getItem(which);
							File path = new File(Environment.getExternalStorageDirectory()+"/CoolerDoTiririca", nomeArquivo);
							
						    if (nomeItemAlertDialog.equals(lista[0])) { // toque
						    	usarAudioComo(path, nomeItem, RingtoneManager.TYPE_RINGTONE);
						    	Toast.makeText(getActivity(), "Você usou " + nomeItem + " como toque", Toast.LENGTH_LONG).show();
						    	
						    } else if (nomeItemAlertDialog.equals(lista[1])) { // alarme
						    	usarAudioComo(path, nomeItem, RingtoneManager.TYPE_ALARM);
						    	Toast.makeText(getActivity(), "Você usou " + nomeItem + " como alarme", Toast.LENGTH_LONG).show();
						    	
						    } else if (nomeItemAlertDialog.equals(lista[2])) { // notificação
						    	usarAudioComo(path, nomeItem, RingtoneManager.TYPE_NOTIFICATION);
						    	Toast.makeText(getActivity(), "Você usou " + nomeItem + " como notificação", Toast.LENGTH_LONG).show();
						    	
						    } else if (nomeItemAlertDialog.equals(lista[3])) { // compartilhar 
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("audio/*");
                                //share.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK); 
                                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + path));
                                startActivity(Intent.createChooser(share, "Compartilhar"));

						    }
							
						}
					});
					msgBuilder.show();
					
					return true;
				}
			});
			
			
			return rootView;
		}
		
		
		
		private void usarAudioComo(File path, String fileName, int typeRingtoneManager){

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, path.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, true);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

            //Insert it into the database
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(path.getAbsolutePath());
            getActivity().getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + path.getAbsolutePath() + "\"", null);
            Uri newUri = getActivity().getContentResolver().insert(uri, values);

            if (typeRingtoneManager == RingtoneManager.TYPE_NOTIFICATION) {
                RingtoneManager.setActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_NOTIFICATION, newUri);
            } else if (typeRingtoneManager == RingtoneManager.TYPE_RINGTONE) { 
                RingtoneManager.setActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_RINGTONE, newUri);
            } else if (typeRingtoneManager == RingtoneManager.TYPE_ALARM) { 
                RingtoneManager.setActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_ALARM, newUri);
            }
		}
		
		private void copiarUmArquivoAssets(String nomeDoArquivo) {
            AssetManager assetManager = getActivity().getAssets();	    
            InputStream in = null;
            OutputStream out = null;
	        
	        try {
	            in = assetManager.open(nomeDoArquivo);
	            File outFile = new File(Environment.getExternalStorageDirectory() + "/CoolerDoTiririca", nomeDoArquivo);
	            out = new FileOutputStream(outFile);
	            copiarArquivo(in, out);
	            in.close();
	            in = null;
	            out.flush();
	            out.close();
	            out = null;
	        } catch(IOException e) {
	            Log.e(TAG, "Erro ao copiar arquivo no asset: " + nomeDoArquivo, e);
	        }       

		}

		private void copiarArquivo(InputStream in, OutputStream out) {
		    byte[] buffer = new byte[1024];
		    int read;
		    try {
		        while((read = in.read(buffer)) != -1){
		            out.write(buffer, 0, read);
		        }
		    } catch (IOException e) {
		        Log.d(TAG, "Erro ao copiar arquivo");
		    }
		}
	}

}

