package dhiraj.com.chatapplication;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ViewTripsRecyclerAdapter extends RecyclerView.Adapter<ViewTripsRecyclerAdapter.ViewTripsRecyclerViewHolder> {
    ArrayList<Trip> arrayListTrip=new ArrayList<>();
    Context mContext;
    private ITripsListener tripListener;

    public ViewTripsRecyclerAdapter(Context mContext, ArrayList<Trip> arrayListTrip, ITripsListener tripListener) {
        this.arrayListTrip = arrayListTrip;
        this.mContext = mContext;
        this.tripListener = tripListener;

    }

    @Override
    public ViewTripsRecyclerAdapter.ViewTripsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view= layoutInflater.inflate(R.layout.view_trip_layout,parent,false);
        ViewTripsRecyclerAdapter.ViewTripsRecyclerViewHolder viewTripsRecyclerViewHolder=new ViewTripsRecyclerAdapter.ViewTripsRecyclerViewHolder(view);
        return viewTripsRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewTripsRecyclerViewHolder holder, int position) {
        final Trip trip=arrayListTrip.get(position);
        Picasso.with(mContext).load(trip.getImage()).into(holder.imageViewTripImage);
        holder.textViewTripNameLocation.setText(trip.getTitle()+" To "+trip.getLocation());
        if(trip.isJoined()){
            holder.buttonJoinTrip.setVisibility(View.INVISIBLE);
        }
        else {
            holder.buttonChatRoom.setVisibility(View.INVISIBLE);
        }
        holder.buttonJoinTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripListener.joinTrip(trip);
            }
        });
        holder.buttonChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripListener.chatRoom(trip);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListTrip.size();
    }

    public static class ViewTripsRecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewTripImage;
        TextView textViewTripNameLocation;
        Button buttonJoinTrip;
        Button buttonChatRoom;
        public ViewTripsRecyclerViewHolder(View itemView) {
            super(itemView);
            imageViewTripImage= (ImageView) itemView.findViewById(R.id.imageViewViewTrip);
            textViewTripNameLocation= (TextView) itemView.findViewById(R.id.textViewViewTripTitleLocation);
            buttonJoinTrip= (Button) itemView.findViewById(R.id.buttonJoin);
            buttonChatRoom= (Button) itemView.findViewById(R.id.buttonChatRoom);
        }
    }

    interface ITripsListener
    {
        void joinTrip(Trip trip);
        void chatRoom(Trip trip);
    }
}
