package com.example.sneakerfinder.ui.main_activity.saved_results;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sneakerfinder.R;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoeScanResults;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple adapter for the shopping list.
 * It takes as list of ShoppingItem objects and renders them to a ListView.
 *
 * This is an adapter version for a RecyclerView.
 * It's a bit complex at first, but it helps to think about the code here in these steps:
 *
 * 1. Adapter itself: The adapter itself is responsible for displaying the content in the list.
 *    Concretely, all you have to do is to describe how each item view is filled with content.
 *    The rest is done automatically by Android, using the adapter (e.g. updating items etc.)
 *
 * 2. To describe how the content is displayed, you need to implement a "view holder" class.
 *    Typically, this is done as an inner class directly in this adapter class.
 *
 *    An instance of view holder is created automatically whenever an item needs to be displayed.
 * 3. "Bookkeeping" stuff: We need to implement some smaller things, like a method for setting the
 *    list of items, and a method for counting the items.
 *
 * 4. Handling clicks: In this example, we also handle clicks, which are passed from view holder (i.e.
 *    the item that is clicked) to the adapter, which in turn passes it to the activity. For this
 *    to work, we implement some interfaces and listeners.
 */

public class SavedResultsAdapter extends RecyclerView.Adapter<SavedResultsAdapter.ItemViewHolder> {

    /*
    Interface that the activity using this adapter's recycler view should implement
    so that we can pass e.g. click events to the activity.
    */
    public interface ItemClickListener {
        void onItemClicked(ShoeScanWithShoeScanResults shoe);
    }


    /*
    Inner class for a view holder for this adapter.
    The view holder represents the part of the GUI that is created per item in the list.
    It is filled with each item's content below (see method onBindViewHolder).
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView titleText;
        private final TextView descText;
        private final ImageView img;
        private final TextView priceText;
        private final View shoeDrillDown;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.shoe_name);
            descText = itemView.findViewById(R.id.shoe_desc);
            img = itemView.findViewById(R.id.shoe_iv);
            priceText = itemView.findViewById(R.id.shoe_price);
            shoeDrillDown = itemView.findViewById(R.id.shoe_drill_down);
            shoeDrillDown.setOnClickListener(this);
            itemView.findViewById(R.id.item_root).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // pass on the click to the adapter (which passes it on to the activity/listener)
            SavedResultsAdapter.this.onItemClicked(this.getLayoutPosition());
        }

    }

    private final LayoutInflater inflater;
    private List<ShoeScanWithShoeScanResults> shoeScans;
    private ItemClickListener listener;

    /***
     * Constructor.
     * @param context Application context, as required by Android.
     */
    public SavedResultsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    // This method is called whenever a new view holder is needed (e.g. new item to show).
    @NonNull
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_shoe, parent, false);
        return new ItemViewHolder(itemView);
    }

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();

    // This method is called whenever a view holder needs content.
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (shoeScans != null){
            ShoeScanWithShoeScanResults scanWithScanResults = shoeScans.get(position);
            ShoeScan shoeScan = scanWithScanResults.shoeScan;
            Date scanDate = shoeScan.scanDate;
            if (scanDate != null)
                holder.descText.setText(DATE_FORMAT.format(scanDate));

            if (shoeScan.resultQuality == ShoeScan.RESULT_QUALITY_HIGH) {
                ShoeScanResultWithShoe topResult = scanWithScanResults.shoeScanResults.get(0);
                if (topResult != null && topResult.shoe != null) {
                    Shoe shoe = topResult.shoe;
                    if (shoe.name != null) holder.titleText.setText(shoe.model);
                    if (shoe.price != null) holder.priceText.setText(shoe.price);
                    if (shoe.thumbnailUrl != null) Picasso.get().load(shoe.thumbnailUrl).into(holder.img);
                }
            } else {
                if (shoeScan.scanImageFilePath == null) holder.img.setImageResource(R.drawable.alert_triangle_grey);
                else Picasso.get().load("file://" + shoeScan.scanImageFilePath).into(holder.img);

                if (!(shoeScan.resultQuality == ShoeScan.RESULT_QUALITY_LOW)) holder.shoeDrillDown.setVisibility(View.GONE);

                Resources res = holder.itemView.getContext().getResources();
                holder.titleText.setTextColor(res.getColor(R.color.red));

                switch (shoeScan.resultQuality) {
                    case ShoeScan.RESULT_QUALITY_ERROR:
                        holder.titleText.setText(R.string.error);
                        holder.priceText.setText(R.string.tap_to_retry_recognition);
                        break;
                    case ShoeScan.RESULT_QUALITY_NO_RESULT:
                        holder.titleText.setText(R.string.no_result);
                        break;
                    case ShoeScan.RESULT_QUALITY_LOW:
                        holder.titleText.setText(R.string.result_is_not_accurate);
                        holder.priceText.setText(R.string.tap_to_see_similar_shoes);
                        break;
                }
            }
        }
    }

    // Setter for the items list
    public void setItems(List<ShoeScanWithShoeScanResults> items){
        this.shoeScans = items;
        notifyDataSetChanged();
        /* AndroidStudio will mark the above line as "inefficient". It's ok for this.
        A better solution would (also) support methods like addItem and removeItem. */
    }

    // Method that returns the current number of items (needed for the adapter class).
    public int getItemCount() {
        if (shoeScans != null)
            return shoeScans.size();
        else return 0;
    }

    // Set the listener (= activity we want to pass events to, e.g. click events)
    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    // We use this method to pass on click events to the listening activity
    private void onItemClicked(int index) {
        if(this.listener != null){
            this.listener.onItemClicked(shoeScans.get(index));
        }
    }
}

