package com.uh.urbanhouserowner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    FirebaseAuth auther;
    FirebaseUser currentuser;
    FirebaseDatabase profiledata;
    DatabaseReference ref;
    StorageReference storageRef;

    ProgressDialog pd;

    Context context;
    List<Modelpost> postList;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public AdapterPosts(Context context, List<Modelpost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, viewGroup, false);

        auther = FirebaseAuth.getInstance();
        currentuser = auther.getCurrentUser();
        profiledata = FirebaseDatabase.getInstance();
        ref = profiledata.getReference("Posts");
        storageRef = getInstance().getReference();
        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int i) {

        final String postUid = FirebaseDatabase.getInstance().getReference().getKey();
        final String uid = postList.get(i).getUid();
        String Uemail = postList.get(i).getUemail();
        String uname = postList.get(i).getUname();
        String udp = postList.get(i).getUdp();
        final String pid = postList.get(i).getPid();
        String ptime = postList.get(i).getPtime();
        String phone = postList.get(i).getPhone();
        String address = postList.get(i).getAddress();
        String pincode = postList.get(i).getPincode();
        final String pimage = postList.get(i).getPimage();
        String city = postList.get(i).getCity();
        String price = postList.get(i).getPrice();
        String date = postList.get(i).getDate();


        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(ptime));
        String pTime = DateFormat.format("dd/mm/yyyy hh:mm:aa", calendar).toString();

        holder.price.setText(price);
        //holder.bhk.setText(bhk);
        holder.address.setText(address);
        holder.date.setText(date);
        holder.price.setText(price);
        /*holder.price.setText(price);
        holder.price.setText(price);
        holder.price.setText(price);
        holder.price.setText(price);*/

        try {
            Picasso.get().load(pimage).fit().centerCrop().into(holder.propimg);
        } catch (Exception e) {
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        /*if(pimage.equals("noImage")){
            //holder.propimg.setVisibility(View.GONE);
        }
        else{
            holder.propimg.setVisibility(View.VISIBLE);
            try{
                Picasso.get().load(pimage).into(holder.propimg);
            }
            catch(Exception e){

            }
        }*/

        holder.cardssss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, cardopen.class);
                intent.putExtra("postid", pid);
                context.startActivity(intent);
                Toast.makeText(context, "delete" + pid, Toast.LENGTH_SHORT).show();


            }
        });

        /*holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showmoreoptions(holder.more,uid,myUid,pid,pimage);
            }
        });*/



       /* holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                begindelete(pid,pimage);
                Toast.makeText(context, "delete button", Toast.LENGTH_SHORT).show();

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "edit button"+pid, Toast.LENGTH_SHORT).show();

            }
        });*/

    }

    private void showmoreoptions(ImageButton more, String uid, String myUid, final String pid, final String pimage) {

        PopupMenu popupMenu = new PopupMenu(context, more, Gravity.END);
        if (uid.equals(myUid)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0) {
                    Toast.makeText(context, "delete clicked xxxx", Toast.LENGTH_SHORT).show();
                    begindelete(pid, pimage);
                } else if (id == 1) {
                    //Toast.makeText(context, "delete clicked xxxx", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, add_property.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", pid);
                    context.startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();

    }

    private void begindelete(String pid, String pImage) {

        if (pImage.equals("noImage")) {
            deletewithoutimage(pid);
           /* DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postUid);
            databaseReference.setValue(null);*/

        } else {
            deletewithimage(pid, pImage);
        }

    }

    private void deletewithimage(final String pid, String pImage) {
        pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        StorageReference picref = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pid);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void deletewithoutimage(String pid) {


        pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pid);

        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /*private void deletewithoutimage(String pid) {

        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Deleting...");

        Query fquery= FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pid);

        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/


    /*private void deletewithimage(final String pid, String pImage) {

        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Deleting...");

        StorageReference picref= FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Query fquery= FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pid);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }*/

    /* private void deletewithoutimage(String pid, String pImage) {

         final ProgressDialog pd=new ProgressDialog(context);
         pd.setMessage("Deleting...");

         Query fquery= FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pid);
         fquery.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot ds : dataSnapshot.getChildren()){
                     ds.getRef().removeValue();
                 }
                 Toast.makeText(context, "Deleted successfully w", Toast.LENGTH_SHORT).show();
                 pd.dismiss();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

     }
 */
    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView propimg, propimg1, propimg2;
        TextView address, bhk, price, date;
        Button delete, edit;
        ImageButton more;

        CardView cardssss;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            propimg = itemView.findViewById(R.id.propertyimg);
            /*propimg1=itemView.findViewById(R.id.propimg1);
            propimg2=itemView.findViewById(R.id.propimg2);*/
            address = itemView.findViewById(R.id.address);
            bhk = itemView.findViewById(R.id.bhk);
            price = itemView.findViewById(R.id.price);
            date = itemView.findViewById(R.id.date);
            /*delete=itemView.findViewById(R.id.delete);
            edit=itemView.findViewById(R.id.edit);*/
            more = itemView.findViewById(R.id.morebutton);
            cardssss = itemView.findViewById(R.id.propcard);

        }
    }
}