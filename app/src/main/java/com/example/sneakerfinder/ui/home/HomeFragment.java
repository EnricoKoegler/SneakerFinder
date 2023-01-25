package com.example.sneakerfinder.ui.home;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentHomeBinding;
import com.example.sneakerfinder.ml.ClassificationResult;
import com.example.sneakerfinder.ml.SneakersClassifier;
import com.example.sneakerfinder.repo.ShoeRepository;
import com.example.sneakerfinder.ui.scan_result.ProductActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.fragment.FragmentKt.findNavController;
import static com.example.sneakerfinder.ui.scan_result.ProductActivity.EXTRA_SHOE_SCAN_ID;
import static com.example.sneakerfinder.ui.scanner.ScannerFragment.showAlertDialog;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnCaptureNow.setOnClickListener(this::onCaptureBtnClick);
        binding.btnUploadFromGallery.setOnClickListener(this::onUploadBtnClick);

        getParentFragmentManager().beginTransaction().
                add(R.id.fragement_placeholder0, ShoeFragment.newInstance("A", "Model", R.drawable.example_shoe4, 100)).
                add(R.id.fragement_placeholder1, ShoeFragment.newInstance("B", "Model", R.drawable.example_shoe3, 100)).
                add(R.id.fragement_placeholder2, ShoeFragment.newInstance("C", "Model", R.drawable.example_shoe4, 100)).
                add(R.id.fragement_placeholder3, ShoeFragment.newInstance("D", "Model", R.drawable.example_shoe4, 100)).
                add(R.id.fragement_placeholder4, ShoeFragment.newInstance("E", "Model", R.drawable.example_shoe3, 100)).
                commit();

        return root;
    }

    private void onCaptureBtnClick(View view) {
        findNavController(this).navigate(R.id.action_home_to_scanner);
    }

    private void onUploadBtnClick(View view) {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
        photoActivityResultLauncher.launch(fileIntent);
    }

    private ActivityResultLauncher<Intent> photoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    // data contains content uri
                    try {
                        String filePath = generateImageFile(result.getData().getData());
                        Toast.makeText(getActivity(), "Schuh wird erkannt...", Toast.LENGTH_LONG).show();
                        viewModel.recognizeShoe(filePath, new ShoeRepository.ShoeRecognitionCallback() {
                            @Override
                            public void onRecognitionComplete(long shoeScanId, ShoeRepository.RecognitionQuality quality) {
                                if (quality == ShoeRepository.RecognitionQuality.HIGH) {
                                    Intent i = new Intent(getActivity(), ProductActivity.class);
                                    i.putExtra(EXTRA_SHOE_SCAN_ID, shoeScanId);
                                    startActivity(i);
                                } else {
                                    showAlertDialog(requireActivity(), "We're sorry!", "SneakersFinder could not find the shoe.", null);
                                }
                            }

                            @Override
                            public void onError(long shoeScanId) {
                                Toast.makeText(getActivity(), "Bei der Erkennung ist ein Fehler aufgetreten", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException e) {
                        Toast.makeText(requireActivity(), "Datei konnte nicht gelesen werden", Toast.LENGTH_LONG).show();
                        Log.e("MediaSelectCopy", e.getMessage());
                    }
                }
            }
    );

    private String generateImageFile(Uri uri) throws IOException {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        String mimeType = contentResolver.getType(uri);

        // Copy file
        InputStream input = contentResolver.openInputStream(uri);

        DateFormat dateFormat = new SimpleDateFormat("ddMMyy_HHmmss", Locale.GERMANY);
        String timestamp = dateFormat.format(new Date());

        String fileExtension = "";
        if (uri.getPath() != null) {
            fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }
        String filename = "Scan" + timestamp + "." + fileExtension;

        File copyFile = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);

        FileOutputStream output = new FileOutputStream(copyFile);

        if (input != null) {
            copy(input, output);
        } else {
            throw new IOException("Failed to open InputStream");
        }

        Log.d("MediaSelectPath", copyFile.getAbsolutePath());

        return copyFile.getAbsolutePath();
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        int read;
        byte[] bytes = new byte[4096];

        while ((read = in.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
    }
}