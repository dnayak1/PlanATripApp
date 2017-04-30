package dhiraj.com.chatapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    ArrayList<Message> messageArrayList=new ArrayList<>();
    Context mContext;
    private IChatRoomListener chatRoomListener;
    SimpleDateFormat simpleDateFormat,simpleDateFormatNewFormat;
    Date inputDate,outputDate;
    String dateString,outputDateString,stringPrettyTime;
    PrettyTime prettyTime;
    FirebaseAuth mAuth;

    public ChatRoomAdapter(Context mContext, ArrayList<Message> messageArrayList, IChatRoomListener chatRoomListener) {
        this.messageArrayList = messageArrayList;
        this.mContext = mContext;
        this.chatRoomListener = chatRoomListener;

    }

    @Override
    public ChatRoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view= layoutInflater.inflate(R.layout.msg_display,parent,false);
        ChatRoomAdapter.ChatRoomViewHolder chatRoomViewHolder=new ChatRoomAdapter.ChatRoomViewHolder(view);
        return chatRoomViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatRoomViewHolder holder, final int position) {
        mAuth=FirebaseAuth.getInstance();
        final Message message=messageArrayList.get(position);
        Picasso.with(mContext).load(message.getSentBy()).into(holder.imageViewSenderPic);
        if(message.getMsg()!=null){
            holder.textViewShowMessage.setText(message.getMsg());
            holder.imageViewShowSendPic.setVisibility(View.GONE);
        }
        else{
            holder.textViewShowMessage.setVisibility(View.GONE);
            Picasso.with(mContext).load(message.getImageFile()).into(holder.imageViewShowSendPic);
        }
        simpleDateFormat=new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
        simpleDateFormatNewFormat=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        dateString=message.getSentTime();
        try {
            inputDate=simpleDateFormat.parse(dateString);
            outputDateString=simpleDateFormatNewFormat.format(inputDate);
            outputDate=simpleDateFormatNewFormat.parse(outputDateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        prettyTime=new PrettyTime();
        stringPrettyTime=prettyTime.format(outputDate);
        holder.textViewSentTime.setText(stringPrettyTime);
        holder.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatRoomListener.deleteChat(position);
            }
        });
        if (mAuth.getCurrentUser().getUid().equals(message.getSentUserId())){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.relativeLayoutMessages.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.relativeLayoutMessages.setLayoutParams(params);
            holder.relativeLayoutMessages.setBackgroundColor(Color.parseColor("#7F7FFF"));
        }
        else{
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.relativeLayoutMessages.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
            holder.relativeLayoutMessages.setLayoutParams(params);
            holder.relativeLayoutMessages.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewSenderPic;
        TextView textViewShowMessage;
        ImageView imageViewShowSendPic;
        TextView textViewSentTime;
        ImageButton imageButtonDelete;
        RelativeLayout relativeLayoutMessages;
        public ChatRoomViewHolder(View itemView) {
            super(itemView);
            imageViewSenderPic= (ImageView) itemView.findViewById(R.id.imageViewSenderPic);
            textViewShowMessage= (TextView) itemView.findViewById(R.id.textViewShowMessage);
            imageViewShowSendPic= (ImageView) itemView.findViewById(R.id.imageViewShowSendPic);
            textViewSentTime= (TextView) itemView.findViewById(R.id.textViewSentTime);
            imageButtonDelete= (ImageButton) itemView.findViewById(R.id.imageButtonDelete);
            relativeLayoutMessages= (RelativeLayout) itemView.findViewById(R.id.relativeLayoutMessages);
        }
    }

    interface IChatRoomListener
    {
        void deleteChat(int position);
    }
}
