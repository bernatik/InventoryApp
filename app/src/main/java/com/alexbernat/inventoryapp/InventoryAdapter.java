package com.alexbernat.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexbernat.inventoryapp.data.InventoryContract;

/**
 * Created by Александр on 28.02.2017.
 */

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    public static final int SALE_INCREMENT = 1;
    private final Context mContext;
    final private InventoryAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;

    public InventoryAdapter(Context context, InventoryAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(mContext).inflate(R.layout.catalog_item, parent, false);
        return new InventoryViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(InventoryViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String productName = mCursor.getString(CatalogActivity.INDEX_PRODUCT_NAME);
        int quantity = mCursor.getInt(CatalogActivity.INDEX_QUANTITY);
        double price = mCursor.getDouble(CatalogActivity.INDEX_PRICE);
        Bitmap image = DbBitmapUtility.getImage(mCursor.getBlob(CatalogActivity.INDEX_IMAGE));
        holder.tvName.setText(productName);
        holder.tvQuantity.setText(String.valueOf(quantity));
        holder.tvPrice.setText(String.valueOf(price));
        holder.ivImage.setImageBitmap(image);

        holder.btnSale.setTag(position);
        holder.btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemId = (int) v.getTag();
                mCursor.moveToPosition(itemId);
                int quantity = mCursor.getInt(mCursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY));
                quantity = quantity - 1;
                if (quantity < 0) {
                    Toast.makeText(mContext, R.string.toast_sale_failed, Toast.LENGTH_SHORT).show();
                } else {
                    long updateId = mCursor.getInt(CatalogActivity.INDEX_NAME_ID);
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY, quantity);
                    Uri currentUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, updateId);
                    int rowsUpdated = mContext.getContentResolver().update(currentUri, values, null, null);
                    String productName = mCursor.getString(CatalogActivity.INDEX_PRODUCT_NAME);
                    if (rowsUpdated != 0) {
                        String message = mContext.getString(R.string.toast_sale_successful, SALE_INCREMENT, productName);
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface InventoryAdapterOnClickHandler {
        void onClick(int idOfProduct);
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName, tvQuantity, tvPrice;
        ImageView ivImage;
        Button btnSale;

        public InventoryViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.product_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.quantity);
            tvPrice = (TextView) itemView.findViewById(R.id.price);
            ivImage = (ImageView) itemView.findViewById(R.id.product_image);
            btnSale = (Button) itemView.findViewById(R.id.sale_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemId = getAdapterPosition();
            mCursor.moveToPosition(itemId);
            mClickHandler.onClick(mCursor.getInt(CatalogActivity.INDEX_NAME_ID));
        }
    }
}
