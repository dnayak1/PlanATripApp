package dhiraj.com.chatapplication;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder> {
    ArrayList<FavoritePlace> arrayListPlaces=new ArrayList<>();
    Context mContext;
    private IPlacesListener placesListener;

    public PlacesAdapter(Context mContext, ArrayList<FavoritePlace> arrayListPlaces, IPlacesListener placesListener) {
        this.arrayListPlaces = arrayListPlaces;
        this.mContext = mContext;
        this.placesListener = placesListener;

    }

    @Override
    public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view= layoutInflater.inflate(R.layout.places_layout,parent,false);
        PlacesAdapter.PlacesViewHolder placesViewHolder=new PlacesAdapter.PlacesViewHolder(view);
        return placesViewHolder;
    }

    @Override
    public void onBindViewHolder(PlacesViewHolder holder, int position) {
        final FavoritePlace favoritePlace=arrayListPlaces.get(position);
        holder.textViewFavoritePlaces.setText(favoritePlace.getPlaceName());
        holder.imageButtonDeletePlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placesListener.deletePlaces(favoritePlace);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListPlaces.size();
    }

    public static class PlacesViewHolder extends RecyclerView.ViewHolder{
        TextView textViewFavoritePlaces;
        ImageButton imageButtonDeletePlaces;
        public PlacesViewHolder(View itemView) {
            super(itemView);
            textViewFavoritePlaces= (TextView) itemView.findViewById(R.id.textViewFavouritePlaces);
            imageButtonDeletePlaces= (ImageButton) itemView.findViewById(R.id.imageButtonDeletePlaces);
        }
    }

    interface IPlacesListener
    {
        void deletePlaces(FavoritePlace favoritePlace);
    }
}
