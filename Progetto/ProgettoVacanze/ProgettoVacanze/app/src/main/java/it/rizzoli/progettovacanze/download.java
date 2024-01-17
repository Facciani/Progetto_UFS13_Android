package it.rizzoli.progettovacanze;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.rizzoli.progettovacanze.databinding.FragmentDownloadBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link download#newInstance} factory method to
 * create an instance of this fragment.
 */
public class download extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ArrayList<String> storageList;
    ArrayList<StorageReference> storageReferences;
    protected FragmentDownloadBinding fragmentDownloadBinding;
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public download() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment download.
     */
    // TODO: Rename and change types and number of parameters
    public static download newInstance(String param1, String param2) {
        download fragment = new download();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView t = getView().findViewById(R.id.textViewFragment);
        t.setText(mParam1+mParam2);

        StorageReference listRef = storage.getReference().child("/"+mParam2);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        storageList = new ArrayList<String>();
                        storageReferences = new ArrayList<StorageReference>();

                        for (StorageReference item : listResult.getItems()) {

                            Log.i("Storage123Item", item.getPath());
                            storageReferences.add(item);
                            storageList.add(item.getName());

                        }


                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getView().getContext(), android.R.layout.simple_list_item_1, storageList);
                        ListView v = getView().findViewById(R.id.listView);
                        v.setAdapter(arrayAdapter);

                        v.setOnItemClickListener(((parent, view1, position, id) -> {

                            StorageReference islandRef = storage.getReference().child(storageReferences.get(position).getPath());

                            String name = storageReferences.get(position).getName();
                            String[] fileinfo = name.split("\\.");


                            islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DownloadManager.Request request = new DownloadManager.Request(uri);

                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setDestinationInExternalFilesDir(getContext(),Environment.DIRECTORY_DOWNLOADS, name);

                                    DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                    downloadManager.enqueue(request);

                                    Toast.makeText(getContext(), "Documento scaricato con successo", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getContext(), "Errore durante il download del file", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("ErroreStorage", e.getMessage());
                        Toast.makeText(getContext(), "Errore durante la visualizzazione dei file", Toast.LENGTH_SHORT).show();
                    };
                });}}
           