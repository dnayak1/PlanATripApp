package dhiraj.com.chatapplication;

/**
 * Created by dhira on 21-04-2017.
 */

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

public class PendingRecyclerAdapter extends RecyclerView.Adapter<PendingRecyclerAdapter.PendingRecyclerViewHolder> {
    ArrayList<User> arrayListPending=new ArrayList<>();
    Context mContext;
    private IPendingListener pendingListener;

    public PendingRecyclerAdapter(Context mContext, ArrayList<User> arrayListPending, IPendingListener pendingListener) {
        this.arrayListPending = arrayListPending;
        this.mContext = mContext;
        this.pendingListener = pendingListener;

    }

    @Override
    public PendingRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view= layoutInflater.inflate(R.layout.pending_layout,parent,false);
        PendingRecyclerAdapter.PendingRecyclerViewHolder pendingRecyclerViewHolder=new PendingRecyclerAdapter.PendingRecyclerViewHolder(view);
        return pendingRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(PendingRecyclerViewHolder holder, int position) {
        final User user=arrayListPending.get(position);
        Picasso.with(mContext).load(user.getImage()).into(holder.imageViewPending);
        holder.textViewPending.setText(user.getFirstName()+" "+user.getLastName());
        holder.imageButtonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pendingListener.acceptUser(user);
            }
        });
        holder.imageButtonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pendingListener.rejectUser(user);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayListPending.size();
    }

    public static class PendingRecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewPending;
        TextView textViewPending;
        ImageButton imageButtonAccept;
        ImageButton imageButtonReject;
        public PendingRecyclerViewHolder(View itemView) {
            super(itemView);
            imageViewPending= (ImageView) itemView.findViewById(R.id.imageViewPending);
            textViewPending= (TextView) itemView.findViewById(R.id.textViewPending);
            imageButtonAccept= (ImageButton) itemView.findViewById(R.id.imageButtonAccept);
            imageButtonReject= (ImageButton) itemView.findViewById(R.id.imageButtonReject);
        }
    }

    interface IPendingListener
    {
        void acceptUser(User user);
        void rejectUser(User user);
    }
}
