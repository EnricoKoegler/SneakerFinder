package com.example.sneakerfinder.ui.main_activity.saved_results;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.databinding.FragmentSavedResultsBinding;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;
import com.example.sneakerfinder.helper.UIHelper;
import com.example.sneakerfinder.ui.scan_processing.ScanProcessingActivity;
import com.example.sneakerfinder.ui.scan_result.ProductActivity;
import com.example.sneakerfinder.ui.similar_shoes.SimilarShoesActivity;
import com.example.sneakerfinder.ui.similar_shoes.SimilarShoesAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.navigation.fragment.FragmentKt.findNavController;

public class SavedResultsFragment extends Fragment implements SavedResultsAdapter.ItemClickListener{

    private FragmentSavedResultsBinding binding;

    private List<ShoeScanWithShoeScanResults> shoeScanWithShoeScanResults;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SavedResultsViewModel savedResultsViewModel =
                new ViewModelProvider(this).get(SavedResultsViewModel.class);

        binding = FragmentSavedResultsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Create an adapter for our list:
        SavedResultsAdapter adapter = new SavedResultsAdapter(root.getContext());

        // Set the adapter to be used by the ListView:
        RecyclerView listview = root.findViewById(R.id.shoe_list_view);
        listview.setAdapter(adapter);
        listview.setLayoutManager(new LinearLayoutManager(root.getContext()));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (shoeScanWithShoeScanResults != null) {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    ShoeScanWithShoeScanResults results =
                            shoeScanWithShoeScanResults.get(adapterPosition);

                    ShoeScanResultWithShoe topResult = results.shoeScanResults.get(0);
                    String topResultName = null;
                    if (topResult != null && topResult.shoe != null) topResultName = topResult.shoe.name;

                    AlertDialog alertDialog = new AlertDialog.Builder(requireActivity()).create();
                    alertDialog.setTitle("Do you really want to delete this scan result?");
                    if (topResultName != null)
                        alertDialog.setMessage(topResultName + " will be gone forever.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            (dialog, which) -> {
                                savedResultsViewModel.deleteShoeScan(results.shoeScan.shoeScanId);
                                dialog.dismiss();
                            });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
                            (dialogInterface, i) -> dialogInterface.dismiss());
                    alertDialog.setIcon(R.drawable.alert_triangle);
                    alertDialog.setOnDismissListener(dialogInterface -> adapter.notifyDataSetChanged());
                    alertDialog.show();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                Paint paint = new Paint();
                paint.setColor(ContextCompat.getColor(requireActivity(), R.color.red));
                paint.setStyle(Paint.Style.FILL);

                c.drawRoundRect(new RectF(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom()), 30 , 30, paint);

                Drawable icon = ContextCompat.getDrawable(requireActivity(), R.drawable.trash_white);

                if (icon != null) {
                    int iconMargin = 40;
                    int iconSize = icon.getIntrinsicHeight();
                    int halfIcon = iconSize / 2;
                    int top = itemView.getTop() + ((itemView.getBottom() - itemView.getTop()) / 2 - halfIcon);

                    icon.setBounds(
                            itemView.getLeft() + iconMargin,
                            top,
                            itemView.getLeft() + iconMargin + icon.getIntrinsicWidth(),
                            top + icon.getIntrinsicHeight());
                    icon.draw(c);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(listview);

        adapter.setItemClickListener(this);

        savedResultsViewModel.getShoeScans().observe(getViewLifecycleOwner(), shoeScanWithShoeScanResults -> {
            if (shoeScanWithShoeScanResults.size() == 0) {
                binding.latestScanResultsNoItems.setVisibility(View.VISIBLE);
            } else {
                binding.latestScanResultsNoItems.setVisibility(View.GONE);
            }
            adapter.setItems(shoeScanWithShoeScanResults);
            this.shoeScanWithShoeScanResults = shoeScanWithShoeScanResults;
        });

        binding.latestScanResultsNoItemsBtn.setOnClickListener(this::onCaptureBtnClick);

        return root;
    }

    private void onCaptureBtnClick(View view) {
        findNavController(this).navigate(R.id.action_saved_results_to_scanner);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClicked(ShoeScanWithShoeScanResults scan) {
        switch (scan.shoeScan.resultQuality) {
            case ShoeScan.RESULT_QUALITY_ERROR:
                Context context = requireActivity();
                UIHelper.showAlertDialog(context,
                        "Error during recognition",
                        "This shoe was not recognized before because a problem occurred. We will try to recognize the shoe now.",
                        (dialogInterface, i) -> {
                            Intent intent = new Intent(requireActivity(), ScanProcessingActivity.class);
                            intent.putExtra(ScanProcessingActivity.EXTRA_RETRY_SHOE_SCAN_ID, scan.shoeScan.shoeScanId);
                            startActivity(intent);
                        });
                break;
            case ShoeScan.RESULT_QUALITY_NO_RESULT:
                break;
            case ShoeScan.RESULT_QUALITY_LOW:
                Intent intent = new Intent(requireActivity(), SimilarShoesActivity.class);
                intent.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, scan.shoeScan.shoeScanId);
                startActivity(intent);
                break;
            case ShoeScan.RESULT_QUALITY_HIGH:
                Intent i = new Intent(getActivity(), ProductActivity.class);
                i.putExtra(ProductActivity.EXTRA_SHOE_SCAN_ID, scan.shoeScan.shoeScanId);
                i.putExtra(ProductActivity.EXTRA_SHOE_ID, scan.shoeScanResults.get(0).shoe.shoeId);
                startActivity(i);
                break;
        }
    }
}