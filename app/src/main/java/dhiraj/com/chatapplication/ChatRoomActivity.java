package dhiraj.com.chatapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoomActivity extends AppCompatActivity  implements ChatRoomAdapter.IChatRoomListener{
    RecyclerView recyclerView;
    EditText editTextTypeMessage;
    ImageButton imageButtonSendMessage;
    ImageButton imageButtonOpenGallery;
    Trip trip;
    User loggedUser;
    String msg;
    FirebaseAuth mAuth;
    FirebaseStorage mStorage;
    FirebaseDatabase mDatabase;
    String date;
    InputStream inputStream=null;
    ArrayList<Message> messageArrayList;
    ChatRoomAdapter chatRoomAdapter;
    LinearLayoutManager layoutManager;
    ImageButton imageButtonOpenMaps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setTitle("Chat Room");
        recyclerView= (RecyclerView) findViewById(R.id.recyclerViewChatRoom);
        editTextTypeMessage= (EditText) findViewById(R.id.editTextTypeMessage);
        imageButtonSendMessage= (ImageButton) findViewById(R.id.imageButtonSendMessage);
        imageButtonOpenGallery= (ImageButton) findViewById(R.id.imageButtonOpenGallery);
        imageButtonOpenMaps= (ImageButton) findViewById(R.id.imageButtonOpenMaps);
        loggedUser= (User) getIntent().getExtras().getSerializable("loggedUser");
        trip= (Trip) getIntent().getExtras().getSerializable("selectedTrip");
        mDatabase=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        mStorage=FirebaseStorage.getInstance();
        messageArrayList=new ArrayList<>();
        getMessage();
        imageButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg=editTextTypeMessage.getText().toString().trim();
                if(msg!=null && !msg.isEmpty()){
                    editTextTypeMessage.setText("");
                    date=new Date(System.currentTimeMillis()).toString();
                    final Message message=new Message();
                    message.setSentUserId(loggedUser.getUserId());
                    message.setMsg(msg);
                    message.setSentBy(loggedUser.getImage());
                    message.setSentTime(date);
                    putMessage(message);
                    
                }
            }
        });
        imageButtonOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
        imageButtonOpenMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri = data.getData();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            inputStream = getContentResolver().openInputStream(imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            String path = "messages/" + mAuth.getCurrentUser().getUid()+".png";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);
            storageReference.putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    date=new Date(System.currentTimeMillis()).toString();
                    Message message=new Message();
                    message.setSentUserId(loggedUser.getUserId());
                    message.setSentBy(loggedUser.getImage());
                    message.setImageFile(taskSnapshot.getDownloadUrl().toString());
                    message.setSentTime(date);
                    putMessage(message);
                }
            });
            storageReference.putStream(inputStream).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatRoomActivity.this, "Sending image failed", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    
    public  void putMessage(final Message message){
        final DatabaseReference databaseReference=mDatabase.getReference("trips").child(trip.getId()).child("tripUsers");
        Query query=databaseReference.orderByChild("tripUsers");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot:dataSnapshot.getChildren()){
                    String userId=userSnapshot.child("userId").getValue().toString();
                    String isJoined=userSnapshot.child("joined").getValue().toString();
                    if(isJoined.equals("true")){
                        DatabaseReference messageReference=databaseReference.child(userId).child("messages").push();
                        message.setMessageId(messageReference.getKey());
                        messageReference.setValue(message);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getMessage(){
        DatabaseReference reference=mDatabase.getReference("trips").child(trip.getId()).child("tripUsers").child(loggedUser.getUserId()).child("messages");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message=dataSnapshot.getValue(Message.class);
                messageArrayList.add(message);
                chatRoomAdapter=new ChatRoomAdapter(ChatRoomActivity.this,messageArrayList,ChatRoomActivity.this);
                recyclerView.setAdapter(chatRoomAdapter);
                layoutManager=new LinearLayoutManager(ChatRoomActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                chatRoomAdapter.notifyDataSetChanged();
//                Toast.makeText(ChatRoomActivity.this, messageArrayList.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                chatRoomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void deleteChat(int position) {
        DatabaseReference reference=mDatabase.getReference("trips").child(trip.getId()).child("tripUsers").child(loggedUser.getUserId()).child("messages");
        reference.child(messageArrayList.get(position).getMessageId()).setValue(null);
        messageArrayList.remove(position);
    }
}
